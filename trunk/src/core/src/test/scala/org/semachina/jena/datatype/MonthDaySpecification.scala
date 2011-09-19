package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import xsd.MonthDayDatatype
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.MonthDay

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class MonthDaySpecification extends SpecificationWithJUnit("Jena xsd:gMonthDay datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:gMonthDay typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new MonthDayDatatype())
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:gMonthDay mapping that " in {
      "can create xsd:gMonthDay literals from org.ontModelAdapter.jena.datatype.types.MOnthDay object" in {
        val monthday = new MonthDay(12, 31)
        val literal = m.createTypedLiteral(monthday, XSD.gMonthDay.getURI)
        literal.getValue must beEqualTo(monthday)
      }
      "can create xsd:gMonthDay literals from string and type" in {
        val monthday = new MonthDay(12, 31)
        val monthdayStr = "--12-31";
        val literal = m.createTypedLiteral(monthdayStr, XSD.gMonthDay.getURI)
        literal.getValue must beEqualTo(monthday)
      }
      "should fail when parsing xsd:gMonthDay literals from others objects" in {
        val day = new Object()
        m.createTypedLiteral(day, XSD.gMonthDay.getURI) must throwA[DatatypeFormatException]
      }
      "should fail when parsing xsd:gMonthDay literals from bad string and type" in {
        val dayStr = "12"
        m.createTypedLiteral(dayStr, XSD.gMonthDay.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
