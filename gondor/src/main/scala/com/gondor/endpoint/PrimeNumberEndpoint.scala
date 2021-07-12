package com.gondor.endpoint

import cats.Applicative
import cats.effect.kernel.Sync
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.syntax.all._
import com.gondor.service.Validator.InvalidRequest
class PrimeNumberEndpoint[F[_]: Sync](primeNumberService: PrimeNumberServiceAlgebra[F])
    extends Http4sDsl[F] {

  private def getPrimeNumber: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / IntVar(intInputValue) =>
      primeNumberService.getPrimeNumbers(intInputValue).value.flatMap {
        case Right(result) =>
          // TODO: create a separate formatter
          val streamOfPrimeNumbers = result.map(_.value.toString)
          Ok(streamOfPrimeNumbers)
        case Left(_ @ InvalidRequest(message)) => BadRequest(message)
        case Left(_) => InternalServerError("Something went wrong to the server")
      }
    }

  def endpoint: HttpRoutes[F] = getPrimeNumber

}

object PrimeNumberEndpoint {
  def apply[F[_]: Sync: Applicative](pnr: PrimeNumberServiceAlgebra[F]) =
    new PrimeNumberEndpoint[F](pnr)
}
