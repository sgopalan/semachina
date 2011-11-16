package org.semachina.jena.config

/**
 * Mixin to help collect sets of configuration changes
 */
trait SemachinaProfile {
  def apply(configuration: SemachinaConfiguration)
}