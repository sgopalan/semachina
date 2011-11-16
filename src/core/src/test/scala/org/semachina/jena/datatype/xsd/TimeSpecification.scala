package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.{DateTime, LocalTime}
import com.hp.hpl.jena.ontology.OntModelSpec

class TimeSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new TimeDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:time typed literal datatype conversions" should {
    "create xsd:time literals from Joda Time LocalTime object" in {
      val time = new LocalTime(12, 30, 15)
      val literal = m.createTypedLiteral(time, XSD.time.getURI)
      literal.getValue must beEqualTo(time)
    }
    "create xsd:time literals from Joda Time DateTime object" in {
      val datetime = new DateTime(2004, 12, 25, 12, 30, 15)
      val time = new LocalTime(12, 30, 15)
      val literal = m.createTypedLiteral(datetime, XSD.time.getURI)
      literal.getValue must beEqualTo(time)
    }
    "create xsd:time literals from java.util.Date object" in {
      val datetime = new DateTime(2004, 12, 25, 12, 30, 15)
      val date = datetime.toDate
      val time = new LocalTime(12, 30, 15)
      val literal = m.createTypedLiteral(date, XSD.time.getURI)
      literal.getValue must beEqualTo(time)
    }
    "create xsd:time literals from string and type" in {
      val time = new LocalTime(12, 30, 15)
      val timeStr = "12:30:15.000"
      val literal = m.createTypedLiteral(timeStr, XSD.time.getURI)
      literal.getValue must beEqualTo(time)
    }
    "fail when parsing xsd:time literals from others objects" in {
      val day = new Object()
      m.createTypedLiteral(day, XSD.time.getURI) must throwA[DatatypeFormatException]
    }
    "fail when parsing xsd:time literals from bad string and type" in {
      val dayStr = "---12"
      m.createTypedLiteral(dayStr, XSD.time.getURI) must throwA[DatatypeFormatException]
    }
  }
}
