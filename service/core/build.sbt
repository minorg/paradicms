name := """core"""
organization := "org.dressdiscover.cms"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
version := "1.0.0-SNAPSHOT"
val projectName = "cms"

val jenaVersion = "3.13.1"
scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  filters,
  guice,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "io.lemonlabs" %% "scala-uri" % "1.5.1",
  "org.apache.jena" % "jena-arq" % jenaVersion,
  "org.apache.jena" % "jena-core" % jenaVersion,
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
  //  "org.slf4j" % "slf4j-simple" % "1.7.25"
)

routesGenerator := InjectedRoutesGenerator

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
