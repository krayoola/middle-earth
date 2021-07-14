package com.gondor.suite

import cats.data.EitherT
import cats.effect.{Concurrent, Sync}
import com.gondor.endpoint.PrimeNumberEndpoint
import com.gondor.model.{ApplicationError, GondorNumberResponse, PrimeNumberDomain}
import com.gondor.service.PrimeNumberServiceAlgebra
import fs2.Stream
import org.http4s.HttpRoutes

class EndpointStreamContext[F[_]: Concurrent: Sync] extends StreamPrimeNumberServiceContext[F] {

  def createEndpoint(expectedResponse : Stream[F, Either[ApplicationError, GondorNumberResponse]]): HttpRoutes[F] = {
    PrimeNumberEndpoint[F](new StreamPrimeNumberService(expectedResponse)).endpoint
  }

  class StreamPrimeNumberService(expectedResponse : Stream[F, Either[ApplicationError, GondorNumberResponse]]) extends PrimeNumberServiceAlgebra[F] {
    override def getPrimeNumbers(maxNumberRange: Int): fs2.Stream[F, Either[ApplicationError, GondorNumberResponse]] = expectedResponse

    override def getPrimeNumbersViaEitherT(maxNumberRange: Int): EitherT[F, ApplicationError, fs2.Stream[F, PrimeNumberDomain]] = ???
  }

}