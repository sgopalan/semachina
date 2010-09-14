package org.semachina.jena

import com.hp.hpl.jena.util.{iterator => jena}

import core._
import java.{lang => jl, util => ju}
import wrapper.ExtendedIteratorWrapper
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._
import scala.collection.JavaConversions._
import com.hp.hpl.jena.datatypes.{DatatypeFormatException, RDFDatatype, TypeMapper}

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
   * <p>
   * If the Java <code>Iterator</code> was previously obtained from an implicit or
   * explicit call of <code>asIterator(scala.collection.Iterator)</code> then the original
   * Scala <code>Iterator</code> will be returned.
   *
   * @param i The <code>Iterator</code> to be converted.
   * @return A Scala <code>Iterator</code> view of the argument.
   */
  implicit def extendedIterator2Wrapper[A](i: jena.ExtendedIterator[A]): ExtendedIteratorWrapper[A] = ExtendedIteratorWrapper(i)

  implicit def toScalaIndividualImpl(i: Individual): ScalaIndividualImpl = {
    if (i.isInstanceOf[ScalaIndividualImpl]) {
      return i.asInstanceOf[ScalaIndividualImpl]
    }
    return ScalaIndividualImpl(i);
  }

  implicit def toScalaOntModelImpl(ontModel: OntModel): ScalaOntModelImpl = {
    if (ontModel.isInstanceOf[ScalaOntModelImpl]) {
      return ontModel.asInstanceOf[ScalaOntModelImpl];
    }
    return ScalaOntModelImpl(ontModel);
  }

  implicit def toScalaOntClassImpl(ontClass: OntClass): ScalaOntClassImpl = {
    if (ontClass.isInstanceOf[ScalaOntClassImpl]) {
      return ontClass.asInstanceOf[ScalaOntClassImpl];
    }
    return ScalaOntClassImpl(ontClass);
  }

  implicit def toProperty(property: String)(implicit ontModel: OntModel): OntProperty = ontModel.expandToOntProperty(property)

  implicit def toClass(clazz: String)(implicit ontModel: OntModel): OntClass = ontModel.expandToOntClass(clazz)

  implicit def toObject(indiv: String)(implicit ontModel: OntModel): Individual = ontModel.expandToIndividual(indiv)

  implicit def toScalaLiteral(lit: Literal): ScalaLiteralImpl = {
    if (lit.isInstanceOf[ScalaLiteralImpl]) {
      return lit.asInstanceOf[ScalaLiteralImpl]
    }
    else {
      return ScalaLiteralImpl(lit);
    }
  }

  @throws(classOf[DatatypeFormatException])
  implicit def toRDFDatatype(typeURI: String): RDFDatatype = {
    val expanded = OWLFactory.PREFIX.expandPrefix(typeURI)
    val dType = TypeMapper.getInstance.getTypeByName(expanded)
    if (dType == null) {
      var message = new StringBuilder();
      message.append(typeURI + " does not match to a valid RDFDataype. Valid URIs include: ")
      TypeMapper.getInstance.listTypes.foreach {
        dType =>
          message.append("\t" + dType.getURI + " with class: " + dType.getJavaClass + "\n")
      }

      throw new DatatypeFormatException(message.toString);
    }
    return dType
  }

  implicit def toQuery(sparql: String)(implicit ontModel: OntModel): Query = {
    var query: Query = new Query
    query.getPrefixMapping.withDefaultMappings(ontModel)
    query = QueryFactory.parse(query, sparql, null, Syntax.defaultSyntax)
    return query
  }

  //implicit def toIndividual(rdfNode:RDFNode) : ScalaIndividualImpl = rdfNode.asIndividual

  //implicit def toLiteral(rdfNode:RDFNode) : ScalaLiteralImpl = rdfNode.asLiteral

  implicit def toQuerySolution(map: Map[String, RDFNode]): QuerySolution = {
    var initialBindings: QuerySolutionMap = new QuerySolutionMap
    map.foreach {kv => initialBindings.add(kv._1, kv._2)}
    return initialBindings
  }

  implicit def toStringLiteralWrapper(value: String): StringLiteralWrapper = new StringLiteralWrapper(value)

  implicit def toObjectLiteralWrapper(value: Any): ObjectLiteralWrapper = new ObjectLiteralWrapper(value)

  def addAltEntry(docURI: String, locationURL: String): Unit =
    OntDocumentManager.getInstance.addAltEntry(docURI, locationURL)
}