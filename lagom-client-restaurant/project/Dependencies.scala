import com.lightbend.cinnamon.sbt.Cinnamon
import com.lightbend.lagom.core.LagomVersion
import com.lightbend.lagom.sbt.LagomImport._
import sbt._

object Dependencies {

  //akka
  lazy val akkaVersion = "2.5.22"

  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  lazy val akkaServiceDiscovery = "com.typesafe.akka" %% "akka-discovery" % akkaVersion

  //akka http
  lazy val akkaHttpVersion = "10.1.7"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion
  lazy val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion

  //akka mgmt
  lazy val akkaMgmtVersion = "1.0.1"

  lazy val akkaMgmt = "com.lightbend.akka.management" %% "akka-management" % akkaMgmtVersion
  lazy val akkaClusterBootstrap = "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaMgmtVersion
  lazy val akkaDiscoveryK8s =  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaMgmtVersion

  //misc
  lazy val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"

  //test
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

  val lagomImplDeps = Seq(
    lagomScaladslPersistenceCassandra,
    lagomScaladslKafkaBroker,
    lagomScaladslTestKit,
    lagomScaladslAkkaDiscovery,
    akkaDiscoveryK8s,
    lagomScaladslCluster,
    macwire,
    scalaTest
  )

  val cinnamonPrometheusDeps = Seq(
    Cinnamon.library.cinnamonPrometheus,
    Cinnamon.library.cinnamonPrometheusHttpServer
  )

  val cinnamonLagomDeps = Seq(
    Cinnamon.library.cinnamonLagom,
    Cinnamon.library.cinnamonJvmMetricsProducer,
    Cinnamon.library.cinnamonScala
  )

  val cinnamonAkkaHttpDeps = Seq(
    Cinnamon.library.cinnamonAkka,
    Cinnamon.library.cinnamonAkkaHttp,
    Cinnamon.library.cinnamonJvmMetricsProducer,
    Cinnamon.library.cinnamonScala,
    Cinnamon.library.cinnamonLagom
  )
}
