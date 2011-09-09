package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import org.semachina.jena.datatype.factory.{DurationFactory, DayFactory}
import org.specs.SpecificationWithJUnit
import org.semachina.jena.datatype.types.Day
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.format.ISOPeriodFormat

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class DurationSpecification extends SpecificationWithJUnit("Jena xsd:duration datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:duration typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype( new DurationFactory() )
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:duration mapping that " in {
      "can create xsd:duration literals from Joda Time Period object" in {
        val duration = ISOPeriodFormat.standard.parsePeriod("P1Y2M3DT5H20M30.123S")
        val literal = m.createTypedLiteral(duration, XSD.duration.getURI)
        literal.getValue must beEqualTo(duration)
      }
      "can create xsd:duration literals from string and type" in {
        val duration = ISOPeriodFormat.standard.parsePeriod("P1Y2M3DT5H20M30.123S")
        val durationStr = "P1Y2M3DT5H20M30.123S"
        val literal = m.createTypedLiteral(durationStr, XSD.duration.getURI)
        literal.getValue must beEqualTo(duration)
      }
      "should fail when parsing xsd:duration literals from others objects" in {
        val day = new Object()
        m.createTypedLiteral(day, XSD.duration.getURI) must throwA[IllegalArgumentException]
      }
      "should fail when parsing xsd:duration literals from bad string and type" in {
        val dayStr = "12"
        m.createTypedLiteral(dayStr, XSD.duration.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
