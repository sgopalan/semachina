package org.semachina.jena.ontology.impl

import com.hp.hpl.jena.ontology.OntModel
import org.semachina.jena.ontology.SemachinaOntModel
import com.hp.hpl.jena.enhanced.EnhGraph

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/21/11
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaOntModelAdapter {
  def apply(ontModel : OntModel) = new SemachinaOntModelAdapter( ontModel )

  implicit def toOntModel(adapter:SemachinaOntModelAdapter) : OntModel = adapter.ontModel
}

class SemachinaOntModelAdapter(val ontModel : OntModel) extends SemachinaOntModel {

  val enhGraph = ontModel.asInstanceOf[EnhGraph]
}