lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := """core-service"""
organization := "org.paradicms"
scalaVersion := "2.12.10"
version := "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(
  filters,
  guice,
  organization.value %% "service-lib" % version.value,
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
)

routesGenerator := InjectedRoutesGenerator

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
