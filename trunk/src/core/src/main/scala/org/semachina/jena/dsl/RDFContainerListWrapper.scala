package org.semachina.jena.dsl

import com.hp.hpl.jena.rdf.model.RDFNode
import com.hp.hpl.jena.ontology.OntModel
import collection.JavaConversions._

class RDFContainerListWrapper(val rdfNodes: Traversable[RDFNode], ontModel: OntModel) {
  def asSeq(uri: String = null) = {
    def seq = if (uri != null) ontModel.createSeq(uri) else ontModel.createSeq
    rdfNodes.foreach {
      rdfNode => seq.add(rdfNode)
    }
    seq
  }

  def asBag(uri: String = null) = {
    def bag = if (uri != null) ontModel.createBag(uri) else ontModel.createSeq
    rdfNodes.foreach {
      rdfNode => bag.add(rdfNode)
    }
    bag
  }

  def asAlt(uri: String = null) = {
    def alt = if (uri != null) ontModel.createAlt(uri) else ontModel.createSeq
    rdfNodes.foreach {
      rdfNode => alt.add(rdfNode)
    }
    alt
  }

  def asRdfList = ontModel.createList(rdfNodes.toIterator)
}