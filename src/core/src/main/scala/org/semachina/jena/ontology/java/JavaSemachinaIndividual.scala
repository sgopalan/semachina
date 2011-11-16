package org.semachina.jena.ontology.java

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.ontology.Individual
import org.semachina.jena.ontology.SemachinaOntModel._
import scala.collection.JavaConversions._
import javax.validation.constraints.NotNull
import org.semachina.jena.ontology.SemachinaIndividual

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/21/11
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */

trait JavaSemachinaIndividual extends SemachinaIndividual {

  def set(ontProperty: Property, @NotNull values: java.lang.Iterable[_ <: RDFNode]): Individual =
    set(ontProperty, iterableAsScalaIterable(values))

  def set(@NotNull ontProperty: String, @NotNull values: java.lang.Iterable[_ <: RDFNode]): Individual =
    return set(getOntModel.resolveProperty(ontProperty), iterableAsScalaIterable(values))

  def add(@NotNull ontProperty: Property, @NotNull values: java.lang.Iterable[_ <: RDFNode]): Individual =
    add(ontProperty, iterableAsScalaIterable(values))

  def add(@NotNull ontProperty: String, @NotNull values: java.lang.Iterable[_ <: RDFNode]): Individual =
    return add(getOntModel.resolveProperty(ontProperty), iterableAsScalaIterable(values))

  def removeThese(@NotNull ontProperty: String, @NotNull values: java.lang.Iterable[_ <: RDFNode]): Individual = {
    return removeThese(getOntModel.resolveProperty(ontProperty), iterableAsScalaIterable(values))
  }

}