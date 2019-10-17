package com.example.lagomsensorstats.impl

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import com.example.lagomsensorstats.api._

class LagomSensorStatsServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new LagomSensorStatsApplication(ctx) with LocalServiceLocator
  }

  def generateId: String = UUID.randomUUID.toString

  val client: LagomSensorStatsService = server.serviceClient.implement[LagomSensorStatsService]

  override protected def afterAll(): Unit = server.stop()

  "Lagom Sensor Stats service" should {

    "create a sensor" in {
      val id = generateId
      client.createSensor().invoke(Sensor(id)).map{ response =>
        response shouldBe a [NotUsed]
      }
    }

    "update and retrieve a sensor" in {
      val id = generateId
      val data = Math.random * 100

      for {
        create <- client.createSensor().invoke(Sensor(id))
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
