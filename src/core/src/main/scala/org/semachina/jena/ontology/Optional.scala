package org.semachina.jena.ontology

import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.sparql.path.Path

abstract sealed class Optional[V](obj: V)(implicit m: Manifest[V]) {
  def value: V = obj

  def valueClass = m.erasure.asInstanceOf[Class[V]]
}

case class OptionalProperty(property: Property) extends Optional[Property](property)

case class OptionalPath(path: Path) extends Optional[Path](path)

case class OptionalString(propOrPath: String) extends Optional[String](propOrPath)
