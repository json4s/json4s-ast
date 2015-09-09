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
    name := "ast",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := scala211Version,
    organization := "org.json4s",
    crossScalaVersions := Seq(scala211Version,scala210Version),
    startYear := Some(2013),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := <url>https://github.com/json4s/json4s-ast</url>
      <licenses>
        <license>
          <name>Apache</name>
          <url>http://opensource.org/licenses/Apache-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:json4s/json4s-ast.git</url>
        <connection>scm:git:git@github.com:json4s/json4s-ast.git</connection>
      </scm>
      <developers>
        <developer>
          <id>casualjim</id>
          <name>Ivan Porto Carrero</name>
          <email>ivan@flanders.co.nz</email>
          <url>http://flanders.co.nz</url>
        </developer>
        <developer>
          <id>rossabaker</id>
          <name>Ross A. Baker</name>
          <email>ross@rossabaker.com</email>
          <url>http://www.rossabaker.com/</url>
        </developer>
        <developer>
          <id>mdedetrich</id>
          <name>Matthew de Detrich</name>
          <email>mdedetrich@gmail.com</email>
          <url>http://mdedetrich.github.io</url>
        </developer>
      </developers>
      <organization>
        <name>json4s</name>
        <url>http://json4s.org</url>
      </organization>
      <contributors>
        <contributor>
          <name>Bryce Anderson</name>
          <email>bryce.anderson22@gmail.com</email>
          <url>http://bryceanderson.net</url>
        </contributor>
        <contributor>
          <name>Eugene Yokota</name>
          <id>eed3si9n</id>
          <email>eed3si9n@gmail.com</email>
          <url>http://eed3si9n.com/</url>
        </contributor>
        <contributor>
          <name>Matt Farmer</name>
          <id>farmdawgnation</id>
          <email>http://farmdawgnation.com</email>
          <url>http://eed3si9n.com/</url>
        </contributor>
        <contributor>
          <name>James Roper</name>
          <id>jroper</id>
          <email>james@jazzy.id.au</email>
          <url>https://jazzy.id.au/</url>
        </contributor>
        <contributor>
          <name>Johannes Rudolph</name>
          <id>jrudolph</id>
          <email>johannes.rudolph@gmail.com</email>
          <url>http://virtual-void.net</url>
        </contributor>
        <contributor>
          <name>Erik Osheim</name>
          <id>non</id>
        </contributor>
        <contributor>
          <name>Jon Pretty</name>
          <id>propensive</id>
          <url>http://rapture.io/</url>
        </contributor>
        <contributor>
          <name>Kazuhiro Sera</name>
          <id>seratch</id>
          <email>seratch@gmail.com</email>
          <url>http://seratch.net//</url>
        </contributor>
        <contributor>
          <name>Mathias</name>
          <id>sirthias</id>
          <url>http://www.decodified.com</url>
        </contributor>
      </contributors>
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
