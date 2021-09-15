enablePlugins(ScalaJSPlugin)
Global / onChangedBuildSource := IgnoreSourceChanges

name := "demo"

version := "0.1"

scalaVersion := "3.0.1"

libraryDependencies ++= Seq(
  ("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13),
  "dev.atedeg" %%% "ecscala" % "0.0.0+254-9ac3dde3+20210915-1529-SNAPSHOT",
  ("com.lihaoyi" %%% "scalatags" % "0.9.4").cross(CrossVersion.for3Use2_13)
)

scalaJSUseMainModuleInitializer := true
