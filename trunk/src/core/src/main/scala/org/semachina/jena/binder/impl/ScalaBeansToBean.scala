package org.semachina.jena.binder.impl

import com.hp.hpl.jena.ontology.{Individual, OntModel}
import org.semachina.jena.binder.annotations.SemachinaBinding
import org.scalastuff.scalabeans.Preamble._
import java.net.URI
import com.hp.hpl.jena.rdf.model._
import scala.collection.JavaConversions._
import collection.mutable.{HashMap, ArrayBuilder}
import org.scalastuff.scalabeans.PropertyDescriptor
import org.semachina.jena.binder.ToBeanBinder
import com.hp.hpl.jena.sparql.path.{PathEval, PathParser, Path}
import org.scalastuff.scalabeans.types._
import org.semachina.jena.binder.annotations.java._

/**
 * ScalaBeans-based implementation of <code>ToBeanBinder</code>
 */
trait ScalaBeansToBean extends ToBeanBinder {

  def getOntModel: OntModel

  def toBean[V <: AnyRef](individual: Individual, beanClass: Class[V]): V = {
    val beanCache = new HashMap[String, AnyRef]
    return toBean(individual, beanClass, beanCache)
  }

  protected def toBean[V <: AnyRef](
                                     individual: Individual,
                                     beanClass: Class[V],
                                     beanCache: HashMap[String, AnyRef]): V = {

    if (beanCache.contains(individual.getURI)) {
      val bean = beanCache(individual.getURI)
      if (bean.isInstanceOf[Class[V]]) {
        return bean.asInstanceOf[V]
      }
    }
    val prefixMapping = SemachinaBinding.getPrefixMapping(beanClass)
    SemachinaBinding.checkRDFType(individual, beanClass, prefixMapping)

    val descriptor = descriptorOf(beanClass)
    val builder = descriptor.newBuilder()

    val properties = descriptor.properties.foreach {
      property: PropertyDescriptor =>

        property.findAnnotation[Id].flatMap {
          idAnnotation =>
            val id = individual.getURI
            if (id != null) {
              val propertyClass = property.scalaType.erasure
              if (propertyClass.isAssignableFrom(classOf[URI])) {
                builder.set(property, URI.create(id))
              }
              else if (propertyClass.isAssignableFrom(classOf[String])) {
                builder.set(property, id)
              }
              else {
                throw new IllegalStateException("Id MUST be String or URI")
              }
            }
            None
        }
        property.findAnnotation[RdfProperty].flatMap {
          rdfPropertyAnnotation: RdfProperty =>
            val expandedURI = prefixMapping.expandPrefix(rdfPropertyAnnotation.value())
            val rdfProperty = getOntModel.getProperty(expandedURI)

            //Need to consider containers when marshalling/unmarshalling
            rdfPropertyAnnotation.container match {
              case RdfContainer.Seq => {}
              case RdfContainer.Alt => {}
              case RdfContainer.Bag => {}
              case RdfContainer.RdfList => {}
              case RdfContainer.None => {}
            }

            property.scalaType match {
              case at@ArrayType(OptionType(componentType)) => {
                val arrBuilder: ArrayBuilder[Any] = at.newArrayBuilder()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    arrBuilder += Option(propertyValue)
                }
                builder.set(property, arrBuilder.result())
              }
              case at@ArrayType(componentType) => {
                val arrBuilder: ArrayBuilder[Any] = at.newArrayBuilder()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    arrBuilder += propertyValue
                }
                builder.set(property, arrBuilder.result())
              }
              case at@ListType(OptionType(componentType)) => {
                val listBuilder = at.newBuilder.get.apply()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    listBuilder += Option(propertyValue)
                }
                builder.set(property, listBuilder.result())
              }
              case at@ListType(componentType) => {
                var listBuilder = at.newBuilder.get.apply()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    listBuilder += propertyValue
                }
                builder.set(property, listBuilder.result())
              }

              case at@SeqType(OptionType(_)) => {
                var seqBuilder = at.newBuilder.get.apply()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(
                        rdfNode, at.erasure.getComponentType.getComponentType, beanCache)

                    seqBuilder += Option(propertyValue)
                }
                builder.set(property, seqBuilder.result())
              }

              case at@SeqType(_) => {
                var seqBuilder = at.newBuilder.get.apply()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(rdfNode, at.erasure.getComponentType, beanCache)
                    seqBuilder += propertyValue
                }
                builder.set(property, seqBuilder.result())
              }
              case at@IterableType(OptionType(_)) => {
                var itBuilder = at.newBuilder.get.apply()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(
                        rdfNode, at.erasure.getComponentType.getComponentType, beanCache)
                    itBuilder += Option(propertyValue)
                }
                builder.set(property, itBuilder.result())
              }
              case at@IterableType(_) => {
                var itBuilder = at.newBuilder.get.apply()
                individual.listPropertyValues(rdfProperty).foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(rdfNode, at.erasure.getComponentType, beanCache)
                    itBuilder += propertyValue
                }
                builder.set(property, itBuilder.result())
              }
              case OptionType(componentType) => {
                val value = individual.getPropertyValue(rdfProperty)
                if (value != null) {
                  val propertyValue =
                    Option(resolvePropertyValue(value, componentType.erasure, beanCache))
                  builder.set(property, propertyValue)
                }
                else {
                  builder.set(property, None)
                }
              }
              case inner: AnyRef => {
                val value = individual.getPropertyValue(rdfProperty)
                if (value != null) {
                  val propertyValue =
                    resolvePropertyValue(value, property.scalaType.erasure, beanCache)
                  builder.set(property, propertyValue)
                }
              }
            }
            None
        }

        property.findAnnotation[PropertyPath].flatMap {
          propertyPathAnnotation: PropertyPath =>
            val path: Path = PathParser.parse(propertyPathAnnotation.value, prefixMapping)
            val values = PathEval.walkForwards(getOntModel, individual, path)

            property.scalaType match {
              case at@ArrayType(OptionType(componentType)) => {
                val arrBuilder: ArrayBuilder[Any] = at.newArrayBuilder()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    arrBuilder += Option(propertyValue)
                }
                builder.set(property, arrBuilder.result())
              }
              case at@ArrayType(componentType) => {
                val arrBuilder: ArrayBuilder[Any] = at.newArrayBuilder()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    arrBuilder += propertyValue
                }
                builder.set(property, arrBuilder.result())
              }
              case at@ListType(OptionType(componentType)) => {
                val listBuilder = at.newBuilder.get.apply()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    listBuilder += Option(propertyValue)
                }
                builder.set(property, listBuilder.result())
              }
              case at@ListType(componentType) => {
                var listBuilder = at.newBuilder.get.apply()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue = resolvePropertyValue(rdfNode, componentType.erasure, beanCache)
                    listBuilder += propertyValue
                }
                builder.set(property, listBuilder.result())
              }

              case at@SeqType(OptionType(_)) => {
                var seqBuilder = at.newBuilder.get.apply()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(
                        rdfNode, at.erasure.getComponentType.getComponentType, beanCache)

                    seqBuilder += Option(propertyValue)
                }
                builder.set(property, seqBuilder.result())
              }

              case at@SeqType(_) => {
                var seqBuilder = at.newBuilder.get.apply()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(rdfNode, at.erasure.getComponentType, beanCache)
                    seqBuilder += propertyValue
                }
                builder.set(property, seqBuilder.result())
              }
              case at@IterableType(OptionType(_)) => {
                var itBuilder = at.newBuilder.get.apply()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(
                        rdfNode, at.erasure.getComponentType.getComponentType, beanCache)
                    itBuilder += Option(propertyValue)
                }
                builder.set(property, itBuilder.result())
              }
              case at@IterableType(_) => {
                var itBuilder = at.newBuilder.get.apply()
                values.foreach {
                  rdfNode: RDFNode =>
                    val propertyValue =
                      resolvePropertyValue(rdfNode, at.erasure.getComponentType, beanCache)
                    itBuilder += propertyValue
                }
                builder.set(property, itBuilder.result())
              }
              case OptionType(componentType) => {
                val value = values.toList.head
                if (value != null) {
                  val propertyValue =
                    Option(resolvePropertyValue(value, componentType.erasure, beanCache))
                  builder.set(property, propertyValue)
                }
                else {
                  builder.set(property, None)
                }
              }
              case inner: AnyRef => {
                val value = values.toList.head
                if (value != null) {
                  val propertyValue =
                    resolvePropertyValue(value, property.scalaType.erasure, beanCache)
                  builder.set(property, propertyValue)
                }
              }
            }
            None
        }
    }
    val bean = builder.result().asInstanceOf[V]

    beanCache(individual.getURI) = bean

    return bean
  }

  protected def resolvePropertyValue[V](
                                         rdfNode: RDFNode,
                                         beanClass: Class[V],
                                         beanCache: HashMap[String, AnyRef]): V = {

    if (rdfNode.isLiteral) {
      val lit = rdfNode.asLiteral
      if (beanClass.isAssignableFrom(classOf[Literal])) {
        return lit.asInstanceOf[V]
      }
      else if (beanClass.isAssignableFrom(classOf[String])) {
        return lit.getString.asInstanceOf[V]
      }
      else if (beanClass.isAssignableFrom(classOf[URI])) {
        return URI.create(lit.getString).asInstanceOf[V]
      }
      else {
        return lit.getValue.asInstanceOf[V]
      }
    }

    if (beanClass.isAssignableFrom(classOf[Statement])) {
      return rdfNode.as(classOf[ReifiedStatement]).getStatement.asInstanceOf[V]
    }

    if (rdfNode.canAs(classOf[Individual])) {
      val individual = rdfNode.as(classOf[Individual])
      if (beanClass.isAssignableFrom(classOf[Individual]) ||
        beanClass.isAssignableFrom(classOf[Resource])) {
        return individual.asInstanceOf[V]
      }

      if (beanClass.getAnnotation(classOf[RdfType]) != null) {
        return toBean[AnyRef](
          individual, beanClass.asInstanceOf[Class[AnyRef]], beanCache).asInstanceOf[V]
      }

      throw new IllegalStateException("individual MUST be individual")
    }



    if (beanClass.isAssignableFrom(classOf[Resource])) {
      return rdfNode.asResource().asInstanceOf[V]
    }

    throw new IllegalStateException("MUST be literal, resource, or individual")
  }
}
