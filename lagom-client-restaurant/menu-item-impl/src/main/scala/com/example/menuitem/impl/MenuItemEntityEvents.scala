package com.example.menuitem.impl

import java.time.Instant

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
  * An event that represents a creation of a menu item
  */
case class MenuItemCreated(name: String, description: String, price: String) extends MenuItemEvent

object MenuItemCreated {

  /**
    * Format for the Menu Item Created event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[MenuItemCreated] = Json.format
}

case class MenuItemOrdered(eventTime: Instant) extends MenuItemEvent

object MenuItemOrdered {
  implicit val format: Format[MenuItemOrdered] = Json.format
}