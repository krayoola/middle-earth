package com.gondor.endpoint

import cats.effect.kernel.Sync
import com.gondor.model.GeneralError
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class PrimeNumberEndpoint[F[_]: Sync](primeNumberService: PrimeNumberServiceAlgebra[F])
    extends Http4sDsl[F] {

  private def getPrimeNumber: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / CustomIntVar(intInputValue) =>
      Ok(primeNumberService.getPrimeNumbers(intInputValue).map {
          case Left(_ @ GeneralError(message)) => message
          case Right(result) => result.number.toString
      }.intersperse(","))
    }

  def endpoint: HttpRoutes[F] = getPrimeNumber

}

object CustomIntVar {
  // ensuring that we only accept positive numeric values
  def unapply(str: String): Option[Int] = {
    if (str.forall(_.isDigit) && str.toInt >= 0)
      Option(str.toInt)
    else
      None
  }
}

object PrimeNumberEndpoint {
  def apply[F[_]: Sync](pnr: PrimeNumberServiceAlgebra[F]) =
    new PrimeNumberEndpoint[F](pnr)
}
