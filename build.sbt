lazy val root = project.in(file(".")).
  aggregate(json4sASTJS, json4sASTJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val json4sAST = crossProject.in(file(".")).
  settings(
    name := "json4s-ast",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.11.7","2.10.5")
  ).
  jvmSettings(
    // Add JVM-specific settings here
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val json4sASTJVM = json4sAST.jvm
lazy val json4sASTJS = json4sAST.js
