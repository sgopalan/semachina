package org.semachina.jena.core

import com.hp.hpl.jena.ontology.{OntClass, Individual, OntProperty, OntModel}
import com.hp.hpl.jena.graph.TransactionHandler
import com.hp.hpl.jena.shared.{Command, Lock}
import com.hp.hpl.jena.util.MonitorModel
import com.hp.hpl.jena.rdf.model.{Statement, Model}
import collection.mutable.{MutableList}
import com.weiglewilczek.slf4s.Logging

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 6, 2010
 * Time: 2:06:26 PM
 * To change this template use File | Settings | File Templates.
 */

trait ScalaOntModel extends OntModel with Logging {
  protected val initHooks: MutableList[() => Unit] = new MutableList[() => Unit]()

  protected val closeHooks: MutableList[() => Unit] = new MutableList[() => Unit]()

  def addInitHook(hook: () => Unit) {
    initHooks += hook
  }

  def addCloseHook(hook: () => Unit) {
    closeHooks += hook
  }

  def init = {
    setNsPrefixes(OWLFactory.PREFIX)
    initHooks.foreach(hook => hook())
  }

  protected def expandURI(property: String): String = {
    var uri: String = expandPrefix(property)
    if (uri == null) {
      throw new IllegalArgumentException("URI cannot be evaluated as null: " + property)
    }
    return uri
  }

  def expandToOntClass(uri: String): OntClass = {
    val expandedURI: String = expandPrefix(uri)

    val ontClass = getOntClass(expandedURI);
    if (ontClass == null) {
      throw new IllegalArgumentException("There is no OntClass for: " + uri)
    }

    return ontClass
  }

  def expandToIndividual(uri: String): Individual = {
    val expandedURI: String = expandPrefix(uri)
    val indiv = getIndividual(expandedURI);
    if (indiv == null) {
      throw new IllegalArgumentException("There is no Individual for: " + uri)
    }

    return indiv
  }

  def expandToOntProperty(property: String): OntProperty = {
    val uri: String = expandURI(property)
    val ontProperty: OntProperty = getOntProperty(uri)
    if (ontProperty == null) {
      throw new IllegalArgumentException("There is no Property for: " + property + " (" + uri + ") ")
    }

    return ontProperty
  }

  protected def processUpdates(adds: java.util.ArrayList[Statement], dels: java.util.ArrayList[Statement]) = {}

  def ->(cmd: Model => Unit) = {
    try {
      val monitor: MonitorModel = new MonitorModel(this)
      var adds: java.util.ArrayList[Statement] = null
      var dels: java.util.ArrayList[Statement] = null

      val command = new Command() {
        override def execute: java.lang.Object = {
          cmd(monitor)

          adds = new java.util.ArrayList[Statement]
          dels = new java.util.ArrayList[Statement]
          monitor.snapshot(adds, dels)

          //if you update the base model in Jena, does the ontmodel update as well
          processUpdates(adds, dels);
          return true.asInstanceOf[AnyRef]
        }
      }

      enterCriticalSection(Lock.WRITE)
      val handler: TransactionHandler = getGraph.getTransactionHandler
      if (handler.transactionsSupported) {
        handler.executeInTransaction(command)
      }
      else {
        try {
          command.execute
        }
        catch {
          case e: Exception => {

            if (adds != null && dels != null) {
              //try to put everything back
              add(dels)
              remove(adds)
            }
            throw e
          }
        }
      }

    }
    finally {
      leaveCriticalSection
    }
  }
}