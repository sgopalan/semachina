package org.semachina.jena.dsl

import com.hp.hpl.jena.rdf.model.Property
import org.semachina.jena.ontology.OptionalProperty

/**
 * DSL Element to tell compiler to return an Option
 */

class PropertyWrapper(property: Property) {
  def ? = OptionalProperty(property)
}