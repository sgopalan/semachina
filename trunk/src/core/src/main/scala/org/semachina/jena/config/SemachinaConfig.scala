package org.semachina.jena.config


import com.hp.hpl.jena.graph.Node
import java.lang.Exception
import com.hp.hpl.jena.rdf.model.{Literal, Property, Model, RDFNode}
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.datatypes.RDFDatatype
import org.semachina.jena.config.features.larq3.Larq3Feature
import org.semachina.jena.config.features.pellet.PelletFeature
import com.hp.hpl.jena.ontology._
import scala.collection.JavaConversions._
import org.semachina.jena.wrapper.{ExtendedIteratorWrapper}
import com.hp.hpl.jena.util.iterator.ExtendedIterator
import com.hp.hpl.jena.enhanced.{EnhNode, Personality, EnhGraph}
import com.hp.hpl.jena.sparql.path.Path
import org.semachina.jena.{SemachinaIndividualTrait, SemachinaOntModelTrait}
import org.semachina.jena.impl.scala._
import org.scalatest.matchers.MustMatchers.AnyRefMustWrapper
import org.semachina.jena.datatype.SimpleRDFDatatype

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jul 30, 2010
 * Time: 8:41:13 AM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaConfig {

  //calls init to set everything up
  //init()

//  def registerDatatype[L <: Object](
//          typeURI: String,
//          javaClass: Class[L] = null,
//          lexer: L => String = {(it: L) => it.toString},
//          parser: String => L,
//          validator: L => Boolean = null)(implicit m: Manifest[L]) = {
//
//    var clazz = javaClass
//    if (clazz == null) {
//      clazz = m.erasure.asInstanceOf[Class[L]]
//    }
//
//    val dataType = new SimpleRDFDatatype[L](typeURI, parser, lexer, validator)
//    getTypeMapper.registerDatatype(dataType)
//  }
//
//  override def initImplementationClasses(model: Personality[RDFNode]): Unit = {
//    val createInstance = (node: Node, eg: EnhGraph) => new SemachinaIndividualImpl(node, eg)
//
//    model.add(classOf[Individual], DefaultImplementationTrait[SemachinaIndividualImpl](createInstance, "", classOf[Individual]))
//  }

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
  def createOntologyModel: SemachinaOntModelImpl = {
    try {
      return new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG))
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

  def createOntologyModel(base: Model): SemachinaOntModelImpl = {
    try {
      return new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG), base)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }

  def createOntologyModel(ontModelSpec: OntModelSpec, base: Model): SemachinaOntModelImpl = {
    try {
      return new SemachinaOntModelImpl(ontModelSpec, base)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }



  /**
   * Implicitly converts a Jena <code>ExtendedIteratorWrapper</code> to a Scala <code>ExtendedIteratorWrapper</code>.
   * The returned Scala <code>Iterator</code> is backed by the provided Java
   * <code>Iterator</code> and any side-effects of using it via the Scala interface will
   * be visible via the Java interface and vice versa.
   * <PROP>
   * If the Java <code>Iterator</code> was previously obtained from an implicit or
   * explicit call of <code>asIterator(scala.collection.Iterator)</code> then the original
   * Scala <code>Iterator</code> will be returned.
   *
   * @param i The <code>Iterator</code> to be converted.
   * @return A Scala <code>Iterator</code> view of the argument.
   */
  implicit def extendedIterator2Wrapper[A](i: ExtendedIterator[A]): ExtendedIteratorWrapper[A] = ExtendedIteratorWrapper(i)

  implicit def extendedWrapper2Iterator[A](wrapper: ExtendedIteratorWrapper[A]): ExtendedIterator[A] = wrapper.i

  implicit def toSemachinaOntModelTrait(ontModel: OntModel) : SemachinaOntModelTrait = ontModel match {
      case scala:SemachinaOntModelTrait =>  scala
      case _ =>  SemachinaOntModelAdapter( ontModel )
  }

  implicit def toOntModel(adapter : SemachinaOntModelAdapter) : OntModel = adapter.ontModel

  implicit def toScalaIndividualTrait(i: Individual) : SemachinaIndividualTrait = i match {
      case scala:SemachinaIndividualTrait =>  scala
      case _ =>  SemachinaIndividualAdapter( i )
  }

  implicit def toIndividual(adapter : SemachinaIndividualAdapter) : Individual = adapter.individual

  implicit def toValuesWrapper(values: Iterable[AnyRef])(implicit ontModel: OntModel) = new {
    def ^^ = values map { case value : AnyRef => ontModel.createTypedLiteral(value) }

    def ^^(dtype: String) = values map { case value : AnyRef => ontModel.resolveTypedLiteral(value, dtype) }

    def ^^(dtype: RDFDatatype) = values map { case value : AnyRef => ontModel.createTypedLiteral(value, dtype) }

    def ^^? = values map { case value : AnyRef => ontModel.parseTypedLiteral(value.toString) }
  }

  implicit def toValueWrapper(value: AnyRef)(implicit ontModel: OntModel) = new {
    def ^^ = ontModel.createTypedLiteral(value)

    def ^^(dtype: String) = ontModel.resolveTypedLiteral(value, dtype)

    def ^^(dtype: RDFDatatype) = ontModel.createTypedLiteral(value, dtype)

    def ^^? = ontModel.parseTypedLiteral(value.toString)
  }


  private implicit def asOption[A](value: A): Option[A] = Option(value)

  implicit def toTypedTuple(pair: Pair[Any, Any])(implicit ontModel: OntModel): Pair[Property, RDFNode] = {
    def ontProperty = asProperty(pair._1, ontModel)
    def node = asRDFNode(pair._2, ontModel)
    Pair(ontProperty, node)
  }

  protected def asProperty(ontProperty: Any, ontModel: OntModel): Property = {

    if (ontProperty.isInstanceOf[Property]) {
      return ontProperty.asInstanceOf[Property]
    }
    else if (ontProperty.isInstanceOf[String]) {
      val prop = ontModel.resolveProperty(ontProperty.asInstanceOf[String])
      return prop
    }
    else {
      throw new IllegalArgumentException("Must be String or Property: " + ontProperty)
    }
  }

  protected def asRDFNode(value: Any, ontModel: OntModel): RDFNode = {
    if (value.isInstanceOf[RDFNode]) {
      value.asInstanceOf[RDFNode]
    }
    else {
      ontModel.createTypedLiteral(value)
    }
  }

  implicit def toProperty(ontProperty:String)(implicit m : OntModel) : Property = m.resolveProperty(ontProperty)

  implicit def toScalaLiteral(rdfNode: RDFNode): Literal = rdfNode.asLiteral

  implicit def toQuerySolution(map: Map[String, RDFNode]): QuerySolution = {
    var initialBindings: QuerySolutionMap = new QuerySolutionMap
    map.foreach {kv => initialBindings.add(kv._1, kv._2)}
    return initialBindings
  }

  implicit def toURIWrapper(uri: String)(implicit ontModel: OntModel) = new {

    def toQuery(): Query = {
      var query: Query = new Query
      query.getPrefixMapping.withDefaultMappings(ontModel)
      query = QueryFactory.parse(query, uri, null, Syntax.defaultSyntax)
      return query
    }
  }


  //  implicit def toValueWrapper(value:Any)(implicit ontModel: OntModel) = new ValueWrapper( value, ontModel )


  implicit def toLarq3Feature(implicit ontModel: OntModel) : Larq3Feature =
    ontModel.getFeature(Larq3Feature.KEY).asInstanceOf[Larq3Feature]

  implicit def toPelletFeature(implicit ontModel: OntModel) : PelletFeature =
    ontModel.getFeature(PelletFeature.KEY).asInstanceOf[PelletFeature]

  def addAltEntry(docURI: String, locationURL: String): Unit =
    OntDocumentManager.getInstance.addAltEntry(docURI, locationURL)

  //get class
  def $(uri: String)(implicit ontModel: OntModel) = ontModel.resolveOntClass(uri)

  def $(uris: String*)(implicit ontModel: OntModel) =
    uris.collect {case uri: String => ontModel.resolveOntClass(uri)}

  //Reference uri
  def &(uri: String)(implicit ontModel: OntModel) = ontModel.resolveIndividual(uri)

  def &(uris: String*)(implicit ontModel: OntModel) =
    uris.collect {case uri: String => ontModel.resolveIndividual(uri)}

  //createIndividual object
  def +&(uriAndClazzes: Tuple2[String, Iterable[OntClass]])(implicit ontModel: OntModel) =
    toSemachinaOntModelTrait( ontModel ).createIndividual(uriAndClazzes._1, asIterable(uriAndClazzes._2))

  def +&(uri:String, clazzesStr: String*)(implicit ontModel: OntModel) = {
    val clazzes = clazzesStr.collect {case clazz: String => ontModel.resolveOntClass(clazz)}
    toSemachinaOntModelTrait( ontModel ).createIndividual(uri, asIterable(clazzes))
  }

  def +&(cls: String)(implicit ontModel: OntModel) = ontModel.createIndividual(ontModel.resolveOntClass(cls))
}