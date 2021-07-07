package com.mordor

import cats.effect.IO
import fs2.Stream

object HelloWorld {

  def isPrime(n: Int): Boolean = Range(2, n - 1).filter(n % _ == 0).length == 0

  val result = Stream
    .iterate(1)(_ + 1)
    .take(20)
    .toList

  def say(): IO[String] = IO.delay("Hello Cats!" + result)
}
