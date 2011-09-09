package org.semachina.jena

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.shared.{Command, Lock}
import com.hp.hpl.jena.ontology.{Individual, OntModel}
import config.{SemachinaFactory, SemachinaConfig}
import scala.collection.JavaConversions._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/19/11
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
trait OntModelReader {

  def getOntModel : OntModel

  def getURIResolver : URIResolver

  def apply(uri:String) = resolveIndividual(uri)

  def resolveIndividual(uri: String): Individual = {
    var indivURI: String = getURIResolver.expandURI(uri)
    return getOntModel.getIndividual(indivURI)
  }

  def ask(sparql: String, initialBindings: QuerySolution): Boolean = {
    var query: Query = createQuery(sparql)
    return ask(query, initialBindings)
  }

  def ask(query: Query, initialBindings: QuerySolution): Boolean = {
    if (!query.isAskType) {
      throw new IllegalArgumentException("Must be SPARQL ask query")
    }
    var qexec: QueryExecution = createQueryExecution(query, initialBindings)
    try {
      prepareQueryExecution(qexec)
      getOntModel.enterCriticalSection(Lock.READ)
      return qexec.execAsk
    }
    finally {
      getOntModel.leaveCriticalSection
      closeQueryExecution(qexec)
      qexec.close
    }
  }

  def describe(sparql: String, initialBindings: QuerySolution): OntModel = {
    var query: Query = createQuery(sparql)
    return describe(query, initialBindings)
  }

  def describe(query: Query, initialBindings: QuerySolution): OntModel = {
    if (!query.isDescribeType) {
      throw new IllegalArgumentException("Must be SPARQL describe query")
    }
    var qexec: QueryExecution = createQueryExecution(query, initialBindings)
    var owlModel: OntModel = null
    try {
      prepareQueryExecution(qexec)
      getOntModel.enterCriticalSection(Lock.READ)
      var resultModel: Model = qexec.execDescribe
      owlModel = SemachinaFactory.createOntologyModel(getOntModel.getSpecification, resultModel)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
    finally {
      getOntModel.leaveCriticalSection
      closeQueryExecution(qexec)
      qexec.close
    }
    return owlModel
  }

  def construct(sparql: String, initialBindings: QuerySolution): OntModel = {
    var query: Query = createQuery(sparql)
    return construct(query, initialBindings)
  }

  def construct(query: Query, initialBindings: QuerySolution): OntModel = {
    if (!query.isConstructType) {
      throw new IllegalArgumentException("Must be SPARQL construct query")
    }
    var qexec: QueryExecution = createQueryExecution(query, initialBindings)
    var owlModel: OntModel = null
    try {
      prepareQueryExecution(qexec)
      getOntModel.enterCriticalSection(Lock.READ)
      var resultModel: Model = qexec.execConstruct
      owlModel = SemachinaFactory.createOntologyModel(getOntModel.getSpecification, resultModel)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
    finally {
      getOntModel.leaveCriticalSection
      closeQueryExecution(qexec)
      qexec.close
    }
    return owlModel
  }

  def select(sparql: String, handler: ResultSetHandler, initialBindings: QuerySolution): Unit = {
    var query: Query = createQuery(sparql)
    select(query, handler, initialBindings)
  }

  def select(query: Query, handler: ResultSetHandler, initialBindings: QuerySolution): Unit = {
    if (handler == null) {
      return
    }
    if (!query.isSelectType) {
      throw new IllegalArgumentException("Must be SPARQL select query")
    }
    var qexec: QueryExecution = createQueryExecution(query, initialBindings)
    try {
      prepareQueryExecution(qexec)
      getOntModel.enterCriticalSection(Lock.READ)
      var rs: ResultSet = qexec.execSelect
      handler.handle(rs)
    }
    finally {
      getOntModel.leaveCriticalSection
      closeQueryExecution(qexec)
      qexec.close
    }
  }

  def select(sparql: String, resultHandler: (ResultSet, QuerySolution) => Unit, initialBindings: QuerySolution): Unit = {
    val resultSetHandler: ResultSetHandler = prepareHandler(resultHandler)
    select(sparql, resultSetHandler, initialBindings)
  }

  protected def createQueryExecution(query: Query, initialBindings: QuerySolution): QueryExecution = {
    var qexec: QueryExecution = QueryExecutionFactory.create(query, getOntModel)
    if (initialBindings != null) {
      qexec.setInitialBinding(initialBindings)
    }
    return qexec
  }

  protected def prepareQueryExecution(qexec: QueryExecution): Unit = {
  }

  protected def closeQueryExecution(queryExec: QueryExecution): Unit = {
  }

  protected def createQuery(sparql: String): Query = {
    var query: Query = new Query
    query.getPrefixMapping.withDefaultMappings(getOntModel)
    query = QueryFactory.parse(query, sparql, null, Syntax.defaultSyntax)
    return query
  }

    def select(query: Query, resultHandler: (ResultSet, QuerySolution) => Unit, initialBindings: QuerySolution): Unit = {
    val resultSetHandler: ResultSetHandler = prepareHandler(resultHandler)
    select(query, resultSetHandler, initialBindings)
  }

  def safeRead(closure: OntModel => Unit) : Unit = {
    val command = ReadWriteContext( closure )
    safeRead(command)
  }

  def safeRead(command: ReadWriteContext): Unit = {
    var wrapped: Command = new Command {
      def execute: AnyRef = {
        var transactModel: OntModel = SemachinaConfig.createOntologyModel
        transactModel.addSubModel(getOntModel)
        try {
          command.execute(transactModel)
        }
        catch {
          case e: Exception => {
            throw new RuntimeException(e.getMessage, e)
          }
        }
        return java.lang.Boolean.TRUE
      }
    }
    wrapped.execute
  }

  protected def prepareHandler(resultHandler: (ResultSet, QuerySolution) => Unit): ResultSetHandler = {
    if (resultHandler == null) {
      throw new IllegalArgumentException("resultHandler cannot be null")
    }

    val handler = new ResultSetHandler {
      def handle(rs: ResultSet): Unit = {
        rs.foreach {soln => resultHandler(rs, soln)}
      }
    }
    return handler
  }

   trait ResultSetHandler {
     def handle(rs: ResultSet): Unit
   }
}

