import sbt._

object Dependencies {
  /** Core dependencies */


  /** Platform dependencies */
  val Json4SsVersion = "3.6.7"

  /** Test dependencies */
  val ScalaTestVersion = "3.0.8"
  val ScalaMockVersion = "4.4.0"

  val coreDependencies = Seq()

  val platformDependencies = Seq(
    "org.json4s" %% "json4s-jackson" % Json4SsVersion
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % ScalaTestVersion % "it,test",
    "org.scalamock" %% "scalamock" % ScalaMockVersion % "test"
  )
}