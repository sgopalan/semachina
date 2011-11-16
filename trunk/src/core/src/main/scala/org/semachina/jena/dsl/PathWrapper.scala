package org.semachina.jena.dsl

import com.hp.hpl.jena.sparql.path.Path
import org.semachina.jena.ontology.OptionalPath

/**
 * DSL element to provide hint to return an Option
 */
class PathWrapper(path: Path) {
  def ? = OptionalPath(path)
}