package com.gondor.endpoint

import cats.effect.kernel.Sync
import com.gondor.model.{GeneralError, InvalidRequest}
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class PrimeNumberEndpoint[F[_]: Sync](primeNumberService: PrimeNumberServiceAlgebra[F])
    extends Http4sDsl[F] {

  private def getPrimeNumber: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / IntVar(intInputValue) =>
      Ok(primeNumberService.getPrimeNumbers(intInputValue).map {
          case Left(_ @ GeneralError(message)) => message
          case Left(_ @ InvalidRequest(message)) => message
          case Right(result) => result.value.toString
      }.intersperse(","))
    }

  def endpoint: HttpRoutes[F] = getPrimeNumber

}

object PrimeNumberEndpoint {
  def apply[F[_]: Sync](pnr: PrimeNumberServiceAlgebra[F]) =
    new PrimeNumberEndpoint[F](pnr)
}
