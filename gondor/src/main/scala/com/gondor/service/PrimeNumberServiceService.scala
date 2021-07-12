package com.gondor.service
import cats.Applicative
import cats.data.EitherT
import cats.effect.kernel.Sync
import com.gondor.repository.PrimeNumberRepository
import com.gondor.service.Validator.PrimeNumberDomainRequest
import com.service.prime.{PrimeNumberRequest, PrimeNumberResponse}

class PrimeNumberService[F[_]: Sync](grpcService: PrimeNumberRepository[F]) extends PrimeNumberServiceAlgebra[F] {
  def getPrimeNumbers(maxNumberRange: Int): EitherT[F, PrimeNumberDomainRequest, fs2.Stream[F, PrimeNumberResponse]] =
    for {
      result <- Validator.validateClientRequest[F](maxNumberRange)
      response <- EitherT.rightT(grpcService.requestForPrimeNumbers(PrimeNumberRequest(result.number)))
    } yield response
}

object PrimeNumberService {
  def apply[F[_]: Sync](grpcService: PrimeNumberRepository[F]) = new PrimeNumberService(grpcService)
}

trait PrimeNumberServiceAlgebra[F[_]] {
  def getPrimeNumbers(maxNumberRange: Int):  EitherT[F, PrimeNumberDomainRequest, fs2.Stream[F, PrimeNumberResponse]]
}

// TODO move this validator
object Validator {
  def validateClientRequest[F[_]: Applicative](value: Int): EitherT[F, InvalidRequest, ValidatedRequest] = {
    EitherT.cond(value > 0, ValidatedRequest(value), InvalidRequest("Invalid input, Input should not be less than 0 integer value"))
  }

  sealed trait PrimeNumberDomainRequest
  case class ValidatedRequest(number: Int) extends PrimeNumberDomainRequest
  case class InvalidRequest(message: String) extends PrimeNumberDomainRequest
}