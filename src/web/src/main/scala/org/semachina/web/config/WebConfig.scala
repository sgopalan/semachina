package org.semachina.web.config

import com.weiglewilczek.slf4s.Logging
import org.springframework.context.annotation.{Bean, Configuration}
import org.semachina.web.controller.{HelloWorldController, CustomerController}
import org.springframework.web.servlet.view.{JstlView, UrlBasedViewResolver}
import org.semachina.config.AppConfig
import org.springframework.beans.factory.annotation.Autowired
import org.semachina.jena.core.{OWLFactory, ScalaOntModelImpl}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 13, 2010
 * Time: 9:01:16 PM
 * To change this template use File | Settings | File Templates.
 */

@Configuration
class WebConfig extends Logging {
  @Autowired
  var appConfig: AppConfig = null;

  @Bean
  def customerController = new CustomerController(OWLFactory.createOntologyModel)

  @Bean
  def helloWorldController = new HelloWorldController

  @Bean(name = Array[String]("viewResolver"))
  def viewResolver = {
    val viewResolver = new UrlBasedViewResolver
    viewResolver.setViewClass(classOf[JstlView])
    viewResolver.setPrefix("/WEB-INF/jsp/")
    viewResolver.setSuffix(".jsp")
    viewResolver
  }
}