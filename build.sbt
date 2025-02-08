ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "Weather-bot",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "org.http4s" %% "http4s-dsl" % "0.23.30",
      "org.http4s" %% "http4s-ember-client" % "0.23.30",
      "org.http4s" %% "http4s-circe" % "0.23.30",
      "io.circe" %% "circe-generic" % "0.14.10",
      "io.circe" %% "circe-core" % "0.14.10",
      "io.circe" %% "circe-parser" % "0.14.10",
      "io.github.apimorphism" %% "telegramium-core" % "9.802.0",
      "io.github.apimorphism" %% "telegramium-high" % "9.802.0"
    )
  )
