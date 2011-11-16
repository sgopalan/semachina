package org.semachina.jena.query

import com.hp.hpl.jena.shared.Lock
import com.hp.hpl.jena.query._

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.Model
import org.semachina.jena.config.SemachinaBuilder
import collection.mutable.ArrayBuffer


trait Arq {

  /**
   * return The underlying <code>OntModel</code>
   */
  def getOntModel: OntModel

  def createQuery(sparql: String): Query = {
    var query: Query = new Query
    query.getPrefixMapping.withDefaultMappings(getOntModel)
    query = QueryFactory.parse(query, sparql, null, Syntax.defaultSyntax)
    return query
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

  def ask(sparql: String, initialBindings: QuerySolution = null): Boolean =
    ask(createQuery(sparql), initialBindings)

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
      owlModel = SemachinaBuilder(base = resultModel).build
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

  def describe(sparql: String, initialBindings: QuerySolution = null): OntModel =
    describe(createQuery(sparql), initialBindings)

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
      owlModel = SemachinaBuilder(base = resultModel).build
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

  def construct(sparql: String, initialBindings: QuerySolution = null): OntModel =
    construct(createQuery(sparql), initialBindings)

  def select(
              query: Query,
              handler: SemachinaQuerySolution => Unit,
              initialBindings: QuerySolution): Unit = {

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

      val rs: ResultSet = qexec.execSelect
      while (rs.hasNext) {
        val soln = rs.next()
        val rowNumber = rs.getRowNumber
        val semachinaSoln = SemachinaQuerySolution.toSemachinaQuerySolution(soln, rowNumber, rs)

        handler(semachinaSoln)
      }
    }
    finally {
      getOntModel.leaveCriticalSection
      closeQueryExecution(qexec)
      qexec.close
    }
  }

  def selectList(
                  query: Query,
                  initialBindings: QuerySolution): List[SemachinaQuerySolution] = {

    val list = new ArrayBuffer[SemachinaQuerySolution]
    val handler: SemachinaQuerySolution => Unit = {
      soln => list += soln
    }
    select(query, handler, initialBindings)
    list.toList
  }

  def select(
              sparql: String,
              handler: SemachinaQuerySolution => Unit,
              initialBindings: QuerySolution = null): Unit =
    select(createQuery(sparql), handler, initialBindings)

  def selectList(
                  sparql: String,
                  initialBindings: QuerySolution = null): List[SemachinaQuerySolution] =
    selectList(createQuery(sparql), initialBindings)

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
}