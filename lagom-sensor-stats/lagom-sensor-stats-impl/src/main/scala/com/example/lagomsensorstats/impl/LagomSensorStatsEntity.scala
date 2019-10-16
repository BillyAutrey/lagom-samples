package com.example.lagomsensorstats.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

/**
  * This is an event sourced entity. It has a state, [[LagomSensorState]], which
  * stores sensor data and the last time it was updated.
  *
  * Event sourced entities are interacted with by sending them commands. This
  * entity supports three commands:
  * [[CreateSensor]] command (used to create the sensor)
  * [[Get]] command (read only command to query for state)
  * [[UpdateSensor]] command (to update current sensor state)
  *
  * Commands get translated to events, and it's the events that get persisted by
  * the entity. Each event will have an event handler registered for it, and an
  * event handler simply applies an event to the current state. This will be done
  * when the event is first created, and it will also be done when the entity is
  * loaded from the database - each event will be replayed to recreate the state
  * of the entity.
  *
  * This entity defines two events, the [[SensorCreated]] event,
  * which is emitted when a [[CreateSensor]] command is received, and a
  * [[SensorUpdated]] event, which is emitted when a [[UpdateSensor]]
  * command is received.
  */
class LagomSensorStatsEntity extends PersistentEntity {

  override type Command = LagomSensorStatsCommand[_]
  override type Event = LagomSensorStatsEvent
  override type State = LagomSensorState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: LagomSensorState = LagomSensorState.empty

  override def behavior: Behavior = {
    case LagomSensorState.empty => emptyBehavior
    case _ => liveBehavior
  }

  private def emptyBehavior =
    Actions().onCommand[CreateSensor.type,Done]{
      case (CreateSensor, ctx, state) =>
        ctx.thenPersist(
          SensorCreated(initialState.timestamp)
        ) { _ =>
          ctx.reply(Done)
        }
    }.onEvent {
      case (SensorCreated(createTime), state) =>
        state.copy(timestamp = createTime)
    }

  private def liveBehavior =
    Actions().onCommand[UpdateSensor,Done]{
      case (UpdateSensor(data), ctx, state) =>
        ctx.thenPersist(
          SensorUpdated(data, LocalDateTime.now.toString)
        ){ _ =>
          ctx.reply(Done)
        }
    }.onReadOnlyCommand[Get.type,LagomSensorState]{
      case (Get,ctx, state) =>
        ctx.reply(state)
    }.onEvent {
      case (SensorUpdated(data, timestamp), state) =>
        state.copy(data = data, timestamp = timestamp)
    }
}

/**
  * The current state held by the persistent entity.
  */
case class LagomSensorState(data: String, timestamp: String)

object LagomSensorState {
  /**
    * Format for the Lagom Sensor state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[LagomSensorState] = Json.format

  val empty = LagomSensorState("","")
}



/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object LagomSensorStatsSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[LagomSensorState],
    JsonSerializer[UpdateSensor],
    JsonSerializer[CreateSensor.type],
    JsonSerializer[SensorUpdated],
    JsonSerializer[SensorCreated]
  )
}
