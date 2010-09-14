package org.semachina.jena.core

import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.enhanced.{Implementation, EnhNode, EnhGraph}
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.rdf.model.{Literal, RDFNode}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Aug 8, 2010
 * Time: 9:39:13 PM
 * To change this template use File | Settings | File Templates.
 */

class ScalaIndividualImpl(node: Node, graph: EnhGraph) extends IndividualImpl(node, graph) {
  def this(indiv: EnhNode) = this (indiv.asNode(), indiv.getGraph())

  def apply(ontProperty: OntProperty): RDFNode = getPropertyValue(ontProperty)

  def update(ontProperty: OntProperty, node: RDFNode) = setPropertyValue(ontProperty, node)

  def getObject(ontProperty: OntProperty): Individual = getPropertyValue(ontProperty).asInstanceOf[Individual]

  def getLiteral(ontProperty: OntProperty): Literal = getPropertyValue(ontProperty).asLiteral

  def +(ontProperty: OntProperty, value: AnyRef): Individual = this + (ontProperty, value, getTypeURI(ontProperty))

  def +(ontProperty: OntProperty, value: AnyRef, typeURI: String): Individual = {
    val rdfNode = unmarshall(typeURI, value);
    addProperty(ontProperty, rdfNode)
    return this;
  }

  def +(ontProperty: OntProperty, value: String, lang: String): Individual = {
    addProperty(ontProperty, value, lang);
    return this;
  }

  def -(ontProperty: OntProperty, value: AnyRef): Individual = this - (ontProperty, value, getTypeURI(ontProperty))

  def -(ontProperty: OntProperty, value: AnyRef, typeURI: String): Individual = {
    val rdfNode = unmarshall(typeURI, value);
    removeProperty(ontProperty, rdfNode)
    return this;
  }

  def --(ontProperty: OntProperty): Individual = {
    removeAll(ontProperty)
    return this;
  }

  protected def unmarshall(typeURI: String, value: AnyRef): RDFNode = {
    if (typeURI == null) {
      if (value.isInstanceOf[RDFNode]) {
        return value.asInstanceOf[RDFNode]
      }
      throw new IllegalStateException("data accessor is null, and value is of type: " + value.getClass)
    }

    var literal = getOntModel().createTypedLiteral(value, typeURI)
    return literal
  }

  protected def getTypeURI(property: OntProperty, typeURI: String = null): String = {
    var uri: String = typeURI
    if (typeURI == null && property.isDatatypeProperty()) {
      var range: OntResource = property.getRange()
      uri = RDFS.Literal.getURI()
      if (range != null) {
        uri = range.getURI()
      }
    }
    return uri
  }

}

object ScalaIndividualImpl {
  def apply(indiv: Individual) = new ScalaIndividualImpl(indiv.asInstanceOf[EnhNode])

  val factory = new Implementation() {
    def wrap(n: Node, eg: EnhGraph): EnhNode = {
      if (canWrap(n, eg)) {
        return new ScalaIndividualImpl(n, eg)
      }
      else {
        throw new ConversionException("Cannot convert node " + n.toString + " to Individual")
      }
    }

    def canWrap(node: Node, eg: EnhGraph): Boolean = {
      var profile: Profile = null;

      if (eg.isInstanceOf[OntModel]) {
        profile = eg.asInstanceOf[OntModel].getProfile
      }

      return (profile != null) && profile.isSupported(node, eg, classOf[Individual])
    }
  }

}