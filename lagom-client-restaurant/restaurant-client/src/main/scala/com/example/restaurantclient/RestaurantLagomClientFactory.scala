package com.example.restaurantclient

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.client._
import play.api.libs.ws.ahc.AhcWSComponents

class RestaurantLagomClientFactory(val actorSystem: ActorSystem, val materializer: Materializer)
  extends LagomClientFactory("my-client", classOf[RestaurantLagomClientFactory].getClassLoader)
    with AkkaDiscoveryComponents
    with AhcWSComponents
