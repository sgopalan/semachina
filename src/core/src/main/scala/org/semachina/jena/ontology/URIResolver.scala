package org.semachina.jena.ontology

import scala.collection.JavaConversions._
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.datatypes.{DatatypeFormatException, TypeMapper, RDFDatatype}
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.rdf.model.{Literal, RDFNode, Property}
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/28/11
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */

object URIResolver {
  def apply(ontModel:OntModel) = {
    new URIResolver() {
      def getEnhGraph = ontModel.asInstanceOf[EnhGraph]
      def getOntModel = ontModel
    }
  }
}

trait URIResolver {

  def getOntModel: OntModel

  def getEnhGraph: EnhGraph

  def expandURI(uri: String): String = {
    var expandedURI: String = getOntModel.expandPrefix(uri)
    if (expandedURI == null) {
      throw new IllegalArgumentException("URI cannot be evaluated as null: " + uri)
    }
    return expandedURI
  }

  def resolveOntClass(uri: String): OntClass = {
    var expandedURI: String = expandURI(uri)
    var ontClass: OntClass = getOntModel.getOntClass(expandedURI)
    return ontClass
  }

  def resolveOntClasses(uris: java.util.Collection[String]): java.util.Collection[OntClass] = {
    val ontClazzes = uris map { case uri: String => resolveOntClass( uri ) }
    return ontClazzes
  }

  def resolveObjectProperty(property: String): ObjectProperty = {
    var uri: String = expandURI(property)
    var ontProperty: ObjectProperty = getOntModel.getObjectProperty(uri)
    return ontProperty
  }

  def resolveDatatypeProperty(property: String): DatatypeProperty = {
    var uri: String = expandURI(property)
    var ontProperty: DatatypeProperty = getOntModel.getDatatypeProperty(uri)
    return ontProperty
  }

  def resolveProperty(property: String): Property = {
    var uri: String = expandURI(property)
    var ontProperty: Property = findByURIAs[Property](uri)
    return ontProperty
  }

  def resolveOntProperty(property: String): OntProperty = {
    var uri: String = expandURI(property)
    var ontProperty: OntProperty = getOntModel.getOntProperty(uri)
    return ontProperty
  }

  def resolveRDFDatatype(typeURI: String): RDFDatatype = {
    var expanded: String = expandURI(typeURI)
    var typeMapper: TypeMapper = TypeMapper.getInstance
    var dType: RDFDatatype = typeMapper.getTypeByName(expanded)
    if (dType == null) {
      var message: StringBuilder = new StringBuilder
      message.append(typeURI + " does not match to a valid RDFDataype. Valid URIs include: ")

      typeMapper.listTypes().foreach {  otherDType =>
        message.append("\t" + otherDType.getURI + " with class: " + otherDType.getJavaClass + "\n")
      }

      throw new DatatypeFormatException(message.toString)
    }
    return dType
  }

  /**Answer the resource with the given URI, if present, as the given facet */
  def findByURIAs[T <: RDFNode](uri: String)(implicit m: Manifest[T]) : T = {
    if (uri == null) {
      throw new IllegalArgumentException("Cannot get() ontology value with a null URI")
    }
    var n: Node = Node.createURI(uri)
    if (getOntModel.getGraph.contains(n, Node.ANY, Node.ANY)) {
      try {
        return getEnhGraph.getNodeAs(n, m.erasure.asInstanceOf[Class[T]] )
      }
      catch {
        case ignore: ConversionException => {
        }
      }
    }
    return null.asInstanceOf[T]
  }

  def resolveTypedLiteral(value: AnyRef, typeURI: String): Literal = {
    var expandedURI: String = expandURI(typeURI)
    return getOntModel.createTypedLiteral(value, expandedURI)
  }

  def parseTypedLiteral(literalString: String): Literal = {
    var literalParts: Array[String] = literalString.split("\\^\\^")
    if (literalParts.length == 2) {
      var expandedURI: String = expandURI(literalParts(1))
      var dtype: RDFDatatype = TypeMapper.getInstance.getSafeTypeByName(expandedURI)
      if (dtype != null) {
        return getOntModel.createTypedLiteral(literalParts(0), dtype)
      }
    }
    return new LiteralImpl(Node.createLiteral(literalString, "", false), null)
  }


}