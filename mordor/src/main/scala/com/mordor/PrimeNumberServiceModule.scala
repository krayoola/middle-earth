package com.mordor

import cats.effect.Sync
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse, PrimeNumberServiceFs2Grpc}
import fs2.Stream
import io.grpc.Metadata


// grpcurl -d '{"maxNumberRange":"17"}' -plaintext \
// -import-path protobuf/src/main/protobuf -proto prime.proto \
// localhost:9999 com.service.PrimeNumberService/GeneratePrimeNumber

class PrimeNumberServiceModule[F[_]] extends PrimeNumberServiceFs2Grpc[F, Metadata] {

   private def isPrime(n: Int): Boolean = {
    if(n <= 1) false
    else Range(2, n - 1).filter(n % _ == 0).length == 0
  }

  override def generatePrimeNumber(request: PrimeNumberRequest, ctx: Metadata): Stream[F, PrimeNumberResponse] =
    Stream
    .iterate(1)(_ + 1)
    .filter(isPrime)
    .takeWhile(_ <= request.maxNumberRange)
    .debug(v => s"value generated: $v")
    .map(PrimeNumberResponse(_))
}

object PrimeNumberServiceModule {
  def apply[F[_]: Sync] = new PrimeNumberServiceModule[F]
}
