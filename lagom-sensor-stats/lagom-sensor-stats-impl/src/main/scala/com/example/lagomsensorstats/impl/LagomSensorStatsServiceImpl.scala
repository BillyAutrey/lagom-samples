package com.example.lagomsensorstats.impl

import com.example.lagomsensorstats.api
import com.example.lagomsensorstats.api.LagomSensorStatsService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the LagomSensorStatsService.
  */
class LagomSensorStatsServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends LagomSensorStatsService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the Lagom Sensor Stats entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomSensorStatsEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the Lagom Sensor Stats entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomSensorStatsEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(LagomSensorStatsEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[LagomSensorStatsEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
