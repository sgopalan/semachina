package org.semachina.web.config

import com.weiglewilczek.slf4s.Logging
import org.springframework.context.annotation.{Bean, Configuration}
import org.semachina.config.AppConfig
import org.springframework.beans.factory.annotation.Autowired
import org.semachina.web.controller.IndexController
import org.fusesource.scalate.spring.view.ScalateViewResolver

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
  def indexController = new IndexController

  @Bean
  def viewResolver = new ScalateViewResolver

}
