package org.semachina.core

import org.semachina.jena.JenaExtension._
import com.hp.hpl.jena.datatypes.DatatypeFormatException
import java.util.Date
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.specs.{SpecificationWithJUnit}
import org.semachina.jena.config.SemachinaConfig

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 12, 2010
 * Time: 5:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

class DatatypeSpecification extends SpecificationWithJUnit("Jena Datatype Specification") {
  description = "Evaluate the functionality for the Jena typed literal datatype conversions"

  SemachinaConfig.init


  "Jena Datatype mapping" should {
    "provide xsd:datetime mapping that " in {
      "can parse xsd:datetime literals from Joda Time DateTime object" in {
        val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
        var literal: Literal = datetime.typedLit(XSD.dateTime.getURI)
        literal must notBeNull
      }
      "can parse xsd:datetime literals from string literal" in {
        val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
        val datetimeStr = "2004-12-25T12:00:00.000-05:00^^http://www.w3.org/2001/XMLSchema#dateTime"
        var literal: Literal = datetimeStr.parseLit()
        var value: DateTime = literal[DateTime]()
        value must beEqualTo(datetime)
      }
      "can parse xsd:datetime literals from string and type" in {
        val datetime = new DateTime(2004, 12, 25, 12, 0, 0, 0)
        val datetimeStr = "2004-12-25T12:00:00.000-05:00"
        var literal: Literal = datetimeStr.typedLit(XSD.dateTime.getURI)
        var value: DateTime = literal[DateTime]()
        value must beEqualTo(datetime)
      }
      "should fail when parsing xsd:datetime literals from others objects" in {
        val datetime = new Date()
        datetime.typedLit(XSD.dateTime.getURI) must throwA[IllegalArgumentException]
      }
      "should fail when parsing xsd:datetime literals from bad string literals" in {
        val datetimeStr = "2004-12-25T12:00:00.000^^http://www.w3.org/2001/XMLSchema#dateTime"
        datetimeStr.parseLit() must throwA[DatatypeFormatException]
      }
      "should fail when parsing xsd:datetime literals from bad string and type" in {
        val datetimeStr = "2004-12-25T12:00:00.000"
        datetimeStr.typedLit(XSD.dateTime.getURI) must throwA[DatatypeFormatException]
      }
    }
  }
}