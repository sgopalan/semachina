package org.semachina.jena.ontology.impl

import com.hp.hpl.jena.ontology.Individual
import org.semachina.jena.ontology.SemachinaIndividual
import javax.validation.constraints.NotNull

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

class SemachinaIndividualAdapter(@NotNull val individual:Individual) extends SemachinaIndividual {
  def getIndividual() = individual
}