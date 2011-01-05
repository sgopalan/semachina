package org.semachina.jena.wrapper

import com.hp.hpl.jena.rdf.model.Literal

import scala.collection.JavaConversions._
import org.semachina.jena.{SemachinaOntModel, TypedDatatypeProperty, SemachinaIndividual}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 13, 2010
 * Time: 11:06:49 AM
 * To change this template use File | Settings | File Templates.
 */

class DataValueWrapper[T](indiv: SemachinaIndividual, ontProperty: TypedDatatypeProperty[T]) {
  val model = indiv.getOntModel.asInstanceOf[SemachinaOntModel]

  def / = indiv.getLiteral(ontProperty)

  def /+ = indiv.listLiterals(ontProperty)

  def <(value: Literal) = indiv.set(ontProperty, value)

  def <(values: Iterable[Literal]) = indiv.setAll(ontProperty, asIterable(values))

  def <<(value: Literal) = indiv.add(ontProperty, value)

  def <<(values: Iterable[Literal]) = indiv.addAll(ontProperty, asIterable(values))

  def -(value: Literal) = indiv.remove(ontProperty, value)

  def -(values: Iterable[Literal]) = indiv.removeThese(ontProperty, asIterable(values))

  def -- = indiv.removeAll(ontProperty)

  def /# = indiv.getValue[T](ontProperty)

  def /#+ = indiv.listValues[T](ontProperty)

  def <#[T](value: T) = indiv.set(ontProperty, model.createTypedLiteral(value))

  def <#[T](values: Iterable[T]) =
    indiv.setAll(ontProperty, asIterable(values.collect {case x: T => model.createTypedLiteral(x)}))

  def <<#[T](value: T) = indiv.add(ontProperty, model.createTypedLiteral(value))

  def <<#[T](values: Iterable[T]) =
    indiv.addAll(ontProperty, asIterable(values.collect {case x: T => model.createTypedLiteral(x)}))

  def -#[T](value: T) =
    indiv.remove(ontProperty, model.createTypedLiteral(value))

  def -#[T](values: Iterable[T]) =
    indiv.removeThese(ontProperty, asIterable(values.collect {case x: T => model.createTypedLiteral(x)}))
}
