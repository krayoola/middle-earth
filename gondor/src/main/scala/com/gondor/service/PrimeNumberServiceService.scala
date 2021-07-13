package com.gondor.service
import cats.ApplicativeError
import cats.effect.kernel.Sync
import com.gondor.model.ApplicationError
import com.gondor.repository.PrimeNumberRepository
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse}
import fs2.Stream

class PrimeNumberService[F[_]: Sync](grpcService: PrimeNumberRepository[F])(implicit F: ApplicativeError[F, Throwable]) extends PrimeNumberServiceAlgebra[F] {

  def getPrimeNumbers(maxNumberRange: Int): Stream[F, Either[ApplicationError, PrimeNumberResponse]] =
    for {
      response <- grpcService.requestForPrimeNumbers(PrimeNumberRequest(maxNumberRange))
    } yield response
}

object PrimeNumberService {
  def apply[F[_]: Sync](grpcService: PrimeNumberRepository[F]) = new PrimeNumberService(grpcService)
}

trait PrimeNumberServiceAlgebra[F[_]] {
  def getPrimeNumbers(maxNumberRange: Int):  Stream[F, Either[ApplicationError, PrimeNumberResponse]]
}