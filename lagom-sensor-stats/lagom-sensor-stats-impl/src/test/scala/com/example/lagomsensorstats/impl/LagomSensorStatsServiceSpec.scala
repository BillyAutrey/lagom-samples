package com.example.lagomsensorstats.impl

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.testkit.scaladsl.TestSink
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.example.lagomsensorstats.api._
import com.example.lagomsensorstats.api

class LagomSensorStatsServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new LagomSensorStatsApplication(ctx)
      with LocalServiceLocator
      with TestTopicComponents
  }

  implicit val system: ActorSystem = server.actorSystem
  implicit val mat: Materializer   = server.materializer

  def generateId: String   = UUID.randomUUID.toString
  def generateData: String = (Math.random * 100).toString

  def createSensor(id: String) = {
    client.createSensor().invoke(Sensor(id))
  }

  val client: LagomSensorStatsService = server.serviceClient.implement[LagomSensorStatsService]

  override protected def afterAll(): Unit = server.stop()

  "Lagom Sensor Stats service" should {

    "create a sensor" in {
      val id = generateId
      createSensor(id).map{ response =>
        response shouldBe a [NotUsed]
      }
    }

    "update and retrieve a sensor" in {
      val id = generateId
      val data = generateData

      for {
        create <- createSensor(id)
        update <- client.updateSensorData().invoke(SensorData(id,data.toString))
        get <- client.getSensorData(id).invoke()
      } yield {
        create shouldBe a [NotUsed]
        update shouldBe a [NotUsed]
        get shouldBe a [SensorData]
        get.data should ===(data.toString)
      }
    }
  }
}