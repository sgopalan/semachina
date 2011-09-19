package org.semachina.jena.ontology.impl

import com.hp.hpl.jena.ontology.impl.IndividualImpl
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.graph.Node
import org.semachina.jena.ontology.SemachinaIndividual

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/24/11
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */

class SemachinaIndividualImpl(n:Node, g: EnhGraph ) extends IndividualImpl( n,g ) with SemachinaIndividual {
  def getIndividual() = this
}