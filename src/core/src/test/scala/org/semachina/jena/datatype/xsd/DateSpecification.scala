package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.specs2._
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.shared.impl.JenaParameters
import com.hp.hpl.jena.ontology.OntModelSpec

class DateSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new DateDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:date typed literal datatype conversions" should {
    "create xsd:date literals from java.lang.Date object" in {
      val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
      val date = datetime.toDate
      val literal = m.createTypedLiteral(date, XSD.date.getURI)
      literal.getValue must beEqualTo(date)
    }
    "create xsd:date literals from Joda Time DateTime object" in {
      val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
      val date = datetime.toDate
      val literal = m.createTypedLiteral(datetime, XSD.date.getURI)
      literal.getValue must beEqualTo(date)
    }
    "create xsd:dateTime literals from org.joda.time.LocalDate" in {
      val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
      val date = datetime.toDate
      val localDate = datetime.toLocalDate
      val literal = m.createTypedLiteral(localDate, XSD.date.getURI)
      literal.getValue must beEqualTo(date)
    }
    "create xsd:date literals from string and type" in {
      val datetime = new DateTime(2004, 12, 25, 0, 0, 0, 0)
      val date = datetime.toDate
      val dateStr = "2004-12-25"
      val literal = m.createTypedLiteral(dateStr, XSD.date.getURI)
      literal.getValue must beEqualTo(date)
    }

    "fail when creating xsd:date literals from others objects" in {
      val datetime = new Integer(7);
      m.createTypedLiteral(datetime, XSD.date.getURI) must throwA[DatatypeFormatException]
    }
    "fail when creating xsd:date literals from bad string and type" in {
      val datetimeStr = "2004-120-25T12:00:00.000"
      m.createTypedLiteral(datetimeStr, XSD.date.getURI) must throwA[DatatypeFormatException]
    }
  }
}
