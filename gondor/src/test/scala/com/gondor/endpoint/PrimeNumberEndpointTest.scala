package com.gondor.endpoint

import cats.effect.IO
import com.gondor.model.GondorNumberResponse
import com.gondor.suit.{EndpointEitherTContext, EndpointStreamContext}
import munit.{CatsEffectSuite}
import org.http4s.implicits.{http4sLiteralsSyntax, _}
import org.http4s.{Method, Request, Status}

class PrimeNumberEndpointTest extends CatsEffectSuite {

  test("prime api should be available") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/prime/5")
      ).unsafeRunSync
        assertEquals(response.status, Status.Ok)
    }
  }

  test("prime api /v3 should be NotImplemented") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/prime/v3/5")
      ).unsafeRunSync
      assertEquals(response.status, Status.NotImplemented)
    }
  }

  test("unsupported should be NotFound") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/unsupported/api")
      ).unsafeRunSync
      assertEquals(response.status, Status.NotFound)
    }
  }

  test("prime api /v2 should be available [pending]") {
    new EndpointEitherTContext[IO] {
//       TODO instantiate an EitherT having problem with Applicative implicit
//      val expectedStreamOfGondorResponses = createDomainStream(GondorNumberResponse(2), GondorNumberResponse(3))
//      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
//        Request(method = Method.GET, uri = uri"/prime/v2/5")
//      ).unsafeRunSync
//      assertEquals(response.status, Status.Ok)
    }
  }



}
