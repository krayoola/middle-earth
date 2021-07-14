package com.gondor.model


sealed trait PrimeNumberDomainRequest

sealed trait ApplicationError extends PrimeNumberDomainRequest {
  def message: String
}


case class InvalidRequest(message: String) extends ApplicationError
case class ValidatedRequest(number: Int)

case class GeneralError(message: String) extends Throwable(message) with ApplicationError
case class GondorNumberResponse(number: Int) extends PrimeNumberDomainRequest