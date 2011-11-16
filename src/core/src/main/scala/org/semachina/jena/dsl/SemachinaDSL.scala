package org.semachina.jena.dsl

import com.hp.hpl.jena.util.iterator.ExtendedIterator
import org.semachina.jena.ontology._
import org.semachina.jena.ontology.impl.SemachinaOntModelAdapter
import com.hp.hpl.jena.rdf.model.{Literal, RDFNode, Property}
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.ontology.{Individual, OntModel}
import scala.collection.JavaConversions._
import org.semachina.jena.ontology.naming.IdStrategy
import com.hp.hpl.jena.sparql.path.Path
import org.semachina.jena.ontology.URIResolver

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 9/9/11
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaDSL extends SemachinaDSL

/**
 * @seeAlso http://www.scala-notes.org/2010/06/avoid-structural-types-when-pimping-libraries/
 */
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

  implicit def toSemachinaOntModel(ontModel: OntModel): SemachinaOntModel = ontModel match {
    case scala: SemachinaOntModel => scala
    case _ => SemachinaOntModelAdapter(ontModel)
  }

  implicit def toScalaIndividual(i: Individual) = SemachinaIndividual(i)

  implicit def asLiteralJavaIterable(values: _root_.java.lang.Iterable[Any])(implicit ontModel: OntModel) =
    values.collect {
      case value: Any => ontModel.createTypedLiteral(value)
    }

  implicit def asLiteralScalaIterable(values: Iterable[Any])(implicit ontModel: OntModel) =
    values.collect {
      case value: Any => ontModel.createTypedLiteral(value)
    }

  implicit def asLiteral(value: Any)(implicit ontModel: OntModel) = ontModel.createTypedLiteral(value)

  implicit def asOption[A](value: A): Option[A] = Option(value)

  implicit def toOntModel(adapter: SemachinaOntModelAdapter): OntModel = adapter.self

  implicit def toLiteralListTransformation(values: Iterable[Any])(implicit ontModel: OntModel) =
    new LiteralListConverter(values, toSemachinaOntModel(ontModel))

  implicit def toLiteralTransformation(value: Any)(implicit ontModel: OntModel) =
    new LiteralConverter(value, toSemachinaOntModel(ontModel))

  implicit def toRDFContainerWrapper(rdfNode: RDFNode)(implicit ontModel: OntModel) =
    new RDFContainerWrapper(rdfNode, ontModel)

  implicit def toRDFContainerListWrapper(rdfNodes: Traversable[RDFNode])(implicit ontModel: OntModel) =
    new RDFContainerListWrapper(rdfNodes, ontModel)

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

  protected def asOptionRDFNode[T](value: Option[T], ontModel: OntModel)(implicit m: Manifest[T]): Option[_ <: RDFNode] = {
    if (m.erasure.isAssignableFrom(classOf[RDFNode])) {
      value.asInstanceOf[Option[RDFNode]]
    }
    else {
      value.flatMap[RDFNode](obj => Option(ontModel.createTypedLiteral(obj)))
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

  //implicit def toProperty(ontProperty:String)(implicit m : OntModel) : Property = m.resolveProperty(ontProperty)

  implicit def toScalaLiteral(rdfNode: RDFNode): Literal = rdfNode.asLiteral

  implicit def toQuerySolution(map: Map[String, RDFNode]): QuerySolution = {
    var initialBindings: QuerySolutionMap = new QuerySolutionMap
    map.foreach {
      kv => initialBindings.add(kv._1, kv._2)
    }
    return initialBindings
  }

  implicit def toQueryWrapper(uri: String)(implicit ontModel: OntModel) =
    new ArqWrapper(uri, toSemachinaOntModel(ontModel))

  implicit def pathWrapper(path: Path) = new PathWrapper(path)

  implicit def propertyWrapper(property: Property) = new PropertyWrapper(property)

  implicit def toURIResolverWrapper(aUri: String)(implicit ontModel: OntModel) =
    URIResolver.toURIResolverWrapper(aUri)(ontModel)

  def +&(types: String*)(implicit ontModel: OntModel): SemachinaIndividual = {
    val semachina = toSemachinaOntModel(ontModel)
    semachina.createIndividual(null, semachina.resolveOntClasses(types.toIterable), false)
  }

  def +&(strategy: IdStrategy, types: String*)(implicit ontModel: OntModel): SemachinaIndividual = {
    val semachina = toSemachinaOntModel(ontModel)
    semachina.createIndividual(null, semachina.resolveOntClasses(types.toIterable), false, strategy)
  }
}