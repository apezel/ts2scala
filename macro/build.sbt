scalaVersion := "2.11.0-M7"

name := "ts2scala-macros"

version := "0.2.1"

organization := "fr.apyx"

libraryDependencies += "org.scala-lang" % "scala-parser-combinators" % "2.11.0-M4"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.0-SNAPSHOT"
)

resolvers += Resolver.sonatypeRepo("snapshots")

addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full)

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