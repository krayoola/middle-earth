package com.gondor.suite

import cats.effect.IO
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse, PrimeNumberServiceFs2Grpc}
import fs2.Stream
import io.grpc.Metadata

class MockGrpcService extends PrimeNumberServiceFs2Grpc[IO, Metadata] {
  def isPrime(n: Int): Boolean = {
    if(n <= 1) false
    else Range(2, n - 1).filter(n % _ == 0).length == 0
  }

  override def generatePrimeNumber(request: PrimeNumberRequest, ctx: Metadata): fs2.Stream[IO, PrimeNumberResponse] = {
    Stream
      .iterate(1)(_ + 1)
      .filter(isPrime)
      .takeWhile(_ <= request.maxNumberRange)
      .map(PrimeNumberResponse(_))
      .covary[IO]
  }
}

final case object BoomError extends Throwable("boom!")

class FailMockGrpcService(error: Throwable) extends PrimeNumberServiceFs2Grpc[IO, Metadata] {
  override def generatePrimeNumber(request: PrimeNumberRequest, ctx: Metadata): fs2.Stream[IO, PrimeNumberResponse] = {
    Stream.empty.append(Stream.raiseError[IO](error))
  }
}

class InterruptibleMockGrpcService(headStream: Stream[IO, PrimeNumberResponse], error: Throwable, tailStream: Option[Stream[IO, PrimeNumberResponse]]) extends PrimeNumberServiceFs2Grpc[IO, Metadata] {
  override def generatePrimeNumber(request: PrimeNumberRequest, ctx: Metadata): fs2.Stream[IO, PrimeNumberResponse] = {
    headStream ++ Stream.raiseError[IO](error) ++ tailStream.fold(Stream.apply[IO, PrimeNumberResponse]())(v => v.covary[IO])
  }
}

