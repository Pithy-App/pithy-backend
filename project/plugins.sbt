
// In project/plugins.sbt. Note, does not support sbt 0.13, only sbt 1.x.
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

// dotenv for using env variables
addSbtPlugin("nl.gn0s1s" % "sbt-dotenv" % "3.0.0")

// assembly for packaging
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.2")

// generate dependencyTree
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
