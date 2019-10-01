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
      //Send a create command
      val outcome = driver.run(CreateMenuItem("name","test","1.00"))

      //Assert that an event was generated with the new state
      outcome.events should contain only MenuItemCreated("name","test","1.00")
      outcome.state.name should ===("name")
      outcome.state.description should ===("test")
      outcome.replies should ===(List(Done))
    }

    "When state is empty, and Get is received, send an error" in withTestDriver { driver =>
      //Send a Get command.  Note, new test.  State is empty.
      val outcome = driver.run(Get)

      //Get on empty returns an error.
      outcome.replies.head shouldBe a [InvalidCommandException]
      outcome.events.size should ===(0)
    }

    "When state is empty, and ChangePrice is received, send an error" in withTestDriver { driver =>
      //Send a Get command.  Note, new test.  State is empty.
      val outcome = driver.run(ChangePrice("2.00"))

      //ChangePrice on empty returns an error.
      outcome.replies.head shouldBe a [InvalidCommandException]
      outcome.events.size should ===(0)
    }

    "When state is valid, and a CreateMenuItem is received, send an error" in withTestDriver { driver =>
      //Creating the initial state for the test, and asserting to guarantee it.
      val createOutcome = driver.run(CreateMenuItem("name","test","1.00"))
      createOutcome.events should contain only MenuItemCreated("name","test","1.00")
      createOutcome.state.name should ===("name")
      createOutcome.replies should ===(List(Done))

      //Try to create the same item
      val outcome = driver.run(CreateMenuItem("name","test","2.00"))

      //Assert that we got an error
      outcome.replies.head shouldBe a [MenuItemException]
      outcome.events.size should ===(0)
    }

    "When state is valid, and a Get is received, send the current state" in withTestDriver { driver =>
      //Creating the initial state for the test, and asserting to guarantee it.
      val createOutcome = driver.run(CreateMenuItem("name","test","1.00"))
      createOutcome.events should contain only MenuItemCreated("name","test","1.00")
      createOutcome.state.name should ===("name")
      createOutcome.replies should ===(List(Done))

      //Run a Get command
      val outcome = driver.run(Get)

      //Assert that we responded with the correct response (state)
      outcome.replies should contain only MenuItemState("name","test","1.00",_: Int,_: LocalDateTime)
    }

    "When state is valid, and a ChangePrice is received, update the price" in withTestDriver { driver =>
      //Creating the initial state for the test, and asserting to guarantee it.
      val createOutcome = driver.run(CreateMenuItem("name","test","1.00"))
      createOutcome.events should contain only MenuItemCreated("name","test","1.00")
      createOutcome.state.name should ===("name")
      createOutcome.replies should ===(List(Done))

      //Run a ChangePrice command
      val outcome = driver.run(ChangePrice("2.00"))

      //Assert that we responded with the correct response (state)
      outcome.events should contain only PriceChanged(_: String,"2.00")
      outcome.replies should ===(List(Done))
      outcome.state.price should ===("2.00")

    }

  }
}
