package com.example.gateway

import akka.NotUsed
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestProbe
import org.mockito.MockitoSugar
import org.mockito.ArgumentMatchersSugar
import com.example.lagomsensorstats.api.{LagomSensorStatsService, Sensor, SensorData, SensorUpdated}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.{Subscriber, Topic}
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ProducerStub, ProducerStubFactory, ServiceTest}
import org.scalatest.concurrent.Eventually
import org.scalatest.{FlatSpec, FunSuite, Matchers, WordSpec}
import play.api.libs.json.Json

class SensorStatsRoutesSpec extends WordSpec with Matchers with MockitoSugar with ArgumentMatchersSugar with ScalatestRouteTest with Eventually{

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
      (1 to 10)
        .map( num => SensorUpdated("1", num.toString, num.toString))
        .foreach(producerStub.send)

      WS("/greeter", wsClient.flow) ~> fixture.websocketRoute ~> check {
        isWebSocketUpgrade shouldEqual true

        wsClient.expectMessage(SensorUpdated("1","1","1").toString)
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