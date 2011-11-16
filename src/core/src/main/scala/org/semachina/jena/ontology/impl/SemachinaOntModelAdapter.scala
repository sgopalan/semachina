package org.semachina.jena.ontology.impl

import com.hp.hpl.jena.ontology.OntModel
import org.semachina.jena.ontology.SemachinaOntModel
import com.hp.hpl.jena.enhanced.EnhGraph

/**
 * Adapter for existing OntModels to take advantage of Semachina
 */
object SemachinaOntModelAdapter {
  def apply(ontModel: OntModel) = new SemachinaOntModelAdapter(ontModel)

  implicit def toOntModel(adapter: SemachinaOntModelAdapter): OntModel = adapter.getOntModel
}

class SemachinaOntModelAdapter(val self: OntModel) extends Proxy with SemachinaOntModel {

  def getEnhGraph = self.asInstanceOf[EnhGraph]

  def getOntModel = self
}