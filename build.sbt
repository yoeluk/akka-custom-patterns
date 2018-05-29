name := "akka-custom-patterns"

version := "0.1"

organization := "com.mydomain"

scalaVersion := "2.12.6"

crossScalaVersions := Seq(scalaVersion.value,"2.11.7")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.0",
)