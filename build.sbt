name := "paymycable"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalaVersion := "2.11.8"

crossPaths := false

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

libraryDependencies ++= Seq(
  ws,
  filters,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.typesafe" % "config" % "1.3.0",
  "org.scala-lang" % "scala-compiler" % "2.11.8",
//  "org.scalaz" % "scalaz-core_2.11" % "7.2.4",
  "org.specs2" %% "specs2" % "3.7",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",
  "org.quartz-scheduler" % "quartz" % "2.2.2",
  "com.typesafe.play" %% "play-mailer" % "4.0.0",
  "net.ruippeixotog" % "scala-scraper_2.11" % "1.0.0" 
)
