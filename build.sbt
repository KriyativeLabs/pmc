name := "paymycable"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

crossPaths := false

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  filters,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.typesafe" % "config" % "1.3.0",
  "org.scala-lang" % "scala-compiler" % "2.11.6",
  "org.specs2" % "specs2_2.10" % "2.3.10",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",
  "org.quartz-scheduler" % "quartz" % "2.2.2"
)
