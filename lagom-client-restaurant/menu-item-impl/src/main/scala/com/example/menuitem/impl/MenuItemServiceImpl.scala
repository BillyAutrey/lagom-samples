package com.example.menuitem.impl

import java.util.UUID

import akka.NotUsed
import com.example.menuitem.api.{MenuItem, MenuItemService, MenuItemShort}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

/**
  * Implementation of the MenuItemService.
  */
class MenuItemServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends MenuItemService {

  private def entityRef(id: String) =
    persistentEntityRegistry.refFor[MenuItemEntity](id)

  /**
   * Example: curl http://localhost:9000/api/menuItem/1
   */
  override def menuItem(id: String): ServiceCall[NotUsed, MenuItem] = ServiceCall { _ =>
    entityRef(id)
      .ask(Get)
      .map( state => MenuItem(state.name,state.description,state.price))
  }

  /**
   * Example: curl http://localhost:9000/api/menuItemShort/1
   */
  override def menuItemShort(id: String): ServiceCall[NotUsed, MenuItemShort] = ServiceCall{ _ =>
    entityRef(id)
      .ask(Get)
      .map( state => MenuItemShort(state.name,state.price))
  }

  /**
   * Creates a menu item
   * Example: curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "name":
   * "Bacon", "description":"Yummy bacon", "price":"0.50"}' http://localhost:9000/api/hello/Alice
   */
  override def createMenuItem(id: String): ServiceCall[MenuItem, NotUsed] = ServiceCall{ request =>
    entityRef(id)
      .ask(CreateMenuItem(request.name, request.description, request.price))
      .map(_ => NotUsed)
  }
}
