package com.hp.hpl.jena.datatypes.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.DatatypeFormatException
import com.hp.hpl.jena.shared.impl.JenaParameters
import _root_.java.lang.Integer

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class PositiveIntegerSpecification extends SpecificationWithJUnit("xsd:positiveInteger Datatype Specification") {
  description = "Evaluate the functionality for the Jena typed literal xsd:positiveInteger datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:positiveInteger mapping that " in {
      "can create xsd:positiveInteger literals from primative int" in {
        val positiveInteger: Int = 1
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can create xsd:positiveInteger literals from primative short" in {
        val positiveInteger: Short = 1
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can create xsd:positiveInteger literals from primative byte" in {
        val positiveInteger: Byte = 1
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can create xsd:positiveInteger literals from primative long" in {
        val positiveInteger: Long = 1
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can create xsd:positiveInteger literals from java.lang.Integer" in {
        val positiveInteger = java.lang.Integer.valueOf(1)
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can create xsd:positiveInteger literals from primative short" in {
        val positiveInteger = java.lang.Short.valueOf(1.toShort)
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can create xsd:positiveInteger literals from java.lang.Byte" in {
        val positiveInteger = java.lang.Byte.valueOf(1.toByte)
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can create xsd:positiveInteger literals from java.lang.Long" in {
        val positiveInteger = java.lang.Long.valueOf(1.toLong)
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
        literal.getValue mustEqual positiveInteger.intValue
      }
      "can marshall xsd:positiveInteger literals as Integer" in {
        val positiveInteger = java.lang.Long.valueOf(1.toLong)
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[Integer]
      }
      "can marshall xsd:positiveInteger literals as Long (if longer than Integer.MAX_VALUE)" in {
        val positiveInteger = Integer.MAX_VALUE.toLong + 1
        val literal = m.createTypedLiteral(positiveInteger, XSD.positiveInteger.getURI)
        literal.getValue must haveClass[java.lang.Long]
      }
      "should fail when parsing xsd:positiveInteger literals from others objects" in {
        val negative = -1
        m.createTypedLiteral(negative, XSD.positiveInteger.getURI) must throwA[DatatypeFormatException]

      }
      "should fail when parsing xsd:positiveInteger literals from bad string and type" in {
        val negativeStr = "-1"
        m.createTypedLiteral(negativeStr, XSD.positiveInteger.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
