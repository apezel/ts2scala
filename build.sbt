scalaVersion := "2.11.0"

name := "ts2scala"

version := "0.1"

mainClass := Some("com.apyx.scala.ts2scala.Main")

organization := "apyx"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"

libraryDependencies <+= scalaVersion { sv =>
  "org.scala-lang" % "scala-reflect" % sv
}

resolvers += Resolver.sonatypeRepo("snapshots")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full)

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
)
