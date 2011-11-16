package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.specs2._
import com.hp.hpl.jena.shared.impl.JenaParameters
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.OntModelSpec

class DateTimeSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new DateTimeDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:dateTime typed literal datatype conversions" should {
    "create xsd:dateTime literals from Joda Time DateTime object" in {
      val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
      val literal = m.createTypedLiteral(datetime, XSD.dateTime.getURI)
      literal.getValue must beEqualTo(datetime)
    }
    "create xsd:dateTime literals from java.util.Date object" in {
      val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
      val date = datetime.toDate
      val literal = m.createTypedLiteral(date, XSD.dateTime.getURI)
      literal.getValue must beEqualTo(datetime)
    }
    "create xsd:dateTime literals from string and type" in {
      val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
      val datetimeStr = "2004-12-25T12:00:00.000-05:00"
      val literal = m.createTypedLiteral(datetimeStr, XSD.dateTime.getURI)
      literal.getValue must beEqualTo(datetime)
    }
    "create xsd:dateTime literals from java.util.Date" in {
      val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
      val date = datetime.toDate
      val literal = m.createTypedLiteral(date, XSD.dateTime.getURI)
      literal.getValue must beEqualTo(datetime)
    }
    "create xsd:dateTime literals from org.joda.time.LocalDate" in {
      val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
      val localDate = datetime.toLocalDate
      val literal = m.createTypedLiteral(localDate, XSD.dateTime.getURI)
      literal.getValue must beEqualTo(datetime)
    }
    "fail when creating xsd:dateTime literals from others objects" in {
      val datetime = new Integer(7)
      m.createTypedLiteral(datetime, XSD.dateTime.getURI) must throwA[DatatypeFormatException]
    }
    "fail when creating xsd:dateTime literals from bad string and type" in {
      val datetimeStr = "2004-12-25T12:00:00.000"
      m.createTypedLiteral(datetimeStr, XSD.dateTime.getURI) must throwA[DatatypeFormatException]
    }
  }
}
