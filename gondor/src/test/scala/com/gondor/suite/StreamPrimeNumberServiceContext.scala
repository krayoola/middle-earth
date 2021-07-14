package com.gondor.suite

import cats.effect.{IO, Sync}
import com.gondor.model.{ApplicationError, GondorNumberResponse, PrimeNumberDomain}
import com.gondor.repository.PrimeNumberRepository
import com.gondor.service.PrimeNumberService
import com.service.prime.PrimeNumberRequest
import fs2.Stream

class StreamPrimeNumberServiceContext[F[_]: Sync] {

  implicit class TestStreamInterpreter[A <: ApplicationError, B <: GondorNumberResponse](stream: Stream[IO, Either[A, B]]) {
    def unwrap: IO[List[Either[A, B]]] = stream.compile.toList
  }

  def createEitherStream(expectedContent : Either[ApplicationError, GondorNumberResponse]*): Stream[F, Either[ApplicationError, GondorNumberResponse]] = {
    expectedContent.foldLeft(Stream.empty.covaryAll[F,Either[ApplicationError, GondorNumberResponse]])( (x , y) => x ++ Stream(y).covary[F])
  }

  def getPrimeNumbers(expectedResponse: fs2.Stream[F, Either[ApplicationError, GondorNumberResponse]]): Stream[F, Either[ApplicationError, GondorNumberResponse]] = {
    // arbitrary request (maxNumberRange) since there is no business logic (yet) on this function
    // as the application grows we externalize the request
    new PrimeNumberService[F](new EitherPrimeNumberRepository(expectedResponse)).getPrimeNumbers(5)
  }

  class EitherPrimeNumberRepository(expectedResponse: fs2.Stream[F, Either[ApplicationError, GondorNumberResponse]]) extends PrimeNumberRepository[F] {
    override def eitherGondorResponseOrApplicationError(primeNumberRequest: PrimeNumberRequest): fs2.Stream[F, Either[ApplicationError, GondorNumberResponse]] = expectedResponse

    override def maybeGondorResponseOrGeneralError(primeNumberRequest: PrimeNumberRequest): fs2.Stream[F, PrimeNumberDomain] = ???
  }
}
