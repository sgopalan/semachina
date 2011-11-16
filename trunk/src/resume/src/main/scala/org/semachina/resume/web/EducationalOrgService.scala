package org.semachina.resume.web

import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._
import thewebsemantic.Bean2RDF
import org.semachina.resume.beans.EducationalOrg
import java.net.URI
import com.hp.hpl.jena.ontology.{Individual, OntModel}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 9/10/11
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */

class EducationalOrgService(modelService : ModelService) {

  val ontModel = modelService.baseModel

  createEducationalOrgs( ontModel )
  
  def addOrUpdateEducationalOrg(edOrg: EducationalOrg, beanWriter:Bean2RDF) =

//   val edOrg = orgURI.&.getOrElse { orgURI +& "cv:EducationalOrg"}
//   edOrg setNotNull( "cv:Name" -> name, "cv:Locality" -> locality )
    beanWriter.save( edOrg ).as(classOf[Individual])

  
  def createEducationalOrgs(implicit transactionModel:OntModel) = {

    val beanWriter = new Bean2RDF( transactionModel, false )

    //educational orgs
    val cmu = addOrUpdateEducationalOrg(
      new EducationalOrg(
        URI.create("me:CMU".!),
        "Carnegie Mellon University",
        "Pittsburgh, PA" ),
      beanWriter
    )

    val uva = addOrUpdateEducationalOrg(
      new EducationalOrg(
        URI.create("me:UVA".!),
        "University of Virginia, School of Continuing Studies",
        "Northern Virginia" ),
      beanWriter
    )

  }
}