package com.example.restaurantclient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.example.menuitem.api.MenuItemService
import com.example.restaurantclient.routes.MenuItemRoutes

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object Loader extends App with MenuItemRoutes{
  implicit val actorSystem   = ActorSystem("my-app")
  implicit val materializer  = ActorMaterializer()(actorSystem)
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher
  val clientFactory = new RestaurantLagomClientFactory(actorSystem, materializer)

  val menuItemClient = clientFactory.serviceClient.implement[MenuItemService]

  val serverBinding: Future[Http.ServerBinding] =
    Http().bindAndHandle(menuItemRoutes, interface = "localhost", port = 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      actorSystem.terminate()
  }

  Await.result(actorSystem.whenTerminated, Duration.Inf)
}
