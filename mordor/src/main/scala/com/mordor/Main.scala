package com.mordor

import cats.effect.IOApp
import cats.effect.IO
import io.grpc.ServerServiceDefinition
import com.service.prime.PrimeNumberServiceFs2Grpc
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all._
import cats.effect.kernel.Async
object Main extends IOApp.Simple {

  val primeNumberService =
    PrimeNumberServiceFs2Grpc.bindServiceResource[IO](
      new PrimeNumberServiceModule()
    )

  def run: IO[Unit] = {
    val service = primeNumberService.use { service =>
      NettyServerBuilder
        .forPort(9999)
        .addService(service)
        .resource[IO](Async[IO])
        .evalMap(server => IO(server.start()))
        .useForever
    }

    service >> IO.unit
  }
}
