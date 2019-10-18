package com.example.lagomsensorstats.impl

import akka.Done
import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class LagomSensorStatsEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private val system = ActorSystem("LagomSensorStatsEntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(LagomSensorStatsSerializerRegistry))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private def withTestDriver(block: PersistentEntityTestDriver[LagomSensorStatsCommand[_], LagomSensorStatsEvent, LagomSensorState] => Unit): Unit = {
    val driver = new PersistentEntityTestDriver(system, new LagomSensorStatsEntity, "lagom-sensor-stats-1")
    block(driver)
    driver.getAllIssues should have size 0
  }

  "Lagom Sensor Stats entity" should {

    "create a sensor" in withTestDriver { driver =>
      val outcome = driver.run(CreateSensor)
      outcome.replies should contain only Done
      outcome.state.timestamp should not be ""
    }

    "update the sensor state" in withTestDriver { driver =>
      val data = "1.23"

      val createOutcome = driver.run(CreateSensor)
      createOutcome.replies should contain only Done
      createOutcome.state.timestamp should not be ""

      val outcome1 = driver.run(UpdateSensor(data))
      outcome1.replies.head shouldBe a [Done]
      outcome1.events.head should matchPattern { case SensorUpdated(`data`,_) => }
    }

    "get the sensor state" in withTestDriver{ driver =>
      val data = "1.23"

      val createOutcome = driver.run(CreateSensor)
      createOutcome.replies should contain only Done
      createOutcome.state.timestamp should not be ""

      val outcome1 = driver.run(UpdateSensor(data))
      outcome1.replies should contain only Done
      outcome1.events.head should matchPattern { case SensorUpdated(`data`,_) => }

      val outcome2 = driver.run(Get)
      outcome2.replies.head should matchPattern { case LagomSensorState(`data`,_) => }
    }

  }
}
