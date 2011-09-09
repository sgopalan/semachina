package org.semachina.jena.impl.scala

import org.mindswap.pellet.Individual
import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.graph.Node
import org.semachina.jena.SemachinaIndividualTrait

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/24/11
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */

class SemachinaIndividualImpl(n:Node, g: EnhGraph ) extends IndividualImpl( n,g ) with SemachinaIndividualTrait {
  def getIndividual() = this


}