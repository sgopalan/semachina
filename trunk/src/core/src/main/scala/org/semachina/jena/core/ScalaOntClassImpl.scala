package org.semachina.jena.core

import com.hp.hpl.jena.ontology.impl.OntClassImpl
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.enhanced.{EnhNode, Implementation, EnhGraph}
import com.hp.hpl.jena.ontology.{OntModel, Profile, ConversionException, OntClass}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Aug 30, 2010
 * Time: 9:19:38 PM
 * To change this template use File | Settings | File Templates.
 */

class ScalaOntClassImpl(n: Node, m: EnhGraph) extends OntClassImpl(n, m) {
  def this(ontClass: EnhNode) = this (ontClass.asNode(), ontClass.getGraph())

  def listIndividuals = listInstances

  def listDirectIndividuals = listInstances(true)

}

object ScalaOntClassImpl {
  def apply(ontClass: OntClass) = new ScalaOntClassImpl(ontClass.asInstanceOf[EnhNode])

  def apply(n: Node, m: EnhGraph) = new ScalaOntClassImpl(n, m);

  /**
   * A factory for generating OntClass facets from nodes in enhanced graphs.
   * Note: should not be invoked directly by user code: use
   * { @link com.hp.hpl.jena.rdf.model.RDFNode # as as() } instead.
   */
  val factory: Implementation = new Implementation {
    def wrap(n: Node, eg: EnhGraph): EnhNode = {
      if (canWrap(n, eg)) {
        return ScalaOntClassImpl(n, eg)
      }
      else {
        throw new ConversionException("Cannot convert node " + n.toString + " to OntClass: it does not have rdf:type owl:Class or equivalent")
      }
    }

    def canWrap(node: Node, eg: EnhGraph): Boolean = {
      var profile: Profile = if ((eg.isInstanceOf[OntModel])) (eg.asInstanceOf[OntModel]).getProfile else null
      return (profile != null) && profile.isSupported(node, eg, classOf[OntClass])
    }
  }

}