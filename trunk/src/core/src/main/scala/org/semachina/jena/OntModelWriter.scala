package org.semachina.jena

import com.hp.hpl.jena.util.MonitorModel
import config.SemachinaConfig
import java.util.ArrayList
import com.hp.hpl.jena.rdf.model.Statement
import com.hp.hpl.jena.ontology.{OntModelSpec, OntModel, Individual, OntClass}
import com.hp.hpl.jena.shared.{Lock, Command}
import com.hp.hpl.jena.graph.TransactionHandler

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/19/11
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
trait OntModelWriter {

  def getOntModel : OntModel

  def getURIResolver : URIResolver

  def createIndividual(newURI: String, cls: OntClass): Individual = {
    return createIndividual(newURI, cls, true)
  }

  def createIndividual(newURI: String, cls: OntClass, isUnique: Boolean): Individual = {
    var expandedURI: String = getURIResolver.expandURI(newURI)
    if (isUnique) {
      var indiv: Individual = getOntModel.getIndividual(expandedURI)
      if (indiv != null) {
        throw new IllegalArgumentException("Individual " + expandedURI + " exists: " + indiv)
      }
    }
    return getOntModel.createIndividual( expandedURI, cls )
  }

  def createIndividual(newURI: String, clazzes: Iterable[OntClass]): Individual = {
    return createIndividual(newURI, clazzes, true)
  }

  def createIndividual(newURI: String, clazzes: Iterable[OntClass], isUnique: Boolean): Individual = {
    var expandedURI: String = getURIResolver.expandURI(newURI)

    var iterator = clazzes.iterator
    if( iterator.hasNext ) {
        var indiv: Individual = createIndividual(newURI, iterator.next, isUnique)
        iterator.foreach { ontClazz => indiv.addRDFType( ontClazz ) }
        return indiv
    }

    throw new IllegalArgumentException( "Must include at least one OntClass" )
  }

  def safeWrite(command: ReadWriteContext): Unit = {
    var wrapped: Command = new Command {
      def execute: AnyRef = {
        try {
          var monitorModel: MonitorModel = new MonitorModel(getOntModel)
          var additions = new ArrayList[Statement]
          var deletions = new ArrayList[Statement]
          var transact = SemachinaConfig.createOntologyModel(OntModelSpec.OWL_MEM, monitorModel)
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
        return java.lang.Boolean.TRUE
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

  def safeWrite(closure: OntModel => Unit) : Unit = {
    val command = ReadWriteContext( closure )
    safeWrite(command)
  }

}