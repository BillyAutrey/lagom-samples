package com.example.order.impl

import com.example.menuitem.api
import com.example.menuitem.api.MenuItemService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the MenuItemService.
  */
class MenuItemServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends MenuItemService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the Menu Item entity for the given ID.
    val ref = persistentEntityRegistry.refFor[MenuItemEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the Menu Item entity for the given ID.
    val ref = persistentEntityRegistry.refFor[MenuItemEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(MenuItemEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[MenuItemEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
