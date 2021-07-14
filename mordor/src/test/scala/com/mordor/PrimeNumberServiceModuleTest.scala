package com.mordor

import cats.effect.{IO, Sync}
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse}
import io.grpc.Metadata
import munit.CatsEffectSuite

class PrimeNumberServiceModuleTest extends CatsEffectSuite {

  test("should generate proper prime numbers when input value is not a prime number") {
    val result = primeNumberService[IO](4)
    result.unwrap.map(_.size) assertEquals 2

    result.unwrap assertEquals List(
      2,
      3
    ).map(PrimeNumberResponse(_))
  }

  test("should generate proper prime numbers") {
    val result = primeNumberService[IO](5)
    result.unwrap.map(_.size) assertEquals 3

    result.unwrap assertEquals List(
      2,
      3,
      5
    ).map(PrimeNumberResponse(_))
  }

  test("should not generate value when input value is 0") {
    val result = primeNumberService[IO](0)
    result.unwrap assertEquals List.empty
  }

  test("should not generate value when input value negative integer value") {
    val result = primeNumberService[IO](-1)
    result.unwrap assertEquals List.empty
  }

  def primeNumberService[F[_]: Sync](
      number: Int
  ): fs2.Stream[F, PrimeNumberResponse] =
    PrimeNumberServiceModule[F].generatePrimeNumber(
      PrimeNumberRequest(number),
      new Metadata()
    )

  implicit class TestStreamUnwrapper(
      stream: fs2.Stream[IO, PrimeNumberResponse]
  ) {
    def unwrap: IO[List[PrimeNumberResponse]] = stream.compile.toList
  }
}
