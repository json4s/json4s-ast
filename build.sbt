val scala211Version="2.11.7"
val scala210Version="2.10.5"

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
    scalaVersion := scala211Version,
    organization := "org.json4s",
    crossScalaVersions := Seq(scala211Version,scala210Version),
    homepage := Some(new URL("https://github.com/json4s/json4s-ast")),
    licenses := Seq(("MIT", new URL("https://github.com/json4s/json4s-ast/raw/HEAD/LICENSE"))),
    startYear := Some(2013),
    scmInfo := Some(ScmInfo(url("http://github.com/json4s/json4s-ast"), "scm:git:git://github.com/json4s/json4s-ast.git", Some("scm:git:git@github.com:json4s/json4s-ast.git")))
  ).
  jvmSettings(
    // Add JVM-specific settings here
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize", "-feature", "-Yinline-warnings"),
    javacOptions ++= Seq("-deprecation", "-Xlint"),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    libraryDependencies ++= Seq(
      "com.storm-enroute" %% "scalameter" % "0.7" % "test"
    ),
    parallelExecution in Test := false
  ).
  jsSettings(
    // Add JS-specific settings here
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Yinline-warnings")
  )

lazy val json4sASTJVM = json4sAST.jvm
lazy val json4sASTJS = json4sAST.js
