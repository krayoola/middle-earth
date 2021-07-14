package com.gondor.service

import cats.effect.IO
import com.gondor.model.{GeneralError, GondorNumberResponse, Invalid}
import com.gondor.suite.{EitherTPrimeNumberServiceContext, StreamPrimeNumberServiceContext}
import munit.CatsEffectSuite

class PrimeNumberServiceTest extends CatsEffectSuite {

  // either
  test("should provide positive Either.Right GondorResponses") {
    new StreamPrimeNumberServiceContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
      val result = getPrimeNumbers(expectedStreamOfGondorResponses)
      result.unwrap.map(_.size) assertEquals 3
      result.unwrap assertEquals List(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
    }
  }

  test("should provide positive Either.Left GeneralError") {
    new StreamPrimeNumberServiceContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Left(GeneralError("error")))
      val result = getPrimeNumbers(expectedStreamOfGondorResponses)
      result.unwrap.map(_.size) assertEquals 1
      result.unwrap assertEquals List(Left(GeneralError("error")))
    }
  }

  // Domain Response
  test("should provide positive PrimeNumberDomain GondorResponses") {
    new EitherTPrimeNumberServiceContext[IO] {
      val streamOfGondorResponses = createDomainStream(GondorNumberResponse(2), GondorNumberResponse(3))
      val result = eitherPrimeNumberService(streamOfGondorResponses).getPrimeNumbersViaEitherT(5)
      result.unwrapL.map(_.size) assertEquals 3
      result.unwrapL assertEquals List(GondorNumberResponse(2), GondorNumberResponse(3))
    }
  }

  test("should provide Invalid Error when provided by negative value") {
    new EitherTPrimeNumberServiceContext[IO] {
      val streamOfGondorResponses = createDomainStream(GondorNumberResponse(2), GondorNumberResponse(3))
      val result = eitherPrimeNumberService(streamOfGondorResponses).getPrimeNumbersViaEitherT(-1)
      result.unwrapR assertEquals Invalid("Invalid input, Input should not be less than 0 integer value")
    }
  }

  test("should provide Invalid Error when provided by zero (0) value") {
    new EitherTPrimeNumberServiceContext[IO] {
      val streamOfGondorResponses = createDomainStream(GondorNumberResponse(2), GondorNumberResponse(3))
      val result = eitherPrimeNumberService(streamOfGondorResponses).getPrimeNumbersViaEitherT(0)
      result.unwrapR assertEquals Invalid("Invalid input, Input should not be less than 0 integer value")
    }
  }

}


