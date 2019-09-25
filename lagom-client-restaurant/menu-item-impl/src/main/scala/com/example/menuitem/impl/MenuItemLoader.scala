package com.example.menuitem.impl

import com.example.menuitem.api.MenuItemService
import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

class MenuItemLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new MenuItemApplication(context) with AkkaDiscoveryComponents with LagomKafkaComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MenuItemApplication(context) with LagomDevModeComponents with LagomKafkaComponents

  override def describeService = Some(readDescriptor[MenuItemService])
}

abstract class MenuItemApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[MenuItemService](wire[MenuItemServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = MenuItemSerializerRegistry

  // Register the Menu Item persistent entity
  persistentEntityRegistry.register(wire[MenuItemEntity])
}
