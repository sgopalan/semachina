package org.semachina.jena

import config.{SemachinaConfig}
import impl.scala.{SemachinaIndividualImpl, SemachinaOntModelAdapter}
import org.semachina.jena.config.features.Feature
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.datatypes.{DatatypeFormatException, TypeMapper, RDFDatatype}
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl
import com.hp.hpl.jena.util.MonitorModel
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.graph.{TransactionHandler, Node}
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.shared.{Command, Lock}
import com.hp.hpl.jena.rdf.model._
import java.util.ArrayList
import collection.JavaConversions._
import java.lang.{IllegalArgumentException, Iterable}
import java.util.HashMap
import collection.JavaConversions

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/21/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaOntModelTrait {

   def apply(ontModel: OntModel) = asSemachinaOntModelTrait( ontModel )

   implicit def asSemachinaOntModelTrait(ontModel: OntModel): SemachinaOntModelTrait = {
    if (ontModel.isInstanceOf[SemachinaOntModelTrait]) {
      return ontModel.asInstanceOf[SemachinaOntModelTrait]
    }
    else {
      return new SemachinaOntModelAdapter(ontModel)
    }
  }
}

trait SemachinaOntModelTrait extends URIResolver with OntModelReader with OntModelWriter with OntModelConfiguration {

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