package com.example.gateway

import akka.http.scaladsl.model.ws._
import com.example.lagomsensorstats.api.LagomSensorStatsService
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.collection.immutable.Nil

class SensorStatsRoutes(implicit val client: LagomSensorStatsService, implicit val materializer: Materializer) {

  val websocketRoute = path("sensor_data") {
    extractUpgradeToWebSocket { upgrade =>
      complete(
        upgrade.handleMessagesWithSinkSource(
          Sink.ignore,
          client.sensorDataTopic().subscribe.atMostOnceSource
            .map(event => TextMessage(event.toString))
        )
      )
    }
  }
}
