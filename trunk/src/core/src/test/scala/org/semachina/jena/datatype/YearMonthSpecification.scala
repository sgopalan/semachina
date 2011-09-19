package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import xsd.YearMonthDatatype
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.YearMonth

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class YearMonthSpecification extends SpecificationWithJUnit("Jena xsd:gYearMonth datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:gYearMonth typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new YearMonthDatatype())
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:gYearMonth mapping that " in {
      "can create xsd:gYearMonth literals from org.ontModelAdapter.jena.datatype.types.MOnthDay object" in {
        val yearmonth = new YearMonth(2010, 12)
        val literal = m.createTypedLiteral(yearmonth, XSD.gYearMonth.getURI)
        literal.getValue must beEqualTo(yearmonth)
      }
      "can create xsd:gYearMonth literals from string and type" in {
        val yearmonth = new YearMonth(2010, 12)
        val yearmonthStr = "2010-12";
        val literal = m.createTypedLiteral(yearmonthStr, XSD.gYearMonth.getURI)
        literal.getValue must beEqualTo(yearmonth)
      }
      "should fail when parsing xsd:gYearMonth literals from others objects" in {
        val day = new Object()
        m.createTypedLiteral(day, XSD.gYearMonth.getURI) must throwA[DatatypeFormatException]
      }
      "should fail when parsing xsd:gYearMonth literals from bad string and type" in {
        val dayStr = "12"
        m.createTypedLiteral(dayStr, XSD.gYearMonth.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
