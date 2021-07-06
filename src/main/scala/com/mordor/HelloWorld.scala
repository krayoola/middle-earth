package com.mordor

import cats.effect.IO
import fs2.Stream

object HelloWorld {

  def say(): IO[String] = IO.delay("Hello Cats!")

}
