package com.example.restaurantclient.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.CompleteOrRecoverWithMagnet
import com.example.menuitem.api.{MenuItem, MenuItemService, MenuItemShort, Price}
import spray.json._

import scala.concurrent.ExecutionContext

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val priceFormat = jsonFormat1(Price.apply)
  implicit val menuItemFormat = jsonFormat3(MenuItem.apply)
  implicit val menuItemShortFormat = jsonFormat2(MenuItemShort.apply)
}

trait MenuItemRoutes extends JsonSupport{

  def menuItemClient: MenuItemService
  def system: ActorSystem
  implicit lazy val executionContext: ExecutionContext = system.dispatcher

  lazy val menuItemRoutes: Route =
    concat(
      path(""){
        get {
          system.log.debug("Getting traffic")
          complete("Server responding")
        }
      },
      pathPrefix("menu") {
        concat(
          path("create" / Segment) { id =>
            parameters('name, 'description, 'price) { (name, description, price) =>
              completeOrRecoverWith(CompleteOrRecoverWithMagnet(
                menuItemClient
                  .createMenuItem(id)
                  .invoke(MenuItem(name, description, Price(price)))
                  .map(_ => s"$id")
              )) { extraction =>
                failWith(extraction)
              }
            }
          },
          path(Segment) { id =>
            get {
              completeOrRecoverWith(CompleteOrRecoverWithMagnet(
                menuItemClient
                  .menuItem(id.toString)
                  .invoke()
              )) { extraction =>
                failWith(extraction)
              }
            }
          }
        )
      }
    )
}
