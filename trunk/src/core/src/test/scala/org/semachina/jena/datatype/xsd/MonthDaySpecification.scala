package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.MonthDay
import com.hp.hpl.jena.ontology.OntModelSpec

class MonthDaySpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new MonthDayDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:gMonthDay typed literal datatype conversions" should {
    "can create xsd:gMonthDay literals from Joda Time MonthDay object" in {
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
