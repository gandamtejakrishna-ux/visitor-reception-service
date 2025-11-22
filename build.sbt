//name := """visitor-reception-service"""
//organization := "com.vms.reception"
//
//version := "1.0-SNAPSHOT"
//
//lazy val root = (project in file(".")).enablePlugins(PlayScala)
//
//scalaVersion := "2.13.16"
//
//libraryDependencies ++= Seq(
//  guice,
//  ws,
//  filters,
//
//  // Testing
//  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,
//
//  // Slick (if reception wants DB later; harmless now)
//  "org.playframework" %% "play-slick"            % "6.1.0",
//  "org.playframework" %% "play-slick-evolutions" % "6.1.0",
//  "mysql" % "mysql-connector-java" % "8.0.26",
//
//  // Kafka Client
//  "org.apache.kafka" %% "kafka" % "3.7.0",
//
//  // Logging
//  "ch.qos.logback" % "logback-classic" % "1.2.13"
//)
//
//TwirlKeys.templateImports += "controllers.routes"
//
import scala.collection.immutable.Seq

name := """visitor-reception-service"""
organization := "com.vms.reception"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies ++= Seq(
  "org.playframework" %% "play-slick"            % "6.1.0",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0",
  "mysql" % "mysql-connector-java" % "8.0.26"
)
libraryDependencies += ws
TwirlKeys.templateImports += "controllers.routes"

lazy val akkaVersion = sys.props.getOrElse("akka.version", "2.8.8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
  "com.typesafe.akka" %% "akka-http" % "10.5.1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
  "org.apache.kafka" %% "kafka" % "3.7.0" // Kafka client
)

libraryDependencies += filters
libraryDependencies += "com.github.jwt-scala" %% "jwt-play-json" % "10.0.1"
libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.scalaplay.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.scalaplay.binders._"
