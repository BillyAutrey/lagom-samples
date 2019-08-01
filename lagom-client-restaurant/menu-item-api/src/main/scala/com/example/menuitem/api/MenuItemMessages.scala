package com.example.menuitem.api

import play.api.libs.json.{Format, Json}

/**
 * Menu Item, and all details needed for printing on a menu or site.
 */
case class MenuItem(name: String, description: String, price: String)
object MenuItem { implicit val format: Format[MenuItem] = Json.format[MenuItem] }

/**
 * Short menu item, for receipt data
 */
case class MenuItemShort(name: String, price: String)
object MenuItemShort{ implicit val format: Format[MenuItemShort] = Json.format[MenuItemShort]}