package com.example.menuitem.impl

import java.util.UUID

import akka.NotUsed
import akka.stream.testkit.scaladsl.TestSink
import com.example.menuitem.api._
import com.example.menuitem.api
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents, TestTopicFactory}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class MenuItemServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest
      .defaultSetup
      .withCassandra()
  ) { ctx =>
    new MenuItemApplication(ctx)
      with LocalServiceLocator
      with TestTopicComponents
  }

  val client: MenuItemService = server.serviceClient.implement[MenuItemService]
  val name = "Name"
  val desc = "Desc"
  val price = Price("1.00")

  override protected def afterAll(): Unit = server.stop()

  private def generateTestId: String = UUID.randomUUID.toString
  private def createMenuItem(id: String) = {
    client.createMenuItem(id).invoke(MenuItem(name,desc,price))
  }

  "Menu Item service" should {

    "Create a menu item" in {
      val testId = generateTestId

      for {
        answer <- createMenuItem(testId)
        menuItem <- client.menuItem(testId).invoke()
      } yield {
        answer shouldBe a [NotUsed] //function returned
        menuItem shouldBe a [MenuItem] //just testing that a get doesn't fail
      }
    }

    "Get a menu item" in {
      val testId = generateTestId

      for {
        _ <- createMenuItem(testId)
        answer <- client.menuItem(testId).invoke()
      } yield {
        answer should ===(MenuItem(name,desc,price))
      }
    }

    "Get an abbreviated menu item" in {
      val testId = generateTestId

      for {
        _ <- createMenuItem(testId)
        answer <- client.menuItemShort(testId).invoke()
      } yield {
        answer should ===(MenuItemShort(name,price))
      }
    }

    "Publish price change events on the topic" in {
      implicit val system = server.actorSystem
      implicit val mat    = server.materializer

      val testId = generateTestId
      val source = client.priceChanges().subscribe.atMostOnceSource

      createMenuItem(testId)
        .map( _ => client.changePrice(testId).invoke(Price("2.00")))
        .map( _ => source
        .runWith(TestSink.probe[api.PriceChanged])
        .request(1)
        .expectNext should ===(api.PriceChanged(testId,"2.00")))

    }

    "Change prices for a menu item" in {
      val testId = generateTestId

      for{
        _ <- createMenuItem(testId)
        response <- client.changePrice(testId).invoke(Price("2.00"))
        item <- client.menuItemShort(testId).invoke()
      } yield {
        response shouldBe a [NotUsed]
        item should === (MenuItemShort(name, Price("2.00")))
      }
    }
  }
}
