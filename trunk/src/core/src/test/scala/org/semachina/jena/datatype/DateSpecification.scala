package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import factory.DateFactory
import org.joda.time.DateTime
import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.shared.impl.JenaParameters

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class DateSpecification extends SpecificationWithJUnit("Jena xsd:date datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:date typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new DateFactory())
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:date mapping that " in {
      "can create xsd:date literals from java.lang.Date object" in {
        val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
        val date = datetime.toDate
        val literal = m.createTypedLiteral(date, XSD.date.getURI)
        literal.getValue must beEqualTo(date)
      }
      "can create xsd:date literals from Joda Time DateTime object" in {
        val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
        val date = datetime.toDate
        val literal = m.createTypedLiteral(datetime, XSD.date.getURI)
        literal.getValue must beEqualTo(date)
      }
      "can create xsd:date literals from string and type" in {
        val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
        val date = datetime.toDate
        val dateStr = "2004-12-25"
        val literal = m.createTypedLiteral(dateStr, XSD.date.getURI)
        literal.getValue must beEqualTo(date)
      }
      "should fail when creating xsd:date literals from others objects" in {
        val datetime = new Integer(7);
        m.createTypedLiteral(datetime, XSD.date.getURI) must throwA[IllegalArgumentException]
      }
      "should fail when creating xsd:date literals from bad string and type" in {
        val datetimeStr = "2004-120-25T12:00:00.000"
        m.createTypedLiteral(datetimeStr, XSD.date.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
