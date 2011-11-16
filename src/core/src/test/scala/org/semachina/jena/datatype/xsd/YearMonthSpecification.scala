package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.YearMonth
import com.hp.hpl.jena.ontology.OntModelSpec

class YearMonthSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new YearMonthDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:gYearMonth typed literal datatype conversions" should {
    "can create xsd:gYearMonth literals from Joda Time MonthDay object" in {
      val yearmonth = new YearMonth(2010, 12)
      val literal = m.createTypedLiteral(yearmonth, XSD.gYearMonth.getURI)
      literal.getValue must beEqualTo(yearmonth)
    }
    "can create xsd:gYearMonth literals from string and type" in {
      val yearmonth = new YearMonth(2010, 12)
      val yearmonthStr = "2010-12";
      val literal = m.createTypedLiteral(yearmonthStr, XSD.gYearMonth.getURI)
      literal.getValue must beEqualTo(yearmonth)
    }
    "should fail when parsing xsd:gYearMonth literals from others objects" in {
      val day = new Object()
      m.createTypedLiteral(day, XSD.gYearMonth.getURI) must throwA[DatatypeFormatException]
    }
    "should fail when parsing xsd:gYearMonth literals from bad string and type" in {
      val dayStr = "12"
      m.createTypedLiteral(dayStr, XSD.gYearMonth.getURI) must throwA[DatatypeFormatException]
    }
  }
}
