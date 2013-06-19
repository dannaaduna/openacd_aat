name := "OpenACD automated acceptance tests"

version := "0.1"

scalaVersion := "2.10.0"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies += "org.scalatest" % "scalatest_2.10.0" % "2.0.M5" % "test"

libraryDependencies += "com.ezuce" % "oacdlt" % "0.0.1-SNAPSHOT"

seq(cucumberSettings : _*)

cucumberJunitReport := true
