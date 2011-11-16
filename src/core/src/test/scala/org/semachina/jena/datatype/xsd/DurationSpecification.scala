package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.specs2._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.format.ISOPeriodFormat
import com.hp.hpl.jena.ontology.OntModelSpec

class DurationSpecification extends mutable.SpecificationWithJUnit {

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype(new DurationDatatype())
  val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)

  "Jena xsd:duration typed literal datatype conversions" should {
    "create xsd:duration literals from Joda Time Period object" in {
      val duration = ISOPeriodFormat.standard.parsePeriod("P1Y2M3DT5H20M30.123S")
      val literal = m.createTypedLiteral(duration, XSD.duration.getURI)
      literal.getValue must beEqualTo(duration)
    }
    "create xsd:duration literals from string and type" in {
      val duration = ISOPeriodFormat.standard.parsePeriod("P1Y2M3DT5H20M30.123S")
      val durationStr = "P1Y2M3DT5H20M30.123S"
      val literal = m.createTypedLiteral(durationStr, XSD.duration.getURI)
      literal.getValue must beEqualTo(duration)
    }
    "fail when parsing xsd:duration literals from others objects" in {
      val day = new Object()
      m.createTypedLiteral(day, XSD.duration.getURI) must throwA[DatatypeFormatException]
    }
    "fail when parsing xsd:duration literals from bad string and type" in {
      val dayStr = "12"
      m.createTypedLiteral(dayStr, XSD.duration.getURI) must throwA[DatatypeFormatException]
    }
  }
}
