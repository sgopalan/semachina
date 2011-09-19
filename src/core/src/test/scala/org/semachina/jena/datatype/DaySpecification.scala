package org.semachina.jena.datatype

import com.hp.hpl.jena.vocabulary.XSD
import xsd.{DayDatatype}
import org.specs.SpecificationWithJUnit
import org.semachina.jena.SemachinaDSL._
import com.hp.hpl.jena.ontology.{ProfileRegistry, OntModelSpec}

import com.hp.hpl.jena.rdf.model.{ModelFactory, Literal}
import com.hp.hpl.jena.datatypes.{TypeMapper, DatatypeFormatException}
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.joda.time.{Days, DateTime}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class DaySpecification extends SpecificationWithJUnit("Jena xsd:gDay datatype Specification") {
  description = "Evaluate the functionality for the Jena xsd:gDay typed literal datatype conversions"

  JenaParameters.enableEagerLiteralValidation = true
  TypeMapper.getInstance().registerDatatype( new DayDatatype() )
  val m = ModelFactory.createOntologyModel()

  "Jena Datatype mapping" should {
    "provide xsd:gDay mapping that " in {
      "can create xsd:gDay literals from org.ontModelAdapter.jena.datatype.types.Day object" in {
        val day = Days.days(12)
        val literal = m.createTypedLiteral(day, XSD.gDay.getURI)
        literal.getValue must beEqualTo(day)
      }
      "can create xsd:gDay literals from java.lang.Integer object" in {
        val dayInt = new Integer(12)
        val day = Days.days(12)
        val literal = m.createTypedLiteral(dayInt, XSD.gDay.getURI)
        literal.getValue must beEqualTo(day)
      }
      "can create xsd:gDay literals from string and type" in {
        val day = Days.days(12)
        val dayStr = "---12";
        val literal = m.createTypedLiteral(dayStr, XSD.gDay.getURI)
        literal.getValue must beEqualTo(day)
      }
      "should fail when parsing xsd:gDay literals from others objects" in {
        val day = new Object()
        m.createTypedLiteral(day, XSD.gDay.getURI) must throwA[DatatypeFormatException]
      }
      "should fail when parsing xsd:gDay literals from bad string and type" in {
        val dayStr = "12"
        m.createTypedLiteral(dayStr, XSD.gDay.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}
