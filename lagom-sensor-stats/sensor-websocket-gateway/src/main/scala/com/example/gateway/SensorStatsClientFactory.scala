package com.example.gateway

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.client.LagomClientFactory
import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaClientComponents
import play.api.libs.ws.ahc.AhcWSComponents

class SensorStatsClientFactory(val actorSystem: ActorSystem, val materializer: Materializer)
  extends LagomClientFactory("websocket-gateway-client", classOf[SensorStatsClientFactory].getClassLoader)
  with AkkaDiscoveryComponents
  with LagomKafkaClientComponents
  with AhcWSComponents
