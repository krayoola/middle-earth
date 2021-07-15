package com.mordor

import io.grpc.Status

object MordorServerStatus {
  val INVALID_INPUT: Status = Status.INVALID_ARGUMENT
    .augmentDescription("Input value should be greater than 0")
}
