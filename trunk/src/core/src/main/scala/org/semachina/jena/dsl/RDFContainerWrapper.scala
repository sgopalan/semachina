package org.semachina.jena.dsl

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.RDFNode

class RDFContainerWrapper(rdfNode: RDFNode, ontModel: OntModel) {

  def asSeq(uri: String = null) = {
    def seq = if (uri != null) ontModel.createSeq(uri) else ontModel.createSeq
    seq.add(rdfNode)
    seq
  }

  def asBag(uri: String = null) = {
    def bag = if (uri != null) ontModel.createBag(uri) else ontModel.createSeq
    bag.add(rdfNode)
    bag
  }

  def asAlt(uri: String = null) = {
    def alt = if (uri != null) ontModel.createAlt(uri) else ontModel.createSeq
    alt.add(rdfNode)
    alt
  }

  def asRdfList() = {
    def rdfList = ontModel.createList()
    rdfList.add(rdfNode)
    rdfList
  }
}