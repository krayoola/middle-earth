package com.gondor.endpoint

import cats.effect.kernel.Async
import cats.implicits._
import com.gondor.model.{ApplicationError, GeneralError, GondorNumberResponse, Invalid}
import com.gondor.service.PrimeNumberServiceAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.middleware.GZip

class PrimeNumberEndpoint[F[_]: Async](primeNumberService: PrimeNumberServiceAlgebra[F])
    extends Http4sDsl[F] {

  // pure Stream approach
  private def getPrimeNumber: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / CustomIntVar(intInputValue) =>
      Ok(primeNumberService.getPrimeNumbers(intInputValue).map {
          case Right(result) => result.number.toString
          case Left(_ @ GeneralError(message)) => message
          case Left(_) => "Something went wrong!"
      }.intersperse(","))
    }

  // EitherT approach
  private def getPrimeNumberV2: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / "v2" / IntVar(intInputValue) =>
      primeNumberService.getPrimeNumbersViaEitherT(intInputValue).value.flatMap {
        case Right(result) =>
            val collected = result.broadcastThrough[F, String] (
              _.collect { case positive: GondorNumberResponse => positive.number.toString }.through(_.intersperse(",")),
              _.collect { case negative: ApplicationError => negative.message }
            )
          Ok(collected)
        case Left(_ @ Invalid(message)) => BadRequest(message)
        case Left(_) => InternalServerError("Something went wrong to the server")
      }
    }

  // TODO: If we have time implement a circuit breaker approach
  private def getPrimeNumberV3: HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "prime" / "v3" / IntVar(_) =>
      NotImplemented("API under construction")
    }

  def endpoint: HttpRoutes[F] = GZip(getPrimeNumber) <+> GZip(getPrimeNumberV2) <+> getPrimeNumberV3

}

object CustomIntVar {
  // ensuring that we only accept positive numeric values
  def unapply(str: String): Option[Int] = {
    if (str.forall(_.isDigit) && str.toInt > 0)
      Option(str.toInt)
    else
      None
  }
}

object PrimeNumberEndpoint {
  def apply[F[_]: Async](pnr: PrimeNumberServiceAlgebra[F]) =
    new PrimeNumberEndpoint[F](pnr)
}
