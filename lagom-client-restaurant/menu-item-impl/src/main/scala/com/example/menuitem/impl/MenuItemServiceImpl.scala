package com.example.menuitem.impl

import akka.NotUsed
import com.example.menuitem.api
import com.example.menuitem.api.{MenuItem, MenuItemService, MenuItemShort, Price}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.collection.immutable
import scala.concurrent.ExecutionContext

/**
  * Implementation of the MenuItemService.
  */
class MenuItemServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends MenuItemService {

  private def entityRef(id: String) =
    persistentEntityRegistry.refFor[MenuItemEntity](id)

  /**
    * Example: curl http://localhost:9000/api/menuItem/1
    */
  override def menuItem(id: String): ServiceCall[NotUsed, MenuItem] = ServiceCall { _ =>
    entityRef(id)
      .ask(Get)
      .map(state => MenuItem(state.name, state.description, Price(state.price)))
  }

  /**
    * Example: curl http://localhost:9000/api/menuItemShort/1
    */
  override def menuItemShort(id: String): ServiceCall[NotUsed, MenuItemShort] = ServiceCall { _ =>
    entityRef(id)
      .ask(Get)
      .map(state => MenuItemShort(state.name, Price(state.price)))
  }

  /**
    * Creates a menu item
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "name":
    * "Bacon", "description":"Yummy bacon", "price":"0.50"}' http://localhost:9000/api/hello/Alice
    */
  override def createMenuItem(id: String): ServiceCall[MenuItem, NotUsed] = ServiceCall { request =>
    entityRef(id)
      .ask(CreateMenuItem(request.name, request.description, request.price.value))
      .map(_ => NotUsed)
  }

  /**
    * Change prices
    *
    * @param id The entity whose price needs to change
    * @return
    */
  override def changePrice(id: String): ServiceCall[Price, NotUsed] = ServiceCall { request =>
    entityRef(id)
      .ask(ChangePrice(request.value))
      .map(_ => NotUsed)
  }

  override def priceChanges(): Topic[api.PriceChanged] = TopicProducer.singleStreamWithOffset { fromOffset =>
    persistentEntityRegistry
      .eventStream(MenuItemEvent.Tag, fromOffset)
      .mapConcat(filterEvents)
  }

  /**
    * Convert implementation events into the API events that we care about.
    * @param elem Event stream elements from the MenuItemEvent topic.
    * @return The single event, converted to an API class instance
    */
  private def convertEvent(elem: EventStreamElement[MenuItemEvent]): api.PriceChanged =
    elem.event match {
      case PriceChanged(id, value) => api.PriceChanged(id, value)
    }

  /**
    * Filters out non-PriceChanged events that occur
    * on the MenuItemEvent topic
    *
    * @param elem Event Stream elements to be filtered
    * @return Immutable sequences of events.  Contains either one or zero elements,
    *         which can be flattened outside of the filter.
    */
  private def filterEvents(elem: EventStreamElement[MenuItemEvent]) =
    elem match {
      case event@EventStreamElement(_, PriceChanged(_, _), offset) =>
        immutable.Seq((convertEvent(event), offset))
      case _ => Nil
    }
}
