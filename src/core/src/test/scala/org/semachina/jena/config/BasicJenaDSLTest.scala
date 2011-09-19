package org.semachina.jena.config

import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.query.ResultSet
import org.semachina.jena.SemachinaDSL._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 2, 2011
 * Time: 7:07:08 PM
 * To change this template use File | Settings | File Templates.
 */

class BasicJenaDSLTest extends SpecificationWithJUnit("SemachinaFactory Specification") {
  description = "Evaluate Basic Jena DSL"

  val baseURI = "http://www.w3.org/2006/vcard/ns"
  val prefix = "vcard"
  val altEntry = classOf[BasicJenaDSLTest].getResource("/vcard.owl").toString
  val example = classOf[BasicJenaDSLTest].getResource("/vcard-example.owl").toString
  val shortURI = "vcard:VCard"
  val longURI = "http://www.w3.org/2006/vcard/ns#VCard"
  val indivURI = "http://example.com/me/corky"

  SemachinaFactory.registerURI( uri = baseURI, prefix = prefix, altEntry = altEntry)

  def getOntModel =  {
    val ontModel = SemachinaFactory.createMemOntologyModel()
    ontModel.read( baseURI )
    ontModel.read( example )

    ontModel.setNsPrefix( prefix, baseURI + "#" )
    ontModel
  }

  "SemachinaDSL" should {
    "Allow for creation of an individual from a string " in {
      implicit val ontModel = getOntModel

      val sri = "http://example.com/me/sri" withTypes shortURI
      sri.hasOntClass( longURI ) must beTrue
    }
    "Allow for creation of an individual from a string " in {
      implicit val ontModel = getOntModel

      val sri = "http://example.com/me/sri" +& shortURI
      sri.hasOntClass( longURI ) must beTrue
    }
    "Get of individual from a string " in {
      implicit val ontModel = getOntModel

      indivURI.indiv must notBeNull
    }
    "Get of individual from a string " in {
      implicit val ontModel = getOntModel

      indivURI.& must notBeNull
    }
    "Get of ontClass from a string " in {
      implicit val ontModel = getOntModel

      shortURI.ontClass must notBeNull
    }
    "Get of property from a string " in {
      implicit val ontModel = getOntModel

      "vcard:adr".property must notBeNull
    }
  }
}