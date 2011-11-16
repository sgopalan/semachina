package org.semachina.jena.config

import impl.DefaultSemachinaProfile
import com.hp.hpl.jena.shared.impl.JenaParameters
import com.hp.hpl.jena.datatypes.TypeMapper
import com.hp.hpl.jena.shared.PrefixMapping
import org.semachina.jena.enhanced.SemachinaImplementation
import org.semachina.jena.datatype.SemachinaBaseDatatype
import com.hp.hpl.jena.rdf.model.{RDFNode, ModelFactory}
import org.semachina.jena.query.ArqConfiguration
import com.hp.hpl.jena.enhanced.{Implementation, BuiltinPersonalities, Personality}
import com.hp.hpl.jena.ontology._
import org.semachina.jena.binder.ObjectBinderManager
import org.openjena.riot.SysRIOT

object SemachinaConfiguration
  extends SemachinaConfiguration with ArqConfiguration with ObjectBinderManager {

  //always load the default semachina profile first
  DefaultSemachinaProfile(this)

  //load subsequent profiles as needed
  def apply(profiles: SemachinaProfile*) = {
    if (profiles != null) {
      profiles.foreach {
        profile => profile(this)
      }
    }
    this
  }
}

/**
 * This trait consolidates much of the Jena Configuration within
 * the library
 *
 * The configurations include
 */
trait SemachinaConfiguration {

  val prefixMapping = ModelFactory.setDefaultModelPrefixes(PrefixMapping.Standard)

  lazy val ontDocumentManager = OntDocumentManager.getInstance

  lazy val typeMapper: TypeMapper = TypeMapper.getInstance

  lazy val personality: Personality[RDFNode] = BuiltinPersonalities.model

  //include methods to configure Jena
  setEnableEagerLiteralValidation(true)
  setEnableSilentAcceptanceOfUnknownDatatypes(false)

  /**
   * <p> Set this flag to true to cause typed literals to be
   * validated as they are created. </p>
   * <p>
   * RDF does not require ill-formed typed literals to be rejected from a graph
   * but rather allows them to be included but marked as distinct from
   * all legally formed typed literals. Jena2 reflects this by optionally
   * delaying validation of literals against datatype type constraints until
   * the first access. </p>
   */
  def setEnableEagerLiteralValidation(enable: Boolean) {
    JenaParameters.enableEagerLiteralValidation = enable
    this
  }

  /**
   * Set this flag to true to allow language-free, plain literals and xsd:strings
   * containing the same character sequence to test as sameAs.
   * <p>
   * RDF plain literals and typed literals of type xsd:string are distinct, not
   * least because plain literals may have a language tag which typed literals
   * may not. However, in the absence of a languge tag it can be convenient
   * for applications if the java objects representing identical character
   * strings in these two ways test as semantically "sameAs" each other.
   * At the time of writing is unclear if such identification would be sanctioned
   * by the RDF working group. </p>
   */
  def setEnablePlainLiteralSameAsString(enable: Boolean) {
    JenaParameters.enablePlainLiteralSameAsString = enable
    this
  }

  /**
   * Set this flag to true to allow unknown literal datatypes to be
   * accepted, if false then such literals will throw an exception when
   * first detected. Note that any datatypes unknown datatypes encountered
   * whilst this flag is 'true' will be automatically registered (as a type
   * whose value and lexical spaces are identical). Subsequently turning off
   * this flag will not unregister those unknown types already encountered.
   * <p>
   * RDF allows any URI to be used to indicate a datatype. Jena2 allows
   * user defined datatypes to be registered but it is sometimes convenient
   * to be able to process models containing unknown datatypes (e.g. when the
   * application does not need to touch the value form of the literal). However,
   * this behaviour means that common errors, such as using the wrong URI for
   * xsd datatypes, may go unnoticed and throw obscure errors late in processing.
   * Hence, the default is the require unknown datatypes to be registered.
   */
  def setEnableSilentAcceptanceOfUnknownDatatypes(enable: Boolean) = {
    JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = enable
    this
  }

  /**
   * Set this flag to true to switch on checking of surrounding whitespace
   * in non-string XSD numeric types. In the false (default) setting then
   * leading and trailing white space is silently trimmed when parsing an
   * XSD numberic typed literal.
   */
  def setEnableWhitespaceCheckingOfTypedLiterals(enable: Boolean) {
    JenaParameters.enableWhitespaceCheckingOfTypedLiterals = enable
    this
  }


  /**
   * Set this flag to true (default) to hide certain internal nodes from the output
   * of inference graphs. Some rule sets (notably owl-fb) create blank nodes as
   * part of their reasoning process. If these match some query they can appear
   * in the results. Such nodes are recorded as "hidden" and if this flag is set
   * all triples involving such hidden nodes will be removed from the output - any
   * indirect consequences will, however, still be visible.
   */
  def setEnableFilteringOfHiddenInfNodes(enable: Boolean) {
    JenaParameters.enableFilteringOfHiddenInfNodes = enable
    this
  }

  /**
   * If this flag is true (default) then attmempts to build an OWL inference
   * graph over another OWL inference graph will log a warning message.
   */
  def setEnableOWLRuleOverOWLRuleWarnings(enable: Boolean) {
    JenaParameters.enableOWLRuleOverOWLRuleWarnings = enable
    this
  }

  /**
   * If this flag is true (default is false) then bNodes are assigned a
   * simple count local to this JVM. This is ONLY for use in debugging
   * systems exhibiting non-deterministic behaviour due to the
   * time-dependence of UIDs, not for normal production use. In particular, it
   * breaks the contract that anonIDs should be unique on the same machine: they
   * will only be unique for this single JVM run.
   */
  def setDisableBNodeUIDGeneration(disable: Boolean) {
    JenaParameters.disableBNodeUIDGeneration = disable
    this
  }

  /**http://openjena.org/wiki/RIOT  */
  def useRIOTReaders = {
    SysRIOT.wireIntoJena()
    this
  }

  /**http://openjena.org/wiki/RIOT  */
  def useJenaReaders = {
    SysRIOT.resetJenaReaders()
    this
  }

  def registerImplementation[V <: RDFNode](impl: SemachinaImplementation[V])(implicit m: Manifest[V]): SemachinaConfiguration = {
    impl.supportedClasses.foreach {
      supportedClass: Class[_] => registerImplementation(supportedClass.asInstanceOf[Class[RDFNode]], impl)
    }
    return this
  }


  def registerImplementation[V <: RDFNode](interface: Class[V], impl: Implementation) = {
    personality.add(interface, impl)
    this
  }

  def getImplementation[V <: RDFNode](interface: Class[V]) = personality.getImplementation(interface)

  def registerDatatype[L <: Object](datatypeFactory: SemachinaBaseDatatype[L]) = {
    typeMapper.registerDatatype(datatypeFactory)
    this
  }

  def registerDatatype[L <: Object](
                                     typeURI: String,
                                     javaClass: Class[L] = null,
                                     lexer: L => String = {
                                       (it: L) => it.toString
                                     },
                                     parser: String => L,
                                     validator: L => Boolean = null)(implicit m: Manifest[L]) = {

    var clazz = javaClass
    if (clazz == null) {
      clazz = m.erasure.asInstanceOf[Class[L]]
    }

    val dataType = new SemachinaBaseDatatype[L](typeURI, parser, lexer, validator)
    typeMapper.registerDatatype(dataType)
    this
  }

  //(URI -> AltEntry)
  def addAltEntry(prefixes: Pair[String, String]*) = {
    prefixes.foreach {
      pair => ontDocumentManager.addAltEntry(pair._1, pair._2)
    }
    this
  }

  //(Prefix -> URI )
  def setNsPrefix(prefixes: Pair[String, String]*) = {
    prefixes.foreach {
      pair => prefixMapping.setNsPrefix(pair._1, pair._2)
    }
    this
  }

  def expandPrefix(shortURI: String) = prefixMapping.expandPrefix(shortURI)

  def shortForm(longURI: String) = prefixMapping.shortForm(longURI)

  def load(uri: String, ontModel: OntModel = null): OntModel = {

    //establish working model
    var workingModel = ontModel
    if (ontModel == null) {
      workingModel = SemachinaBuilder().build
    }

    //load import and return model
    ontDocumentManager.loadImport(workingModel, uri)
    workingModel.setNsPrefixes(prefixMapping)

    return workingModel
  }
}