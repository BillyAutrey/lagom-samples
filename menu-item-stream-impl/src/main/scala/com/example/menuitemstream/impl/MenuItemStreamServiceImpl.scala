package com.example.menuitemstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.menuitemstream.api.MenuItemStreamService
import com.example.menuitem.api.MenuItemService

import scala.concurrent.Future

/**
  * Implementation of the MenuItemStreamService.
  */
class MenuItemStreamServiceImpl(menuItemService: MenuItemService) extends MenuItemStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(menuItemService.hello(_).invoke()))
  }
}
