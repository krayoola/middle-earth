package com.gondor

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple {

  def run: IO[Unit] = Server.buildServer[IO]()
    .use(_ => IO.never)
    .as(ExitCode.Success)

}
