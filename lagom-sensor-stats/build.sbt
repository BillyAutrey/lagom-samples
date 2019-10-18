import Dependencies._

organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"


lazy val cinnamonSettings = Seq(
  cinnamon in run := true,
  cinnamon in test := true
)

lazy val `lagom-sensor-stats` = (project in file("."))
  .aggregate(`lagom-sensor-stats-api`, `lagom-sensor-stats-impl`, `sensor-websocket-gateway`)

lazy val `lagom-sensor-stats-api` = (project in file("lagom-sensor-stats-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-sensor-stats-impl` = (project in file("lagom-sensor-stats-impl"))
  .enablePlugins(LagomScala)
  .enablePlugins(Cinnamon)
  .settings(
    libraryDependencies ++= lagomImplDeps
  )
  .settings(lagomForkedTestSettings)
  .settings(cinnamonSettings)
  .dependsOn(`lagom-sensor-stats-api`)

lazy val `sensor-websocket-gateway` = (project in file("sensor-websocket-gateway"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(Cinnamon)
  .settings(
    libraryDependencies ++= akkaHttpGatewayDeps
    ++ cinnamonPrometheusDeps
    ++ cinnamonAkkaHttpDeps
    ++ mockito
  )
  .settings(dockerBaseImage := "openjdk:8-jre-slim")
  .settings(cinnamonSettings)
  .dependsOn(`lagom-sensor-stats-api`)
