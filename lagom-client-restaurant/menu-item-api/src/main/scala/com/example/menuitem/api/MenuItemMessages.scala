package com.example.menuitem.api

import play.api.libs.json.{Format, Json}

/**
 * Menu Item, and all details needed for printing on a menu or site.
 */
case class MenuItem(name: String, description: String, price: Price)
object MenuItem { implicit val format: Format[MenuItem] = Json.format[MenuItem] }

/**
  * Short menu item, for receipt data
  */
case class MenuItemShort(name: String, price: Price)
object MenuItemShort{ implicit val format: Format[MenuItemShort] = Json.format[MenuItemShort]}

/**
  * Price data
  */
case class Price(value: String)
object Price{ implicit val format: Format[Price] = Json.format[Price]}

/**
  * Price change message
  */
case class PriceChanged(id: String, value: String)
object PriceChanged{ implicit val format: Format[PriceChanged] = Json.format[PriceChanged]}