package org.semachina.jena

import com.hp.hpl.jena.util.iterator.ExtendedIterator
import ontology.impl.{SemachinaIndividualAdapter, SemachinaOntModelAdapter}
import org.semachina.jena.utils.ExtendedIteratorWrapper
import org.semachina.jena.utils.ExtendedIteratorWrapper._
import org.semachina.jena.ontology.impl.SemachinaOntModelAdapter._
import org.semachina.jena.ontology.{SemachinaIndividual, SemachinaOntModel}
import com.hp.hpl.jena.datatypes.RDFDatatype
import com.hp.hpl.jena.rdf.model.{Literal, RDFNode, Property}
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.ontology.{Individual, OntModel}
import scala.collection.JavaConversions._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 9/9/11
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaDSL extends SemachinaDSL

trait SemachinaDSL {
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

  implicit def toSemachinaOntModel(ontModel: OntModel) : SemachinaOntModel = ontModel match {
      case scala:SemachinaOntModel =>  scala
      case _ =>  SemachinaOntModelAdapter( ontModel )
  }

  implicit def toScalaIndividual(i: Individual) : SemachinaIndividual = i match {
      case scala:SemachinaIndividual =>  scala
      case _ =>  SemachinaIndividualAdapter( i )
  }

  implicit def asOption[A](value: A): Option[A] = Option(value)

  implicit def toOntModel(adapter : SemachinaOntModelAdapter) : OntModel = adapter.ontModel

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

  implicit def toQueryWrapper(uri: String)(implicit ontModel: OntModel) = new {
    val semachinaOntModel =  toSemachinaOntModel( ontModel )

    def toQuery(): Query = {
      var query: Query = new Query
      query.getPrefixMapping.withDefaultMappings(ontModel)
      query = QueryFactory.parse(query, uri, null, Syntax.defaultSyntax)
      return query
    }

    def select(resultHandler: (ResultSet, QuerySolution) => Unit, initialBindings: QuerySolution) =
      semachinaOntModel.select( toQuery(), resultHandler, initialBindings  )
  }

  implicit def resolverWrapper(uri : String)(implicit ontModel: OntModel) = new {
    val semachinaOntModel =  toSemachinaOntModel( ontModel )

    def ontClass = semachinaOntModel.resolveOntClass(uri)

    def property = semachinaOntModel.resolveProperty(uri)

    def indiv = semachinaOntModel.resolveIndividual(uri)

    def withTypes(clazzes : String*) : Individual =
      semachinaOntModel.createIndividual( uri, semachinaOntModel.resolveOntClasses( clazzes.toIterable ) )

    def &() = semachinaOntModel.resolveIndividual(uri)

    def +&(clazzes : String*) : Individual =
      semachinaOntModel.createIndividual( uri, semachinaOntModel.resolveOntClasses( clazzes.toIterable ) )
  }
}