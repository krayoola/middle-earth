package com.gondor.service

import cats.data.EitherT
import cats.effect.{IO, Sync}
import com.gondor.model.{ApplicationError, GeneralError, GondorNumberResponse, Invalid, PrimeNumberDomain}
import com.gondor.repository.PrimeNumberRepository
import com.service.prime.PrimeNumberRequest
import munit.CatsEffectSuite
import fs2.Stream

class PrimeNumberServiceTest extends CatsEffectSuite {

  // either
  test("should provide positive Either.Right GondorResponses") {
    new DomainContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
      val result = getPrimeNumbers(expectedStreamOfGondorResponses)
      result.unwrap.map(_.size) assertEquals 3
      result.unwrap assertEquals List(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
    }
  }

  test("should provide positive Either.Left GeneralError") {
    new DomainContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Left(GeneralError("error")))
      val result = getPrimeNumbers(expectedStreamOfGondorResponses)
      result.unwrap.map(_.size) assertEquals 1
      result.unwrap assertEquals List(Left(GeneralError("error")))
    }
  }

  // Domain Response
  test("should provide positive PrimeNumberDomain GondorResponses") {
    new EitherContext[IO] {
      val streamOfGondorResponses = createDomainStream(GondorNumberResponse(2), GondorNumberResponse(3))
      val result = eitherPrimeNumberService(streamOfGondorResponses).getPrimeNumbersViaEitherT(5)
      result.unwrapL.map(_.size) assertEquals 3
      result.unwrapL assertEquals List(GondorNumberResponse(2), GondorNumberResponse(3))
    }
  }

  test("should provide Invalid Error when provided by negative value") {
    new EitherContext[IO] {
      val streamOfGondorResponses = createDomainStream(GondorNumberResponse(2), GondorNumberResponse(3))
      val result = eitherPrimeNumberService(streamOfGondorResponses).getPrimeNumbersViaEitherT(-1)
      result.unwrapR assertEquals Invalid("Invalid input, Input should not be less than 0 integer value")
    }
  }

  test("should provide Invalid Error when provided by zero (0) value") {
    new EitherContext[IO] {
      val streamOfGondorResponses = createDomainStream(GondorNumberResponse(2), GondorNumberResponse(3))
      val result = eitherPrimeNumberService(streamOfGondorResponses).getPrimeNumbersViaEitherT(0)
      result.unwrapR assertEquals Invalid("Invalid input, Input should not be less than 0 integer value")
    }
  }

  // --------------------------------------------------------------------------------------------------------------
  // helpers
  class DomainContext[F[_]: Sync] {

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


  class EitherContext[F[_]: Sync] {
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
}


