package org.semachina.jena.enhanced

import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.enhanced.EnhNode
import com.hp.hpl.jena.graph.Node
import collection.JavaConversions.JIterableWrapper
import com.hp.hpl.jena.rdf.model.RDFNode

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 6:56:25 PM
 * To change this template use File | Settings | File Templates.
 */



abstract class SemachinaImplementationImpl[V <: RDFNode](
    clazz : Class[V],
    failMessage: String,
    supportedClazzes: Iterable[Class[_]])
  extends SemachinaImplementation[V] {

  protected def createInstance(node: Node, enhGraph:EnhGraph) : EnhNode

  def this(clazz : Class[V], failMessage: String,supportedClazzes: java.lang.Iterable[Class[_]]) =
    this( clazz, failMessage, JIterableWrapper(supportedClazzes ) )

  def this(clazz : Class[V], failMessage: String, supportedClazz: Class[_]) =
    this( clazz, failMessage, List[Class[_]]( supportedClazz ) )

  val implClass = clazz
  val convertFailedMessage = failMessage
  val supportedClasses = supportedClazzes
  val create = (node: Node, enhGraph:EnhGraph) => createInstance( node, enhGraph )
}