val scalaVersion_2_13 = "2.13.1"

lazy val commonSettings = Seq(
  organization := "com.github.tototoshi",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := scalaVersion_2_13,
  scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
  scalacOptions += "-deprecation"
)

lazy val core = project
  .in(file("core"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    name := "core",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "org.scalatest" %%% "scalatest" % "3.1.2" % Test
    )
  )
  .dependsOn(electron)

lazy val worker = project
  .in(file("worker"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    name := "worker",
    scalaJSUseMainModuleInitializer := true
  )
  .dependsOn(core)

lazy val react = project
  .in(file("react"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    name := "react",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0"
    )
  )

lazy val front = project
  .in(file("front"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    name := "front",
    scalaJSUseMainModuleInitializer := true,
  )
  .dependsOn(core, react)

lazy val electron = project
  .in(file("electron"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    name := "electron"
  )

lazy val app = project
  .in(file("app"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    name := "app",
    scalaJSUseMainModuleInitializer := true
  )
  .dependsOn(electron)

lazy val root = project
  .in(file("."))
  .aggregate(core, electron, worker, front, app)
