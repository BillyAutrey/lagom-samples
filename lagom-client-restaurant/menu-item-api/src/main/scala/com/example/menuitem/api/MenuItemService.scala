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
    * Example: curl http://localhost:9000/api/menuItem/bacon
    */
  def menuItem(id: String): ServiceCall[NotUsed, MenuItem]

  /**
    * Example: curl http://localhost:9000/api/menuItemShort/bacon
    */
  def menuItemShort(id: String): ServiceCall[NotUsed, MenuItemShort]

  /**
    * Creates a menu item with a specific ID
    *
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"name": "Bacon", "description":"Yummy bacon", "price":"0.50"}' http://localhost:9000/api/createMenuItem/bacon
    *
    * @param id The identifier used to reference this entity
    */
  def createMenuItem(id: String): ServiceCall[MenuItem, NotUsed]

  override final def descriptor: Descriptor = {
    import Service._

    named("menu-item-svc")
      .withCalls(
        pathCall("/api/menuItem/:id", menuItem _),
        pathCall("/api/menuItemShort/:id", menuItemShort _),
        pathCall("/api/createMenuItem/:id", createMenuItem _ )
      )
      .withAutoAcl(true)
  }
}