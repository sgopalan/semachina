package org.semachina.jena.ontology.impl

import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.graph.Node
import org.semachina.jena.ontology.SemachinaIndividual
import org.semachina.jena.ontology.java.JavaSemachinaIndividual
import org.semachina.jena.ontology.naming.IdStrategy
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.enhanced.{EnhNode, EnhGraph}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/24/11
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */

class SemachinaIndividualImpl(
                               n: Node, g: EnhGraph, override val idStrategy: Option[IdStrategy])
  extends IndividualImpl(n, g) with SemachinaIndividual with JavaSemachinaIndividual {

  def this(individual: Individual, idStrategy: Option[IdStrategy]) =
    this (individual.asNode, individual.asInstanceOf[EnhNode].getGraph, idStrategy)
}