import sbt._

trait Dependencies {

  val playVersion = "2.4.3"

  val specs2Version = "3.6.1"

  val testDependencies = Seq(
    "org.mockito" % "mockito-core" % "1.10.19",
    "org.specs2" %% "specs2-core" % specs2Version,
    "org.specs2" %% "specs2-matcher" % specs2Version,
    "org.specs2" %% "specs2-junit" % specs2Version,
    "org.specs2" %% "specs2-mock" % specs2Version,
    "junit" % "junit" % "4.12",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.slf4j" % "log4j-over-slf4j" % "1.7.12"
  ).map(_ % "test")

  val appDependencies = Seq(
    "com.jsuereth" %% "scala-arm" % "1.4",
    "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
    "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
    "de.heikoseeberger" %% "akka-http-play-json" % "1.1.0",
    "org.apache.pdfbox" % "pdfbox" % "1.8.10"
  )
}
