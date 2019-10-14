package com.example.lagomsensorstatsstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.lagomsensorstatsstream.api.LagomSensorStatsStreamService
import com.example.lagomsensorstats.api.LagomSensorStatsService
import com.softwaremill.macwire._

class LagomSensorStatsStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomSensorStatsStreamApplication(context) {
      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomSensorStatsStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomSensorStatsStreamService])
}

abstract class LagomSensorStatsStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[LagomSensorStatsStreamService](wire[LagomSensorStatsStreamServiceImpl])

  // Bind the LagomSensorStatsService client
  lazy val lagomSensorStatsService: LagomSensorStatsService = serviceClient.implement[LagomSensorStatsService]
}
