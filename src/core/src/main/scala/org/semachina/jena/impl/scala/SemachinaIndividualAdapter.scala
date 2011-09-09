package org.semachina.jena.impl.scala

import com.hp.hpl.jena.ontology.Individual
import org.semachina.jena.SemachinaIndividualTrait

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/24/11
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaIndividualAdapter {
  def apply(individual : Individual) = new SemachinaIndividualAdapter( individual )
}

class SemachinaIndividualAdapter(val individual:Individual) extends SemachinaIndividualTrait {
  def getIndividual() = individual
}