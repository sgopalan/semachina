package org.semachina.jena.ontology

import com.hp.hpl.jena.util.MonitorModel
import _root_.java.util.ArrayList
import _root_.java.lang.Boolean
import com.hp.hpl.jena.rdf.model.Statement
import com.hp.hpl.jena.ontology.{OntModel, Individual, OntClass}
import com.hp.hpl.jena.shared.{Lock, Command}
import com.hp.hpl.jena.graph.TransactionHandler
import org.semachina.jena.ontology.naming.IdStrategy
import org.semachina.jena.config.SemachinaBuilder
import com.hp.hpl.jena.enhanced.EnhGraph

object OntModelTransaction {
  def apply(
             ontModel: OntModel, enhGraph: EnhGraph = null): OntModelTransaction = {
    if (ontModel.isInstanceOf[OntModelTransaction]) {
      return ontModel.asInstanceOf[OntModelTransaction]
    }

    return new OntModelTransaction() {
      def getEnhGraph = if (enhGraph != null) enhGraph else ontModel.asInstanceOf[EnhGraph]

      def getOntModel = ontModel
    }
  }
}

trait OntModelTransaction extends URIResolver {

  def createIndividual(newURI: String, cls: OntClass): Individual = {
    return createIndividual(newURI, cls, true)
  }

  def createIndividual(newURI: String, cls: OntClass, isUnique: Boolean): SemachinaIndividual = {
    var expandedURI: String = if (newURI != null) resolveURI(newURI) else null
    if (isUnique && expandedURI != null) {
      var indiv: Individual = getOntModel.getIndividual(expandedURI)
      if (indiv != null) {
        throw new IllegalArgumentException("Individual " + expandedURI + " exists: " + indiv)
      }
    }
    val indiv = getOntModel.createIndividual(expandedURI, cls)
    return SemachinaIndividual(indiv)
  }

  def createIndividual(
                        newURI: String, cls: OntClass, isUnique: Boolean, idStrategy: IdStrategy): SemachinaIndividual = {
    var expandedURI: String = if (newURI != null) resolveURI(newURI) else null
    if (isUnique && expandedURI != null) {
      var indiv: Individual = getOntModel.getIndividual(expandedURI)
      if (indiv != null) {
        throw new IllegalArgumentException("Individual " + expandedURI + " exists: " + indiv)
      }
    }
    return SemachinaIndividual(getOntModel.createIndividual(expandedURI, cls), idStrategy)
  }

  def createIndividual(newURI: String, clazzes: Iterable[OntClass]): SemachinaIndividual = {
    return createIndividual(newURI, clazzes, true)
  }

  def createIndividual(newURI: String, clazzes: Iterable[OntClass], isUnique: Boolean): SemachinaIndividual = {
    var expandedURI: String = if (newURI != null) resolveURI(newURI) else null

    var iterator = clazzes.iterator
    if (iterator.hasNext) {
      var indiv = createIndividual(expandedURI, iterator.next, isUnique)
      iterator.foreach {
        ontClazz => indiv.addRDFType(ontClazz)
      }
      return indiv
    }

    throw new IllegalArgumentException("Must include at least one OntClass")
  }

  def createIndividual(
                        newURI: String, clazzes: Iterable[OntClass], isUnique: Boolean, idStrategy: IdStrategy): SemachinaIndividual = {
    var expandedURI: String = if (newURI != null) resolveURI(newURI) else null

    var iterator = clazzes.iterator
    if (iterator.hasNext) {
      var indiv = createIndividual(expandedURI, iterator.next, isUnique, idStrategy)
      iterator.foreach {
        ontClazz => indiv.addRDFType(ontClazz)
      }
      return indiv
    }

    throw new IllegalArgumentException("Must include at least one OntClass")
  }

  def readonly(closure: OntModel => Unit): Unit = {
    val command = ReadWriteContext(closure)
    readonly(command)
  }

  def readonly(command: ReadWriteContext): Unit = {
    var wrapped: Command = new Command {
      def execute: AnyRef = {
        var transactModel: OntModel = SemachinaBuilder().build
        transactModel.addSubModel(getOntModel)
        try {
          command.execute(transactModel)
        }
        catch {
          case e: Exception => {
            throw new RuntimeException(e.getMessage, e)
          }
        }
        return Boolean.TRUE
      }
    }
    wrapped.execute
  }

  def transact(command: ReadWriteContext): Unit = {
    var wrapped: Command = new Command {
      def execute: AnyRef = {
        try {
          var monitorModel: MonitorModel = new MonitorModel(getOntModel)
          var additions = new ArrayList[Statement]
          var deletions = new ArrayList[Statement]
          var transact = SemachinaBuilder(base = monitorModel).build
          monitorModel.snapshot
          command.execute(transact)
          monitorModel.snapshot(additions, deletions)
          getOntModel.rebind
        }
        catch {
          case e: Exception => {
            throw new RuntimeException(e.getMessage, e)
          }
        }
        return Boolean.TRUE
      }
    }
    try {
      getOntModel.enterCriticalSection(Lock.WRITE)
      var handler: TransactionHandler = getOntModel.getGraph.getTransactionHandler
      if (handler.transactionsSupported) {
        handler.executeInTransaction(wrapped)
      }
      else {
        wrapped.execute
      }
    }
    finally {
      getOntModel.leaveCriticalSection
    }
  }

  def transact(closure: OntModel => Unit): Unit = {
    val command = ReadWriteContext(closure)
    transact(command)
  }

}