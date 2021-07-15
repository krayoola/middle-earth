package com.mordor

import cats.ApplicativeError
import cats.effect.kernel.Async
import com.mordor.MordorServerStatus.INVALID_INPUT
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse, PrimeNumberServiceFs2Grpc}
import fs2.Stream
import io.grpc.Metadata

// grpcurl -d '{"maxNumberRange":"17"}' -plaintext \
// -import-path protobuf/src/main/protobuf -proto prime.proto \
// 127.0.0.1:9999 com.service.PrimeNumberService/GeneratePrimeNumber

class PrimeNumberServiceModule[F[_]: Async](implicit
    F: ApplicativeError[F, Throwable]
) extends PrimeNumberServiceFs2Grpc[F, Metadata] {

  private def isPrime(n: Int): Boolean = {
    if (n <= 1) false
    else Range(2, n - 1).filter(n % _ == 0).length == 0
  }

  private def operateStream(
      inputNumber: Int
  ): Stream[F, PrimeNumberResponse] = {
    if (inputNumber > 0)
      Stream
        .iterate(1)(_ + 1)
        .filter(isPrime)
        .takeWhile(_ <= inputNumber)
        .debug(v => s"value generated: $v")
        .map(PrimeNumberResponse(_))
    else
      Stream.raiseError[F](
        INVALID_INPUT.asRuntimeException()
      )
  }

  override def generatePrimeNumber(
      request: PrimeNumberRequest,
      ctx: Metadata
  ): Stream[F, PrimeNumberResponse] =
    operateStream(request.maxNumberRange)
}

object PrimeNumberServiceModule {
  def apply[F[_]: Async] = new PrimeNumberServiceModule[F]
}
