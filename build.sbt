// Rename this as you see fit
name := "geotrellis-sbt-template"

version := "0.2.0"

scalaVersion := "2.11.12"

organization := "com.azavea"

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Yinline-warnings",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:existentials")

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

resolvers ++= Seq(
  "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases",
  "locationtech-snapshots" at "https://repo.locationtech.org/content/groups/snapshots"
)
resolvers := Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Unidata Repository" at "https://artifacts.unidata.ucar.edu/content/repositories/unidata-releases",
  MavenRepository("geotools","http://download.osgeo.org/webdav/geotools"),
  "nscala-time" at "https://mvnrepository.com/artifact/org.locationtech.geotrellis/geotrellis-shapefile"
)


 
libraryDependencies ++= Seq(
  "org.locationtech.geotrellis" %% "geotrellis-spark" % "1.2.0-RC2",
  "org.apache.spark"      %% "spark-core"       % "2.2.0" % Provided,
  "org.scalatest"         %%  "scalatest"       % "2.2.0" % Test
)


libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.12.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.12.0"
libraryDependencies += "org.locationtech.geotrellis" % "geotrellis-spark-etl_2.11" % "2.1.0"

libraryDependencies += "com.vividsolutions" % "jts" % "1.13"
libraryDependencies += "org.geotools" % "gt-main" % "14.1"
libraryDependencies += "org.geotools" % "gt-shapefile" % "10.4"


libraryDependencies += "org.locationtech.geotrellis" %% "geotrellis-shapefile" % "2.3.1"
//akka dependence
val akkaActorVersion = "2.4.17"
val akkaHttpVersion  = "10.0.3"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaActorVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
)


// https://mvnrepository.com/artifact/org.geotools/gt-shapefile

// https://mvnrepository.com/artifact/org.locationtech.geotrellis/geotrellis-shapefile
//libraryDependencies += "org.locationtech.geotrellis" %% "geotrellis-shapefile" % "2.3.1"



// When creating fat jar, remote some files with
// bad signatures and resolve conflicts by taking the first
// versions of shared packaged types.
assemblyMergeStrategy in assembly := {
  case "reference.conf" => MergeStrategy.concat
  case "application.conf" => MergeStrategy.concat
  case "META-INF/MANIFEST.MF" => MergeStrategy.discard
  case "META-INF\\MANIFEST.MF" => MergeStrategy.discard
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.discard
  case "META-INF/ECLIPSEF.SF" => MergeStrategy.discard
  case _ => MergeStrategy.first
}

initialCommands in console := """
 |import geotrellis.raster._
 |import geotrellis.vector._
 |import geotrellis.proj4._
 |import geotrellis.spark._
 |import geotrellis.spark.io._
 |import geotrellis.spark.io.hadoop._
 |import geotrellis.spark.tiling._
 |import geotrellis.spark.util._
 |import geotrellis.spark-etl._
 |import geotrellis.shapefile._
 """.stripMargin
