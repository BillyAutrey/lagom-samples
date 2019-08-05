package com.example.restaurantclient.routes

import akka.actor.ActorSystem
import akka.event.slf4j.Logger
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.CompleteOrRecoverWithMagnet
import com.example.menuitem.api.{MenuItem, MenuItemService, MenuItemShort}
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val menuItemFormat = jsonFormat3(MenuItem.apply)
  implicit val menuItemShortFormat = jsonFormat2(MenuItemShort.apply)
}

trait MenuItemRoutes extends JsonSupport{

  def menuItemClient: MenuItemService
  def system: ActorSystem

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
          path(JavaUUID) { id =>
            get {
              completeOrRecoverWith(CompleteOrRecoverWithMagnet(menuItemClient.menuItem(id.toString).invoke())) { extraction =>
                failWith(extraction)
              }
            }
          },
          path("create") {
            parameters('name, 'description, 'price) { (name, description, price) =>
              completeOrRecoverWith(CompleteOrRecoverWithMagnet(menuItemClient.createMenuItem().invoke(MenuItem(name, description, price)))) { extraction =>
                failWith(extraction)
              }
            }
          }
        )
      }
    )
}
