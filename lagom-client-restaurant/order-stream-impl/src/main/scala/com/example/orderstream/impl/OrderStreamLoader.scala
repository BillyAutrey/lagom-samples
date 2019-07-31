package com.example.orderstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.order.api.OrderService
import com.example.orderstream.api.OrderStreamService
import com.softwaremill.macwire._

class OrderStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new OrderStreamApplication(context) {
      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new OrderStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[OrderStreamService])
}

abstract class OrderStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[OrderStreamService](wire[OrderStreamServiceImpl])

  // Bind the OrderService client
  lazy val orderService: OrderService = serviceClient.implement[OrderService]
}
