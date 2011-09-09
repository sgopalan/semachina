package org.semachina.jena.config

import org.semachina.jena.datatype.factory._
import org.semachina.jena.impl.scala.{SemachinaOntModelImpl, SemachinaIndividualImpl}
import com.hp.hpl.jena.ontology.{ProfileRegistry, OntModelSpec, Individual, OntModel}
import org.semachina.jena.SemachinaOntModelTrait
import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.enhanced.EnhGraph

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 2, 2011
 * Time: 7:06:13 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaFactory extends JenaConfiguration with ArqConfiguration {

  //register Implementation -->  SemachinaIndividualImpl
  registerImplementation(
    SemachinaImplementation[SemachinaIndividualImpl](
      (node: Node, eg: EnhGraph) => new SemachinaIndividualImpl(node, eg),
      "",
      classOf[Individual])
  )

  //register new data types
  registerDatatype(new DateTimeFactory)
  registerDatatype(new DateFactory)
  registerDatatype(new DayFactory)
  registerDatatype(new DurationFactory)
  registerDatatype(new MonthDayFactory)
  registerDatatype(new TimeFactory)
  registerDatatype(new YearFactory)
  registerDatatype(new YearMonthFactory)

  /**
   * <PROP>
   * Answer a new ontology model which will process in-memory models of
   * ontologies expressed the default ontology language (OWL).
   * The default document manager
   * will be used to load the ontology's included documents.
   * </PROP>
   * <PROP><strong>Note:</strong>The default model chosen for OWL, RDFS and DAML+OIL
   * includes a weak reasoner that includes some entailments (such to
   * transitive closure on the sub-class and sub-property hierarchies). Users
   * who want either no inference at all, or alternatively
   * more complete reasoning, should use
   * one of the other <code>createOntologyModel</code> methods that allow the
   * preferred OntModel specification to be stated.</PROP>
   *
   * @return A new ontology model
   */
  def createMemOntologyModel():  OntModel with SemachinaOntModelTrait = {
    try {
      return new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG))
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }

  def createOntologyModel():  OntModel with SemachinaOntModelTrait = {
    try {
      return new SemachinaOntModelImpl(OntModelSpec.OWL_DL_MEM)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }

  def createOntologyModel(ontModelSpec: OntModelSpec): OntModel with SemachinaOntModelTrait = {
    try {
      return new SemachinaOntModelImpl(ontModelSpec)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }


  def createOntologyModel(ontModelSpec: OntModelSpec, base: Model): OntModel with SemachinaOntModelTrait = {
    try {
      return new SemachinaOntModelImpl(ontModelSpec, base)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }
}