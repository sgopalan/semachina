package org.semachina.jena.config

import com.hp.hpl.jena.sparql.core.describe.{DescribeHandlerRegistry, DescribeHandlerFactory}
import com.hp.hpl.jena.sparql.function.{FunctionRegistry, FunctionFactory}
import com.hp.hpl.jena.query.ARQ
import com.hp.hpl.jena.sparql.pfunction.{PropertyFunctionFactory, PropertyFunctionRegistry}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 4, 2011
 * Time: 1:02:42 PM
 * To change this template use File | Settings | File Templates.
 */

trait ArqConfiguration {

  //filter function registry
  val filterFunctionRegistry = FunctionRegistry.get

  //describe handler registry
  val describeHandlerRegistry = DescribeHandlerRegistry.get

  //property function registry
  val propertyFunctionRegistry = PropertyFunctionRegistry.get

  def registerDescribeHandler(handlerFactory: DescribeHandlerFactory) = {
    describeHandlerRegistry.add(handlerFactory)
    this
  }

  def registerFilterFunction(uri: String, functionFactory: FunctionFactory) = {
    filterFunctionRegistry.put(uri, functionFactory)
    this
  }

  def registerFilterFunction(uri: String, functionClazz: Class[_]) = {
    filterFunctionRegistry.put(uri, functionClazz)
    this
  }

  def enablePropertyFunctions(enable: Boolean) = ARQ.set(ARQ.enablePropertyFunctions, enable)

  def registerPropertyFunction(uri: String, functionFactory: PropertyFunctionFactory) = {
    propertyFunctionRegistry.put(uri, functionFactory)
    this
  }

  def registerPropertyFunction(uri: String, functionClazz: Class[_]) = {
    propertyFunctionRegistry.put(uri, functionClazz)
    this
  }
}