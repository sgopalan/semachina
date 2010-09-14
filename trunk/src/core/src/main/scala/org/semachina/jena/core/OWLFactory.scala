package org.semachina.jena.core

import com.hp.hpl.jena.rdf.model.{ModelMaker, Model}
import com.hp.hpl.jena.ontology.{OntDocumentManager, OntModel}
import com.hp.hpl.jena.shared.PrefixMapping

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 9, 2010
 * Time: 7:40:02 PM
 * To change this template use File | Settings | File Templates.
 */

object OWLFactory {
  val PREFIX: PrefixMapping = PrefixMapping.Factory.create
          .setNsPrefixes(PrefixMapping.Standard)

  def getOntDocumentManager(): OntDocumentManager = OntDocumentManager.getInstance

  /**
   * <p>
   * Answer a new ontology model which will process in-memory models of
   * ontologies expressed the default ontology language (OWL).
   * The default document manager
   * will be used to load the ontology's included documents.
   * </p>
   * <p><strong>Note:</strong>The default model chosen for OWL, RDFS and DAML+OIL
   * includes a weak reasoner that includes some entailments (such as
   * transitive closure on the sub-class and sub-property hierarchies). Users
   * who want either no inference at all, or alternatively
   * more complete reasoning, should use
   * one of the other <code>createOntologyModel</code> methods that allow the
   * preferred OntModel specification to be stated.</p>
   * @return A new ontology model
   * @see OntModelSpec # getDefaultSpec
   * @see # createOntologyModel ( OntModelSpec, Model )
   */
  def createOntologyModel(): ScalaOntModelImpl = {
    return ScalaOntModelImpl()
  }

  /**
   * <p>
   * Answer a new ontology model which will process in-memory models of
   * ontologies expressed the default ontology language (OWL).
   * The default document manager
   * will be used to load the ontology's included documents.
   * </p>
   *
   * @param spec An ontology model specification that defines the language and reasoner to use
   * @param maker A model maker that is used to get the initial store for the ontology (unless
   * the base model is given),
   * and create addtional stores for the models in the imports closure
   * @param base The base model, which contains the contents of the ontology to be processed
   * @return A new ontology model
   * @see OntModelSpec
   */
  def createOntologyModel(maker: ModelMaker, base: Model): OntModel = {
    return ScalaOntModelImpl(maker, base)
  }


  /**
   * <p>
   * Answer a new ontology model, constructed according to the given ontology model specification,
   * and starting with the ontology data in the given model.
   * </p>
   *
   * @param spec An ontology model specification object, that will be used to construct the ontology
   * model with different options of ontology language, reasoner, document manager and storage model
   * @param base An existing model to treat as an ontology model, or null.
   * @return A new ontology model
   * @see OntModelSpec
   */
  def createOntologyModel(base: Model): OntModel = {
    return ScalaOntModelImpl(base)
  }


}