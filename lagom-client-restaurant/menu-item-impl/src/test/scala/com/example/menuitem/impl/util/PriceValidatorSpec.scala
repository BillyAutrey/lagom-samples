package com.example.menuitem.impl.util

import com.example.menuitem.api.Price
import org.scalatest.{Matchers, WordSpec}
import org.scalacheck.Prop.{forAll,propBoolean}
import org.scalacheck.{Prop, Properties}

class PriceValidatorSpec extends WordSpec with Matchers{

  "PriceValidatorSpec" should {
    "Return false for a bad value" in {
      PriceValidator.validate(Price("1.1")) shouldBe false
    }

    "Return true for a good value" in {
      PriceValidator.validate(Price("1.10")) shouldBe true
    }
  }
}

object PriceValidatorProperties extends Properties("PriceValidator"){

  //This passes.
  property("validate true for positive doubles") = Prop.forAll { d: Double =>
    (d >= 0 && d < 10000) ==> PriceValidator.validate(Price(f"$d%1.2f"))
  }

  //This fails, because PriceValidator.validate does not test for negative values!
  property("validate false for negative doubles") = Prop.forAll { d: Double =>
    (d <= 0 && d > -10000) ==> !PriceValidator.validate(Price(f"$d%1.2f"))
  }

  //This passes.
  property("validate false without .") = Prop.forAll { s: String =>
    !PriceValidator.validate(Price(s.replace('.',',')))
  }

}