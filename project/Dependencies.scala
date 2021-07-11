import sbt._

object Dependencies {

  object Version {
    val CATS_EFFECT = "3.1.1"
    // val MUNIT = "1.0.5"
    val MONADIC_FOR = "0.3.1"
    val FS2 = "3.0.6"
    val HTTP4s = "0.23.0-RC1"
    val PureConfigVersion = "0.16.0"
    val logBackVersion: String = "1.2.3"

  }

  val pureConfig = Seq(
    "com.github.pureconfig" %% "pureconfig" % Version.PureConfigVersion,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % Version.PureConfigVersion
  )

  val logback =    Seq("ch.qos.logback"  %  "logback-classic" % Version.logBackVersion)

  val catsEffect = Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std"
  ).map(_ % Version.CATS_EFFECT)

  val fs2 = Seq("co.fs2" %% "fs2-core" % Version.FS2)

  // val munit = Seq(
  //   "org.typelevel" %% "munit-cats-effect-3" % Version.MUNIT % Test
  // )

  val grpc = Seq(
    "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion
  )

  val http4s = Seq(
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-dsl"
  ).map(_ % Version.HTTP4s)

  val scalaCompilers = Seq(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % Version.MONADIC_FOR)
  )

}
