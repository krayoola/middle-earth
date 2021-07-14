package com.gondor.repository

import cats.effect.IO
import com.gondor.model.{ApplicationError, GeneralError, GondorNumberResponse}
import com.gondor.suit.{BoomError, FailMockGrpcService, InterruptibleMockGrpcService, MockGrpcService}
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse}
import fs2.Stream
import munit.CatsEffectSuite

class PrimeNumberGrpcRepositoryTest extends CatsEffectSuite {

  // Either
  test("should receive Either.Right and accept correct prime positive primeNumbers") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).eitherGondorResponseOrApplicationError(PrimeNumberRequest(5))
    result.compile.toList.map(_.size) assertEquals 3
    result.compile.toList assertEquals List(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)), Right(GondorNumberResponse(5)))
  }

  test("should not provide prime numbers on zero numeric input value") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).eitherGondorResponseOrApplicationError(PrimeNumberRequest(0))
    result.compile.toList.map(_.size) assertEquals 0
    result.compile.toList assertEquals List()
  }

  test("should not provide prime numbers on negative values") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).eitherGondorResponseOrApplicationError(PrimeNumberRequest(-5))
    result.compile.toList.map(_.size) assertEquals 0
    result.compile.toList assertEquals List()
  }

  test("should receive Either.Left when error occur") {
    val result = PrimeNumberGrpcRepository[IO](new FailMockGrpcService(BoomError)).eitherGondorResponseOrApplicationError(PrimeNumberRequest(-5))
    result.compile.toList assertEquals List(Left(GeneralError("There seems to be a problem due to : boom!")))
  }

  //PrimeNumberDomainRequest

  test("should provide GondorNumberResponses and accept correct prime positive input value") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).maybeGondorResponseOrGeneralError(PrimeNumberRequest(5))
    result.compile.toList assertEquals List(GondorNumberResponse(2), GondorNumberResponse(3),GondorNumberResponse(5))
  }

  test("should not provide GondorNumberResponses with prime numbers on a negative input value") {
    val result = PrimeNumberGrpcRepository[IO](new MockGrpcService).maybeGondorResponseOrGeneralError(PrimeNumberRequest(-1))
    result.compile.toList.map(_.size) assertEquals 0
    result.compile.toList assertEquals List()
  }

  test("should provide GondorNumberResponses & GeneralError when the stream encountered a problem") {
    val headStream = Stream(2,3).map( v => PrimeNumberResponse(v)).covary[IO]
    val result = PrimeNumberGrpcRepository[IO](new InterruptibleMockGrpcService(headStream, BoomError, None)).maybeGondorResponseOrGeneralError(PrimeNumberRequest(5))
    result.compile.toList.map(_.size) assertEquals 3
    result.compile.toList assertEquals List(GondorNumberResponse(2), GondorNumberResponse(3), GeneralError("There seems to be a problem due to : boom!"))
  }

  test("should provide any GondorNumberResponse after encountering an error") {
    val headStream = Stream(2,3).map( v => PrimeNumberResponse(v)).covary[IO]
    val tailStream = Stream(5,7).map( v => PrimeNumberResponse(v)).covary[IO]
    val result = PrimeNumberGrpcRepository[IO](new InterruptibleMockGrpcService(headStream, BoomError, Option(tailStream))).maybeGondorResponseOrGeneralError(PrimeNumberRequest(5))
    result.compile.toList.map(_.size) assertEquals 3
    result.compile.toList assertEquals List(GondorNumberResponse(2), GondorNumberResponse(3), GeneralError("There seems to be a problem due to : boom!"))
  }
}


