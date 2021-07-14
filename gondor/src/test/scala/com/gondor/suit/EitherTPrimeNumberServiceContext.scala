package com.gondor.suit

import cats.data.EitherT
import cats.effect.{IO, Sync}
import com.gondor.model.{ApplicationError, GondorNumberResponse, PrimeNumberDomain}
import com.gondor.repository.PrimeNumberRepository
import com.gondor.service.PrimeNumberService
import com.service.prime.PrimeNumberRequest
import fs2.Stream

class EitherTPrimeNumberServiceContext[F[_]: Sync] {
  implicit class TestEitherTInterpreter(stream: EitherT[IO, ApplicationError, fs2.Stream[IO, PrimeNumberDomain]]) {
    def unwrapR: IO[ApplicationError] = stream.value.map {
      case Left(error) => error
    }

    def unwrapL: IO[List[PrimeNumberDomain]] = stream.value.flatMap {
      case Right(value) => value.compile.toList
    }
  }

  def createDomainStream(expectedContent : PrimeNumberDomain*): Stream[F, PrimeNumberDomain] = {
    expectedContent.foldLeft(Stream.empty.covaryAll[F,PrimeNumberDomain])( (x , y) => x ++ Stream(y).covary[F])
  }

  def eitherPrimeNumberService(expectedResponse: fs2.Stream[F, PrimeNumberDomain]): PrimeNumberService[F] = {
    PrimeNumberService[F](new MaybePrimeNumberRepository(expectedResponse))
  }

  class MaybePrimeNumberRepository(response: fs2.Stream[F, PrimeNumberDomain]) extends PrimeNumberRepository[F] {

    override def eitherGondorResponseOrApplicationError(primeNumberRequest: PrimeNumberRequest): Stream[F, Either[ApplicationError, GondorNumberResponse]] = ???

    override def maybeGondorResponseOrGeneralError(primeNumberRequest: PrimeNumberRequest): Stream[F, PrimeNumberDomain] = response
  }
}