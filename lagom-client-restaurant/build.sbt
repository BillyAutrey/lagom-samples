import Dependencies._

organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.8"

lazy val `lagom-client-restaurant` = (project in file("."))
  .aggregate(`menu-item-api`, `menu-item-impl`, /*`order-api`, `order-impl`, `order-stream-api`, `order-stream-impl`,*/ `restaurant-client`)

lazy val `menu-item-api` = (project in file("menu-item-api"))
  .settings(
    libraryDependencies += lagomScaladslApi
  )

lazy val cinnamonSettings = Seq(
  cinnamon in run := true,
  cinnamon in test := true
)

lazy val `menu-item-impl` = (project in file("menu-item-impl"))
  .enablePlugins(LagomScala)
  .enablePlugins(Cinnamon)
  .settings(
    libraryDependencies ++= lagomImplDeps
    ++ cinnamonPrometheusDeps
    ++ cinnamonLagomDeps
  )
  .settings(lagomForkedTestSettings)
  .settings(cinnamonSettings)
  .dependsOn(`menu-item-api`)


lazy val `restaurant-client` = (project in file("restaurant-client"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(Cinnamon)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslClient,
      lagomScaladslAkkaDiscovery,
      akkaSlf4j,
      logback,
      akkaHttp,
      akkaMgmt,
      sprayJson,
      scalaTest
    )
      ++ cinnamonPrometheusDeps
      ++ cinnamonAkkaHttpDeps
  )
  .settings(dockerBaseImage := "openjdk:8-jre-slim")
  .settings(cinnamonSettings)
  .dependsOn(/*`order-api`,*/`menu-item-api`)