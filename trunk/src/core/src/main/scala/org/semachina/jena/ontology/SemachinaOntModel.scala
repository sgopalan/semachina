package org.semachina.jena.ontology

import impl.SemachinaOntModelAdapter
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.ontology._
import org.semachina.jena.query.Arq
import org.semachina.jena.binder.IndividualBinder

/**
 * Defines the basic methods for a Semachina OntModel
 */

object SemachinaOntModel {

  def apply(ontModel: OntModel) = asSemachinaOntModelTrait(ontModel)

  implicit def asSemachinaOntModelTrait(ontModel: OntModel): SemachinaOntModel = {
    if (ontModel.isInstanceOf[SemachinaOntModel]) {
      ontModel.asInstanceOf[SemachinaOntModel]
    }
    else {
      new SemachinaOntModelAdapter(ontModel)
    }
  }
}

trait SemachinaOntModel extends OntModelConfiguration with OntModelTransaction with Arq {

  def getEnhGraph: EnhGraph

  def getOntModel: OntModel

  def toIndividual(bean: AnyRef): SemachinaIndividual = {
    val binder = IndividualBinder.default(getOntModel)
    binder.toIndividual(bean)
  }
}