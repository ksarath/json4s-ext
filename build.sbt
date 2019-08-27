import Dependencies._

lazy val commonSettings = Seq(
  name         := "json4s-ext",
  version      := "0.1.0-SNAPSHOT",
  scalaVersion := "2.13.0"
)

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  //"-Ypartial-unification",
  //"-Ywarn-unused-import"
)

lazy val `json4s-ext-build` = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    dependencyOverrides ++= coreDependencies,
    libraryDependencies ++= platformDependencies ++ testDependencies
  )
