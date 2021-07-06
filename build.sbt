import Dependencies._

ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.6"

lazy val root = (project in file(".")).settings(
  name := "mordor",
  libraryDependencies ++=
    catsEffect ++
      munit ++
      fs2 ++
      scalaCompilers
)
