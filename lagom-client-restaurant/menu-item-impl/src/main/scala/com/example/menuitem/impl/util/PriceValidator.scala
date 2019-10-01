package com.example.menuitem.impl.util

import com.example.menuitem.api.Price

object PriceValidator {
  def validate(price: Price): Boolean = {
    val priceSplit = price.value.split("\\.")
    priceSplit match {
      case Array(left: String,right: String) => if(right.length == 2) true else false
      case _ => false
    }
  }
}
