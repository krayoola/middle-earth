package com.gondor.service

import com.gondor.repository.PrimeNumberRepository
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse}

class PrimeNumberService[F[_]](grpcService: PrimeNumberRepository[F]) extends PrimeNumberServiceAlgebra[F] {
  def getPrimeNumbers(maxNumberRange: Int): fs2.Stream[F, PrimeNumberResponse] = {
    grpcService.requestForPrimeNumbers(PrimeNumberRequest(maxNumberRange))
    // add validation later here
  }
}

object PrimeNumberService {
  def apply[F[_]](grpcService: PrimeNumberRepository[F]) = new PrimeNumberService(grpcService)
}

trait PrimeNumberServiceAlgebra[F[_]] {
  def getPrimeNumbers(maxNumberRange: Int): fs2.Stream[F, PrimeNumberResponse]
}