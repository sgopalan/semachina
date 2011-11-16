package org.semachina.jena

import config.{SemachinaConfiguration, SemachinaBuilder}
import org.specs.SpecificationWithJUnit
import org.semachina.jena.dsl.SemachinaDSL._

class BasicJenaDSLTest extends SpecificationWithJUnit("SemachinaConfiguration Specification") {
  description = "Evaluate Basic Jena DSL"

  val baseURI = "http://www.w3.org/2006/vcard/ns"
  val prefix = "vcard"
  val altEntry = classOf[BasicJenaDSLTest].getResource("/vcard.owl").toString
  val example = classOf[BasicJenaDSLTest].getResource("/vcard-example.owl").toString
  val shortURI = "vcard:VCard"
  val longURI = "http://www.w3.org/2006/vcard/ns#VCard"
  val indivURI = "http://example.com/me/corky"

  SemachinaConfiguration.addAltEntry(baseURI -> altEntry)

  def getOntModel = {
    val ontModel = SemachinaBuilder()
      .read(baseURI)
      .read(example)
      .setNsPrefix(prefix -> (baseURI + "#"))
      .build

    ontModel
  }

  "SemachinaDSL" should {
    "Allow for creation of an individual from a string " in {
      implicit val ontModel = getOntModel

      val sri = "http://example.com/me/sri" withTypes shortURI
      sri.hasOntClass(longURI) must beTrue
    }
    "Allow for creation of an individual from a string " in {
      implicit val ontModel = getOntModel

      val sri = "http://example.com/me/sri" +& shortURI
      sri.hasOntClass(longURI) must beTrue
    }
    "Get of individual from a string " in {
      implicit val ontModel = getOntModel

      indivURI.& must notBeNull
    }
    "Get of ontClass from a string " in {
      implicit val ontModel = getOntModel

      shortURI.$ must notBeNull
    }
    "Get of property from a string " in {
      implicit val ontModel = getOntModel

      "vcard:adr".PROP must notBeNull
    }
  }
}