package org.semachina.jena.impl.scala

import scala.collection.JavaConversions._

import com.hp.hpl.jena.datatypes.DatatypeFormatException
import com.hp.hpl.jena.datatypes.RDFDatatype
import com.hp.hpl.jena.datatypes.TypeMapper
import com.hp.hpl.jena.enhanced.EnhNode
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.graph.TransactionHandler
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.ontology.impl.OntModelImpl
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl
import com.hp.hpl.jena.shared.Lock
import org.semachina.jena.SemachinaOntModel
import org.semachina.jena.SemachinaIndividual
import org.semachina.jena.impl.SemachinaOntModelImpl
import org.semachina.jena.config.DefaultSemachinaFactory
import org.semachina.jena.features.Feature
import org.semachina.jena.ResultSetHandler
import org.semachina.jena.impl.SimpleReadWriteContext
import java.util.HashMap
import java.util.Iterator
import java.util.Map

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 4/1/11
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */

class ScalaOntModelImpl(spec: OntModelSpec, model: Model) extends SemachinaOntModelImpl(spec, model) {

  //there needs to be a better way of calling multiple super constructors
  def this(spec: OntModelSpec) = this( spec, spec.createBaseModel )

  def apply(uri:String) = resolveIndividual(uri).asInstanceOf[SemachinaIndividual]

  def select(query: Query, resultHandler: (ResultSet, QuerySolution) => Unit, initialBindings: QuerySolution): Unit = {
    val resultSetHandler: ResultSetHandler = prepareHandler(resultHandler)
    select(query, resultSetHandler, initialBindings)
  }

  def select(sparql: String, resultHandler: (ResultSet, QuerySolution) => Unit, initialBindings: QuerySolution): Unit = {
    val resultSetHandler: ResultSetHandler = prepareHandler(resultHandler)
    select(sparql, resultSetHandler, initialBindings)
  }

  def safeRead(closure: SemachinaOntModel => Unit) = {
    val command = new SimpleReadWriteContext() {
      def execute(transactionModel: SemachinaOntModel) = closure(transactionModel)
    }

    super.safeRead(command)
  }

  def safeWrite(persist:Model)(closure: SemachinaOntModel => Unit) = {
    val command = new SimpleReadWriteContext() {
      def execute(transactionModel: SemachinaOntModel) = closure(transactionModel)
    }

    super.safeWrite(command)
  }

  def safeWrite(closure: SemachinaOntModel => Unit) = {
    val command = new SimpleReadWriteContext() {
      def execute(transactionModel: SemachinaOntModel) = closure(transactionModel)
    }

    super.safeWrite(command)
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
}