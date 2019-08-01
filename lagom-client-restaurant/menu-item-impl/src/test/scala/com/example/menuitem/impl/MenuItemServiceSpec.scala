package com.example.menuitem.impl

import com.example.menuitem.api._
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class MenuItemServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new MenuItemApplication(ctx) with LocalServiceLocator
  }

  val client: MenuItemService = server.serviceClient.implement[MenuItemService]

  override protected def afterAll(): Unit = server.stop()

  "Menu Item service" should {

    "Create a menu item" in {
      for {
        id <- client.createMenuItem().invoke(MenuItem("name", "desc", "1.00"))
      } yield {
        id shouldBe a [String]
      }
    }

    "Get a menu item" in {
      for {
        id <- client.createMenuItem().invoke(MenuItem("name","desc","1.00"))
        answer <- client.menuItem(id).invoke()
      } yield {
        answer should ===(MenuItem("name","desc","1.00"))
      }
    }

    "Get an abbreviated menu item" in {
      for {
        id <- client.createMenuItem().invoke(MenuItem("name","desc","1.00"))
        answer <- client.menuItemShort(id).invoke()
      } yield {
        answer should ===(MenuItemShort("name","1.00"))
      }
    }
  }
}
