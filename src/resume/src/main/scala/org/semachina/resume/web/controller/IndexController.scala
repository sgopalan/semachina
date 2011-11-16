package org.semachina.resume.web.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.stereotype.Controller
import javax.annotation.PostConstruct
import org.springframework.web.servlet.ModelAndView
import com.hp.hpl.jena.ontology.{ProfileRegistry, OntModelSpec}
import com.google.code.geocoder.{GeocoderRequestBuilder, Geocoder}
import com.google.code.geocoder.model.{GeocodeResponse, GeocoderRequest}
import com.hp.hpl.jena.query.{QuerySolutionMap, QuerySolution, ResultSet}
import org.joda.time.LocalDate
import java.util.Date
import com.hp.hpl.jena.vocabulary.{RDFS, RDF}

import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._
import org.semachina.resume.web.{NewResumeService, ResumeService, ModelService}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 3/22/11
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
class IndexController(resumeService:NewResumeService) {

  @RequestMapping(Array("/resume"))
  def resume: ModelAndView = {
    val resume = resumeService.getResume("me:sgopalan_cv")
    new ModelAndView("/WEB-INF/pages/scalate/index.ssp", Map[String, Any]("resumeModel" -> resume.getOntModel, "resume" -> resume))
  }
}