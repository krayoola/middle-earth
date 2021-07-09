package com.mordor

import cats.effect.IO
import fs2.Stream
import com.service.prime.PrimeNumberServiceFs2Grpc
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse}
import io.grpc.stub.StreamObserver
import scala.concurrent.ExecutionContext.Implicits.global
import io.grpc.Metadata

class PrimeNumberServiceModule extends PrimeNumberServiceFs2Grpc[IO, Metadata] {

  def isPrime(n: Int): Boolean = Range(2, n - 1).filter(n % _ == 0).length == 0

  override def generatePrimeNumber(
      request: PrimeNumberRequest,
      ctx: Metadata
  ): Stream[IO, PrimeNumberResponse] =
    Stream
      .iterate(1)(_ + 1)
      .filter(isPrime)
      .takeWhile(_ <= request.maxNumberRange)
      .map(v => PrimeNumberResponse(v))

}


// grpcurl -d '{"maxNumberRange":"17"}' -plaintext \
// -import-path protobuf/src/main/protobuf -proto prime.proto \
// localhost:9999 com.service.PrimeNumberService/GeneratePrimeNumber
