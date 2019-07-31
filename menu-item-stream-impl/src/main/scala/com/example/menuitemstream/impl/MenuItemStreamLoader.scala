package com.example.menuitemstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.menuitemstream.api.MenuItemStreamService
import com.example.menuitem.api.MenuItemService
import com.softwaremill.macwire._

class MenuItemStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new MenuItemStreamApplication(context) {
      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MenuItemStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[MenuItemStreamService])
}

abstract class MenuItemStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[MenuItemStreamService](wire[MenuItemStreamServiceImpl])

  // Bind the MenuItemService client
  lazy val menuItemService: MenuItemService = serviceClient.implement[MenuItemService]
}
