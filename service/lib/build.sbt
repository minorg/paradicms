lazy val root = (project in file("."))

// Constants
val jenaVersion = "3.13.1"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "io.lemonlabs" %% "scala-uri" % "1.5.1",
  "org.apache.jena" % "jena-arq" % jenaVersion,
  "org.apache.jena" % "jena-core" % jenaVersion,
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.25"
)

name := "service-lib"
organization := "org.paradicms"
scalaVersion := "2.12.10"
version := "1.0.0-SNAPSHOT"
