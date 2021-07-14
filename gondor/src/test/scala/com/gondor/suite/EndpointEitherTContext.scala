package com.gondor.suite

import cats.Applicative
import cats.data.EitherT
import cats.effect.Concurrent
import com.gondor.endpoint.PrimeNumberEndpoint
import com.gondor.model.{ApplicationError, GondorNumberResponse, PrimeNumberDomain}
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes

class EndpointEitherTContext[F[_] : Concurrent: Applicative] {

  def createEndpoint(expectedResponse: fs2.Stream[F, PrimeNumberDomain]): HttpRoutes[F] = {
    PrimeNumberEndpoint[F](new EitherTPrimeNumberService(expectedResponse)).endpoint
  }

  class EitherTPrimeNumberService(expectedResponse: fs2.Stream[F, PrimeNumberDomain]) extends PrimeNumberServiceAlgebra[F] {
    override def getPrimeNumbers(maxNumberRange: Int): fs2.Stream[F, Either[ApplicationError, GondorNumberResponse]] = ???
    override def getPrimeNumbersViaEitherT(maxNumberRange: Int): EitherT[F, ApplicationError, fs2.Stream[F, PrimeNumberDomain]] = {
      EitherT.rightT {
        expectedResponse
      }
    }
  }
}