import Dependencies._

ThisBuild / scalaVersion := "2.13.6"

lazy val root = project
  .in(file("."))
  .aggregate(protobuf, mordor, gondor)

lazy val protobuf =
  project
    .in(file("protobuf"))
    .enablePlugins(Fs2Grpc)

lazy val mordor =
  project
    .in(file("mordor"))
    .enablePlugins(JavaAppPackaging)
    .enablePlugins(DockerPlugin)
    .settings(
      organization := "com.mordor",
      name := "mordor",
      version := "0.0.1-SNAPSHOT",
      libraryDependencies ++=
        pureConfig ++
          grpc ++
          fs2 ++
          scalaCompilers
    )
    .dependsOn(protobuf)

lazy val gondor =
  project
    .in(file("gondor"))
    .enablePlugins(JavaAppPackaging)
    .enablePlugins(DockerPlugin)
    .settings(
      organization := "com.gondor",
      name := "gondor",
      version := "0.0.1-SNAPSHOT",
      libraryDependencies ++=
        pureConfig ++
        logback ++
        http4s ++
          grpc ++
          fs2 ++
          munit ++
          scalaCompilers
    )
    .dependsOn(protobuf)

addCommandAlias("mordor", ";clean ;compile ;project mordor ;run")
addCommandAlias("gondor", ";clean ;compile ;project gondor ;run")
addCommandAlias("dockerGondor", ";clean ;compile ;project gondor ;docker:publishLocal")
addCommandAlias("dockerMordor", ";clean ;compile ;project gondor ;docker:publishLocal")
