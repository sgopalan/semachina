package org.semachina.jena.ontology

import com.hp.hpl.jena.shared.PrefixMapping
import com.hp.hpl.jena.sparql.path.{PathEval, PathParser, Path}
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.util.iterator.{ExtendedIterator, Map1, WrappedIterator}
import impl.SemachinaIndividualImpl
import org.semachina.jena.ontology.SemachinaOntModel._
import com.hp.hpl.jena.datatypes.RDFDatatype
import javax.validation.constraints.NotNull
import org.semachina.jena.ontology.naming.IdStrategy
import com.hp.hpl.jena.util.ResourceUtils
import org.semachina.jena.config.SemachinaConfiguration

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/21/11
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaIndividual {

  /**
   * A Mapper to convert <oode>Statement</code> to <code>SemachinaIndividual</code>
   */
  protected var stmtToObjectConverter: Map1[Statement, SemachinaIndividual] = new Map1[Statement, SemachinaIndividual] {
    def map1(x: Statement): SemachinaIndividual = {
      var i = SemachinaIndividual(x.getObject.as(classOf[Individual]))
      return i
    }
  }
  /**
   * A Mapper to convert <oode>Statement</code> to <code>Literal</code>
   */
  protected var stmtToLiteralConverter: Map1[Statement, Literal] = new Map1[Statement, Literal] {
    def map1(x: Statement): Literal = {
      return x.getLiteral
    }
  }
  /**
   * A Mapper to convert <oode>RDFNode</code> to <code>SemachinaIndividual</code>
   */
  protected var nodeToObjectConverter: Map1[RDFNode, SemachinaIndividual] = new Map1[RDFNode, SemachinaIndividual] {
    def map1(x: RDFNode): SemachinaIndividual = {
      var i = SemachinaIndividual(x.as(classOf[Individual]))
      return i
    }
  }
  /**
   * A Mapper to convert <oode>RDFNode</code> to <code>Literal</code>
   */
  protected var nodeToLiteralConverter: Map1[RDFNode, Literal] = new Map1[RDFNode, Literal] {
    def map1(x: RDFNode): Literal = {
      return x.asLiteral
    }
  }

  def apply(individual: Individual, idStrategy: IdStrategy = null): SemachinaIndividual = {
    if (idStrategy != null) {
      return new SemachinaIndividualImpl(individual, Option(idStrategy))
    }

    if (individual.isInstanceOf[SemachinaIndividual]) {
      return individual.asInstanceOf[SemachinaIndividual]
    }
    else {
      return new SemachinaIndividualImpl(individual, Option(idStrategy))
    }
  }

  def renameIndividual(old: Individual, uri: String): SemachinaIndividual = {
    val individual =
      SemachinaIndividual(ResourceUtils.renameResource(old, uri).as(classOf[Individual]))

    individual
  }


}

trait SemachinaIndividual extends Individual {

  val idStrategy: Option[IdStrategy]

  def ! = executeIdStrategy

  def withIdStrategy(idStrategy: IdStrategy) = SemachinaIndividual(this, idStrategy)

  def executeIdStrategy: SemachinaIndividual = {
    for {
      s <- idStrategy
    }
    yield {
      return SemachinaIndividual.renameIndividual(this, s.toId(this))
    }
    this
  }

  def apply(setup: SemachinaIndividual => Unit = null) = {
    if (setup != null) setup(this)

    executeIdStrategy
  }

  def getSeq(property: Property): Seq = {
    val rdfNode = getPropertyValue(property)
    return rdfNode.as(classOf[Seq])
  }

  def getRdfList(property: Property): RDFList = {
    val rdfNode = getPropertyValue(property)
    return rdfNode.as(classOf[RDFList])
  }

  def getAlt(property: Property): Alt = {
    val rdfNode = getPropertyValue(property)
    return rdfNode.as(classOf[Alt])
  }

  def getBag(property: Property): Bag = {
    val rdfNode = getPropertyValue(property)
    return rdfNode.as(classOf[Bag])
  }

  def get(propOrPath: String): SemachinaIndividual = {
    var ontProperty: Property = getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return get(ontProperty)
    }
    var path: Path = PathParser.parse(propOrPath, getOntModel.asInstanceOf[PrefixMapping])
    return get(path)
  }

  def get(propOrPath: OptionalString): Option[SemachinaIndividual] = Option(get(propOrPath.value))

  def get(path: Path): SemachinaIndividual = {
    var iterator: NodeIterator = PathEval.walkForwards(getOntModel, this, path)
    try {
      if (iterator.hasNext) {
        var next = SemachinaIndividual(iterator.next.as(classOf[Individual]))
        return next
      }
    }
    finally {
      iterator.close
    }
    return null
  }

  def get(path: OptionalPath): Option[SemachinaIndividual] = Option(get(path.value))

  def get(ontProperty: Property): SemachinaIndividual = {
    var rdfNode: RDFNode = getPropertyValue(ontProperty)
    if (rdfNode == null) {
      return null
    }
    if (rdfNode.isResource) {
      var next = SemachinaIndividual(rdfNode.as(classOf[Individual]))
      return next
    }
    throw new IllegalStateException(rdfNode + " is not an object")
  }

  def get(ontProperty: OptionalProperty): Option[SemachinaIndividual] = Option(get(ontProperty.value))

  def list(propOrPath: String): ExtendedIterator[SemachinaIndividual] = {
    var ontProperty: Property = getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return list(ontProperty)
    }
    var path: Path = PathParser.parse(propOrPath, getOntModel.asInstanceOf[PrefixMapping])
    return list(path)
  }

  def list(ontProperty: Property): ExtendedIterator[SemachinaIndividual] = {
    val iterator = listProperties(ontProperty).mapWith(SemachinaIndividual.stmtToObjectConverter)
    return WrappedIterator.create(iterator)
  }

  def list(path: Path): ExtendedIterator[SemachinaIndividual] = {
    var iterator: NodeIterator = PathEval.walkForwards(getOntModel, this, path)
    return WrappedIterator.create(iterator.mapWith(SemachinaIndividual.nodeToObjectConverter))
  }

  def bindAll[V](propOrPath: String)(implicit m: Manifest[V]): ExtendedIterator[V] = {
    var ontProperty: Property = getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return bindAll(ontProperty)(m)
    }
    var path: Path = PathParser.parse(propOrPath, getOntModel.asInstanceOf[PrefixMapping])
    return bindAll(path)(m)
  }

  def bindAll[V](ontProperty: Property)(implicit m: Manifest[V]): ExtendedIterator[V] = {
    val clazz = m.erasure.asInstanceOf[Class[V]]
    var stmtToValueConverter: Map1[Statement, V] = new Map1[Statement, V] {
      def map1(x: Statement): V = {
        var value: AnyRef = x.getLiteral.getValue
        if (value.isInstanceOf[V]) {
          return value.asInstanceOf[V]
        }
        return SemachinaConfiguration.getObjectBinderOption[V](value.getClass)(m)
          .getOrElse[V] {
          throw new IllegalArgumentException(value + " is not of type " + m.erasure.toString)
        }
      }
    }
    return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToValueConverter))
  }

  def bindAll[V](path: Path)(implicit m: Manifest[V]): ExtendedIterator[V] = {
    val clazz = m.erasure.asInstanceOf[Class[V]]
    var nodeToValueConverter: Map1[RDFNode, V] = new Map1[RDFNode, V] {
      def map1(x: RDFNode): V = {
        var value: AnyRef = x.asLiteral.getValue
        if (value.isInstanceOf[V]) {
          return value.asInstanceOf[V]
        }
        return SemachinaConfiguration.getObjectBinderOption[V](value.getClass)(m).getOrElse[V] {
          throw new IllegalArgumentException(value + " is not of type " + m.erasure.toString)
        }
      }
    }
    var iterator: NodeIterator = PathEval.walkForwards(getOntModel, this, path)
    return WrappedIterator.create(iterator.mapWith(nodeToValueConverter))
  }

  def value[V <: Object](propOrPath: String)(implicit m: Manifest[V]): V = {
    var ontProperty: Property = getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return value(ontProperty)(m)
    }
    var path: Path = PathParser.parse(propOrPath, getOntModel.asInstanceOf[PrefixMapping])
    return value(path)(m)
  }

  def value[V <: Object](propOrPath: OptionalString)(implicit m: Manifest[V]) =
    Option(value[V](propOrPath.value)(m))

  def value[V <: Object](ontProperty: Property)(implicit m: Manifest[V]): V = {
    val clazz = m.erasure.asInstanceOf[Class[V]]
    var rdfNode: RDFNode = getPropertyValue(ontProperty)
    if (rdfNode == null) {
      return null.asInstanceOf[V]
    }

    val lit = rdfNode.asLiteral
    if (clazz.isAssignableFrom(classOf[Literal])) {
      return lit.asInstanceOf[V]
    }

    var value: AnyRef = lit.getValue
    if (value.isInstanceOf[V]) {
      return value.asInstanceOf[V]
    }

    return SemachinaConfiguration.getObjectBinderOption[V](value.getClass)(m).getOrElse[V] {
      throw new IllegalArgumentException(value + " is not of type " + m.erasure.toString)
    }
  }

  def value[V <: Object](ontProperty: OptionalProperty)(implicit m: Manifest[V]) =
    Option(value[V](ontProperty.value)(m))

  def value[V <: Object](path: Path)(implicit m: Manifest[V]): V = {
    val clazz = m.erasure.asInstanceOf[Class[V]]
    var iterator: NodeIterator = PathEval.walkForwards(getOntModel, this, path)
    try {
      if (iterator.hasNext) {
        val lit = iterator.next.asLiteral
        if (clazz.isAssignableFrom(classOf[Literal])) {
          return lit.asInstanceOf[V]
        }

        var value: AnyRef = lit.getValue
        if (value.isInstanceOf[V]) {
          return value.asInstanceOf[V]
        }

        return SemachinaConfiguration.getObjectBinderOption[V](value.getClass)(m).getOrElse[V] {
          throw new IllegalArgumentException(value + " is not of type " + m.erasure.toString)
        }
      }
    }
    finally {
      iterator.close
    }
    return null.asInstanceOf[V]
  }

  def value[V <: Object](path: OptionalPath)(implicit m: Manifest[V]) =
    Option(value[V](path.value)(m))

  def values[V <: Object](propOrPath: String)(implicit m: scala.reflect.Manifest[V]) =
    bindAll[V](propOrPath)(m)

  def values[V <: Object](ontProperty: Property)(implicit m: scala.reflect.Manifest[V]) =
    bindAll[V](ontProperty)(m)

  def values[V <: Object](path: Path)(implicit m: scala.reflect.Manifest[V]) =
    bindAll[V](path)(m)

  //set
  def set(ontProperty: Property, @NotNull value: Object, dataType: RDFDatatype): Individual = {
    val dataValue: RDFNode = getOntModel.createTypedLiteral(value, dataType)
    return set(ontProperty, dataValue)
  }

  def set(ontProperty: Property, @NotNull value: RDFNode): Individual = {
    setPropertyValue(ontProperty, value)
    return this
  }

  def set(ontProperty: String, @NotNull value: Option[_ <: RDFNode]): Individual =
    set(getOntModel.resolveProperty(ontProperty), value)

  def set(ontProperty: Property, @NotNull value: Option[_ <: RDFNode]): Individual = {
    for {rdfNode <- value} yield {
      set(ontProperty, rdfNode)
    }
    return this
  }

  //does this work properly under the hood
  def set(ontProperty: String, @NotNull value: RDFNode): Individual = {
    return set(getOntModel.resolveProperty(ontProperty), value)
  }

  def set(ontProperty: Property, @NotNull values: Iterable[_ <: RDFNode]): Individual = {
    remove(ontProperty)
    if (values != null) {
      for (value <- values) {
        add(ontProperty, value)
      }
    }
    return this
  }

  def set(@NotNull ontProperty: Property, @NotNull values: Iterable[Object], @NotNull dataType: RDFDatatype): Individual = {
    remove(ontProperty)
    if (values != null) {
      for (value <- values) {
        val dataValue: RDFNode = getOntModel.createTypedLiteral(value, dataType)
        add(ontProperty, dataValue)
      }
    }
    return this
  }

  def set(ontProperty: String, values: Iterable[_ <: RDFNode]): Individual = {
    return set(getOntModel.resolveProperty(ontProperty), values)
  }

  def add(ontProperty: String, @NotNull value: Option[_ <: RDFNode]): Individual =
    add(getOntModel.resolveProperty(ontProperty), value)

  def add(ontProperty: Property, @NotNull value: Option[_ <: RDFNode]): Individual = {
    for {rdfNode <- value} yield {
      set(ontProperty, rdfNode)
    }
    return this
  }

  def add(@NotNull ontProperty: Property, @NotNull value: RDFNode): Individual = {
    addProperty(ontProperty, value)
    return this
  }

  def add(@NotNull ontProperty: String, @NotNull value: RDFNode): Individual = {
    return add(getOntModel.resolveProperty(ontProperty), value)
  }

  def add(@NotNull ontProperty: Property, @NotNull value: Object, dataType: RDFDatatype): Individual = {
    val dataValue: RDFNode = getOntModel.createTypedLiteral(value, dataType)
    return add(ontProperty, dataValue)
  }

  def add(@NotNull ontProperty: Property, @NotNull values: Iterable[_ <: RDFNode]): Individual = {
    if (values != null) {
      for (value <- values) {
        add(ontProperty, value)
      }
    }
    return this
  }

  def add(@NotNull ontProperty: String, @NotNull values: Iterable[_ <: RDFNode]): Individual = {
    return add(getOntModel.resolveProperty(ontProperty), values)
  }

  def add(@NotNull ontProperty: Property, @NotNull values: Iterable[Object], @NotNull dataType: RDFDatatype): Individual = {
    if (values != null) {
      for (value <- values) {
        val dataValue: RDFNode = getOntModel.createTypedLiteral(value, dataType)
        add(ontProperty, dataValue)
      }
    }
    return this
  }

  def remove(@NotNull ontProperty: Property, @NotNull value: RDFNode): Individual = {
    removeProperty(ontProperty, value)
    return this
  }

  def removeThese(@NotNull ontProperty: Property, @NotNull values: Iterable[_ <: RDFNode]): Individual = {
    if (values != null) {
      for (value <- values) {
        remove(ontProperty, value)
      }
    }
    return this
  }

  def remove(@NotNull ontProperty: String, @NotNull value: RDFNode): Individual = {
    return remove(getOntModel.resolveProperty(ontProperty), value)
  }

  def removeThese(@NotNull ontProperty: String, @NotNull values: Iterable[_ <: RDFNode]): Individual = {
    return removeThese(getOntModel.resolveProperty(ontProperty), values)
  }

  def remove(@NotNull ontProperties: AnyRef*): SemachinaIndividual = {
    ontProperties.foreach {
      ontProperty => ontProperty match {
        case str: String => removeAll(getOntModel.resolveProperty(str))
        case prop: Property => removeAll(prop)
      }
    }
    return this
  }

  protected def path(propPath: String): NodeIterator = {
    var path: Path = PathParser.parse(propPath, getOntModel.asInstanceOf[PrefixMapping])
    return PathEval.walkForwards(getOntModel, this, path)
  }
}