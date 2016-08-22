name := "paymycable"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalaVersion := "2.11.6"

crossPaths := false

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

libraryDependencies ++= Seq(
  ws,
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
  "org.quartz-scheduler" % "quartz" % "2.2.2",
  "com.typesafe.play" %% "play-mailer" % "4.0.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
  "com.github.tototoshi" %% "scala-csv" % "1.3.3",
  "com.codeborne" % "phantomjsdriver" % "1.3.0",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.23",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.3"
)