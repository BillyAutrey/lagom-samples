package com.example.gateway

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.management.scaladsl.AkkaManagement
import akka.stream.{ActorMaterializer, Materializer}
import com.example.lagomsensorstats.api.LagomSensorStatsService

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object Loader extends App{

  implicit val system: ActorSystem = ActorSystem("sensor-websocket-gateway")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val host = system.settings.config.getString("akka.management.http.hostname")

  AkkaManagement(system).start()

  val sensorStatsClientFactory = new SensorStatsClientFactory(system, materializer)
  implicit val client = sensorStatsClientFactory.serviceClient.implement[LagomSensorStatsService]

  val sensorRoutes = new SensorStatsRoutes

  val serverBinding: Future[Http.ServerBinding] =
    Http().bindAndHandle(sensorRoutes.websocketRoute, host, port = 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      system.log.info(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)


}
