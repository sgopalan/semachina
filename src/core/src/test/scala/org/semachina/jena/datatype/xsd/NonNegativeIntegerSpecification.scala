package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.DatatypeFormatException
import com.hp.hpl.jena.shared.impl.JenaParameters

class NonNegativeIntegerSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  val m = ModelFactory.createOntologyModel()

  "Jena typed literal xsd:nonNegativeInteger datatype conversions" should {
    "can create xsd:nonNegativeInteger literals from primative int" in {
      val nonNegativeInteger: Int = 1
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can create xsd:nonNegativeInteger literals from primative short" in {
      val nonNegativeInteger: Short = 1
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can create xsd:nonNegativeInteger literals from primative byte" in {
      val nonNegativeInteger: Byte = 1
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can create xsd:nonNegativeInteger literals from primative long" in {
      val nonNegativeInteger: Long = 1
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can create xsd:nonNegativeInteger literals from java.lang.Integer" in {
      val nonNegativeInteger = java.lang.Integer.valueOf(1)
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can create xsd:nonNegativeInteger literals from primative short" in {
      val nonNegativeInteger = java.lang.Short.valueOf(1.toShort)
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can create xsd:nonNegativeInteger literals from java.lang.Byte" in {
      val nonNegativeInteger = java.lang.Byte.valueOf(1.toByte)
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can create xsd:nonNegativeInteger literals from java.lang.Long" in {
      val nonNegativeInteger = java.lang.Long.valueOf(1.toLong)
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
      literal.getValue mustEqual nonNegativeInteger.intValue
    }
    "can marshall xsd:nonNegativeInteger literals as Integer" in {
      val nonNegativeInteger = java.lang.Long.valueOf(1.toLong)
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[Integer]
    }
    "can marshall xsd:nonNegativeInteger literals as Long (if longer than Integer.MAX_VALUE)" in {
      val nonNegativeInteger = Integer.MAX_VALUE.toLong + 1
      val literal = m.createTypedLiteral(nonNegativeInteger, XSD.nonNegativeInteger.getURI)
      literal.getValue must haveClass[java.lang.Long]
    }
    "should fail when parsing xsd:nonNegativeInteger literals from others objects" in {
      val negative = -1
      m.createTypedLiteral(negative, XSD.nonNegativeInteger.getURI) must throwA[DatatypeFormatException]

    }
    "should fail when parsing xsd:nonNegativeInteger literals from bad string and type" in {
      val negativeStr = "-1"
      m.createTypedLiteral(negativeStr, XSD.nonNegativeInteger.getURI) must throwA[DatatypeFormatException]
    }
  }
}
