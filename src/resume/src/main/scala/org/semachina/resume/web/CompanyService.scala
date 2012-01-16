package org.semachina.resume.web

import com.hp.hpl.jena.ontology.OntModel
import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._

import java.net.URI
import org.semachina.resume.beans.Company

class CompanyService(modelService: ModelService) {

  val ontModel = modelService.baseModel

  createCompanies(ontModel)


  def addOrUpdateCompany(company: Company)(implicit ontModel: OntModel) = company.&

  def createCompanies(implicit transactionModel: OntModel) = {

    //professional employment

    Company(
      URI.create("me:MerrillLynch".!),
      "Merrill Lynch",
      "New York",
      "United States",
      List("Financial Services"),
      "http://www.ml.com/",
      "").&

    Company(
      URI.create("me:DigitalLightwave".!),
      "Digital Lightwave",
      "New Jersey",
      "United States",
      List("Telecommunications/Networks"),
      "http://www.lightwave.com/",
      "").&

    Company(
      URI.create("me:MITRE".!),
      "Digital Lightwave",
      "New Jersey",
      "United States",
      List("Telecommunications/Networks"),
      "http://www.lightwave.com/",
      "").&

    Company(
      URI.create("me:Sapient".!),
      "Sapient Corporation",
      "New York",
      "United States",
      List("Technology Consulting"),
      "http://www.sapient.com/",
      "").&

    Company(
      URI.create("me:PEC".!),
      "PEC Solutions (now Avaya Goverment Solutions)",
      "Northern Virgina / Washington, DC",
      "United States",
      List("Technology Consulting", "Federal"),
      "http://www.pec.com/",
      "").&

    Company(
      URI.create("me:BoozAllenHamilton".!),
      "Booz Allen Hamilton",
      "Northern Virgina / Washington, DC",
      "United States",
      List("Management Consulting", "Federal"),
      "http://www.bah.com/",
      "").&

    Company(
      URI.create("me:TwoSigma".!),
      "Two Sigma Investments, LLC",
      "New York",
      "United States",
      List("Financial Services"),
      "http://www.twosigma.com/",
      "").&
  }
}