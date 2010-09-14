package org.semachina.jena.config

import com.hp.hpl.jena.shared.PrefixMapping
import org.semachina.xml.types._
import org.semachina.xml.utils.XMLDateUtils
import java.util.Date
import com.hp.hpl.jena.shared.impl.{JenaParameters}
import com.hp.hpl.jena.enhanced.BuiltinPersonalities
import com.hp.hpl.jena.ontology.{Individual, OntClass}
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.vocabulary.{RDFS, XSD}
import javax.xml.namespace.QName
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.core._

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

  def init(): Unit = {

    JenaParameters.enableEagerLiteralValidation = true
    JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = false

    PelletReasoning.initPellet

    BuiltinPersonalities.model
            .add(classOf[Individual], ScalaIndividualImpl.factory)
            .add(classOf[Literal], ScalaLiteralImpl.factory)
            .add(classOf[OntClass], ScalaOntClassImpl.factory)


    ScalaRDFDatatype(
      typeURI = XSD.date.getURI,
      lexer = {(value: Date) => XMLDateUtils.toLexicalDate(value)},
      parser = {lexicalForm => XMLDateUtils.parseDate(lexicalForm)})

    ScalaRDFDatatype(
      typeURI = XSD.dateTime.getURI,
      lexer = {(value: DateTime) => ISODateTimeFormat.dateTime().print(value)},
      parser = {lexicalForm => ISODateTimeFormat.dateTime().parseDateTime(lexicalForm)})

    ScalaRDFDatatype(
      typeURI = XSD.gDay.getURI,
      parser = lexicalForm => new Day(lexicalForm))

    //    ScalaRDFDatatype(
    //      typeURI = XSD.decimal.getURI,
    //      parser = lexicalForm => new java.math.BigDecimal( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.xdouble.getURI,
    //      parser = lexicalForm => java.lang.Double.valueOf( lexicalForm ) )

    ScalaRDFDatatype(
      typeURI = XSD.duration.getURI,
      parser = lexicalForm => new Duration(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.ENTITY.getURI,
      parser = lexicalForm => new Entity(lexicalForm))

    //    ScalaRDFDatatype(
    //      typeURI = XSD.xfloat.getURI,
    //      parser = lexicalForm => java.lang.Float.valueOf( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.hexBinary.getURI,
    //      parser = lexicalForm => new HexBinary( lexicalForm ) )

    ScalaRDFDatatype(
      typeURI = XSD.ID.getURI,
      parser = lexicalForm => new Id(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.IDREF.getURI,
      parser = lexicalForm => new IDRef(lexicalForm))

    //    ScalaRDFDatatype(
    //      typeURI = XSD.xint.getURI,
    //      parser = lexicalForm => java.lang.Integer.valueOf( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.integer.getURI,
    //      parser = lexicalForm => java.lang.Integer.valueOf( lexicalForm ) )

    ScalaRDFDatatype(
      typeURI = XSD.language.getURI,
      parser = lexicalForm => new Language(lexicalForm))

    ScalaRDFDatatype(
      typeURI = RDFS.Literal.getURI,
      parser = lexicalForm => String.valueOf(lexicalForm))
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.xlong.getURI,
    //      parser = lexicalForm => java.lang.Long.valueOf( lexicalForm ) )

    ScalaRDFDatatype(
      typeURI = XSD.gMonth.getURI,
      parser = lexicalForm => new Month(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.gMonthDay.getURI,
      parser = lexicalForm => new MonthDay(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.Name.getURI,
      parser = lexicalForm => new Name(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.NCName.getURI,
      parser = lexicalForm => new NCName(lexicalForm))

    //    ScalaRDFDatatype[java.lang.Long](
    //      typeURI = XSD.negativeInteger.getURI,
    //      parser = lexicalForm => java.lang.Long.parseLong( lexicalForm ),
    //      validator = (value: java.lang.Long) => value.longValue < 0)

    ScalaRDFDatatype(
      typeURI = XSD.NMTOKEN.getURI,
      parser = lexicalForm => new NMToken(lexicalForm))

    //    ScalaRDFDatatype(
    //      typeURI = XSD.nonNegativeInteger.getURI,
    //      parser = lexicalForm => new NonNegativeInteger( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.nonPositiveInteger.getURI,
    //      parser = lexicalForm => new NonPositiveInteger( lexicalForm ) )

    ScalaRDFDatatype(
      typeURI = XSD.normalizedString.getURI,
      parser = lexicalForm => new NormalizedString(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.NOTATION.getURI,
      parser = lexicalForm => throw new UnsupportedOperationException())

    //    ScalaRDFDatatype(
    //      typeURI = XSD.positiveInteger.getURI,
    //      parser = lexicalForm => new PositiveInteger( lexicalForm ) )

    ScalaRDFDatatype(
      typeURI = XSD.QName.getURI,
      parser = lexicalForm => QName.valueOf(lexicalForm))

    //    ScalaRDFDatatype[java.lang.Short] (
    //      typeURI = XSD.xshort.getURI,
    //      parser = lexicalForm => java.lang.Short.parseShort( lexicalForm ) )
    //
    //    ScalaRDFDatatype(
    //      typeURI = XSD.xstring.getURI,
    //      parser = lexicalForm => lexicalForm )

    ScalaRDFDatatype(
      typeURI = XSD.time.getURI,
      parser = lexicalForm => new Time(lexicalForm))

    ScalaRDFDatatype(
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

    ScalaRDFDatatype(
      typeURI = XSD.anyURI.getURI,
      parser = lexicalForm => java.net.URI.create(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.gYear.getURI,
      parser = lexicalForm => new Year(lexicalForm))

    ScalaRDFDatatype(
      typeURI = XSD.gYearMonth.getURI,
      parser = lexicalForm => new YearMonth(lexicalForm))
  }


}