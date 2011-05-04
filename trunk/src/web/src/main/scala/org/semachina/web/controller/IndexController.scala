package org.semachina.web.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import sample.SomeClass

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Dec 23, 2010
 * Time: 11:05:39 AM
 * To change this template use File | Settings | File Templates.
 */

@Controller
class IndexController {

  @RequestMapping(Array("/layout"))
  def layout = "/index.scaml"

  @RequestMapping(Array("/view"))
  def view: ModelAndView = {
    val mav = new ModelAndView
    mav.addObject("it", new SomeClass)
    return mav
  }

  @RequestMapping(Array("/", "/render"))
  def render = "render:/index.scaml"

}
