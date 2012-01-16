package org.semachina.resume.web.config

import org.fusesource.scalate.spring.view.ScalateViewResolver
import org.semachina.resume.web.controller.IndexController
import org.semachina.resume.web._

import org.springframework.web.servlet.config.annotation.{ResourceHandlerRegistry, WebMvcConfigurerAdapter, EnableWebMvc}
import org.springframework.context.annotation.{Bean, Configuration}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 13, 2010
 * Time: 9:01:16 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Spring MVC Configuration.
 * @author Craig Walls
 */
@Configuration
//@ComponentScan(basePackages = Array("org.semachina"), excludeFilters = Array(new Filter(Array(classOf[Configuration]))))
@EnableWebMvc
class WebConfig extends WebMvcConfigurerAdapter {

  override def addResourceHandlers(registry: ResourceHandlerRegistry): Unit = {
    registry.addResourceHandler("/css/**").addResourceLocations("/css/")
    registry.addResourceHandler("/foaf-magnus/**").addResourceLocations("/foaf-magnus/")
    registry.addResourceHandler("/images/**").addResourceLocations("/images/")
  }

  @Bean
  def modelService = new ModelService

  @Bean
  def companyService = new CompanyService(modelService)

  @Bean
  def educationalOrgService = new EducationalOrgService(modelService)

  @Bean
  def newResumeService = new NewResumeService(modelService, companyService, educationalOrgService)

  //Controllers MUST be defs or else they won't process
  @Bean
  def indexController = new IndexController(newResumeService)

  @Bean
  def viewResolver = {
    val viewResolver = new ScalateViewResolver
    viewResolver.setOrder(1)
    viewResolver
  }

  //  @Bean
  //  def viewResolver: ViewResolver = {
  //    var viewResolver = new InternalResourceViewResolver()
  //    viewResolver.setOrder(0)
  //    viewResolver.setPrefix("/WEB-INF/pages/html/")
  //    viewResolver.setSuffix(".html")
  //    viewResolver.setViewClass( classOf[JstlView])
  //    return viewResolver
  //  }

}