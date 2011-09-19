package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import xsd.TimeDatatype
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.LocalTime
/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class TimeSpecification extends SpecificationWithJUnit("Jena xsd:time datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:time typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new TimeDatatype())
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:time mapping that " in {
      "can create xsd:time literals from Joda Time LocalTime object" in {
        val time = new LocalTime(12, 30, 15)
        val literal = m.createTypedLiteral(time, XSD.time.getURI)
        literal.getValue must beEqualTo(time)
      }
      "can create xsd:time literals from string and type" in {
        val time = new LocalTime(12, 30, 15)
        val timeStr = "12:30:15.000"
        val literal = m.createTypedLiteral(timeStr, XSD.time.getURI)
        literal.getValue must beEqualTo(time)
      }
      "should fail when parsing xsd:time literals from others objects" in {
        val day = new Object()
        m.createTypedLiteral(day, XSD.time.getURI) must throwA[DatatypeFormatException]
      }
      "should fail when parsing xsd:time literals from bad string and type" in {
        val dayStr = "---12"
        m.createTypedLiteral(dayStr, XSD.time.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
