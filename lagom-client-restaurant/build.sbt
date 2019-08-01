organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"

import com.lightbend.lagom.core.LagomVersion

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val lagomScaladslAkkaDiscoveryServiceLocator = "com.lightbend.lagom" %% "lagom-scaladsl-akka-discovery-service-locator" % LagomVersion.current withSources() //See Lagom issue
val akkaHttp = "com.typesafe.akka" %% "akka-http"   % "10.1.7" withSources()
val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"

lazy val `lagom-client-restaurant` = (project in file("."))
  .aggregate(`menu-item-api`, `menu-item-impl`, `order-api`, `order-impl`, `order-stream-api`, `order-stream-impl`, `restaurant-client`)

lazy val `menu-item-api` = (project in file("menu-item-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `menu-item-impl` = (project in file("menu-item-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`menu-item-api`)

lazy val `order-api` = (project in file("order-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `order-impl` = (project in file("order-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`order-api`)

lazy val `order-stream-api` = (project in file("order-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `order-stream-impl` = (project in file("order-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`order-stream-api`, `order-api`)

lazy val `restaurant-client` = (project in file("restaurant-client"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslClient withSources(),
      //lagomScaladslAkkaDiscovery, // Broken in 1.5.1, See Lagom PR 1948 for this fix
      lagomScaladslAkkaDiscoveryServiceLocator,
      akkaHttp,
      macwire,
      sprayJson,
      scalaTest
    )
  )
  .dependsOn(`order-api`,`menu-item-api`)