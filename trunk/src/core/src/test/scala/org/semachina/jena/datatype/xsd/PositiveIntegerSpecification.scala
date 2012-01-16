package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.DatatypeFormatException
import com.hp.hpl.jena.shared.impl.JenaParameters
import _root_.java.lang.Integer

class PositiveIntegerSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  val m = ModelFactory.createOntologyModel()

  "Jena typed literal xsd:positiveInteger datatype conversions" should {
    "create xsd:positiveInteger literals from primative int" in {
      val positiveInteger: Int = 1
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "create xsd:positiveInteger literals from primative short" in {
      val positiveInteger: Short = 1
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "create xsd:positiveInteger literals from primative byte" in {
      val positiveInteger: Byte = 1
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "create xsd:positiveInteger literals from primative long" in {
      val positiveInteger: Long = 1
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "create xsd:positiveInteger literals from java.lang.Integer" in {
      val positiveInteger = java.lang.Integer.valueOf(1)
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "create xsd:positiveInteger literals from primative short" in {
      val positiveInteger = java.lang.Short.valueOf(1.toShort)
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "create xsd:positiveInteger literals from java.lang.Byte" in {
      val positiveInteger = java.lang.Byte.valueOf(1.toByte)
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "create xsd:positiveInteger literals from java.lang.Long" in {
      val positiveInteger = java.lang.Long.valueOf(1.toLong)
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual positiveInteger.intValue
    }
    "marshall xsd:positiveInteger literals as Integer" in {
      val positiveInteger = java.lang.Long.valueOf(1.toLong)
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[Integer]
    }
    "marshall xsd:positiveInteger literals as Long (if longer than Integer.MAX_VALUE)" in {
      val positiveInteger = Integer.MAX_VALUE.toLong + 1
      val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
      literal.getValue must haveClass[java.lang.Long]
    }
    "fail when parsing xsd:positiveInteger literals from others objects" in {
      val negative = -1
      m.createTypedLiteral(negative, XSD.positiveInteger.getURI) must throwA[DatatypeFormatException]

    }
    "fail when parsing xsd:positiveInteger literals from bad string and type" in {
      val negativeStr = "-1"
      m.createTypedLiteral(negativeStr, XSD.positiveInteger.getURI) must throwA[DatatypeFormatException]
    }
  }
}
