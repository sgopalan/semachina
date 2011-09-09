package org.semachina.jena.impl.scala

import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.ontology.impl.OntModelImpl
import com.hp.hpl.jena.enhanced.EnhGraph
import org.semachina.jena.SemachinaOntModelTrait

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/21/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */

class SemachinaOntModelImpl(spec: OntModelSpec, model: Model)
  extends OntModelImpl(spec, model) with SemachinaOntModelTrait {

  val ontModel = this
  val enhGraph = this.asInstanceOf[EnhGraph]

  def this(spec: OntModelSpec) = this(spec, spec.createBaseModel())

  override def close() = {
    closeFeatures
    super.close
  }
}