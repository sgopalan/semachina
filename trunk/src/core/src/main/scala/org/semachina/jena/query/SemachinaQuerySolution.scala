package org.semachina.jena.query

import com.hp.hpl.jena.ontology.Individual
import org.semachina.jena.ontology._
import org.semachina.jena.config.SemachinaConfiguration
import com.hp.hpl.jena.query.{QuerySolutionMap, ResultSet, QuerySolution}
import com.hp.hpl.jena.rdf.model.{RDFNode, Literal}

object SemachinaQuerySolution {
  def initialBindings(bindings: Pair[String, RDFNode]*) = {
    val map = new QuerySolutionMap
    bindings.foreach {
      case (varName, value) => map.add(varName, value)
    }
    map
  }

  def toSemachinaQuerySolution(soln: QuerySolution, rowNum: Int, aResultSet: ResultSet) = {
    new SemachinaQuerySolution {
      def querySolution: QuerySolution = soln

      val rowNumber: Int = rowNum

      def resultSet: ResultSet = aResultSet
    }
  }
}

trait SemachinaQuerySolution {
  def querySolution: QuerySolution

  val rowNumber: Int

  def resultSet: ResultSet

  def getResultVars = resultSet.getResultVars

  def getIndividual(varName: String): SemachinaIndividual = {
    return SemachinaIndividual(querySolution.get(varName).as(classOf[Individual]))
  }

  def getIndividual(varName: OptionalString): Option[SemachinaIndividual] =
    Option(getIndividual(varName.value))

  def value[V <: Object](varName: String)(implicit m: Manifest[V]): V = {
    val clazz = m.erasure.asInstanceOf[Class[V]]
    var literal = querySolution.getLiteral(varName)
    if (literal == null) {
      return null.asInstanceOf[V]
    }

    if (clazz.isAssignableFrom(classOf[Literal])) {
      return literal.asInstanceOf[V]
    }

    var value: AnyRef = literal.getValue
    if (value.isInstanceOf[V]) {
      return value.asInstanceOf[V]
    }

    return SemachinaConfiguration.getObjectBinderOption[V](value.getClass)(m).getOrElse[V] {
      throw new IllegalArgumentException(value + " is not of type " + m.erasure.toString)
    }
  }

  def value[V <: Object](varName: OptionalString)(implicit m: Manifest[V]): Option[V] =
    Option(value[V](varName.value)(m))

  /**Return the value of the named variable in this binding, casting to a Resource.
   *  A return of null indicates that the variable is not present in this solution.
   *  An exception indicates it was present but not a resource.
   * @param varName
   * @return Resource
   */
  def getResource(varName: String) = querySolution.getResource(varName)

  def getResource(varName: OptionalString) = Option(querySolution.getResource(varName.value))

  /**Return the value of the named variable in this binding, casting to a Literal.
   *  A return of null indicates that the variable is not present in this solution.
   *  An exception indicates it was present but not a literal.
   * @param varName
   * @return Resource
   */
  def getLiteral(varName: String) = querySolution.getLiteral(varName)

  def getLiteral(varName: OptionalString) = Option(querySolution.getLiteral(varName.value))


  /**Return true if the named variable is in this binding */
  def contains(varName: String) = querySolution.contains(varName)

  /**Iterate over the variable names (strings) in this QuerySolution.
   * @return Iterator of strings
   */
  def varNames = querySolution.varNames()
}