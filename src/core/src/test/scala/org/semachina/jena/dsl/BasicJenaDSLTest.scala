package org.semachina.jena.dsl

import org.specs2._
import org.semachina.jena.dsl.SemachinaDSL._
import org.semachina.jena.config.{SemachinaBuilder, SemachinaConfiguration}

class BasicJenaDSLTest extends mutable.SpecificationWithJUnit {

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

      indivURI.& must not beNull
    }
    "Get of ontClass from a string " in {
      implicit val ontModel = getOntModel

      shortURI.$ must not beNull
    }
    "Get of property from a string " in {
      implicit val ontModel = getOntModel

      "vcard:adr".PROP must not beNull
    }
  }
}