package org.semachina.jena.dsl

import com.hp.hpl.jena.query.{QuerySolution, Query}
import org.semachina.jena.query.{SemachinaQuerySolution, Arq}

class ArqWrapper(query: Query, arq: Arq) {

  def this(queryStr: String, arq: Arq) = this (arq.createQuery(queryStr), arq)

  def toQuery() = query

  def ask(initialBindings: QuerySolution = null) = arq.ask(query, initialBindings)

  def describe(initialBindings: QuerySolution = null) = arq.describe(query, initialBindings)

  def construct(initialBindings: QuerySolution = null) = arq.construct(query, initialBindings)

  def select(resultHandler: SemachinaQuerySolution => Unit, initialBindings: QuerySolution = null) =
    arq.select(query, resultHandler, initialBindings)

  def selectList(initialBindings: QuerySolution = null) = arq.selectList(query, initialBindings)
}