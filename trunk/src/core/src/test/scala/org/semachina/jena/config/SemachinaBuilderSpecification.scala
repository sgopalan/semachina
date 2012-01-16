package org.semachina.jena.config

import org.specs2._
import com.hp.hpl.jena.ontology._

class SemachinaBuilderSpecification extends mutable.SpecificationWithJUnit {

  val baseURI = "http://www.w3.org/2006/vcard/ns#"
  val vcardPrefix = "vcardNS"
  val entry = "http://www.w3.org/2006/vcard/ns"
  val altEntry = SemachinaConfiguration.getResource("/vcard.owl").toString
  val shortURI = vcardPrefix + ":VCard"
  val longURI = "http://www.w3.org/2006/vcard/ns#VCard"

  "SemachinaBuilder functionality" should {
    "set OntModel Spec" in {
      val ontModel =
        SemachinaBuilder(spec = OntModelSpec.OWL_DL_MEM_RDFS_INF).build

      ontModel.getSpecification must beEqualTo(OntModelSpec.OWL_DL_MEM_RDFS_INF)
    }
    "set Base" in {
      failure
    }.pendingUntilFixed
    "set Personality" in {
      failure
    }.pendingUntilFixed
    "set Prefix" in {
      failure
    }.pendingUntilFixed
    "set Alt Entry" in {
      failure
    }.pendingUntilFixed
    "add SubModel" in {
      failure
    }.pendingUntilFixed
    "add SubModel URI" in {
      failure
    }.pendingUntilFixed
    "set rebind" in {
      failure
    }.pendingUntilFixed
    "check default" in {
      failure
    }.pendingUntilFixed
  }
}
