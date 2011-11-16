package org.semachina.resume.web.config

import org.fusesource.scalate.spring.view.ScalateViewResolver
import org.springframework.context.annotation.{Bean, Configuration}
import org.semachina.resume.web.controller.IndexController
import org.semachina.jena.config.SemachinaConfiguration
import org.semachina.resume.web._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 13, 2010
 * Time: 9:01:16 PM
 * To change this template use File | Settings | File Templates.
 */

@Configuration
//@ImportResource(Array("classpath:/mvc-resources.xml"))
class WebConfig {

  @Bean
  val modelService = new ModelService

  @Bean
  val companyService = new CompanyService( modelService )

  @Bean
  val educationalOrgService = new EducationalOrgService( modelService )

  @Bean
  val newResumeService = new NewResumeService( modelService, companyService, educationalOrgService )

  //Controllers MUST be defs or else they won't process
  @Bean
  def indexController = new IndexController( newResumeService )

//  @Bean
//  def viewResolver = {
//    val viewResolver = new ScalateViewResolver
//    //viewResolver.setPrefix("/WEB-INF/")
//    viewResolver
//  }

}