package com.example.lagomsensorstats.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the commands that the LagomSensorStatsEntity supports.
  */
sealed trait LagomSensorStatsCommand[R] extends ReplyType[R]

case object CreateSensor extends LagomSensorStatsCommand[Done] {
  implicit val format: Format[CreateSensor.type] = Json.format
}

case object Get extends LagomSensorStatsCommand[LagomSensorState] {
  implicit val format: Format[Get.type] = Json.format
}

case class UpdateSensor(data: String) extends LagomSensorStatsCommand[Done]

object UpdateSensor {
  implicit val format: Format[UpdateSensor] = Json.format
}
