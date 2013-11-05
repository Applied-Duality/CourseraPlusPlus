
import sbt._
import Keys._
import java.io.File



object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq (
    name := "rp-exercise-4",
    version := "0.1",
    scalaVersion := "2.10.2",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "1.9.1" % "test"
      , "com.netflix.rxjava" % "rxjava-scala" % "0.14.6"
      , "org.json4s" % "json4s-native_2.10" % "3.2.5"
      , "org.scala-lang" % "scala-swing" % "2.10.3"
      , "net.databinder.dispatch" % "dispatch-core_2.10" % "0.11.0"
      , "org.scala-lang" % "scala-reflect" % "2.10.3"
    ),
    logBuffered := false
  )
}


object ExerciseBuild extends Build {
  
  lazy val root = Project(
    "root",
    file("."),
    settings = BuildSettings.buildSettings ++ Seq()
  ) dependsOn ()

}










