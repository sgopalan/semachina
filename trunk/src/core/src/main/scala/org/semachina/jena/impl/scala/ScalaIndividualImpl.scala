package org.semachina.jena.impl.scala

import org.semachina.jena.impl.SemachinaIndividualImpl
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.RDFNode
import com.hp.hpl.jena.util.iterator.ExtendedIterator
import org.semachina.jena.SemachinaOntModel
import org.semachina.jena.SemachinaIndividual
import com.hp.hpl.jena.sparql.path.Path
import com.hp.hpl.jena.util.iterator.Map1
import com.hp.hpl.jena.ontology.impl.OntResourceImpl._
import org.semachina.jena.impl.SemachinaIndividualImpl._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 4/1/11
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */

class ScalaIndividualImpl(n: Node, g: EnhGraph) extends SemachinaIndividualImpl(n, g) {

  //retrieve Single values
  def obj(propOrPath: Any): Option[SemachinaIndividual] = {
    val value = propOrPath match {
      case str:String =>  getObject( str )
      case ontProperty:Property => getObject( ontProperty )
      case path:Path => getObject( path )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
    return Option( value )
  }

  def literal(propOrPath: Any): Option[Literal] = {
    val value = propOrPath match {
      case str:String =>  getLiteral( str )
      case ontProperty:Property => getLiteral( ontProperty )
      case path:Path => getLiteral( path )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
    return Option( value )
  }

  def value[V <: Object](propOrPath: Any, transform: Object => V): Option[V] = {
    val converter = map1( transform )
    val value = propOrPath match {
      case str:String =>  getValue( str, converter )
      case ontProperty:Property => getValue( ontProperty, converter )
      case path:Path => getValue( path, converter )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
    return Option( value )
  }

  def value[V <: Object](propOrPath: Any)(implicit m: scala.reflect.Manifest[V]): Option[V] = {
    val clazz = m.erasure.asInstanceOf[Class[V]]
    val value = propOrPath match {
      case str:String =>  getValue( str, clazz )
      case ontProperty:Property => getValue( ontProperty, clazz )
      case path:Path => getValue( path, clazz )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
    return Option( value )
  }

  def list(propOrPath: Any): ExtendedIterator[SemachinaIndividual] = {
    propOrPath match {
      case str:String =>  listObjects( str )
      case ontProperty:Property => listObjects( ontProperty )
      case path:Path => listObjects( path )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
  }

  def literals(propOrPath: Any): ExtendedIterator[Literal] = {
    propOrPath match {
      case str:String =>  listLiterals( str )
      case ontProperty: Property => listLiterals( ontProperty )
      case path:Path => listLiterals( path )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
  }

  def values[V <: Object](propOrPath: Any, transform: Object => V): ExtendedIterator[V] = {
    val converter = map1( transform )
    propOrPath match {
      case str:String =>  listValues( str, converter )
      case ontProperty:Property => listValues( ontProperty, converter )
      case path:Path => listValues( path, converter )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
  }

  def values[V <: Object](propOrPath: Any)(implicit m: scala.reflect.Manifest[V]): ExtendedIterator[V] = {
    val clazz = m.erasure.asInstanceOf[Class[V]]
    propOrPath match {
      case str:String =>  listValues( str, clazz )
      case ontProperty:Property => listValues( ontProperty, clazz )
      case path:Path => listValues( path, clazz )
      case _ => throw new IllegalArgumentException( "Must be String, Property, or Path: " + propOrPath )
    }
  }

  //set
  def set(values: Pair[Any, Any]*): SemachinaIndividual = {
    values.foreach {
      case (ontProperty, value) =>
        process( asProperty( ontProperty ), value, { (ontProperty, value) => setPropertyValue( ontProperty, value ) } )
    }
    return this
  }
  //add
  def add(values: Pair[Any, Any]*): SemachinaIndividual = {
    values.foreach {
      case (ontProperty, value) =>
        process( asProperty( ontProperty ), value, { (ontProperty, value) => addProperty( ontProperty, value ) } )
    }
    return this
  }

  //remove
  def remove(values: Pair[Any, Any]*): SemachinaIndividual = {
    values.foreach {
      case (ontProperty, value) =>
        process( asProperty( ontProperty ), value, { (ontProperty, value) => removeProperty( ontProperty, value ) } )
    }
    return this
  }

  def removeAll(ontProperties: Property*): SemachinaIndividual = {
    ontProperties.foreach {
      ontProperty => super.removeAll(asProperty(ontProperty))
    }
    return this
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
      case str : String => getOntModel.resolveProperty( str )
      case prop : Property => prop
      case _ => throw new IllegalArgumentException("Must be String or Property: " + ontProperty)
    }
  }

  protected def process(ontProperty: Property, value: Any, action: (Property, RDFNode) => Unit) {
    value match {
      case node: RDFNode => action( ontProperty, node )
      case product: Product => process( ontProperty, product.productIterator, action )
      case iterator: Iterable[Any] => iterator.foreach { process( ontProperty, _ , action ) }
      case any : Any => action( ontProperty, getOntModel().createTypedLiteral( any ) )
    }
  }
}