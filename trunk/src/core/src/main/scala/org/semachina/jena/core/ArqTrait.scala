package org.semachina.jena.core


import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.shared.Lock
import com.hp.hpl.jena.query._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 6, 2010
 * Time: 1:54:20 PM
 * To change this template use File | Settings | File Templates.
 */

trait ArqTrait extends ScalaOntModel {
  def ask(query: Query, initialBindings: QuerySolution = null): Boolean = {
    if (!query.isAskType) {
      throw new IllegalArgumentException("Must be SPARQL ask query")
    }

    return doQuery[Boolean](query, initialBindings, {
      queryExec =>
        return queryExec.execAsk
    })
  }

  def describe(query: Query, initialBindings: QuerySolution = null): OntModel = {
    if (!query.isDescribeType) {
      throw new IllegalArgumentException("Must be SPARQL describe query")
    }

    return doQuery[OntModel](query, initialBindings, {
      queryExec =>
        val resultModel: Model = queryExec.execDescribe
        val owlModel = ScalaOntModelImpl(resultModel)
        owlModel.withDefaultMappings(this)
        return owlModel
    })
  }

  def construct(query: Query, initialBindings: QuerySolution = null): OntModel = {

    if (!query.isConstructType) {
      throw new IllegalArgumentException("Must be SPARQL construct query")
    }

    return doQuery[OntModel](query, initialBindings, {
      queryExec =>
        val resultModel: Model = queryExec.execConstruct
        val owlModel = ScalaOntModelImpl(resultModel)
        owlModel.withDefaultMappings(this)
        return owlModel
    })
  }

  def select(query: Query,
             initialBindings: QuerySolution,
             resultHandler: (ResultSet, QuerySolution) => Unit): Unit = {

    if (!query.isSelectType) {
      throw new IllegalArgumentException("Must be SPARQL select query")
    }

    return doQuery[Unit](query, initialBindings, {
      queryExec =>
        var rs: ResultSet = queryExec.execSelect
        while (rs.hasNext) {
          val result: QuerySolution = rs.nextSolution
          resultHandler(rs, result)
        }
    })
  }

  def select(query: Query, resultHandler: (ResultSet, QuerySolution) => Unit): Unit =
    select(query, null, resultHandler);

  protected def prepareQueryExecution(queryExec: QueryExecution) = {
  }

  protected def closeQueryExecution(queryExec: QueryExecution) = {
  }

  protected def doQuery[Q](query: Query, initialBindings: QuerySolution = null, execute: QueryExecution => Q): Q = {
    var queryExec: QueryExecution = null
    if (initialBindings != null) {
      queryExec = QueryExecutionFactory.create(query, this, initialBindings)
    }
    else {
      queryExec = QueryExecutionFactory.create(query, this)
    }

    prepareQueryExecution(queryExec)
    try {
      enterCriticalSection(Lock.READ)
      return execute(queryExec)
    }
    finally {
      leaveCriticalSection
      closeQueryExecution(queryExec)
      queryExec.close
    }
  }
}