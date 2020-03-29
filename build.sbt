// Projects
lazy val root = (project in file ("."))
  .aggregate(bookApp, genericApp, genericLib)
  .settings(
    name := "paradicms",
    skip in publish := true
  )
lazy val baseLib =
  (project in file("lib/scala/base")).settings(
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "io.lemonlabs" %% "scala-uri" % "1.5.1",
      "org.apache.jena" % "jena-arq" % jenaVersion,
      "org.apache.jena" % "jena-core" % jenaVersion,
      "org.apache.jena" % "jena-rdfconnection" % jenaVersion,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test,
      "org.slf4j" % "slf4j-simple" % slf4jVersion % Test
    ),
    name := "paradicms-base"
  )
lazy val bookApp = (project in file("app/book"))
  .dependsOn(genericLib % "compile->compile;test->test")
  .enablePlugins(PlayScala)
  .settings(
    //    libraryDependencies ++= Seq(
    //      organization.value %% "generic-lib" % version.value,
    //      organization.value %% "test-lib" % version.value % Test
    //    ),
    name := "book-app",
    routesGenerator := InjectedRoutesGenerator,
    // Adds additional packages into Twirl
    //TwirlKeys.templateImports += "com.example.controllers._"

    // Adds additional packages into conf/routes
    // play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
    skip in publish := true
  )
lazy val genericApp = (project in file("app/generic"))
  .dependsOn(genericLib % "compile->compile;test->test")
  .enablePlugins(PlayScala)
  .settings(
    //    libraryDependencies ++= Seq(
    //      organization.value %% "generic-lib" % version.value,
    //      organization.value %% "test-lib" % version.value % Test,
    //    ),
    name := "generic-app",
    routesGenerator := InjectedRoutesGenerator,
    // Adds additional packages into Twirl
    //TwirlKeys.templateImports += "com.example.controllers._"

    // Adds additional packages into conf/routes
    // play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
    skip in publish := true
  )
lazy val genericLib =
  (project in file("lib/scala/generic"))
    .dependsOn(baseLib % "compile->compile;test->test")
    .settings(
    libraryDependencies ++= Seq(
      filters,
      guice,
      ws,
      "com.typesafe.play" %% "play" % playVersion,
      // Include jena-text for the test store
      "org.apache.jena" % "jena-text" % jenaVersion % Test,
      "org.sangria-graphql" %% "sangria" % "1.4.2",
      "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
      "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
      "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
    ),
    name := "paradicms-generic"
  )

// Constants
val jenaVersion = "3.14.0"
val playVersion = "2.8.0"
val slf4jVersion = "1.7.25"


// Test settings
parallelExecution in ThisBuild := false


// Build settings
organization in ThisBuild := "org.paradicms"
scalaVersion in ThisBuild := "2.12.10"
version in ThisBuild := "1.0.1-SNAPSHOT"
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

// Publish settings
// Adapted from https://leonard.io/blog/2017/01/an-in-depth-guide-to-deploying-to-maven-central/ and
// https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html
developers in ThisBuild := List(
  Developer("gordom6",
    "Minor Gordon",
    "sonatype@minorgordon.net",
    url("https://github.com/minorg"))
)
homepage in ThisBuild := Some(url("https://github.com/minorg/paradicms"))
licenses in ThisBuild += ("GPL-3.0", url("https://opensource.org/licenses/GPL-3.0"))
publishMavenStyle in ThisBuild := true
publishTo in ThisBuild := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
scmInfo in ThisBuild := Some(ScmInfo(url("https://github.com/minorg/paradicms"), "git@github.com:minorg/paradicms.git"))
skip in publish := true // Don't publish the default project ('paradicms')
