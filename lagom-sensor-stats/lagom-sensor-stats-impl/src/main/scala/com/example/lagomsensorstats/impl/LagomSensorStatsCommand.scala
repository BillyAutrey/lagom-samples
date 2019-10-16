package com.example.lagomsensorstats.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the commands that the LagomSensorStatsEntity supports.
  */
sealed trait LagomSensorStatsCommand[R] extends ReplyType[R]


/**
  * A command to switch the greeting message.
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case object CreateSensor extends LagomSensorStatsCommand[Done] {

  /**
    * Format for the use greeting message command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[CreateSensor.type] = Json.format
}

/**
  * A command to switch the greeting message.
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case object Get extends LagomSensorStatsCommand[LagomSensorState] {

  /**
    * Format for the use greeting message command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[Get.type] = Json.format
}

/**
  * A command to say hello to someone using the current greeting message.
  *
  * The reply type is String, and will contain the message to say to that
  * person.
  */
case class UpdateSensor(data: String) extends LagomSensorStatsCommand[Done]

object UpdateSensor {

  /**
    * Format for the hello command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[UpdateSensor] = Json.format
}
