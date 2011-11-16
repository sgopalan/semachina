package org.semachina.jena.config

import com.hp.hpl.jena.rdf.model.{RDFNode, Model}
import com.hp.hpl.jena.ontology.{OntModel, OntModelSpec}
import com.hp.hpl.jena.enhanced.{BuiltinPersonalities, Personality}
import com.hp.hpl.jena.shared.PrefixMapping
import org.semachina.jena.ontology.SemachinaOntModel
import org.semachina.jena.ontology.impl.SemachinaOntModelImpl
import scala.collection.mutable.ListBuffer
import javax.validation.constraints.NotNull
import org.semachina.jena.features.Feature

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 10/24/11
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */

case class SemachinaBuilder(
                             @NotNull private val spec: OntModelSpec = OntModelSpec.OWL_MEM,
                             private val base: Model = null,
                             @NotNull private val prefixes: PrefixMapping = SemachinaConfiguration.prefixMapping,
                             @NotNull private val personality: Personality[RDFNode] = SemachinaConfiguration.personality) {

  lazy private val readURIs = new ListBuffer[String]
  lazy private val subModels = new ListBuffer[OntModel]
  lazy private val subModelURIs = new ListBuffer[String]
  lazy private val features = new ListBuffer[Feature]
  private var rebindModel = false


  lazy val standardPersonality: SemachinaBuilder = copy(personality = BuiltinPersonalities.model)
  lazy val semachinaPersonality: SemachinaBuilder = copy(personality = SemachinaConfiguration.personality)

  lazy val standardPrefixes: SemachinaBuilder = copy(prefixes = PrefixMapping.Standard)
  lazy val extendedPrefixes: SemachinaBuilder = copy(prefixes = PrefixMapping.Extended)
  lazy val semachinaPrefxes: SemachinaBuilder = copy(prefixes = SemachinaConfiguration.prefixMapping)


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
  lazy val noReasoningOntModel: SemachinaBuilder = copy(spec = OntModelSpec.OWL_MEM)


  def setNsPrefix(prefixes: Pair[String, String]*) = {
    prefixes.foreach {
      pair => this.prefixes.setNsPrefix(pair._1, pair._2)
    }
    this
  }

  def read(uri: String, altEntry: String = null) = {
    readURIs += uri
    if (altEntry != null) {
      SemachinaConfiguration.addAltEntry(uri -> altEntry)
    }
    this
  }

  def addSubModelURI(uris: String*) = {
    this.subModelURIs.appendAll(uris)
    this
  }

  def addSubModel(subModels: OntModel*) = {
    this.subModels.appendAll(subModels)
    this
  }

  def rebind(rebind: Boolean = true) = {
    this.rebindModel = rebind
    this
  }

  def build: OntModel with SemachinaOntModel = {
    try {
      val useBase = if (base != null) base else spec.createBaseModel()
      val ontModel = new SemachinaOntModelImpl(spec, useBase, personality)

      //load features
      features.foreach {
        feature => ontModel.addFeature(feature)
      }

      //load files
      readURIs.foreach {
        uri => ontModel.read(uri)
      }

      //set prefixes... do this after reading
      ontModel.setNsPrefixes(prefixes)

      //set submodels
      subModels.foreach {
        subModel => ontModel.addSubModel(subModel)
      }

      subModelURIs.foreach {
        uri =>
          val subModel = SemachinaConfiguration.load(uri)
          ontModel.addSubModel(subModel)
      }

      if (rebindModel) {
        ontModel.rebind()
      }

      return ontModel
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }
}