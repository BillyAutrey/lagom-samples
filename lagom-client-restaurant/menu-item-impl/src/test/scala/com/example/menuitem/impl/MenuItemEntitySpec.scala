package com.example.menuitem.impl

import java.time.LocalDateTime

import akka.Done
import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.InvalidCommandException
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class MenuItemEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private val system = ActorSystem("MenuItemEntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(MenuItemSerializerRegistry))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private def withTestDriver(block: PersistentEntityTestDriver[MenuItemCommand[_], MenuItemEvent, MenuItemState] => Unit): Unit = {
    val driver = new PersistentEntityTestDriver(system, new MenuItemEntity, "menu-item-1")
    block(driver)
    driver.getAllIssues should have size 0
  }

  "Menu Item entity" should {

    "When state is empty, and a CreateMenuItem is received, Create a menu item" in withTestDriver { driver =>
      val createResult = driver.run(CreateMenuItem("name","test","1.00"))
      createResult.events should contain only MenuItemCreated("name","test","1.00")
    }

    "When state is empty, and Get is received, send an error" in withTestDriver { driver =>
      val getResult = driver.run(Get)
      getResult.replies.head shouldBe a [InvalidCommandException]
    }

    "When state is valid, and a CreateMenuItem is received, send an error" in withTestDriver { driver =>
      val createResult = driver.run(CreateMenuItem("name","test","1.00"))
      createResult.events should contain only MenuItemCreated("name","test","1.00")
      val createResult2 = driver.run(CreateMenuItem("name","test","2.00"))
      createResult2.replies.head shouldBe a [MenuItemException]
    }

    "When state is valid, and a Get is received, send the current state" in withTestDriver { driver =>
      val createResult = driver.run(CreateMenuItem("name","test","1.00"))
      createResult.events should contain only MenuItemCreated("name","test","1.00")
      val result = driver.run(Get)
      result.replies should contain only MenuItemState("name","test","1.00",_: Int,_: LocalDateTime)
    }

  }
}
