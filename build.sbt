sbtPlugin := true

organization := "de.envisia.sbt"

name := "sbt-systemjs"

version := "0.0.1-M1"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.webjars.npm" % "typescript" % "1.8.0",
  "org.webjars.npm" % "minimatch" % "3.0.0",
  "org.webjars.npm" % "es6-symbol" % "3.0.1" exclude("org.webjars.npm", "es5-ext"),
  "org.webjars.npm" % "es5-ext" % "0.10.11" exclude("org.webjars.npm", "es6-symbol") exclude("org.webjars.npm", "es6-iterator"),
  "org.webjars.npm" % "es6-iterator" % "2.0.0" exclude("org.webjars.npm", "es5-ext"),
  "org.webjars.npm" % "systemjs-builder" % "0.15.7"
)

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Webjars relabel" at "https://dl.bintray.com/envisia/maven",
  "Webjars" at "http://dl.bintray.com/webjars/maven",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/",
  Resolver.mavenLocal
)

addSbtPlugin("com.typesafe.sbt" %% "sbt-web" % "1.1.1")

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.1.1")

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }
