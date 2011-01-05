package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import factory.DateTimeFactory
import com.hp.hpl.jena.rdf.model.ModelFactory
import org.joda.time.DateTime
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class DateTimeSpecification extends SpecificationWithJUnit("Jena xsd:dateTime datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:dateTime typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new DateTimeFactory())
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:datetime mapping that " in {
      "can create xsd:dateTime literals from Joda Time DateTime object" in {
        val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
        val literal = m.createTypedLiteral(datetime, XSD.dateTime.getURI)
        literal.getValue must beEqualTo(datetime)
      }
      "can create xsd:dateTime literals from java.util.Date object" in {
        val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
        val date = datetime.toDate
        val literal = m.createTypedLiteral(date, XSD.dateTime.getURI)
        literal.getValue must beEqualTo(datetime)
      }
      "can create xsd:dateTime literals from string and type" in {
        val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
        val datetimeStr = "2004-12-25T12:00:00.000-05:00"
        val literal = m.createTypedLiteral(datetimeStr, XSD.dateTime.getURI)
        literal.getValue must beEqualTo(datetime)
      }
      "should fail when creating xsd:dateTime literals from others objects" in {
        val datetime = new Integer(7)
        m.createTypedLiteral(datetime, XSD.dateTime.getURI) must throwA[IllegalArgumentException]
      }
      "should fail when creating xsd:dateTime literals from bad string and type" in {
        val datetimeStr = "2004-12-25T12:00:00.000"
        m.createTypedLiteral(datetimeStr, XSD.dateTime.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
