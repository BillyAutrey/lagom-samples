package com.example.menuitem.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/**
  * The Menu Item service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the MenuItemService.
  */
trait MenuItemService extends Service {

  /**
    * Example: curl http://localhost:9000/api/menuItem/1
    */
  def menuItem(id: String): ServiceCall[NotUsed, MenuItem]

  /**
    * Example: curl http://localhost:9000/api/menuItemShort/1
    */
  def menuItemShort(id: String): ServiceCall[NotUsed, MenuItemShort]

  /**
   * Creates a menu item
   * Example: curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "name":
   * "Bacon", "description":"Yummy bacon", "price":"0.50"}' http://localhost:9000/api/hello/Alice
   */
  def createMenuItem(): ServiceCall[MenuItem, String]

  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("menu-item-service")
      .withCalls(
        pathCall("/api/menuItem/:id", menuItem _),
        pathCall("/api/menuItemShort/:id", menuItemShort _),
        pathCall("/api/createMenuItem", createMenuItem() )
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}