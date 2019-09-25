package com.example.menuitem.impl

import java.util.UUID

import akka.NotUsed
import com.example.menuitem.api._
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

/**
  * An example of starting a server for a single test.
  * This ensures a clean slate (and clean cassandra) for each test.
  * However, it also comes with a high service startup cost per test.
  */
class MenuItemServiceForEachSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  val testId = UUID.randomUUID().toString

  "Menu Item service" should {

    "Create a menu item" in ServiceTest.withServer(ServiceTest.defaultSetup.withCassandra()) {
      ctx =>
        new MenuItemApplication(ctx) with LocalServiceLocator with TestTopicComponents
    } {  server =>
      val client: MenuItemService = server.serviceClient.implement[MenuItemService]
      for {
        answer <- client.createMenuItem(testId).invoke(MenuItem("name", "desc", Price("1.00")))
        menuItem <- client.menuItem(testId).invoke()
      } yield {
        answer shouldBe a [NotUsed] //function returned
        menuItem shouldBe a [MenuItem] //just testing that a get doesn't fail
      }
    }
  }
}
