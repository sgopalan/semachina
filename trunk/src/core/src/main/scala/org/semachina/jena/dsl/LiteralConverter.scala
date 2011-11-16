package org.semachina.jena.dsl

import org.semachina.jena.ontology.URIResolver
import com.hp.hpl.jena.datatypes.RDFDatatype

class LiteralConverter(value: Any, uriResolver: URIResolver) {
  def ^^ = uriResolver.getOntModel.createTypedLiteral(value)

  def ^^(dtype: String) = uriResolver.resolveTypedLiteral(value, dtype)

  def ^^(dtype: RDFDatatype) = uriResolver.getOntModel.createTypedLiteral(value, dtype)

  def ^^? = uriResolver.parseTypedLiteral(value.toString)

  def ^^@(lang: String) = uriResolver.getOntModel.createLiteral(value.toString, lang)
}
