package com.example.menuitem.impl

import play.api.libs.json.{Format, Json}

/**
 * An exception thrown by the shopping cart validation
 *
 * @param message The message
 */
case class MenuItemException(message: String) extends RuntimeException(message)

object MenuItemException {

  /**
   * Format for the MenuItemException.
   *
   * When a command fails, the error needs to be serialized and sent back to
   * the node that requested it, this is used to do that.
   */
  implicit val format: Format[MenuItemException] = Json.format[MenuItemException]
}
