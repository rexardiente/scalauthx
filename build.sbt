name := """scalauthx"""

organization := "com.ejisan"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

libraryDependencies += "org.specs2" %% "specs2-core" % "3.7.1" % Test

resolvers += "Ejisan Github" at "https://ejisan.github.io/repo/"

publishTo := Some(Resolver.file("ejisan", file(Path.userHome.absolutePath + "/Development/repo.ejisan"))(Patterns(true, Resolver.mavenStyleBasePattern)))
