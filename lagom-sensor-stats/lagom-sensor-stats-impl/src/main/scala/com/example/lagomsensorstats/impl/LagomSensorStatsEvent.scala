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

/**
  * An event that represents a change in greeting message.
  */
case class SensorUpdated(data: String, timestamp: String) extends LagomSensorStatsEvent

object SensorUpdated {

  /**
    * Format for the sensor updated event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[SensorUpdated] = Json.format
}

case class SensorCreated(timestamp: String) extends LagomSensorStatsEvent

object SensorCreated {
  implicit val format: Format[SensorCreated] = Json.format
}
