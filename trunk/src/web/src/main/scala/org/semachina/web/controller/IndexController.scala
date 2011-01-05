/**
 * Copyright (C) 2009-2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.scalate.sample.springmvc.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sample.SomeClass
import org.joda.time.DateTime
import org.semachina.web.data.Article

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
  def render: ModelAndView = {
    def articles = Array(
      new Article(1, "Title #1", "This is the body #1", new DateTime()),
      new Article(2, "Title #2", "This is the body #2", new DateTime()),
      new Article(3, "Title #3", "This is the body #3", new DateTime()),
      new Article(4, "Title #4", "This is the body #4", new DateTime()),
      new Article(5, "Title #5", "This is the body #5", new DateTime())
    )

    val mav = new ModelAndView("render:/index.scaml")
    mav.addObject("articles", articles)
    return mav
  }

}