scalaVersion := "2.11.0"

name := "ts2scala-macros"

version := "0.2.1"

organization := "fr.apyx"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"

libraryDependencies <+= scalaVersion { sv =>
  "org.scala-lang" % "scala-reflect" % sv
}

resolvers += Resolver.sonatypeRepo("snapshots")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full)

publishMavenStyle := true

pomIncludeRepository := (_ => false)

pomExtra := (
    <url>http://github.com/apezel/ts2scala/</url>
      <licenses>
        <license>
          <name>MIT</name>
          <url>http://opensource.org/licenses/MIT</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:apezel/ts2scala.git</url>
        <connection>scm:git:git@github.com:apezel/ts2scala.git</connection>
      </scm>
      <developers>
        <developer>
          <id>apezel</id>
          <name>Arnaud PEZEL</name>
          <url>http://www.apyx.fr/</url>
        </developer>
      </developers>)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-encoding", "utf8"
)
