package com.gondor.model


sealed trait PrimeNumberDomainRequest

sealed trait ApplicationError extends PrimeNumberDomainRequest {
  def message: String
}

case class GeneralError(message: String) extends Throwable(message) with ApplicationError

case class GondorNumberResponse(number: Int) extends PrimeNumberDomainRequest
case class ValidatedRequest(number: Int)