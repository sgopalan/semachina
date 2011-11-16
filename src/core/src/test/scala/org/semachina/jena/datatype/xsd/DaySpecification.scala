package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.Days
import com.hp.hpl.jena.ontology.OntModelSpec

class DaySpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new DayDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:gDay typed literal datatype conversions" should {
    "create xsd:gDay literals from Joda Time Days object" in {
      val day = Days.days(12)
      val literal = m.createTypedLiteral(day, XSD.gDay.getURI)
      literal.getValue must beEqualTo(day)
    }
    "create xsd:gDay literals from java.lang.Integer object" in {
      val dayInt = new Integer(12)
      val day = Days.days(12)
      val literal = m.createTypedLiteral(dayInt, XSD.gDay.getURI)
      literal.getValue must beEqualTo(day)
    }
    "create xsd:gDay literals from string and type" in {
      val day = Days.days(12)
      val dayStr = "---12";
      val literal = m.createTypedLiteral(dayStr, XSD.gDay.getURI)
      literal.getValue must beEqualTo(day)
    }
    "fail when parsing xsd:gDay literals from others objects" in {
      val day = new Object()
      m.createTypedLiteral(day, XSD.gDay.getURI) must throwA[DatatypeFormatException]
    }
    "fail when parsing xsd:gDay literals from bad string and type" in {
      val dayStr = "12"
      m.createTypedLiteral(dayStr, XSD.gDay.getURI) must throwA[DatatypeFormatException]
    }
  }
}
