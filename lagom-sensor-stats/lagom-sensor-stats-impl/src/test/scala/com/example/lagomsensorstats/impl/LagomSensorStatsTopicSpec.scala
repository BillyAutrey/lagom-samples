package com.example.lagomsensorstats.impl

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.testkit.scaladsl.TestSink
import com.example.lagomsensorstats.api
import com.example.lagomsensorstats.api.{LagomSensorStatsService, Sensor, SensorData}
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class LagomSensorStatsTopicSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  def generateId: String = UUID.randomUUID.toString

  def generateData: String = (Math.random * 100).toString

  "Lagom Sensor Stats service" should {

    "publish sensor updates to a topic" in
      ServiceTest.withServer(ServiceTest.defaultSetup.withCassandra()) { ctx =>
        new LagomSensorStatsApplication(ctx)
          with LocalServiceLocator
          with TestTopicComponents
      } { server =>
        implicit val system: ActorSystem = server.actorSystem
        implicit val mat: Materializer = server.materializer
        val client: LagomSensorStatsService = server.serviceClient.implement[LagomSensorStatsService]

        val id = generateId
        val data = generateData
        val source = client.sensorDataTopic().subscribe.atMostOnceSource

        client.createSensor().invoke(Sensor(id))
          .map(_ => client.updateSensorData().invoke(SensorData(id, data.toString)))
          .map(_ => source
            .runWith(TestSink.probe[api.SensorUpdated])
            .request(1)
            .expectNext should matchPattern { case api.SensorUpdated(`id`, `data`, _) => }
          )
      }
  }
}
