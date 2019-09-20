package com.example.menuitem.impl

import java.time.LocalDateTime

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

/**
  * This is an event sourced entity. It has a state, [[MenuItemState]], which
  * stores menu item data.
  */
class MenuItemEntity extends PersistentEntity {

  override type Command = MenuItemCommand[_]
  override type Event = MenuItemEvent
  override type State = MenuItemState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: MenuItemState = MenuItemState.empty

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case MenuItemState.empty => uninitialized
    case _ => initialized
  }

  private def uninitialized =
    Actions()
      .onCommand[CreateMenuItem,Done]{
        case (CreateMenuItem(name, desc, price), ctx, state) =>
          ctx.thenPersist(
            MenuItemCreated(name,desc,price)
          ) { _ =>
            ctx.reply(Done)
          }
      }
      .onReadOnlyCommand[Get.type,MenuItemState]{
        case (Get, ctx, state) =>
          ctx.invalidCommand(s"Menu item $entityId has not been initialized.")
      }
      .onEvent {
        case (MenuItemCreated(name,desc,price), state) =>
          state.copy(name = name, description = desc, price = price)
      }

  private def initialized =
    Actions()
      .onCommand[CreateMenuItem,Done]{ case (_,ctx,_) =>
        ctx.commandFailed(MenuItemException("Cannot create, this menu item already exists"))
        ctx.done
      }
      .onCommand[ChangePrice,Done]{ case (ChangePrice(value),ctx,state) =>
        ctx.thenPersist(
          PriceChanged(entityId,value)
        ){ _ =>
          ctx.reply(Done)
        }
      }
      .onReadOnlyCommand[Get.type,MenuItemState]{
        case (Get, ctx, state) =>
          ctx.reply(state)
      }
      .onEvent{
        case (PriceChanged(_,value), state) =>
          state.copy(price = value)
      }
}

/**
  * The current state held by the persistent entity.
  */
case class MenuItemState(name: String, description: String, price: String, orderCount: Int, created: LocalDateTime)

object MenuItemState {
  /**
    * Format for the menu item state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[MenuItemState] = Json.format

  val empty = MenuItemState("", "", "", 0, LocalDateTime.now)
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
object MenuItemSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[CreateMenuItem],
    JsonSerializer[Get.type],
    JsonSerializer[MenuItemCreated],
    JsonSerializer[MenuItemState],
    JsonSerializer[MenuItemException]
  )
}