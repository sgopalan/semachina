package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import factory.{MonthFactory, DayFactory}
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import types.{Month}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class MonthSpecification extends SpecificationWithJUnit("Jena xsd:gMonth datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:gMonth typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype( new MonthFactory() )
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:gMonth mapping that " in {
      "can create xsd:gMonth literals from org.semachina.jena.datatype.types.Month object" in {
        val month = new Month(1)
        val literal = m.createTypedLiteral(month, XSD.gMonth.getURI)
        literal.getValue must beEqualTo(month)
      }
      "can create xsd:gMonth literals from java.lang.Integer object" in {
        val monthInt = 1
        val month = new Month(1)
        val literal = m.createTypedLiteral(monthInt, XSD.gMonth.getURI)
        literal.getValue must beEqualTo(month)
      }
      "can create xsd:gMonth literals from string and type" in {
        val month = new Month(1)
        //this is apparently a bogus format due to errata in the spec.  This has been corrected
        //in the spec via addendum, but it may not have been corrected in all of the tooling.
        val monthStr = "--01--";
        val literal = m.createTypedLiteral(monthStr, XSD.gMonth.getURI)
        literal.getValue must beEqualTo(month)
      }
      "should fail when parsing xsd:gMonth literals from others objects" in {
        val day = new Object()
        m.createTypedLiteral(day, XSD.gMonth.getURI) must throwA[IllegalArgumentException]
      }
      "should fail when parsing xsd:gMonth literals from bad string and type" in {
        val dayStr = "12"
        m.createTypedLiteral(dayStr, XSD.gMonth.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
