package com.example.lagomsensorstats.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object LagomSensorStatsService  {
  val TOPIC_NAME = "sensordata"
}

/**
  * The Lagom Sensor Stats service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagomSensorStatsService.
  */
trait LagomSensorStatsService extends Service {

//  /**
//    * Example: curl http://localhost:9000/api/hello/Alice
//    */
//  def hello(id: String): ServiceCall[NotUsed, String]
//
//  /**
//    * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
//    * "Hi"}' http://localhost:9000/api/hello/Alice
//    */
//  def useGreeting(id: String): ServiceCall[GreetingMessage, Done]

  def createSensor(): ServiceCall[Sensor,NotUsed]

  def getSensorData(id: String): ServiceCall[NotUsed,SensorData]

  def updateSensorData(): ServiceCall[SensorData,NotUsed]

  /**
    * This gets published to Kafka.
    */
  def sensorDataTopic(): Topic[SensorUpdated]

  override final def descriptor: Descriptor = {
    import Service._
    named("lagom-sensor-stats")
      .withCalls(
        pathCall("/api/create", createSensor _),
        pathCall("/api/getData/:id", getSensorData _),
        pathCall("/api/updateData", updateSensorData _ )
      )
      .withTopics(
        topic(LagomSensorStatsService.TOPIC_NAME, sensorDataTopic _)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user).  Here, we configure a partition key strategy that extracts the
          // sensor id as the partition key.
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[SensorData](_.id)
          )
      )
      .withAutoAcl(true)
  }
}

/**
  * The sensor class
  */
case class Sensor(id: String)

object Sensor {
  /**
    * Format for converting sensor definitions to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[Sensor] = Json.format[Sensor]
}



/**
  * The data associated with a sensor
  */
case class SensorData(id: String, data: String)

object SensorData {
  /**
    * Format for converting sensor data to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[SensorData] = Json.format[SensorData]
}

case class SensorUpdated(id: String, data: String, timestamp: String)

object SensorUpdated {
  implicit val format: Format[SensorUpdated] = Json.format[SensorUpdated]
}