package com.example.restaurantclient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.management.scaladsl.AkkaManagement
import akka.stream.ActorMaterializer
import com.example.menuitem.api.MenuItemService
import com.example.restaurantclient.routes.MenuItemRoutes
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object Loader extends App with MenuItemRoutes{
  implicit val system   = ActorSystem("restaurant-client")
  implicit val materializer  = ActorMaterializer()(system)
  implicit val executionContext: ExecutionContext = system.dispatcher
  val clientFactory = new RestaurantLagomClientFactory(system, materializer)

  val host = system.settings.config.getString("akka.management.http.hostname")

  val menuItemClient = clientFactory.serviceClient.implement[MenuItemService]

  AkkaManagement(system).start()

  val serverBinding: Future[Http.ServerBinding] =
    Http().bindAndHandle(menuItemRoutes, host, port = 8080)

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
