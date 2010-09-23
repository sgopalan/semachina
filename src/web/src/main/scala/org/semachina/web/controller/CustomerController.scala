package org.semachina.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, ModelAttribute}
import org.springframework.web.bind.annotation.RequestMethod._
import org.semachina.web.data.Customer
import collection.mutable.MutableList
import com.hp.hpl.jena.ontology.OntModel
import com.weiglewilczek.slf4s.Logging
import org.springframework.context.{ApplicationContext, ApplicationContextAware}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 13, 2010
 * Time: 9:18:40 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
class CustomerController(ontModel: OntModel) extends ApplicationContextAware with Logging {
  protected var appContext: ApplicationContext = null;

  protected val customers: MutableList[Customer] = new MutableList[Customer]()

  def setApplicationContext(ctx: ApplicationContext): Unit = {
    appContext = ctx
  }

  @ModelAttribute("command")
  def createCustomerForFormBinding = new Customer

  @RequestMapping(value = Array("/customers/new"), method = Array(GET))
  def showNewCustomerForm() = "newCustomer"

  @RequestMapping(value = Array("/customers/new"), method = Array(POST))
  def createNewCustomer(@ModelAttribute("command") customer: Customer) = {
    val id = customers.size.toLong
    customer.id = id
    customers += customer
    logger.info("OntModel: " + ontModel.toString + " , appCtx: " + appContext.toString)
    "redirect:/customers/" + id + ".html"
  }


  @RequestMapping(value = Array("/customers/{customerId}"), method = Array(GET))
  def viewCustomer(@PathVariable customerId: Long) = {
    var customer = customers(customerId.toInt)
    new ModelAndView("customer", "customer", customer)
  }
}