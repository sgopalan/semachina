package org.semachina.jena

import impl.SemachinaIndividualImpl._
import scala.reflect.Manifest
import scala.collection.JavaConversions._

import com.hp.hpl.jena.util.{iterator => jena}

import features.larq3.Larq3Feature
import features.pellet.PelletFeature
import impl._
import java.{lang => jl, util => ju}
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._

import com.hp.hpl.jena.datatypes.RDFDatatype
import org.semachina.jena.wrapper.ExtendedIteratorWrapper
import com.hp.hpl.jena.enhanced.EnhNode
import com.hp.hpl.jena.util.iterator.Map1
import com.hp.hpl.jena.util.iterator.ExtendedIterator
import com.hp.hpl.jena.util.iterator.WrappedIterator
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.ontology.OntProperty
import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.rdf.model.NodeIterator
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.RDFNode
import com.hp.hpl.jena.rdf.model.Statement
import com.hp.hpl.jena.util.iterator.ExtendedIterator
import com.hp.hpl.jena.util.iterator.Map1
import com.hp.hpl.jena.util.iterator.WrappedIterator
import java.util.Iterator
import com.hp.hpl.jena.sparql.path.Path
import com.hp.hpl.jena.sparql.path.PathParser
import com.hp.hpl.jena.sparql.path.PathEval
import com.hp.hpl.jena.shared.PrefixMapping

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jun 29, 2010
 * Time: 8:37:05 AM
 * To change this template use File | Settings | File Templates.
 */


object JenaExtension {

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
  implicit def extendedIterator2Wrapper[A](i: jena.ExtendedIterator[A]): ExtendedIteratorWrapper[A] = ExtendedIteratorWrapper(i)

  implicit def extendedWrapper2Iterator[A](wrapper: ExtendedIteratorWrapper[A]): jena.ExtendedIterator[A] = wrapper.i

  implicit def toSemachinaOntModel(ontModel: OntModel) = {
    if( ontModel.isInstanceOf[impl.scala.ScalaOntModelImpl]) {
      ontModel.asInstanceOf[impl.scala.ScalaOntModelImpl]
    }
    else {
      new impl.scala.ScalaOntModelImpl( ontModel.getSpecification, ontModel.getBaseModel )
    }
  }

  implicit def toScalaIndividual(i: Individual) = {
    if( i.isInstanceOf[impl.scala.ScalaIndividualImpl]) {
      i.asInstanceOf[impl.scala.ScalaIndividualImpl]
    }
    else {
      new impl.scala.ScalaIndividualImpl( i.asInstanceOf[EnhNode].asNode, i.asInstanceOf[EnhNode].getGraph )
    }
  }

  implicit def ?:[A](value: A): Option[A] = Option(value)

  implicit def toTypedTuple(pair: Pair[Any, Any])(implicit ontModel: SemachinaOntModel): Pair[Property, RDFNode] = {
    def ontProperty = asProperty(pair._1, ontModel)
    def node = asRDFNode(pair._2, ontModel)
    Pair(ontProperty, node)
  }

  protected def asProperty(ontProperty: Any, ontModel: SemachinaOntModel): Property = {
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

  protected def asRDFNode(value: Any, ontModel: SemachinaOntModel): RDFNode = {
    if (value.isInstanceOf[RDFNode]) {
      value.asInstanceOf[RDFNode]
    }
    else {
      ontModel.createTypedLiteral(value)
    }
  }

  implicit def toProperty(ontProperty:String)(implicit m : SemachinaOntModel) : Property = m.resolveProperty(ontProperty)

  implicit def toScalaOntClassImpl(ontClass: OntClass): SemachinaOntClass = ontClass.asInstanceOf[SemachinaOntClass]

  implicit def toScalaLiteral(rdfNode: RDFNode): Literal = rdfNode.asLiteral

  implicit def toQuerySolution(map: Map[String, RDFNode]): QuerySolution = {
    var initialBindings: QuerySolutionMap = new QuerySolutionMap
    map.foreach {kv => initialBindings.add(kv._1, kv._2)}
    return initialBindings
  }

  implicit def toURIWrapper(uri: String)(implicit ontModel: SemachinaOntModel) = new {

    def toQuery(): Query = {
      var query: Query = new Query
      query.getPrefixMapping.withDefaultMappings(ontModel)
      query = QueryFactory.parse(query, uri, null, Syntax.defaultSyntax)
      return query
    }
  }


  //  implicit def toValueWrapper(value:Any)(implicit ontModel: OntModel) = new ValueWrapper( value, ontModel )
  implicit def toValueWrapper(value: Any)(implicit ontModel: SemachinaOntModel) = new {
    def ^^ = ontModel.createTypedLiteral(value)

    def ^^(dtype: String) = ontModel.createTypedLiteral(value, dtype)

    def ^^(dtype: RDFDatatype) = ontModel.createTypedLiteral(value, dtype)

    def ^% = ontModel.parseTypedLiteral(value.toString)
  }

  implicit def toLarq3Feature(implicit ontModel: SemachinaOntModel) =
    ontModel.getFeature[Larq3Feature](Larq3Feature.KEY)

  implicit def toPelletFeature(implicit ontModel: SemachinaOntModel) =
    ontModel.getFeature[PelletFeature](PelletFeature.KEY)

  def addAltEntry(docURI: String, locationURL: String): Unit =
    OntDocumentManager.getInstance.addAltEntry(docURI, locationURL)

  //get class
  def $(uri: String)(implicit ontModel: SemachinaOntModel) = ontModel.resolveOntClass(uri)

  def $(uris: String*)(implicit ontModel: SemachinaOntModel) =
    uris.collect {case uri: String => ontModel.resolveOntClass(uri)}

  //Reference uri
  def &(uri: String)(implicit ontModel: SemachinaOntModel) = ontModel.resolveIndividual(uri)

  def &(uris: String*)(implicit ontModel: SemachinaOntModel) =
    uris.collect {case uri: String => ontModel.resolveIndividual(uri)}

  //createIndividual object
  def +&(uriAndClazzes: Tuple2[String, Iterable[OntClass]])(implicit ontModel: SemachinaOntModel) =
    ontModel.createIndividual(uriAndClazzes._1, asIterable(uriAndClazzes._2))

  def +&(uri:String, clazzesStr: String*)(implicit ontModel: SemachinaOntModel) = {
    val clazzes = clazzesStr.collect {case clazz: String => ontModel.resolveOntClass(clazz)}
    ontModel.createIndividual(uri, asIterable(clazzes))
  }

  def +&(cls: String)(implicit ontModel: SemachinaOntModel) = ontModel.createIndividual(ontModel.resolveOntClass(cls))

}