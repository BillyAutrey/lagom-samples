package com.example.lagomsensorstats.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the events that the LagomSensorStatsEntity supports.
  */
sealed trait LagomSensorStatsEvent extends AggregateEvent[LagomSensorStatsEvent] {
  def aggregateTag: AggregateEventTag[LagomSensorStatsEvent] = LagomSensorStatsEvent.Tag
}

object LagomSensorStatsEvent {
  val Tag: AggregateEventTag[LagomSensorStatsEvent] = AggregateEventTag[LagomSensorStatsEvent]
}

case class SensorUpdated(data: String, timestamp: String) extends LagomSensorStatsEvent

object SensorUpdated {
  implicit val format: Format[SensorUpdated] = Json.format
}

case class SensorCreated(timestamp: String) extends LagomSensorStatsEvent

object SensorCreated {
  implicit val format: Format[SensorCreated] = Json.format
}
