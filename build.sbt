name := "rm_sim"

version := "0.1"

scalaVersion := "2.13.1"

assemblyJarName in assembly := "RM.jar"
test in assembly := {}
target in assembly := file("bin")
mainClass in assembly := Some("Main")
