package com.gondor.suit

import cats.Applicative
import cats.data.EitherT
import cats.effect.{Concurrent, Sync}
import com.gondor.endpoint.PrimeNumberEndpoint
import com.gondor.model.{ApplicationError, GondorNumberResponse, PrimeNumberDomain}
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes

class EndpointEitherTContext[F[_] : Sync: Concurrent: Applicative] extends EitherTPrimeNumberServiceContext[F] {

  def createEndpoint(expectedResponse: fs2.Stream[F, PrimeNumberDomain]): HttpRoutes[F] = {
    PrimeNumberEndpoint[F](new EitherTPrimeNumberService(expectedResponse)).endpoint
  }

  class EitherTPrimeNumberService(expectedResponse: fs2.Stream[F, PrimeNumberDomain]) extends PrimeNumberServiceAlgebra[F] {
    override def getPrimeNumbers(maxNumberRange: Int): fs2.Stream[F, Either[ApplicationError, GondorNumberResponse]] = ???
    override def getPrimeNumbersViaEitherT(maxNumberRange: Int): EitherT[F, ApplicationError, fs2.Stream[F, PrimeNumberDomain]] = ??? //EitherT.rightT(expectedResponse)
  }
}