import Dependencies._

ThisBuild / organization := "com.mordor"
ThisBuild / scalaVersion := "2.13.6"

lazy val root = project
  .in(file("."))
  .aggregate(protobuf, mordor)

lazy val protobuf =
  project
    .in(file("protobuf"))
    .enablePlugins(Fs2Grpc)

lazy val mordor =
  project
    .in(file("mordor"))
    .settings(
      libraryDependencies ++=
        catsEffect ++
          munit ++
          fs2 ++
          scalaCompilers
    )
    .dependsOn(protobuf)
