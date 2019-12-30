organization in ThisBuild := "org.paradicms"
scalaVersion in ThisBuild := "2.12.10"
version in ThisBuild := "1.0.0-SNAPSHOT"


// Constants
val jenaVersion = "3.13.1"
val playVersion = "2.8.0"


// Test settings
parallelExecution in ThisBuild := false


// Resolvers
if ((Path.userHome / ".m2").isDirectory) {
  resolvers in ThisBuild += Resolver.mavenLocal
} else {
  resolvers in ThisBuild += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  // updateOptions := updateOptions.value.withLatestSnapshots(false)
}


// Projects
lazy val root = project
  .aggregate(generic, serviceLib)
  .settings(
    skip in publish := true
  )

lazy val serviceLib =
  (project in file("service/lib")).settings(
    libraryDependencies ++= Seq(
      filters,
      guice,
      ws,
      "com.typesafe.play" %% "play" % playVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "io.lemonlabs" %% "scala-uri" % "1.5.1",
      "org.apache.jena" % "jena-arq" % jenaVersion,
      "org.apache.jena" % "jena-core" % jenaVersion,
      "org.sangria-graphql" %% "sangria" % "1.4.2",
      "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
      "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
      "org.scalatest" %% "scalatest" % "3.0.8" % Test,
      "org.slf4j" % "slf4j-simple" % "1.7.25" % Test
    ),
    name := "service-lib"
  )

lazy val generic = (project in file("service/generic"))
  .dependsOn(serviceLib)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      organization.value %% "service-lib" % version.value,
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
    ),
    routesGenerator := InjectedRoutesGenerator,
    // Adds additional packages into Twirl
    //TwirlKeys.templateImports += "com.example.controllers._"

    // Adds additional packages into conf/routes
    // play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
    skip in publish := true
  )
