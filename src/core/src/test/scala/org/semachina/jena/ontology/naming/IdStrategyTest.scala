package org.semachina.jena.ontology.naming

import org.specs.SpecificationWithJUnit
import org.semachina.jena.config.SemachinaConfiguration
import org.semachina.jena.ontology.SemachinaIndividual
import org.semachina.jena.dsl.SemachinaDSL._
import com.hp.hpl.jena.ontology.Individual
import org.semachina.jena.config.SemachinaBuilder

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 10/15/11
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */

class IdStrategyTest extends SpecificationWithJUnit("Id Strategy Specification") {
  description = "Evaluate various naming strategies for IDs"


  val altEntry = classOf[IdStrategyTest].getResource("/vcard.owl").toString
  val example = classOf[IdStrategyTest].getResource("/vcard-example.owl").toString
  val indivURI = "http://example.com/me/corky"

  SemachinaConfiguration.addAltEntry("http://www.w3.org/2006/vcard/ns" -> altEntry)
  SemachinaConfiguration.setNsPrefix("vcard" -> "http://www.w3.org/2006/vcard/ns#")
  SemachinaConfiguration.setNsPrefix("ex" -> "http://example.com/me/")
  val baseModel = SemachinaConfiguration.load("http://www.w3.org/2006/vcard/ns")
  baseModel.readWithPrefixes(example)

  "IdStrategy" should {
    "provide provide URI naming methods to " in {
      "create Hierarchical IDs" in {
        implicit val ontModel = SemachinaBuilder().build
        ontModel.addSubModel(baseModel)

        val idStrategy = IdStrategy.newHierarchical(
        "locations", {
          res: Individual => SemachinaIndividual(res).value[String]("vcard:country-name")
        },
        "^vcard:adr"
        )

        val parent = indivURI.&

        val child = +&(idStrategy, "vcard:Location", "vcard:Home").apply {
          i =>
            i add("vcard:country-name", "USA" ^^ "xsd:string")
            parent add("vcard:adr", i)
        }

        child.getURI must beEqualTo("http://example.com/me/corky/locations/USA")
      }
      "create Label-Based IDs" in {
        implicit val ontModel = SemachinaBuilder().build
        ontModel.addSubModel(baseModel)

        val idStrategy = IdStrategy.newLabelBased("locations", "^vcard:adr", "en")

        val parent = indivURI.&

        var child = +&(idStrategy, "vcard:Location", "vcard:Home").apply {
          i =>
            i.setLabel("This is my summer home", "en")
            parent add("vcard:adr", i)
        }

        child.getURI must beEqualTo("http://example" +
          ".com/me/corky/locations/this_is_my_summer_home")
      }
    }
  }
}