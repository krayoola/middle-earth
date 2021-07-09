import sbt._

object Dependencies {

  object Version {
    val CATS_EFFECT = "3.1.1"
    // val MUNIT = "1.0.5"
    val MONADIC_FOR = "0.3.1"
    val FS2 = "3.0.6"
  }

  val catsEffect = Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % Version.CATS_EFFECT,
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % Version.CATS_EFFECT,
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % Version.CATS_EFFECT
  )

  val fs2 = Seq("co.fs2" %% "fs2-core" % Version.FS2)

  // val munit = Seq(
  //   "org.typelevel" %% "munit-cats-effect-3" % Version.MUNIT % Test
  // )

  val grpc = Seq(
    "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion
  )

  val scalaCompilers = Seq(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % Version.MONADIC_FOR)
  )

}
