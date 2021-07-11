package com.gondor.config

import cats.effect.{Async, Resource}
import pureconfig.generic.auto._
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.module.catseffect.syntax._

object Settings {

  final case class ClientConfig(host: String, port: Int)
  final case class ServerConfig(host: String, port: Int)

  case class Config(server: ServerConfig, client: ClientConfig)

  object Config {
    // TODO: Wrap it with blocking.. CE3 changed and remove blocking class..
    def load[F[_]: Async](configFile: String): Resource[F, Config] = {
        Resource.liftK(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[F, Config])
    }
  }
}
