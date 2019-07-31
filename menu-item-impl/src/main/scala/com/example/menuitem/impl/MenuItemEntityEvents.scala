package com.example.menuitem.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the events that the MenuItemEntity supports.
  */
sealed trait MenuItemEvent extends AggregateEvent[MenuItemEvent] {
  def aggregateTag: AggregateEventTag[MenuItemEvent] = MenuItemEvent.Tag
}

object MenuItemEvent {
  val Tag: AggregateEventTag[MenuItemEvent] = AggregateEventTag[MenuItemEvent]
}

/**
  * An event that represents a change in greeting message.
  */
case class GreetingMessageChanged(message: String) extends MenuItemEvent

object GreetingMessageChanged {

  /**
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[GreetingMessageChanged] = Json.format
}
