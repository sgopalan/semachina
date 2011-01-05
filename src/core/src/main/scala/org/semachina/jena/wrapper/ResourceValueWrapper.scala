package org.semachina.jena.wrapper

import com.hp.hpl.jena.ontology.ObjectProperty
import com.hp.hpl.jena.rdf.model.RDFNode
import scala.collection.JavaConversions._
import org.semachina.jena.{SemachinaOntModel, ResourceProperty, TypedDatatypeProperty, SemachinaIndividual}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 13, 2010
 * Time: 11:06:49 AM
 * To change this template use File | Settings | File Templates.
 */

class ResourceValueWrapper(indiv: SemachinaIndividual, ontProperty: ResourceProperty) {
  val model = indiv.getOntModel.asInstanceOf[SemachinaOntModel]

  def / = indiv.getPropertyValue(ontProperty)

  def /+ = indiv.listPropertyValues(ontProperty)

  def /(chainedProperty: ObjectProperty) = new ObjectValueWrapper(indiv.getObject(ontProperty), chainedProperty)

  def /[V](chainedProperty: TypedDatatypeProperty[V]) = new DataValueWrapper[V](indiv.getObject(ontProperty), chainedProperty)

  def <(value: RDFNode) = indiv.set(ontProperty, value)

  def <(values: Iterable[RDFNode]) = indiv.setAll(ontProperty, asIterable(values))

  def <<(value: RDFNode) = indiv.add(ontProperty, value)

  def <<(values: Iterable[RDFNode]) = indiv.addAll(ontProperty, asIterable(values))

  def -(value: RDFNode) = indiv.remove(ontProperty, value)

  def -(values: Iterable[RDFNode]) = indiv.removeThese(ontProperty, asIterable(values))

  def -- = indiv.removeAll(ontProperty)

  def <#[T](value: T) = indiv.set(ontProperty, model.createTypedLiteral(value))

  def <#[T](values: Iterable[T]) =
    indiv.setAll(ontProperty, asIterable(values.collect{
      case x: T => model.createTypedLiteral(x)
    }))

  def <<#[T](value: T) = indiv.add(ontProperty, model.createTypedLiteral(value))

  def <<#[T](values: Iterable[T]) =
    indiv.addAll(ontProperty, asIterable(values.collect{
      case x: T => model.createTypedLiteral(x)
    }))

  def -#[T](value: T) =
    indiv.remove(ontProperty, model.createTypedLiteral(value))

  def -#[T](values: Iterable[T]) =
    indiv.removeThese(ontProperty, asIterable(values.collect{
      case x: T => model.createTypedLiteral(x)
    }))

}