package com.example.order.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the events that the OrderEntity supports.
  */
sealed trait OrderEvent extends AggregateEvent[OrderEvent] {
  def aggregateTag: AggregateEventTag[OrderEvent] = OrderEvent.Tag
}

object OrderEvent {
  val Tag: AggregateEventTag[OrderEvent] = AggregateEventTag[OrderEvent]
}

/**
  * An event that represents a change in greeting message.
  */
case class GreetingMessageChanged(message: String) extends OrderEvent

object GreetingMessageChanged {

  /**
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[GreetingMessageChanged] = Json.format
}
