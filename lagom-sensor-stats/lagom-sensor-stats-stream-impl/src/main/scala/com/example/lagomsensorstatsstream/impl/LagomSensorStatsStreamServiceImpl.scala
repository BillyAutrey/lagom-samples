package com.example.lagomsensorstatsstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.lagomsensorstatsstream.api.LagomSensorStatsStreamService
import com.example.lagomsensorstats.api.LagomSensorStatsService

import scala.concurrent.Future

/**
  * Implementation of the LagomSensorStatsStreamService.
  */
class LagomSensorStatsStreamServiceImpl(lagomSensorStatsService: LagomSensorStatsService) extends LagomSensorStatsStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(lagomSensorStatsService.hello(_).invoke()))
  }
}
