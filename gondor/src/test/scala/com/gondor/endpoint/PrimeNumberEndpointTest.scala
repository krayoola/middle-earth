package com.gondor.endpoint

import cats.effect.IO
import com.gondor.model.{ApplicationError, GondorNumberResponse}
import com.gondor.suite.{EndpointEitherTContext, EndpointStreamContext}
import fs2.Stream
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.headers.`Transfer-Encoding`
import org.http4s.implicits.{http4sLiteralsSyntax, _}

class PrimeNumberEndpointTest extends CatsEffectSuite {
  val chunked: Header.Raw = `Transfer-Encoding`(TransferCoding.chunked).toRaw1

  // v1
  test("prime api should be available") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/prime/5")
      ).unsafeRunSync

      val transferEncoding = response.headers.headers.find(_ == chunked).get
      assertEquals(transferEncoding, chunked)
      assertEquals(response.status, Status.Ok)
      response.as[String] assertEquals "2,3"
    }
  }

  test("prime api should not accept negative integer values") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = Stream.empty.covaryAll[IO,Either[ApplicationError, GondorNumberResponse]]
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/prime/-1")
      ).unsafeRunSync
      assertEquals(response.status, Status.NotFound)
    }
  }

  test("prime api should not accept string values") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = createEitherStream(Right(GondorNumberResponse(2)), Right(GondorNumberResponse(3)))
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/prime/aa")
      ).unsafeRunSync
      assertEquals(response.status, Status.NotFound)
    }
  }

  // v2
  test("prime api /v2 should be available") {
    new EndpointEitherTContext[IO] {
      val expectedStreamOfGondorResponses = Stream(GondorNumberResponse(2), GondorNumberResponse(3)).covary[IO]
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/prime/v2/5")
      ).unsafeRunSync
      val transferEncoding = response.headers.headers.find(_ == chunked).get
      assertEquals(transferEncoding, chunked)
      assertEquals(response.status, Status.Ok)
      response.as[String] assertEquals "2,3"
    }
  }

  // others
  test("prime api /v3 should be NotImplemented") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = Stream.empty.covaryAll[IO,Either[ApplicationError, GondorNumberResponse]]
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/prime/v3/5")
      ).unsafeRunSync
      assertEquals(response.status, Status.NotImplemented)
    }
  }

  test("unsupported should be NotFound") {
    new EndpointStreamContext[IO] {
      val expectedStreamOfGondorResponses = Stream.empty.covaryAll[IO,Either[ApplicationError, GondorNumberResponse]]
      val response = createEndpoint(expectedStreamOfGondorResponses).orNotFound.run(
        Request(method = Method.GET, uri = uri"/unsupported/api")
      ).unsafeRunSync
      assertEquals(response.status, Status.NotFound)
    }
  }

}
