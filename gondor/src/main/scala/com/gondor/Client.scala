package com.gondor

import cats.effect.Async
import cats.effect.kernel.Resource
import com.gondor.config.Settings
import com.gondor.endpoint.PrimeNumberEndpoint
import com.gondor.repository.PrimeNumberGrpcRepository
import com.gondor.service.PrimeNumberService
import com.service.prime.PrimeNumberServiceFs2Grpc
import fs2.grpc.syntax.all.fs2GrpcSyntaxManagedChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.middleware.Logger
import scala.concurrent.ExecutionContext.global

object Client {

  private def buildApplication[F[_]: Async](
      config: String
  ): Resource[F, Server] =
    for {
      config <- Settings.Config.load[F](config)
      grpcChannel <- NettyChannelBuilder
        .forAddress(config.server.host, config.server.port)
        .usePlaintext() // use plainText for now..
        .resource[F]
      grpcService <- PrimeNumberServiceFs2Grpc.stubResource[F](grpcChannel)
      pnGrpcRepo = PrimeNumberGrpcRepository[F](grpcService)
      pnService = PrimeNumberService[F](pnGrpcRepo)
      primeNumberEndpoint = PrimeNumberEndpoint[F](pnService)
      finalHttpApp = Logger.httpApp(true, true)(
        primeNumberEndpoint.endpoint.orNotFound
      )
      httpServer <- BlazeServerBuilder[F](global)
        .bindHttp(config.client.port, config.client.host)
        .withHttpApp(finalHttpApp)
        .resource
    } yield httpServer

  def buildServer[F[_]: Async](
      config: String = "application.conf"
  ): Resource[F, Server] =
    buildApplication(config)
}
