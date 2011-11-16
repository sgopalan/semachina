package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.Months
import com.hp.hpl.jena.ontology.OntModelSpec

class MonthSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new MonthDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:gMonth typed literal datatype conversions" should {
    "can create xsd:gMonth literals from Joda Time Months object" in {
      val month = Months.months(1)
      val literal = m.createTypedLiteral(month, XSD.gMonth.getURI)
      literal.getValue must beEqualTo(month)
    }
    "can create xsd:gMonth literals from java.lang.Integer object" in {
      val monthInt = 1
      val month = Months.months(1)
      val literal = m.createTypedLiteral(monthInt, XSD.gMonth.getURI)
      literal.getValue must beEqualTo(month)
    }
    "can create xsd:gMonth literals from string and type" in {
      val month = Months.months(1)
      //this is apparently a bogus format due to errata in the spec.  This has been corrected
      //in the spec via addendum, but it may not have been corrected in all of the tooling.
      val monthStr = "--01";
      val literal = m.createTypedLiteral(monthStr, XSD.gMonth.getURI)
      literal.getValue must beEqualTo(month)
    }
    "should fail when parsing xsd:gMonth literals from others objects" in {
      val day = new Object()
      m.createTypedLiteral(day, XSD.gMonth.getURI) must throwA[DatatypeFormatException]
    }
    "should fail when parsing xsd:gMonth literals from bad string and type" in {
      val dayStr = "12"
      m.createTypedLiteral(dayStr, XSD.gMonth.getURI) must throwA[DatatypeFormatException]
    }
  }
}
