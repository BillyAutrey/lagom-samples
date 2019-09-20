package com.example.menuitem.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, JsSuccess, Json, Reads, Writes}

/**
  * This interface defines all the commands that the MenuItemEntity supports.
  */
sealed trait MenuItemCommand[R] extends ReplyType[R]

/**
  * A command to create a menu item
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case class CreateMenuItem(name: String, description: String, price: String) extends MenuItemCommand[Done]

object CreateMenuItem {

  /**
    * Format for the use greeting message command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[CreateMenuItem] = Json.format
}

case object Get extends MenuItemCommand[MenuItemState] {
  implicit val format: Format[Get.type] = Format(
    Reads( _ => JsSuccess(Get)),
    Writes( _ => Json.obj() )
  )
}

case class ChangePrice(value: String) extends MenuItemCommand[Done]

object ChangePrice {
  implicit val format: Format[ChangePrice] = Json.format
}