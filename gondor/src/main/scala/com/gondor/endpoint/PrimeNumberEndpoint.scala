package com.gondor.endpoint

import cats.Applicative
import cats.effect.kernel.Sync
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class PrimeNumberEndpoint[F[_]: Sync: Applicative](pns: PrimeNumberServiceAlgebra[F])
    extends Http4sDsl[F] {

  private def getPrimeNumber: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / inputDigit =>
      val streamOfPrimeNumbers = pns.getPrimeNumbers(inputDigit.toInt).map(_.value)
      Ok(streamOfPrimeNumbers.map(_.toString))
    }

  def endpoint: HttpRoutes[F] = getPrimeNumber

}

object PrimeNumberEndpoint {
  def apply[F[_]: Sync: Applicative](pnr: PrimeNumberServiceAlgebra[F]) =
    new PrimeNumberEndpoint[F](pnr)
}
