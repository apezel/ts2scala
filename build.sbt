scalaVersion := "2.11.0-SNAPSHOT"

name := "ts2scala"

version := "0.1-SNAPSHOT"

mainClass := Some("com.apyx.scala.ts2scala.Main")

organization := "apyx"

libraryDependencies += "org.scala-lang" % "scala-parser-combinators" % "2.11.0-M4"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.0-SNAPSHOT"
)

resolvers += Resolver.sonatypeRepo("snapshots")

addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full)

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
)