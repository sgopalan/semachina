package org.semachina.jena.ontology

import com.hp.hpl.jena.shared.PrefixMapping
import com.hp.hpl.jena.sparql.path.{PathEval, PathParser, Path}
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.util.iterator.{ExtendedIterator, Map1, WrappedIterator}
import impl.SemachinaIndividualAdapter
import org.semachina.jena.ontology.SemachinaOntModel._
import scala.collection.JavaConversions._
import com.hp.hpl.jena.datatypes.RDFDatatype

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
  protected var stmtToObjectConverter: Map1[Statement, Individual] = new Map1[Statement, Individual] {
    def map1(x: Statement): Individual = {
      var i: Individual = x.getObject.as(classOf[Individual])
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
  protected var nodeToObjectConverter: Map1[RDFNode, Individual] = new Map1[RDFNode, Individual] {
    def map1(x: RDFNode): Individual = {
      var i: Individual = x.as(classOf[Individual])
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

   def apply(individual: Individual) = asSemachinaIndividualTrait( individual )

   implicit def asSemachinaIndividualTrait(individual: Individual): SemachinaIndividual = {
    if (individual.isInstanceOf[SemachinaIndividual]) {
      return individual.asInstanceOf[SemachinaIndividual]
    }
    else {
      return new SemachinaIndividualAdapter(individual)
    }
  }
}

trait SemachinaIndividual {

  def getIndividual(): Individual



  def path(propPath: String): NodeIterator = {
    var path: Path = PathParser.parse(propPath, getIndividual.getOntModel.asInstanceOf[PrefixMapping])
    return PathEval.walkForwards(getIndividual.getOntModel, getIndividual, path)
  }

  def get(propOrPath: String): Individual = {
    var ontProperty: Property = getIndividual.getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return get(ontProperty)
    }
    var path: Path = PathParser.parse(propOrPath, getIndividual.getOntModel.asInstanceOf[PrefixMapping])
    return get(path)
  }

  def get(path: Path): Individual = {
    var iterator: NodeIterator = PathEval.walkForwards(getIndividual.getOntModel, getIndividual, path)
    try {
      if (iterator.hasNext) {
        var next: Individual = iterator.next.as(classOf[Individual])
        return next
      }
    }
    finally {
      iterator.close
    }
    return null
  }

  def get(ontProperty: Property): Individual = {
    var rdfNode: RDFNode = getIndividual.getPropertyValue(ontProperty)
    if (rdfNode == null) {
      return null
    }
    if (rdfNode.isResource) {
      var next: Individual = rdfNode.as(classOf[Individual])
      return next
    }
    throw new IllegalStateException(rdfNode + " is not an object")
  }

  def list(propOrPath: String): ExtendedIterator[Individual] = {
    var ontProperty: Property = getIndividual.getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return list(ontProperty)
    }
    var path: Path = PathParser.parse(propOrPath, getIndividual.getOntModel.asInstanceOf[PrefixMapping])
    return list(path)
  }

  def list(ontProperty: Property): ExtendedIterator[Individual] = {
    val iterator = getIndividual.listProperties(ontProperty).mapWith(SemachinaIndividual.stmtToObjectConverter)
    return WrappedIterator.create( iterator )
  }

  def list(path: Path): ExtendedIterator[Individual] = {
    var iterator: NodeIterator = PathEval.walkForwards(getIndividual.getOntModel, getIndividual, path)
    return WrappedIterator.create(iterator.mapWith(SemachinaIndividual.nodeToObjectConverter))
  }

  def value[V<: Object](propOrPath: String, clazz: Class[V]): V = {
    var ontProperty: Property = getIndividual.getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return value(ontProperty, clazz)
    }
    var path: Path = PathParser.parse(propOrPath, getIndividual.getOntModel.asInstanceOf[PrefixMapping])
    return value(path, clazz)
  }

  def value[V<: Object](ontProperty: Property, clazz: Class[V]): V = {
    var rdfNode: RDFNode = getIndividual.getPropertyValue(ontProperty)
    if (rdfNode == null) {
      return null.asInstanceOf[V]
    }
    else if (rdfNode.isLiteral) {
      val lit = rdfNode.asLiteral
      if( clazz.isAssignableFrom( classOf[Literal])) {
        return lit.asInstanceOf[V]
      }
      return lit.getValue.asInstanceOf[V]
    }
    throw new IllegalStateException(rdfNode + " is not a literal")
  }

  def value[V<: Object](path: Path, clazz: Class[V]): V = {
    var iterator: NodeIterator = PathEval.walkForwards(getIndividual.getOntModel, getIndividual, path)
    try {
      if (iterator.hasNext) {
        val lit = iterator.next.asLiteral
        if( clazz.isAssignableFrom( classOf[Literal])) {
          return lit.asInstanceOf[V]
        }
        return lit.getValue.asInstanceOf[V]
      }
    }
    finally {
      iterator.close
    }
    return null.asInstanceOf[V]
  }

  def value[V<: Object](propOrPath: String, clazz: Class[V], converter: Map1[AnyRef, V]): V = {
    var ontProperty: Property = getIndividual.getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return value(ontProperty, clazz, converter)
    }
    var path: Path = PathParser.parse(propOrPath, getIndividual.getOntModel.asInstanceOf[PrefixMapping])
    return value(path, clazz, converter)
  }

  def value[V<: Object](ontProperty: Property, clazz: Class[V], converter: Map1[AnyRef, V]): V = {
    var rdfNode: RDFNode = getIndividual.getPropertyValue(ontProperty)
    if (rdfNode == null) {
      return null.asInstanceOf[V]
    }
    if (rdfNode.isLiteral) {
      val lit = rdfNode.asLiteral
      if( clazz.isAssignableFrom( classOf[Literal])) {
        return lit.asInstanceOf[V]
      }
      var value: AnyRef = lit.getValue
      if (converter != null) {
        return converter.map1(value)
      }
      return value.asInstanceOf[V]
    }
    throw new IllegalStateException(rdfNode + " is not a literal")
  }

  def value[V<: Object](path: Path, clazz: Class[V], converter: Map1[AnyRef, V]): V = {
    var iterator: NodeIterator = PathEval.walkForwards(getIndividual.getOntModel, getIndividual, path)
    try {
      if (iterator.hasNext) {
        val lit = iterator.next.asLiteral
        if( clazz.isAssignableFrom( classOf[Literal])) {
          return lit.asInstanceOf[V]
        }
        var value: AnyRef = lit.getValue
        if (converter != null) {
          return converter.map1(value)
        }
        return value.asInstanceOf[V]
      }
    }
    finally {
      iterator.close
    }
    return null.asInstanceOf[V]
  }

  def value[V <: Object](propOrPath: String, transform: Object => V)(implicit m: scala.reflect.Manifest[V]): V  =
    value( propOrPath, m.erasure.asInstanceOf[Class[V]], map1( transform ) )

  def value[V <: Object](ontProperty:Property, transform: Object => V)(implicit m: scala.reflect.Manifest[V]): V  =
    value( ontProperty, m.erasure.asInstanceOf[Class[V]], map1( transform ) )

  def value[V <: Object](path:Path, transform: Object => V)(implicit m: scala.reflect.Manifest[V]): V  =
    value( path, m.erasure.asInstanceOf[Class[V]], map1( transform ) )

  def value[V <: Object](propOrPath: String)(implicit m: scala.reflect.Manifest[V]) : V =
    value( propOrPath, m.erasure.asInstanceOf[Class[V]] )

  def value[V <: Object](ontProperty:Property)(implicit m: scala.reflect.Manifest[V]) : V =
    value( ontProperty, m.erasure.asInstanceOf[Class[V]] )

  def value[V <: Object](path:Path)(implicit m: scala.reflect.Manifest[V]) : V =
    value( path, m.erasure.asInstanceOf[Class[V]] )

  def list[V](propOrPath: String, clazz: Class[V]): ExtendedIterator[V] = {
    var ontProperty: Property = getIndividual.getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return list(ontProperty, clazz)
    }
    var path: Path = PathParser.parse(propOrPath, getIndividual.getOntModel.asInstanceOf[PrefixMapping])
    return list(path, clazz)
  }

  def list[V](ontProperty: Property, clazz: Class[V]): ExtendedIterator[V] = {
    var stmtToValueConverter: Map1[Statement, V] = new Map1[Statement, V] {
      def map1(x: Statement): V = {
        var value: AnyRef = x.getLiteral.getValue
        return clazz.cast(value)
      }
    }
    return WrappedIterator.create(getIndividual.listProperties(ontProperty).mapWith(stmtToValueConverter))
  }

  def list[V](path: Path, clazz: Class[V]): ExtendedIterator[V] = {
    var nodeToValueConverter: Map1[RDFNode, V] = new Map1[RDFNode, V] {
      def map1(x: RDFNode): V = {
        var value: AnyRef = x.asLiteral.getValue
        return clazz.cast(value)
      }
    }
    var iterator: NodeIterator = PathEval.walkForwards(getIndividual.getOntModel, getIndividual, path)
    return WrappedIterator.create(iterator.mapWith(nodeToValueConverter))
  }

  def list[V](propOrPath: String, converter: Map1[AnyRef, V]): ExtendedIterator[V] = {
    var ontProperty: Property = getIndividual.getOntModel.resolveProperty(propOrPath)
    if (ontProperty != null) {
      return list(ontProperty, converter)
    }
    var path: Path = PathParser.parse(propOrPath, getIndividual.getOntModel.asInstanceOf[PrefixMapping])
    return list(path, converter)
  }

  def list[V](ontProperty: Property, converter: Map1[AnyRef, V]): ExtendedIterator[V] = {
    var stmtToValueConverter: Map1[Statement, V] = new Map1[Statement, V] {
      def map1(x: Statement): V = {
        var value: AnyRef = x.getLiteral.getValue
        if (converter != null) {
          return converter.map1(value)
        }
        return value.asInstanceOf[V]
      }
    }
    return WrappedIterator.create(getIndividual.listProperties(ontProperty).mapWith(stmtToValueConverter))
  }

  def list[V](path: Path, converter: Map1[AnyRef, V]): ExtendedIterator[V] = {
    var nodeToValueConverter: Map1[RDFNode, V] = new Map1[RDFNode, V] {
      def map1(x: RDFNode): V = {
        var value: AnyRef = x.asLiteral.getValue
        if (converter != null) {
          return converter.map1(value)
        }
        return value.asInstanceOf[V]
      }
    }
    var iterator: NodeIterator = PathEval.walkForwards(getIndividual.getOntModel, getIndividual, path)
    return WrappedIterator.create(iterator.mapWith(nodeToValueConverter))
  }

  def values[V <: Object](propOrPath: String, transform: Object => V) =
    list( propOrPath, map1( transform ) )

  def values[V <: Object](ontProperty:Property, transform: Object => V) =
    list( ontProperty, map1( transform ) )

  def values[V <: Object](path:Path, transform: Object => V) =
    list( path, map1( transform ) )

  def values[V <: Object](propOrPath: String)(implicit m: scala.reflect.Manifest[V]) =
    list( propOrPath, m.erasure.asInstanceOf[Class[V]] )

  def values[V <: Object](ontProperty:Property)(implicit m: scala.reflect.Manifest[V]) =
    list( ontProperty, m.erasure.asInstanceOf[Class[V]] )

  def values[V <: Object](path:Path)(implicit m: scala.reflect.Manifest[V]) =
    list( path, m.erasure.asInstanceOf[Class[V]] )


  //set
  def set(values: Pair[Any, Any]*): Individual = {
    values.foreach {
      case (ontProperty, value) =>
        val prop = asProperty( ontProperty )
        removeAll( prop )
        process( prop , value, { (ontProperty, value) => getIndividual.addProperty( ontProperty, value ) } )
    }
    return getIndividual
  }

  def setNotNull(values: Pair[Any, Any]*): Individual = {
    values.foreach {
      case (ontProperty, value) =>
        if( value != null ) {
          val prop = asProperty( ontProperty )
          removeAll( prop )
          process( prop , value, { (ontProperty, value) => getIndividual.addProperty( ontProperty, value ) } )
        }
    }
    return getIndividual
  }

  def set(ontProperty: Property, value: Object, dataType: RDFDatatype) : Individual = {
    val dataValue : RDFNode = getIndividual.getOntModel.createTypedLiteral( value, dataType )
    return set( ontProperty, dataValue )
  }

  def setNotNull(ontProperty: Property, value: Object, dataType: RDFDatatype) : Individual =
    if( value != null) { set(ontProperty, value, dataType ) } else return getIndividual

  def set(ontProperty: Property, value: RDFNode): Individual = {
    getIndividual.setPropertyValue(ontProperty, value)
    return getIndividual
  }

  def setNotNull(ontProperty: Property, value: RDFNode): Individual =
     if( value != null) { set(ontProperty, value ) } else return getIndividual

  def set(ontProperty: String, value: RDFNode): Individual = {
    return set(getIndividual.getOntModel.resolveProperty(ontProperty), value)
  }

  def setNotNull(ontProperty: String, value: RDFNode): Individual =
    if( value != null) { set(ontProperty, value ) } else return getIndividual

  def set(ontProperty: Property, values: java.lang.Iterable[_ <: RDFNode]): Individual =
    set( ontProperty, asIterable( values ) )

  def set(ontProperty: Property, values: Iterable[_ <: RDFNode]): Individual = {
    getIndividual.removeAll(ontProperty)
    if (values != null) {
      for (value <- values) {
        add(ontProperty, value)
      }
    }
    return getIndividual
  }

  def set(ontProperty: Property, values: Iterable[Object], dataType: RDFDatatype): Individual = {
    getIndividual.removeAll(ontProperty)
    if (values != null) {
      for (value <- values) {
        val dataValue : RDFNode = getIndividual.getOntModel.createTypedLiteral( value, dataType )
        add(ontProperty, dataValue)
      }
    }
    return getIndividual
  }

  def set(ontProperty: String, values: java.lang.Iterable[_ <: RDFNode]): Individual =
    return set( getIndividual.getOntModel.resolveProperty(ontProperty), asIterable( values ) )

  def set(ontProperty: String, values: Iterable[_ <: RDFNode]): Individual = {
    return set(getIndividual.getOntModel.resolveProperty(ontProperty), values)
  }

  def add(ontProperty: Property, value: RDFNode): Individual = {
    getIndividual.addProperty(ontProperty, value)
    return getIndividual
  }

  def addNotNull(ontProperty: Property, value: RDFNode) : Individual =
    if( value != null ) { add(ontProperty, value) } else return getIndividual

  def add(ontProperty: String, value: RDFNode): Individual = {
    return add(getIndividual.getOntModel.resolveProperty(ontProperty), value)
  }

  def addNotNull(ontProperty: String, value: RDFNode) : Individual =
    if( value != null ) { add(ontProperty, value) } else return getIndividual

  def add(ontProperty: Property, value: Object, dataType: RDFDatatype) : Individual = {
    val dataValue : RDFNode = getIndividual.getOntModel.createTypedLiteral( value, dataType )
    return add( ontProperty, dataValue )
  }

  def addNotNull(ontProperty: Property, value: Object, dataType: RDFDatatype) : Individual =
    if( value != null) { add(ontProperty, value, dataType ) }  else return getIndividual

  def add(ontProperty: Property, values: java.lang.Iterable[_ <: RDFNode]): Individual =
    add( ontProperty, asIterable( values ) )

  def add(ontProperty: Property, values: Iterable[_ <: RDFNode]): Individual = {
    if (values != null) {
      for (value <- values) {
        add(ontProperty, value)
      }
    }
    return getIndividual
  }

  def add(ontProperty: String, values: java.lang.Iterable[_ <: RDFNode]): Individual =
    return add( getIndividual.getOntModel.resolveProperty(ontProperty), asIterable( values ) )

  def add(ontProperty: String, values: Iterable[_ <: RDFNode]): Individual = {
    return add(getIndividual.getOntModel.resolveProperty(ontProperty), values)
  }

  def add(ontProperty: Property, values: Iterable[Object], dataType: RDFDatatype): Individual = {
    if (values != null) {
      for (value <- values) {
        val dataValue : RDFNode = getIndividual.getOntModel.createTypedLiteral( value, dataType )
        add(ontProperty, dataValue)
      }
    }
    return getIndividual
  }

  def remove(ontProperty: Property, value: RDFNode): Individual = {
    getIndividual.removeProperty(ontProperty, value)
    return getIndividual
  }

  def removeThese(ontProperty: Property, values: Iterable[_ <: RDFNode]): Individual = {
    if (values != null) {
      for (value <- values) {
        remove(ontProperty, value)
      }
    }
    return getIndividual
  }




  def remove(ontProperty: String, value: RDFNode): Individual = {
    return remove(getIndividual.getOntModel.resolveProperty(ontProperty), value)
  }

  def removeThese(ontProperty: String, values: Iterable[_ <: RDFNode]): Individual = {
    return removeThese(getIndividual.getOntModel.resolveProperty(ontProperty), values)
  }





  //add
  def add(values: Pair[Any, Any]*): Individual = {
    values.foreach {
      case (ontProperty, value) =>
        process( asProperty( ontProperty ), value, { (ontProperty, value) => getIndividual.addProperty( ontProperty, value ) } )
    }
    return getIndividual
  }

  //remove
  def remove(values: Pair[Any, Any]*): Individual = {
    values.foreach {
      case (ontProperty, value) =>
        process( asProperty( ontProperty ), value, { (ontProperty, value) => getIndividual.removeProperty( ontProperty, value ) } )
    }
    return getIndividual
  }

  def removeAll(ontProperties: Property*): Individual = {
    ontProperties.foreach {
      ontProperty => getIndividual.removeAll(asProperty(ontProperty))
    }
    return getIndividual
  }

  //other actions
  protected def map1[V <: Object](transform: Object => V): Map1[Object, V] = {
    new Map1[Object, V] {
      def map1(value: AnyRef): V = {
        return transform(value)
      }
    }
  }

  protected def asProperty(ontProperty: Any): Property = {
    ontProperty match {
      case str : String => {
        val ontModel = getIndividual.getOntModel
        val prop = ontModel.resolveProperty( str )
        if( prop == null ) {
          throw new IllegalArgumentException("Must be String or Property: " + ontProperty)
        }
        return prop
      }
      case prop : Property => prop
      case _ => throw new IllegalArgumentException("Must be String or Property: " + ontProperty)
    }
  }

  protected def process(ontProperty: Property, value: Any, action: (Property, RDFNode) => Unit) {
    value match {
      case node: RDFNode => action( ontProperty, node )
      case iterator: java.lang.Iterable[_] => iterator.foreach{ process( ontProperty, _ , action ) }
      case iterator: Iterable[_] => iterator.foreach { process( ontProperty, _ , action ) }
      //case product: Product => process( ontProperty, product.productIterator, action )
      case any : Any => action( ontProperty, getIndividual.getOntModel.createTypedLiteral( any ) )
      case _ => throw new IllegalArgumentException("Something is broken: " + ontProperty.toString() + " : " + value )
    }
  }
}