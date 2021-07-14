package com.gondor.service
import cats.ApplicativeError
import cats.data.EitherT
import cats.effect.kernel.Sync
import com.gondor.model._
import com.gondor.repository.PrimeNumberRepository
import com.service.prime.PrimeNumberRequest
import fs2.Stream

class PrimeNumberService[F[_]: Sync](grpcService: PrimeNumberRepository[F])(implicit F: ApplicativeError[F, Throwable]) extends PrimeNumberServiceAlgebra[F] {

  def getPrimeNumbers(maxNumberRange: Int): Stream[F, Either[ApplicationError, GondorNumberResponse]] =
    for {
      response <- grpcService.eitherGondorResponseOrApplicationError(PrimeNumberRequest(maxNumberRange))
    } yield response

  def getPrimeNumbersV2(maxNumberRange: Int): EitherT[F, ApplicationError, fs2.Stream[F, PrimeNumberDomainRequest]] =
    for {
      result <- validateClientRequest(maxNumberRange)
      // TODO: Transform properly, right now it works since error is hidden within the Stream that we captured on next layer.
      response <- EitherT.rightT(grpcService.maybeGondorResponseOrGeneralError(PrimeNumberRequest(result.number)))
    } yield response


  def validateClientRequest(value: Int): EitherT[F, InvalidRequest, ValidatedRequest] = {
    EitherT.cond(value > 0, ValidatedRequest(value), InvalidRequest("Invalid input, Input should not be less than 0 integer value"))
  }
}

object PrimeNumberService {
  def apply[F[_]: Sync](grpcService: PrimeNumberRepository[F]) = new PrimeNumberService(grpcService)
}

trait PrimeNumberServiceAlgebra[F[_]] {
  def getPrimeNumbers(maxNumberRange: Int): Stream[F, Either[ApplicationError, GondorNumberResponse]]
  def getPrimeNumbersV2(maxNumberRange: Int): EitherT[F, ApplicationError, fs2.Stream[F, PrimeNumberDomainRequest]]
}