package org.semachina.jena.config

import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.enhanced.{EnhGraph, Implementation, EnhNode}
import com.hp.hpl.jena.ontology.{OntModel, Profile, ConversionException}
import com.hp.hpl.jena.rdf.model.RDFNode

object SemachinaImplementation {

  def apply[V <: RDFNode](
    createInstance : (Node, EnhGraph) => EnhNode,
    failMessage: String,
    supportedClazzes: Class[_]*)
    (implicit m : Manifest[V]) : SemachinaImplementation[V] =  {

    val implFactory = new SemachinaImplementation[V] {
      override val implClass = m.erasure.asInstanceOf[Class[V]]
      override val convertFailedMessage = failMessage
      override val supportedClasses = supportedClazzes
      override val create = createInstance
    }
    return implFactory
  }
}

trait SemachinaImplementation[V <: RDFNode] extends Implementation {

  def implClass: Class[V]
  def convertFailedMessage: String
  def supportedClasses: Iterable[Class[_]]
  def create : (Node, EnhGraph) => EnhNode

  require(supportedClasses == null || supportedClasses.size > 0, "There needs to be at least one supported class")

  def wrap(node: Node, eg: EnhGraph): EnhNode = {
    if (canWrap(node, eg)) {
      return create(node, eg)
    }
    else {
      throw new ConversionException("Cannot convert node " + node + " to " + implClass + ".\n" + convertFailedMessage)
    }
  }

  def canWrap(node: Node, eg: EnhGraph): Boolean = {
    var profile: Profile = if ((eg.isInstanceOf[OntModel])) (eg.asInstanceOf[OntModel]).getProfile else null
    if (profile != null) {
      for (clazz <- supportedClasses) {
        if (!profile.isSupported(node, eg, clazz)) {
          return false
        }
      }
      return true
    }
    return false
  }
}