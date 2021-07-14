package com.gondor.model


// should be named 'sauron' ;)
sealed trait PrimeNumberDomain

sealed trait ApplicationError extends PrimeNumberDomain {
  def message: String
}

case class Invalid(message: String) extends Throwable(message) with ApplicationError
case class ValidatedRequest(number: Int)

case class GeneralError(message: String) extends Throwable(message) with ApplicationError
case class GondorNumberResponse(number: Int) extends PrimeNumberDomain