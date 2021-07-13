package com.gondor.repository

import cats.effect.IO
import com.gondor.model.{ApplicationError, GeneralError, GondorNumberResponse}
import com.gondor.suit.{BoomError, FailMockGrpcService, MockGrpcService}
import com.service.prime.PrimeNumberRequest
import fs2.Stream
import munit.CatsEffectSuite

class PrimeNumberGrpcRepositoryTest extends CatsEffectSuite {

  test("should receive Right and accept correct prime positive primeNumbers") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).requestForPrimeNumbers(PrimeNumberRequest(5))
    result.compile.toList.map(_.size) assertEquals 3
    result.compile.toList assertEquals List(Right(GondorNumberResponse(2)),Right(GondorNumberResponse(3)),Right(GondorNumberResponse(5)))
  }

  test("should not provide prime numbers on zero numeric input value") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).requestForPrimeNumbers(PrimeNumberRequest(0))
    result.compile.toList.map(_.size) assertEquals 0
    result.compile.toList assertEquals List()
  }

  test("should not provide prime numbers on negative values") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).requestForPrimeNumbers(PrimeNumberRequest(-5))
    result.compile.toList.map(_.size) assertEquals 0
    result.compile.toList assertEquals List()
  }

  test("should receive Left when error occur") {
    val result = PrimeNumberGrpcRepository[IO](new FailMockGrpcService(BoomError)).requestForPrimeNumbers(PrimeNumberRequest(-5))
    result.compile.toList assertEquals List(Left(GeneralError("There seems to be a problem due to : boom!")))
  }

}


