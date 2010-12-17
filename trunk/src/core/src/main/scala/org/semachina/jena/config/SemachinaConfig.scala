package org.semachina.jena.config

import com.hp.hpl.jena.shared.PrefixMapping
import org.semachina.xml.types._
import org.semachina.xml.utils.XMLDateUtils
import java.util.Date
import com.hp.hpl.jena.shared.impl.{JenaParameters}
import com.hp.hpl.jena.vocabulary.{RDFS, XSD}
import javax.xml.namespace.QName
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.impl._
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry
import com.hp.hpl.jena.query.larq3.library.TextMatch
import com.hp.hpl.jena.enhanced.{EnhNode, EnhGraph, Implementation, BuiltinPersonalities}
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.rdf.model.{RDFNode, Literal}
import com.hp.hpl.jena.ontology._
import org.semachina.jena.impl.{SemachinaOntClassImpl, SemachinaTypedDatatypePropertyImpl, SemachinaResourcePropertyImpl, DefaultImplementationImpl}
import org.semachina.jena.{SemachinaIndividual, SemachinaOntClass, ResourceProperty}
import org.semachina.jena.config.OWLFactory._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jul 30, 2010
 * Time: 8:41:13 AM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaConfig {
  /**
  A PrefixMapping built on Standard with some extras
   */
  val PREFIX: PrefixMapping = PrefixMapping.Factory.create
          .setNsPrefixes(PrefixMapping.Standard)

  val factory = new DefaultSemachinaFactory()

  def init(): Unit = {

    //register function... This should happen in config, not here
    PropertyFunctionRegistry.get().put("http://jena.hpl.hp.com/ARQ/property#textMatch3", classOf[TextMatch])
    

    JenaParameters.enableEagerLiteralValidation = true
    JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = false

    factory.initImplementationClasses
    factory.initDatatypes

//    BuiltinPersonalities.model
//            .add(classOf[Individual],
//              new DefaultImplementationImpl[SemachinaIndividualImpl](
//                classOf[SemachinaIndividualImpl], classOf[Individual]) {
//                  def create(node: Node, eg: EnhGraph) = new SemachinaIndividualImpl( node, eg )
//              } )
//            .add(classOf[SemachinaIndividual],
//              new DefaultImplementationImpl[SemachinaIndividualImpl](
//                classOf[SemachinaIndividualImpl], classOf[Individual]) {
//                  def create(node: Node, eg: EnhGraph) = new SemachinaIndividualImpl( node, eg )
//              } )
//            .add(classOf[OntClass],
//              new DefaultImplementationImpl[SemachinaOntClassImpl](
//                classOf[SemachinaOntClassImpl], "It does not have rdf:type owl:Class or equivalent", classOf[OntClass]) {
//                  def create(node: Node, eg: EnhGraph) = new SemachinaOntClassImpl( node, eg )
//              } )
//            .add(classOf[SemachinaOntClass],
//              new DefaultImplementationImpl[SemachinaOntClassImpl](
//                classOf[SemachinaOntClassImpl], "It does not have rdf:type owl:Class or equivalent", classOf[OntClass]) {
//                  def create(node: Node, eg: EnhGraph) = new SemachinaOntClassImpl( node, eg )
//              } )
//            .add(classOf[DatatypeProperty],
//              new DefaultImplementationImpl[SemachinaTypedDatatypePropertyImpl[Nothing]](
//                classOf[SemachinaTypedDatatypePropertyImpl[Nothing]], classOf[DatatypeProperty]) {
//                  def create(node: Node, eg: EnhGraph) = new SemachinaTypedDatatypePropertyImpl[Nothing]( node, eg )
//              } )
//            .add(classOf[ResourceProperty],
//              new DefaultImplementationImpl[SemachinaResourcePropertyImpl](
//                classOf[SemachinaResourcePropertyImpl], classOf[ResourceProperty]) {
//                  def create(node: Node, eg: EnhGraph) = new SemachinaResourcePropertyImpl( node, eg )
//              } )


//    registerDatatype(
//      typeURI = XSD.date.getURI,
//      lexer = {(value: Date) => XMLDateUtils.toLexicalDate(value)},
//      parser = {lexicalForm => X
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
// LDateUtils.parseDate(lexicalForm)})

//    registerDatatype(
//      typeURI = XSD.dateTime.getURI,
//      lexer = {(value: DateTime) => ISODateTimeFormat.dateTime().print(value)},
//      parser = {lexicalForm => ISODateTimeFormat.dateTime().parseDateTime(lexicalForm)})

//    registerDatatype(
//      typeURI = XSD.time.getURI,
//      parser = lexicalForm => new Time(lexicalForm))

    registerDatatype(
      typeURI = XSD.gDay.getURI,
      parser = lexicalForm => new Day(lexicalForm))

    //    registerDatatype(
    //      typeURI = XSD.decimal.getURI,
    //      parser = lexicalForm => new java.math.BigDecimal( lexicalForm ) )
    //
    //    registerDatatype(
    //      typeURI = XSD.xdouble.getURI,
    //      parser = lexicalForm => java.lang.Double.valueOf( lexicalForm ) )

    registerDatatype(
      typeURI = XSD.duration.getURI,
      parser = lexicalForm => new Duration(lexicalForm))

    registerDatatype(
      typeURI = XSD.ENTITY.getURI,
      parser = lexicalForm => new Entity(lexicalForm))

    //    registerDatatype(
    //      typeURI = XSD.xfloat.getURI,
    //      parser = lexicalForm => java.lang.Float.valueOf( lexicalForm ) )
    //
    //    registerDatatype(
    //      typeURI = XSD.hexBinary.getURI,
    //      parser = lexicalForm => new HexBinary( lexicalForm ) )

    registerDatatype(
      typeURI = XSD.ID.getURI,
      parser = lexicalForm => new Id(lexicalForm))

    registerDatatype(
      typeURI = XSD.IDREF.getURI,
      parser = lexicalForm => new IDRef(lexicalForm))

    //    registerDatatype(
    //      typeURI = XSD.xint.getURI,
    //      parser = lexicalForm => java.lang.Integer.valueOf( lexicalForm ) )
    //
    //    registerDatatype(
    //      typeURI = XSD.integer.getURI,
    //      parser = lexicalForm => java.lang.Integer.valueOf( lexicalForm ) )

    registerDatatype(
      typeURI = XSD.language.getURI,
      parser = lexicalForm => new Language(lexicalForm))

    registerDatatype(
      typeURI = RDFS.Literal.getURI,
      parser = lexicalForm => String.valueOf(lexicalForm))
    //
    //    registerDatatype(
    //      typeURI = XSD.xlong.getURI,
    //      parser = lexicalForm => java.lang.Long.valueOf( lexicalForm ) )

    registerDatatype(
      typeURI = XSD.gMonth.getURI,
      parser = lexicalForm => new Month(lexicalForm))

    registerDatatype(
      typeURI = XSD.gMonthDay.getURI,
      parser = lexicalForm => new MonthDay(lexicalForm))

    registerDatatype(
      typeURI = XSD.Name.getURI,
      parser = lexicalForm => new Name(lexicalForm))

    registerDatatype(
      typeURI = XSD.NCName.getURI,
      parser = lexicalForm => new NCName(lexicalForm))

    //    registerDatatype[java.lang.Long](
    //      typeURI = XSD.negativeInteger.getURI,
    //      parser = lexicalForm => java.lang.Long.parseLong( lexicalForm ),
    //      validator = (value: java.lang.Long) => value.longValue < 0)

    registerDatatype(
      typeURI = XSD.NMTOKEN.getURI,
      parser = lexicalForm => new NMToken(lexicalForm))

    //    registerDatatype(
    //      typeURI = XSD.nonNegativeInteger.getURI,
    //      parser = lexicalForm => new NonNegativeInteger( lexicalForm ) )
    //
    //    registerDatatype(
    //      typeURI = XSD.nonPositiveInteger.getURI,
    //      parser = lexicalForm => new NonPositiveInteger( lexicalForm ) )

    registerDatatype(
      typeURI = XSD.normalizedString.getURI,
      parser = lexicalForm => new NormalizedString(lexicalForm))

    registerDatatype(
      typeURI = XSD.NOTATION.getURI,
      parser = lexicalForm => throw new UnsupportedOperationException())

    //    ScalaRDFDatatype(
    //      typeURI = XSD.positiveInteger.getURI,
    //      parser = lexicalForm => new PositiveInteger( lexicalForm ) )

    registerDatatype(
      typeURI = XSD.QName.getURI,
      parser = lexicalForm => QName.valueOf(lexicalForm))

    //    ScalaRDFDatatype[java.lang.Short] (
    //      typeURI = XSD.xshort.getURI,
    //      parser = lexicalForm => java.lang.Short.parseShort( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.xstring.getURI,
    //      parser = lexicalForm => lexicalForm )

    registerDatatype(
      typeURI = XSD.token.getURI,
      parser = lexicalForm => new Token(lexicalForm))

    //    ScalaRDFDatatype(
    //      typeURI = XSD.unsignedByte.getURI,
    //      parser = lexicalForm => new UnsignedByte( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.unsignedInt.getURI,
    //      parser = lexicalForm => new UnsignedInt( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.unsignedLong.getURI,
    //      parser = lexicalForm => new UnsignedLong( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.unsignedShort.getURI,
    //      parser = lexicalForm => new UnsignedShort( lexicalForm ) )

    registerDatatype(
      typeURI = XSD.anyURI.getURI,
      parser = lexicalForm => java.net.URI.create(lexicalForm))

    registerDatatype(
      typeURI = XSD.gYear.getURI,
      parser = lexicalForm => new Year(lexicalForm))

    registerDatatype(
      typeURI = XSD.gYearMonth.getURI,
      parser = lexicalForm => new YearMonth(lexicalForm))
  }
}