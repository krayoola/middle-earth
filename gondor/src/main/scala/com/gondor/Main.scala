package com.gondor

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple {

  def run: IO[Unit] = Server
    .buildServer[IO]()
    .use(_ => IO.never)
    .attempt
    .map {
      case Left(_)  => ExitCode.Error // TODO: show error later
      case Right(_) => ExitCode.Success
    }

}
