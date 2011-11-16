package org.semachina.jena.dsl

import org.semachina.jena.ontology.URIResolver
import com.hp.hpl.jena.datatypes.RDFDatatype

/**
 * DSL element to ease literal list creation
 */

class LiteralListConverter(values: Iterable[Any], uriResolver: URIResolver) {
  def ^^ = values map {
    case value: AnyRef => uriResolver.getOntModel.createTypedLiteral(value)
  }

  def ^^(dtype: String) = values map {
    case value: AnyRef => uriResolver.resolveTypedLiteral(value, dtype)
  }

  def ^^(dtype: RDFDatatype) = values map {
    case value: AnyRef => uriResolver.getOntModel.createTypedLiteral(value, dtype)
  }

  def ^^? = values map {
    case value: AnyRef => uriResolver.parseTypedLiteral(value.toString)
  }

  def ^^@(lang: String) = values map {
    case value: AnyRef => uriResolver.getOntModel.createLiteral(value.toString, lang)
  }
}