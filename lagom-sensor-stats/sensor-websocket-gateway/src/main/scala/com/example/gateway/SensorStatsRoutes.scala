package com.example.gateway

import akka.NotUsed
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import com.example.lagomsensorstats.api.LagomSensorStatsService
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}

class SensorStatsRoutes(implicit val client: LagomSensorStatsService, implicit val materializer: Materializer) {

  def greeter: Flow[NotUsed, NotUsed, Any] =
    Flow[NotUsed].mapConcat {
      case _ => TextMessage(client.sensorDataTopic().subscribe.atMostOnceSource) :: Nil
    }

  val websocketRoute = path("greeter") {
    handleWebSocketMessages(greeter)
  }
}
