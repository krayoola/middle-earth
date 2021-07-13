package com.gondor.model


sealed trait PrimeNumberDomainRequest

sealed trait ApplicationError extends PrimeNumberDomainRequest {
  def message: String
}

case class InvalidRequest(message: String) extends Throwable(message) with ApplicationError

case class GeneralError(message: String) extends Throwable(message) with ApplicationError