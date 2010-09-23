package org.semachina.web.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.stereotype.Controller

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 13, 2010
 * Time: 9:18:51 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
class HelloWorldController {
  @RequestMapping(Array("/hello.html"))
  def showHello = "helloPage"
}