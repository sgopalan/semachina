package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import factory.{YearFactory, MonthFactory}
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import types.{Year, Month}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class YearSpecification extends SpecificationWithJUnit("Jena xsd:gYear datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:gYear typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype( new YearFactory() )
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:gYear mapping that " in {
      "can create xsd:gYear literals from org.ontModelAdapter.jena.datatype.types.Year object" in {
        val year = new Year(2010)
        val literal = m.createTypedLiteral(year, XSD.gYear.getURI)
        literal.getValue must beEqualTo(year)
      }
      "can create xsd:gYear literals from java.lang.Integer object" in {
        val yearInt = 2010
        val year = new Year(2010)
        val literal = m.createTypedLiteral(yearInt, XSD.gYear.getURI)
        literal.getValue must beEqualTo(year)
      }
      "can create xsd:gYear literals from string and type" in {
        val yearStr = "2010"
        val year = new Year(2010)
        val literal = m.createTypedLiteral(yearStr, XSD.gYear.getURI)
        literal.getValue must beEqualTo(year)
      }
      "should fail when parsing xsd:gYear literals from others objects" in {
        val day = new Object()
        m.createTypedLiteral(day, XSD.gYear.getURI) must throwA[IllegalArgumentException]
      }
      "should fail when parsing xsd:gYear literals from bad string and type" in {
        val yearMonthStr = "2001-12"
        m.createTypedLiteral(yearMonthStr, XSD.gYear.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
