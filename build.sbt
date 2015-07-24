import scala.xml.Group

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5", "2.11.7")

name := "json4s-ast"

organization := "org.json4s"

scalacOptions ++= Seq("-target:jvm-1.7", "-unchecked", "-deprecation", "-optimize", "-feature", "-Yinline-warnings")

javacOptions ++= Seq("-deprecation", "-Xlint")

packageOptions <+= (name, version, organization) map {
  (title, version, vendor) =>
    Package.ManifestAttributes(
      "Created-By" -> "Simple Build Tool",
      "Built-By" -> System.getProperty("user.name"),
      "Build-Jdk" -> System.getProperty("java.version"),
      "Specification-Title" -> title,
      "Specification-Version" -> version,
      "Specification-Vendor" -> vendor,
      "Implementation-Title" -> title,
      "Implementation-Version" -> version,
      "Implementation-Vendor-Id" -> vendor,
      "Implementation-Vendor" -> vendor
    )
}

publishTo <<= version { version: String =>
  if (version.trim.endsWith("SNAPSHOT"))
    Some(Opts.resolver.sonatypeSnapshots)
  else
    Some(Opts.resolver.sonatypeStaging)
}

homepage := Some(new URL("https://github.com/json4s/json4s-ast"))

startYear := Some(2013)

licenses := Seq(("MIT", new URL("https://github.com/json4s/json4s-ast/raw/HEAD/LICENSE")))

scmInfo := Some(ScmInfo(url("http://github.com/json4s/json4s-ast"), "scm:git:git://github.com/json4s/json4s-ast.git", Some("scm:git:git@github.com:json4s/json4s-ast.git")))

pomExtra <<= (pomExtra, name, description) { (pom, name, desc) =>
  pom ++ Group(
     <developers>
        <developer>
          <id>casualjim</id>
          <name>Ivan Porto Carrero</name>
          <url>http://flanders.co.nz/</url>
        </developer>
      </developers>
  )
}
