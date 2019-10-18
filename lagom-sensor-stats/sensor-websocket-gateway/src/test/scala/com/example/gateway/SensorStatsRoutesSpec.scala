package com.example.gateway

import akka.NotUsed
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import com.example.lagomsensorstats.api.{LagomSensorStatsService, Sensor, SensorData, SensorUpdated}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.testkit.{ProducerStub, ProducerStubFactory}
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers, WordSpec}

class SensorStatsRoutesSpec extends WordSpec with Matchers with MockitoSugar with ArgumentMatchersSugar with ScalatestRouteTest with Eventually {

  //stubs for the Lagom service, to allow us to replicate topic behavior
  private val stubFactory = new ProducerStubFactory(system, materializer)
  private val producerStub = stubFactory.producer[SensorUpdated](LagomSensorStatsService.TOPIC_NAME)

  implicit val client: LagomSensorStatsServiceStub = new LagomSensorStatsServiceStub(producerStub)

  val wsClient = WSProbe()

  //Routes to test
  val fixture = new SensorStatsRoutes

  "SensorStatsRoutes" should {
    "return a websocket" in {
      //Send a bunch of SensorUpdated messages
      val messages = (1 to 10)
        .map(num => SensorUpdated("1", num.toString, num.toString))
      
      messages.foreach(producerStub.send)

      WS("/sensor_data", wsClient.flow) ~> fixture.websocketRoute ~> check {
        isWebSocketUpgrade shouldEqual true

        (1 to 10)
          .foreach(num =>
            wsClient.expectMessage(SensorUpdated("1", num.toString, num.toString).toString)
          )

      }
    }
  }
}

// Stubbed version of our SensorStatsService, allowing us to inject a producerStub
class LagomSensorStatsServiceStub(stub: ProducerStub[SensorUpdated]) extends LagomSensorStatsService {
  override def createSensor(): ServiceCall[Sensor, NotUsed] = ???

  override def getSensorData(id: String): ServiceCall[NotUsed, SensorData] = ???

  override def updateSensorData(): ServiceCall[SensorData, NotUsed] = ???

  /**
    * This gets published to Kafka.
    */
  override def sensorDataTopic(): Topic[SensorUpdated] = stub.topic
}