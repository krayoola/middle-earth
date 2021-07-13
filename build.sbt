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
          grpc ++
          fs2 ++
          scalaCompilers
    )
    .dependsOn(protobuf)

lazy val gondor =
  project
    .in(file("gondor"))
    .settings(
      libraryDependencies ++=
        pureConfig ++
        logback ++
        http4s ++
          grpc ++
          fs2 ++
          scalaCompilers
    )
    .dependsOn(protobuf)

addCommandAlias("mordor", ";clean ;compile ;project mordor ;run")
addCommandAlias("gondor", ";clean ;compile ;project gondor ;run")
