package com.example.menuitem.impl

import akka.NotUsed
import akka.stream.testkit.scaladsl.TestSink
import com.example.menuitem.api._
import com.example.menuitem.api
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class MenuItemServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new MenuItemApplication(ctx) with LocalServiceLocator with TestTopicComponents
  }

  val client: MenuItemService = server.serviceClient.implement[MenuItemService]
  val testId: String = "test"

  override protected def afterAll(): Unit = server.stop()

  "Menu Item service" should {

    "Create a menu item" in {
      for {
        answer <- client.createMenuItem(testId).invoke(MenuItem("name", "desc", Price("1.00")))
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
        answer should ===(MenuItem("name","desc",Price("1.00")))
      }
    }

    "Get an abbreviated menu item" in {
      for {
        answer <- client.menuItemShort(testId).invoke()
      } yield {
        answer should ===(MenuItemShort("name",Price("1.00")))
      }
    }

    "Publish price change events on the topic" in {
      implicit val system = server.actorSystem
      implicit val mat    = server.materializer

      val source = client.priceChanges().subscribe.atMostOnceSource

      client.createMenuItem(testId).invoke(MenuItem("name", "desc", Price("1.00")))
      client.changePrice(testId).invoke(Price("2.00"))

      source
        .runWith(TestSink.probe[api.PriceChanged])
        .request(1)
        .expectNext should ===(api.PriceChanged(testId,"2.00"))

    }
  }
}
