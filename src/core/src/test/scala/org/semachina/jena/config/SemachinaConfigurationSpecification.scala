package org.semachina.jena.config

import org.specs.SpecificationWithJUnit
import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.ontology.{OntDocumentManager, Individual}

class SemachinaConfigurationSpecification
  extends SpecificationWithJUnit("SemachinaConfiguration Specification") {

  description = "Evaluate the functionality for creating and managing Model configuration"
  val entry = "http://www.w3.org/2006/vcard/ns"

  val baseURI = "http://www.w3.org/2006/vcard/ns#"

  val vcardPrefix = "vcardNS"

  val baseExampleURI = "http://example.com/me/"
  val examplePrefix = "ex"

  val altEntry =
    classOf[SemachinaConfigurationSpecification].getResource("/vcard.owl").toString

  val example =
    classOf[SemachinaConfigurationSpecification].getResource("/vcard-example.owl").toString

  val shortURI = vcardPrefix + ":VCard"
  val longURI = "http://www.w3.org/2006/vcard/ns#VCard"

  val indivURI = "http://example.com/me/corky"
  val exShortURI = examplePrefix + ":corky"

  "SemachinaConfiguration" should {
    "register prefixes / expand prefixes for URIs" in {
      SemachinaConfiguration.setNsPrefix(vcardPrefix -> baseURI)

      val uri = SemachinaConfiguration expandPrefix shortURI
      uri must beEqualTo(longURI)
    }
    "register prefixes / short form URIs " in {
      SemachinaConfiguration.setNsPrefix(vcardPrefix -> baseURI)

      val uri = SemachinaConfiguration shortForm longURI
      uri must beEqualTo(shortURI)
    }
    "register alt entries for URIs" in {
      SemachinaConfiguration.addAltEntry(entry -> altEntry)
      val storedEntry =
        OntDocumentManager.getInstance.getFileManager.getLocationMapper.getAltEntry(entry)
      storedEntry must beEqualTo(altEntry)
    }
    "register alt entries for URIs / load with a given OntModel" in {
      SemachinaConfiguration.addAltEntry(entry -> altEntry)

      val ontModel = SemachinaBuilder().build
      SemachinaConfiguration.load(entry, ontModel)

      ontModel.read(example)

      val individual = ontModel.getIndividual(indivURI)
      individual must notBeNull
    }
    "register alt entries for URIs / load with prefix for a given OntModel" in {
      SemachinaConfiguration.addAltEntry("http://www.w3.org/2006/vcard/ns" -> altEntry)

      val ontModel = SemachinaBuilder().build
      SemachinaConfiguration.setNsPrefix(vcardPrefix -> baseURI)

      ontModel.read(example)

      val individual = ontModel.getIndividual(indivURI)
      individual must notBeNull
    }
    "register alt entries for URIs / load without a given OntModel" in {
      SemachinaConfiguration.addAltEntry("http://www.w3.org/2006/vcard/ns" -> altEntry)
      SemachinaConfiguration.setNsPrefix(vcardPrefix -> baseURI)

      val ontModel = SemachinaBuilder().build

      ontModel.read(baseURI)
      ontModel.read(example)

      val individual = ontModel.getIndividual(indivURI)
      individual must notBeNull
    }
    "register custom implementations for Jena interfaces" in {
      val interfaceClass = classOf[Individual]
      val individualFactory = IndividualImpl.factory

      //set the implementation
      SemachinaConfiguration.registerImplementation(interfaceClass, individualFactory)

      //get the implementation
      val retrievedFactory = SemachinaConfiguration.getImplementation(interfaceClass)

      retrievedFactory must beEqualTo(individualFactory)
    }
  }
}