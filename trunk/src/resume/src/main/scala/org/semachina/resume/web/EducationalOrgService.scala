package org.semachina.resume.web

import org.semachina.jena.dsl.SemachinaDSL._
import org.semachina.resume.beans.EducationalOrg
import java.net.URI
import com.hp.hpl.jena.ontology.OntModel

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 9/10/11
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */

class EducationalOrgService(modelService: ModelService) {

  val ontModel = modelService.baseModel

  createEducationalOrgs(ontModel)

  def addOrUpdateEducationalOrg(edOrg: EducationalOrg)(implicit ontModel: OntModel) = {
    edOrg.&
  }

  def createEducationalOrgs(implicit transactionModel: OntModel) = {

    //educational orgs
    EducationalOrg(
      URI.create("me:CMU".!),
      "Carnegie Mellon University",
      "Pittsburgh, PA").&

    EducationalOrg(
      URI.create("me:UVA".!),
      "University of Virginia, School of Continuing Studies",
      "Northern Virginia").&

  }
}