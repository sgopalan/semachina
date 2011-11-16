package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.Years
import com.hp.hpl.jena.ontology.OntModelSpec

class YearSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new YearDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:gYear typed literal datatype conversions" should {
    "can create xsd:gYear literals from Joda Time Years object" in {
      val year = Years.years(2010)
      val literal = m.createTypedLiteral(year, XSD.gYear.getURI)
      literal.getValue must beEqualTo(year)
    }
    "can create xsd:gYear literals from java.lang.Integer object" in {
      val yearInt = 2010
      val year = Years.years(2010)
      val literal = m.createTypedLiteral(yearInt, XSD.gYear.getURI)
      literal.getValue must beEqualTo(year)
    }
    "can create xsd:gYear literals from string and type" in {
      val yearStr = "2010"
      val year = Years.years(2010)
      val literal = m.createTypedLiteral(yearStr, XSD.gYear.getURI)
      literal.getValue must beEqualTo(year)
    }
    "should fail when parsing xsd:gYear literals from others objects" in {
      val day = new Object()
      m.createTypedLiteral(day, XSD.gYear.getURI) must throwA[DatatypeFormatException]
    }
    "should fail when parsing xsd:gYear literals from bad string and type" in {
      val yearMonthStr = "2001-12"
      m.createTypedLiteral(yearMonthStr, XSD.gYear.getURI) must throwA[DatatypeFormatException]
    }
  }
}
