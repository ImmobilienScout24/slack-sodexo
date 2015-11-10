import sbtrelease.ReleasePlugin._
import sbt.Keys._
import sbt._

object Build extends Build with Dependencies {

  val javaVersion = "1.8"
  val encoding = "utf-8"

  lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

  lazy val root = Project(id = "root", base = file("."), settings = Defaults.coreDefaultSettings ++ releaseSettings ++ net.virtualvoid.sbt.graph.Plugin.graphSettings).settings(
    name := "slack-sodexo",
    organization := "de.is24",
    scalaVersion := "2.11.7",
    ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
    libraryDependencies ++= appDependencies ++ testDependencies,
    javacOptions ++= Seq("-source", javaVersion, "-target", javaVersion, "-Xlint"),
    scalacOptions ++= Seq("-feature", "-language:postfixOps", "-target:jvm-" + javaVersion, "-unchecked", "-deprecation", "-encoding", encoding),
    compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
    (compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle,
    resolvers ++= Seq(
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
    ), 
    publishTo := {
      val nexus = "http://nexus.rz.is/content/repositories/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "snapshots")
      else
        Some("releases" at nexus + "releases")
    }


  )


}
