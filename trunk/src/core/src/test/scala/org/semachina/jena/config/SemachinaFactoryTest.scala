package org.semachina.jena.config

import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.query.ResultSet

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 2, 2011
 * Time: 7:07:08 PM
 * To change this template use File | Settings | File Templates.
 */

class SemachinaFactoryTest extends SpecificationWithJUnit("SemachinaFactory Specification") {
  description = "Evaluate the functionality for creating and managing Model configuration"

  val baseURI = "http://www.w3.org/2006/vcard/ns"
  val prefix = "vcard"
  val altEntry = classOf[SemachinaFactoryTest].getResource("/vcard.owl").toString
  val example = classOf[SemachinaFactoryTest].getResource("/vcard-example.owl").toString
  val shortURI = "vcard:VCard"
  val longURI = "http://www.w3.org/2006/vcard/ns#VCard"
  val indivURI = "http://example.com/me/corky"

  "SemachinaFactory" should {
    "provide global mechanisms to " in {
      "register prefixes / expand prefixes for URIs" in {
        SemachinaFactory.registerURI( uri = baseURI, prefix = prefix)

        val uri = SemachinaFactory expandPrefix shortURI
        uri must beEqualTo( longURI )
      }
      "register prefixes / short form URIs " in {
        SemachinaFactory.registerURI( uri = baseURI, prefix = prefix)

        val uri = SemachinaFactory shortForm longURI
        uri must beEqualTo( shortURI )
      }
      "register alt entries for URIs" in {
        SemachinaFactory.registerURI( uri = baseURI, altEntry = altEntry)
        val ontModel = SemachinaFactory.load( baseURI )

        ontModel.read( example )

        val individual = ontModel.getIndividual( indivURI )
        individual must notBeNull
      }
      "register alt entries for URIs / load with a given OntModel" in {
        SemachinaFactory.registerURI( uri = baseURI, altEntry = altEntry)

        val ontModel = SemachinaFactory.createOntologyModel()
        SemachinaFactory.load( baseURI, ontModel )

        ontModel.read( example )

        val individual = ontModel.getIndividual( indivURI )
        individual must notBeNull
      }
      "register alt entries for URIs / load with prefix for a given OntModel" in {
        SemachinaFactory.registerURI( uri = baseURI, altEntry = altEntry)

        val ontModel = SemachinaFactory.createOntologyModel()
        SemachinaFactory.loadPrefix( prefix, ontModel )

        ontModel.read( example )

        val individual = ontModel.getIndividual( indivURI )
        individual must notBeNull
      }
      "register alt entries for URIs / load without a given OntModel" in {
        SemachinaFactory.registerURI( uri = baseURI, prefix = prefix, altEntry = altEntry)

        val ontModel = SemachinaFactory.createOntologyModel()

        ontModel.read( baseURI )
        ontModel.read( example )

        val individual = ontModel.getIndividual( indivURI )
        individual must notBeNull
      }
      "register custom implementations for Jena interfaces" in {
        val interfaceClass = classOf[ Individual ]
        val individualFactory = IndividualImpl.factory

        //set the implementation
        SemachinaFactory.registerImplementation( interfaceClass, individualFactory )

        //get the implementation
        val retrievedFactory = SemachinaFactory.getImplementation( interfaceClass )

        retrievedFactory must beEqualTo( individualFactory )
      }
    }
  }

  /**
   * Created by IntelliJ IDEA.
   * User: sgopalan
   * Date: Nov 30, 2010
   * Time: 9:08:48 AM
   * To change this template use File | Settings | File Templates.
   */
  trait ResultSetHandler {
    def handle(rs: ResultSet): Unit
  }
}