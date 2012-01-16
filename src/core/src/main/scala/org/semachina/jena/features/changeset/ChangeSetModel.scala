package org.semachina.jena.features.changeset

import java.util.List
import com.hp.hpl.jena.rdf.model.{Statement, StmtIterator, Model, ModelChangedListener}


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 1/4/12
 * Time: 10:14 PM
 * To change this template use File | Settings | File Templates.
 */

class ChangeSetModel extends ModelChangedListener {
  def addedStatement(s: Statement) = addedStatements(Array(s))

  def addedStatements(statements: Array[Statement]) {}

  def addedStatements(statements: List[Statement]) {}

  def addedStatements(statements: StmtIterator) {}

  def addedStatements(m: Model) = addedStatements(m.listStatements())

  def removedStatement(s: Statement) {}

  def removedStatements(statements: Array[Statement]) {}

  def removedStatements(statements: List[Statement]) {}

  def removedStatements(statements: StmtIterator) {}

  def removedStatements(m: Model) = removedStatements(m.listStatements())

  def notifyEvent(m: Model, event: AnyRef) = {
    /**ignore */
  }
}