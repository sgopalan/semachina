package org.semachina.resume.web.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView

import scala.collection.JavaConversions._
import org.semachina.resume.web.NewResumeService

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 3/22/11
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
class IndexController(resumeService: NewResumeService) {

  @RequestMapping(Array("/resume"))
  def resume: ModelAndView = {
    val resume = resumeService.getResume("me:sgopalan_cv")
    new ModelAndView("/WEB-INF/pages/scalate/index.ssp", Map[String, Any]("resumeModel" -> resume.getOntModel, "resume" -> resume))
  }

  @RequestMapping(Array("/magnus"))
  def magnus = "/WEB-INF/pages/html/foaf-magnus.ssp"

  @RequestMapping(Array("/fluid"))
  def fluid = "/WEB-INF/pages/html/fluid.ssp"
}