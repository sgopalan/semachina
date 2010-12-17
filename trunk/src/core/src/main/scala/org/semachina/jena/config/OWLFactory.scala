package org.semachina.jena.config

import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.shared.PrefixMapping
import org.mindswap.pellet.jena.PelletReasonerFactory
import org.apache.lucene.store.{Directory, RAMDirectory}
import com.hp.hpl.jena.datatypes.{TypeMapper, RDFDatatype, DatatypeFormatException}
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl
import com.hp.hpl.jena.graph.impl.{LiteralLabelFactory, LiteralLabel}
import com.hp.hpl.jena.rdf.model.{Literal, ModelMaker, Model}
import scala.collection.JavaConversions._
import com.hp.hpl.jena.enhanced.EnhNode
import com.hp.hpl.jena.ontology._
import org.semachina.jena.impl._
import org.semachina.jena._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 9, 2010
 * Time: 7:40:02 PM
 * To change this template use File | Settings | File Templates.
 */

object OWLFactory {
//  val PREFIX: PrefixMapping = PrefixMapping.Factory.create
//          .setNsPrefixes(PrefixMapping.Standard)
//
//  def getOntDocumentManager(): OntDocumentManager = OntDocumentManager.getInstance
//
//
//
//  @throws(classOf[DatatypeFormatException])
//  def toRDFDatatype(typeURI: String): RDFDatatype = {
//    val expanded = OWLFactory.PREFIX.expandPrefix(typeURI)
//    val dType = TypeMapper.getInstance.getTypeByName(expanded)
//    if (dType == null) {
//      var message = new StringBuilder();
//      message.append(typeURI + " does not match to a valid RDFDataype. Valid URIs include: ")
//      TypeMapper.getInstance.listTypes.foreach {
//        dType =>
//          message.append("\t" + dType.getURI + " with class: " + dType.getJavaClass + "\n")
//      }
//
//      throw new DatatypeFormatException(message.toString);
//    }
//    return dType
//  }
//
//  /**
//   * Build a typed literal from its value form.
//   *
//   * @param value the value of the literal
//   * @param dtype the type of the literal, null for old style "typed" literals
//   */
//  def createTypedLiteral(value: Any, dType: RDFDatatype): Literal = {
//    var ll: LiteralLabel = LiteralLabelFactory.create(value, "", dType)
//    return new LiteralImpl(Node.createLiteral(ll), null)
//  }
//
//  /**
//   * Build a typed literal label from its value form using
//   * whatever datatype is currently registered to the the default
//   * representation for this java class. No language tag is supplied.
//   * @param value the literal value to encapsulate
//   */
//  def createTypedLiteral(value: Any): Literal = {
//    var dType: RDFDatatype = TypeMapper.getInstance.getTypeByValue(value);
//    return createTypedLiteral(value, dType);
//  }
//
//  /**
//   * Build a typed literal from its lexical form. The
//   * lexical form will be parsed now and the value stored. If
//   * the form is not legal this will throw an exception.
//   *
//   * @param lex the lexical form of the literal
//   * @param typeURI the uri of the type of the literal, null for old style "typed" literals
//   * @throws DatatypeFormatException if lex is not a legal form of dtype
//   */
//  def createTypedLiteral(value:Any, typeURI: String): Literal = {
//    def expandedURI = OWLFactory.PREFIX.expandPrefix( typeURI )
//
//    var dt: RDFDatatype = TypeMapper.getInstance.getSafeTypeByName(expandedURI)
//    var ll: LiteralLabel = null;
//    if( value.isInstanceOf[String] ) {
//      ll = LiteralLabelFactory.createLiteralLabel(value.asInstanceOf[String], "", dt)
//    }
//    else {
//      ll = LiteralLabelFactory.create(value, "", dt)
//    }
//
//    return new LiteralImpl(Node.createLiteral(ll), null)
//  }
//
//  def createLiteral(literalString: String, lang: String = ""): Literal = {
//    return new LiteralImpl(Node.createLiteral(literalString, lang, false), null)
//  }
//
//  def parseTypedLiteral(literalString: String): Literal = {
//    var literalParts: Array[String] = literalString.split("\\^\\^")
//    if (literalParts.length == 2) {
//      val dtype = TypeMapper.getInstance.getSafeTypeByName(literalParts(1))
//      if (dtype != null) {
//        return createTypedLiteral(literalParts(0), dtype)
//      }
//    }
//    return new LiteralImpl(Node.createLiteral(literalString, "", false), null)
//  }
//
//  def toTypedDatatypeProperty[V](ontProperty:DatatypeProperty) : TypedDatatypeProperty[V] = {
//    val enhNode = ontProperty.asInstanceOf[EnhNode]
//    return new ScalaDatatypePropertyImpl[V]( enhNode.asNode, enhNode.getGraph )
//  }
//
//  def toTypedDatatypeProperty[V](ontProperty:DatatypeProperty, convert : Literal => V = null ) : TypedDatatypeProperty[V] = {
//    if( convert != null ) {
//      return toTypedDatatypeProperty[V](ontProperty)
//    }
//
//    val enhNode = ontProperty.asInstanceOf[EnhNode]
//    return new ScalaDatatypePropertyImpl[V]( enhNode.asNode, enhNode.getGraph, convert )
//  }
//
//
//  def createScalaIndividual(newUri : String, clazzes : Iterable[SemachinaOntClass], ontModel:SemachinaOntModel) : SemachinaIndividual = {
//    val expandedURI = ontModel.expandPrefix( newUri )
//
//    if( ontModel.containsResource( ontModel.getResource( expandedURI ) ) ) {
//      throw new IllegalArgumentException()
//    }
//
//    val indiv = clazzes.head.createIndividual( expandedURI )
//    clazzes.foreach { indiv.setRDFType }
//    return indiv.asInstanceOf[SemachinaIndividual]
//  }
//
//
//  def createSemachinaOntClass(ontClass: OntClass): SemachinaOntClass = {
//    if (ontClass.isInstanceOf[SemachinaOntClass]) {
//      return ontClass.asInstanceOf[SemachinaOntClass];
//    }
//    return new SemachinaOntClassImpl(ontClass);
//  }

  def registerDatatype[L <: Object](
           typeURI: String,
           javaClass: Class[L] = null,
           lexer: L => String = {(it: L) => it.toString},
           parser: String => L,
           validator: L => Boolean = null)(implicit m: Manifest[L]) = {
    var clazz = javaClass
    if (clazz == null) {
      clazz = m.erasure.asInstanceOf[Class[L]]
    }

    val dataType = new ScalaRDFDatatype[L](typeURI, clazz, lexer, parser, validator)

    //should move this out
    TypeMapper.getInstance.registerDatatype(dataType);
  }

}