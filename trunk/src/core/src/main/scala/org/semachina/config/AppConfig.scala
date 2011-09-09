package org.semachina.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.semachina.jena.config.SemachinaConfig


@Configuration
class AppConfig {
  val logger = LoggerFactory.getLogger(classOf[AppConfig])
}