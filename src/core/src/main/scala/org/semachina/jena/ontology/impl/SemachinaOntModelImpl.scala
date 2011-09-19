package org.semachina.jena.ontology.impl

import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.ontology.impl.OntModelImpl
import org.semachina.jena.ontology.SemachinaOntModel
import com.hp.hpl.jena.enhanced.{Personality, EnhGraph}
import com.hp.hpl.jena.rdf.model.{RDFNode, Model}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/21/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */

class SemachinaOntModelImpl(spec: OntModelSpec, model: Model, personality:Personality[RDFNode])
  extends OntModelImpl(spec, model) with SemachinaOntModel {

  val ontModel = this
  val enhGraph = this.asInstanceOf[EnhGraph]

  //set the personality if there is one
  if( personality != null ) {
    getPersonality.add( personality )
  }

  def this(spec: OntModelSpec, personality:Personality[RDFNode]) = this(spec, spec.createBaseModel(), personality)

  def this(spec: OntModelSpec) = this(spec, spec.createBaseModel(), null )

  def this(spec: OntModelSpec, model: Model) = this(spec, model, null)

  override def close() = {
    closeFeatures
    super.close
  }
}