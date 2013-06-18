name := "OpenACD automated acceptance tests"

version := "0.1"

scalaVersion := "2.10.0"

libraryDependencies += "org.scalatest" % "scalatest_2.10.0" % "2.0.M5" % "test"

seq(cucumberSettings : _*)

cucumberJunitReport := true

