package com.mordor

import cats.effect.{Async, Resource}
import com.mordor.config.Settings
import com.service.prime.PrimeNumberServiceFs2Grpc
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder

object Server {

  private def buildApplication[F[_]: Async](
      config: String
  ): Resource[F, Server] = {
    for {
     config <- Settings.Config.load(config)
     service <- PrimeNumberServiceFs2Grpc.bindServiceResource[F](PrimeNumberServiceModule[F])
     server <- NettyServerBuilder
       .forPort(config.server.port)
       .addService(service)
       .resource[F](Async[F])
    } yield server
  }

  def buildServer[F[_]: Async](
      config: String = "application.conf"
  ): Resource[F, Server] =
    buildApplication(config)

}
