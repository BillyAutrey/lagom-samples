package com.example.restaurantclient.routes

import akka.NotUsed
import akka.actor.ActorSystem
import org.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.example.menuitem.api.{MenuItem, MenuItemService, Price}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.Future

class MenuItemRoutesSpec extends WordSpec with Matchers with MockitoSugar with ScalatestRouteTest {

  //mocks
  private val mockService = mock[MenuItemService]
  private val mockCall = mock[ServiceCall[NotUsed,MenuItem]]

  //return values
  private val menuItem = MenuItem("name","desc",Price("1.00"))

  //stubbed behavior
  MockitoSugar.doReturn(mockCall).when(mockService).menuItem("1")
  MockitoSugar.doReturn(Future.successful(menuItem)).when(mockCall).invoke()

  //Use the Akka HTTP Testkit's actor system, and override with mocked service
  class MenuItemRoutesFixture extends MenuItemRoutes{
    val system: ActorSystem = system
    val menuItemClient: MenuItemService = mockService
  }

  val fixture = new MenuItemRoutesFixture

  "MenuItemRoutes" should {
    "return a menuItem" in {
      Get("/menu/1") ~> fixture.menuItemRoutes ~> check {
        responseAs[String] ===(menuItem)
      }
    }
  }

}
