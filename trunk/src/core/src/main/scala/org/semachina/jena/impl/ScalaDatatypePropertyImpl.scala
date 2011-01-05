package org.semachina.jena.impl

import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.enhanced.{EnhNode, EnhGraph}
import com.hp.hpl.jena.rdf.model.Literal
/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 16, 2010
 * Time: 8:15:08 PM
 * To change this template use File | Settings | File Templates.
 */

class ScalaDatatypePropertyImpl[V](
        node: Node, graph: EnhGraph, val convert: Literal => V = {it: Literal => it.getValue().asInstanceOf[V]})
        extends SemachinaTypedDatatypePropertyImpl[V](node, graph) {
  def this(indiv: EnhNode) = this (indiv.asNode(), indiv.getGraph())

  def this(indiv: EnhNode, convert: Literal => V) = this (indiv.asNode(), indiv.getGraph(), convert)
}
