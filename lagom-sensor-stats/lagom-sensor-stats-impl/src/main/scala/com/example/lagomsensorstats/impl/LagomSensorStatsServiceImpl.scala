package com.example.lagomsensorstats.impl

import akka.NotUsed
import com.example.lagomsensorstats.api
import com.example.lagomsensorstats.api.{LagomSensorStatsService, Sensor, SensorData}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.collection.immutable
import scala.concurrent.ExecutionContext

/**
  * Implementation of the LagomSensorStatsService.
  */
class LagomSensorStatsServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit executionContext: ExecutionContext) extends LagomSensorStatsService {

  def getRef(id: String) = persistentEntityRegistry.refFor[LagomSensorStatsEntity](id)

  override def sensorDataTopic(): Topic[api.SensorUpdated] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry
          .eventStream(LagomSensorStatsEvent.Tag, fromOffset)
          .mapConcat(filterSensorData)
    }

  private def convertEvent(sensorEvent: EventStreamElement[LagomSensorStatsEvent]): api.SensorUpdated = {
    sensorEvent.event match {
      case SensorUpdated(data, timestamp) => api.SensorUpdated(sensorEvent.entityId, data, timestamp)
    }
  }

  private def filterSensorData(sensorEvent: EventStreamElement[LagomSensorStatsEvent]) = {
    sensorEvent match {
      case event @ EventStreamElement(_,SensorUpdated(_,_), offset) =>
        immutable.Seq((convertEvent(event), offset))
      case _ => Nil
    }
  }

  override def createSensor(): ServiceCall[Sensor, NotUsed] = ServiceCall{ sensor =>
      getRef(sensor.id)
        .ask(CreateSensor)
        .map(_ => NotUsed)
  }

  override def getSensorData(id: String): ServiceCall[NotUsed, SensorData] = ServiceCall{ _ =>
    getRef(id)
      .ask(Get)
      .map( state => SensorData(id,state.data))
  }

  override def updateSensorData(): ServiceCall[SensorData, NotUsed] = ServiceCall{ data =>
    getRef(data.id)
      .ask(UpdateSensor(data.data))
      .map(_ => NotUsed)
  }
}
