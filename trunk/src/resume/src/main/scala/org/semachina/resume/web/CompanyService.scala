package org.semachina.resume.web

import com.hp.hpl.jena.ontology.OntModel
import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._
import thewebsemantic._
import com.hp.hpl.jena.ontology.Individual
import java.lang.reflect.Method
import collection.mutable
import java.net.URI
import org.semachina.resume.beans.Company


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 9/10/11
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */



class CompanyService(modelService : ModelService) {

  val ontModel = modelService.baseModel

  createCompanies( ontModel )



  def addOrUpdateCompany(company:Company)(implicit beanWriter:Bean2RDF) =
    beanWriter.save( company ).as(classOf[Individual])

  def createCompanies(implicit transactionModel:OntModel) = {
    implicit val beanWriter = new Bean2RDF(transactionModel, false)
    //professional employment
    val merrillLynch = addOrUpdateCompany(
      new Company(
        URI.create("me:MerrillLynch".!),
        "Merrill Lynch",
        "New York",
        "United States",
        List("Financial Services"),
        "http://www.ml.com/",
        "")
    )

    val digitalLightwave = addOrUpdateCompany(
      new Company(
        URI.create("me:DigitalLightwave".!),
        "Digital Lightwave",
        "New Jersey",
        "United States",
        List("Telecommunications/Networks"),
        "http://www.lightwave.com/",
        "")
    )

    val mitre = addOrUpdateCompany(
      new Company(
        URI.create("me:MITRE".!),
        "Digital Lightwave",
        "New Jersey",
        "United States",
        List("Telecommunications/Networks"),
        "http://www.lightwave.com/",
        "")
    )

    val sapient = addOrUpdateCompany(
      new Company(
        URI.create("me:Sapient".!),
        "Sapient Corporation",
        "New York",
        "United States",
        List("Technology Consulting"),
        "http://www.sapient.com/",
        "")
    )

    val pec = addOrUpdateCompany(
      new Company(
        URI.create("me:PEC".!),
        "PEC Solutions (now Avaya Goverment Solutions)",
        "Northern Virgina / Washington, DC",
        "United States",
        List("Technology Consulting", "Federal"),
        "http://www.pec.com/",
        "")
    )

    val bah = addOrUpdateCompany(
      new Company(
        URI.create("me:BoozAllenHamilton".!),
        "Booz Allen Hamilton",
        "Northern Virgina / Washington, DC",
        "United States",
        List("Management Consulting", "Federal"),
        "http://www.bah.com/",
        "")
    )

    val twoSigma = addOrUpdateCompany(
      new Company(
        URI.create("me:TwoSigma".!),
        "Two Sigma Investments, LLC",
        "New York",
        "United States",
        List("Financial Services"),
        "http://www.twosigma.com/",
        "" )
    )
  }
}