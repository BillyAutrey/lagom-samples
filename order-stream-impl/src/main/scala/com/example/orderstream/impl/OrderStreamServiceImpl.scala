package com.example.orderstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.order.api.OrderService
import com.example.orderstream.api.OrderStreamService

import scala.concurrent.Future

/**
  * Implementation of the MenuItemStreamService.
  */
class OrderStreamServiceImpl(orderService: OrderService) extends OrderStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(orderService.hello(_).invoke()))
  }
}
