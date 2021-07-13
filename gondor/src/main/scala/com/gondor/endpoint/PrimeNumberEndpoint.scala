package com.gondor.endpoint

import cats.Applicative
import cats.effect.kernel.Sync
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import com.gondor.service.Validator.{GeneralError, InvalidRequest}
import com.service.prime.PrimeNumberResponse

class PrimeNumberEndpoint[F[_]: Sync](primeNumberService: PrimeNumberServiceAlgebra[F])
    extends Http4sDsl[F] {

  private def getPrimeNumber: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / IntVar(intInputValue) =>
      primeNumberService.getPrimeNumbers(intInputValue).value.flatMap {
        case Right(result) =>
          val formattedValues: fs2.Stream[F, String] = Formatter.format[F](result)
          Ok(formattedValues)
        case Left(_ @ InvalidRequest(message)) => BadRequest(message)
        case Left(_ @ GeneralError(message)) => BadRequest(message)
        case Left(_) => InternalServerError("Something went wrong to the server")
      }
    }

  def endpoint: HttpRoutes[F] = getPrimeNumber

}

object Formatter {
  def format[F[_]](streamInput : fs2.Stream[F, PrimeNumberResponse] ): fs2.Stream[F, String] =
    streamInput.map(_.value.toString).intersperse(",")
}

object PrimeNumberEndpoint {
  def apply[F[_]: Sync: Applicative](pnr: PrimeNumberServiceAlgebra[F]) =
    new PrimeNumberEndpoint[F](pnr)
}
