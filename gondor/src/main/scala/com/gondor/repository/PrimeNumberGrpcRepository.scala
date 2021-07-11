package com.gondor.repository

import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse, PrimeNumberServiceFs2Grpc}
import io.grpc.Metadata

class PrimeNumberGrpcRepository[F[_]](grpcService: PrimeNumberServiceFs2Grpc[F, Metadata]) extends PrimeNumberRepository[F] {
  def requestForPrimeNumbers(primeNumberRequest: PrimeNumberRequest): fs2.Stream[F, PrimeNumberResponse] =
    grpcService.generatePrimeNumber(primeNumberRequest, new Metadata())
}

object PrimeNumberGrpcRepository {
  def apply[F[_]](grpcService: PrimeNumberServiceFs2Grpc[F, Metadata]) = new PrimeNumberGrpcRepository(grpcService)
}

trait PrimeNumberRepository[F[_]] {
  def requestForPrimeNumbers(primeNumberRequest: PrimeNumberRequest): fs2.Stream[F, PrimeNumberResponse]
}