import Dependencies._

organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"



lazy val `lagom-sensor-stats` = (project in file("."))
  .aggregate(`lagom-sensor-stats-api`, `lagom-sensor-stats-impl`, `lagom-sensor-stats-stream-api`, `lagom-sensor-stats-stream-impl`)

lazy val `lagom-sensor-stats-api` = (project in file("lagom-sensor-stats-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-sensor-stats-impl` = (project in file("lagom-sensor-stats-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= lagomImplDeps
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`lagom-sensor-stats-api`)

lazy val `lagom-sensor-stats-stream-api` = (project in file("lagom-sensor-stats-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-sensor-stats-stream-impl` = (project in file("lagom-sensor-stats-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagom-sensor-stats-stream-api`, `lagom-sensor-stats-api`)
