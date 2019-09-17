package com.example.menuitem.impl

import akka.NotUsed
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
  val testId: String = "test"

  override protected def afterAll(): Unit = server.stop()

  "Menu Item service" should {

    "Create a menu item" in {
      for {
        answer <- client.createMenuItem(testId).invoke(MenuItem("name", "desc", "1.00"))
        menuItem <- client.menuItem(testId).invoke()
      } yield {
        answer shouldBe a [NotUsed] //function returned
        menuItem shouldBe a [MenuItem] //just testing that a get doesn't fail
      }
    }

    "Get a menu item" in {
      for {
        answer <- client.menuItem(testId).invoke()
      } yield {
        answer should ===(MenuItem("name","desc","1.00"))
      }
    }

    "Get an abbreviated menu item" in {
      for {
        answer <- client.menuItemShort(testId).invoke()
      } yield {
        answer should ===(MenuItemShort("name","1.00"))
      }
    }
  }
}
