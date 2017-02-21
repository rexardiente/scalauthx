name := """scalauthx"""

organization := "com.ejisan"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

crossScalaVersions := Seq("2.11.8", "2.12.1")

libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.6" % Test

resolvers += "Ejisan Github" at "https://ejisan.github.io/repo/"

publishTo := Some(Resolver.file("ejisan", file(Path.userHome.absolutePath + "/Development/repo.ejisan"))(Patterns(true, Resolver.mavenStyleBasePattern)))
