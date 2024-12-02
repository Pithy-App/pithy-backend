ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.15"
ThisBuild / organization := "com.pithy"

val akkaVersion = "2.8.6"
val akkaHttpVersion = "10.5.3"
val circeVersion = "0.14.10"
val DoobieVersion = "0.13.4"
val NewTypeVersion = "0.4.4"


lazy val root = (project in file("."))
  .aggregate(app, database, lambda, platform, auth, openai)
  .dependsOn(app, database, lambda, platform, auth, openai)
  .settings(
    name := "pithy-backend-root",
    assembly / assemblyJarName := "PithyBackendRoot.jar"
  )

lazy val app = (project in file("app"))
  .dependsOn(platform, auth, utils, openai)
  .settings(
    name := "pithy-backend-app",
    assembly / assemblyJarName := "PithyBackendApp.jar",
    assembly / mainClass := Some("com.pithy.AppMainDev"),

    libraryDependencies ++= sharedDependency
)

// authentication module
lazy val auth = (project in file("auth"))
    .dependsOn(utils)
    .settings(
      name := "pithy-backend-auth",
      assembly / assemblyJarName := "PithyBackendAuth.jar",
      assembly / mainClass := Some("com.pithy.AuthMainDev"),
      libraryDependencies ++= sharedDependency
    )

// platform module for platform specific code - such as reddit api client
lazy val platform = (project in file("platform"))
  .dependsOn(utils)
  .settings(
    name := "pithy-backend-platform",
    assembly / assemblyJarName := "PithyBackendPlatform.jar",
    assembly / mainClass := Some("com.pithy.reddit.RedditMainDev"),

    libraryDependencies ++= Seq(
      // JSON parser
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,

      // OpenAI API for scala. This is an open source project maintained by community
      "io.cequence" %% "openai-scala-client" % "1.1.0",
    ),
    libraryDependencies ++= sharedDependency
  )

// openai module for interaction with openai
lazy val openai = (project in file("openai"))
  .dependsOn(utils, platform)
  .settings(
    name := "pithy-backend-openai",
    assembly / assemblyJarName := "PithyBackendOpenAI.jar",
    assembly / mainClass := Some("com.pithy.openai.OpenAIMainDev"),

    libraryDependencies ++= sharedDependency
  )

// database layer module
lazy val database = (project in file("database"))
  .settings(
    name := "pithy-backend-database",

    libraryDependencies ++= sharedDependency,
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core"     % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari"   % DoobieVersion
    )
  )

// AWS Lambdas module
lazy val lambda = (project in file("lambda"))
  .dependsOn(app, utils)
  .settings(
    name := "pithy-backend-lambda",

    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
      "com.amazonaws" % "aws-lambda-java-events" % "3.12.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0"
    )
  )

// utility module
lazy val utils = (project in file("utils"))
  .settings(
    name := "pithy-backend-utils",
    libraryDependencies ++= sharedDependency,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0"
    )
  )


// common dependencies
lazy val sharedDependency = Seq(
  // Config
  "com.typesafe" % "config" % "1.4.3",

  // Test
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.mockito" % "mockito-core" % "5.12.0" % Test,
  "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test,

  // Logging
  "org.slf4j" % "slf4j-api" % "2.0.13",
  "ch.qos.logback" % "logback-classic" % "1.5.6",

  // akka
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
//  "com.typesafe.akka" %% "akka-protobuf-v3" % akkaVersion
)

// Set the merge strategy to discard META-INF files, otherwise there will be de duplicate errors
ThisBuild / assembly / assemblyMergeStrategy := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

// Since javaOptions are only set when running a fork jvm
// Setting this javaOptions to allow unnamed modules to access internal classes
run / fork := true
run / javaOptions ++= Seq(
  "-Xmx2G", // Set max heap size to 2 GB
  "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED",
  //  "--add-opens=java.base/java.lang=ALL-UNNAMED",
  //  "--add-opens=java.base/java.util=ALL-UNNAMED",
)