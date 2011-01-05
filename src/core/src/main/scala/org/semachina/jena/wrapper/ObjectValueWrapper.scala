package org.semachina.jena.wrapper

import com.hp.hpl.jena.ontology.{ObjectProperty, Individual}
import scala.collection.JavaConversions._
import org.semachina.jena.{TypedDatatypeProperty, SemachinaIndividual}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 13, 2010
 * Time: 11:06:49 AM
 * To change this template use File | Settings | File Templates.
 */

class ObjectValueWrapper(indiv: SemachinaIndividual, ontProperty: ObjectProperty) {
  def / = indiv.getObject(ontProperty)

  def /+ = indiv.listObjects(ontProperty)

  def /(chainedProperty: ObjectProperty) = new ObjectValueWrapper(indiv.getObject(ontProperty), chainedProperty)

  def /[V](chainedProperty: TypedDatatypeProperty[V]) = new DataValueWrapper[V](indiv.getObject(ontProperty), chainedProperty)

  def <(value: Individual) = indiv.set(ontProperty, value)

  def <(values: Iterable[Individual]) = indiv.setAll(ontProperty, asIterable(values))

  def <<(value: Individual) = indiv.add(ontProperty, value)

  def <<(values: Iterable[Individual]) = indiv.addAll(ontProperty, asIterable(values))

  def -(value: Individual) = indiv.remove(ontProperty, value)

  def -(values: Iterable[Individual]) = indiv.removeThese(ontProperty, asIterable(values))

  def -- = indiv.removeAll(ontProperty)
}