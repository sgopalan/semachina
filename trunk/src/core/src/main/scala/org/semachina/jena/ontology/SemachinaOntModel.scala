package org.semachina.jena.ontology

import impl.SemachinaOntModelAdapter
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.ontology._
/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/21/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaOntModel {

   def apply(ontModel: OntModel) = asSemachinaOntModelTrait( ontModel )

   implicit def asSemachinaOntModelTrait(ontModel: OntModel): SemachinaOntModel = {
    if (ontModel.isInstanceOf[SemachinaOntModel]) {
      return ontModel.asInstanceOf[SemachinaOntModel]
    }
    else {
      return new SemachinaOntModelAdapter(ontModel)
    }
  }
}

trait SemachinaOntModel extends URIResolver with OntModelReader with OntModelWriter with OntModelConfiguration {

  protected val ontModel: OntModel
  protected val enhGraph: EnhGraph

  def getEnhGraph: EnhGraph = {
    return this.enhGraph
  }

  def getOntModel: OntModel = {
    return this.ontModel
  }

  def getURIResolver = this
}