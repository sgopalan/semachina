package org.semachina.jena.binder.impl

import com.hp.hpl.jena.ontology.Individual
import org.semachina.jena.binder.annotations.SemachinaBinding
import org.scalastuff.scalabeans.Preamble._
import com.hp.hpl.jena.util.ResourceUtils
import org.semachina.jena.ontology.{SemachinaIndividual, SemachinaOntModel}
import org.semachina.jena.ontology.naming.IdStrategy
import org.semachina.jena.binder.ToIndividualBinder
import org.semachina.jena.binder.annotations.java.{RdfContainer, RdfType, RdfProperty, Id}
import com.hp.hpl.jena.rdf.model.{Container, RDFNode, Property}
import collection.JavaConversions._

/**
 * ScalaBeans-based implementation of <code>ToIndividualBinder</code>
 */
trait ScalaBeansToIndividual extends ToIndividualBinder {

  /**
   * SemachinaOntModel conversion.
   *
   * Trying not to use implicit conversions within core code to avoid confusion
   */
  private val semachina = SemachinaOntModel(getOntModel)

  def toIndividual(bean: AnyRef, executeIdStrategy: Boolean = true): SemachinaIndividual = {
    val beanClass = bean.getClass
    val descriptor = descriptorOf(beanClass)

    val prefixMapping = SemachinaBinding.getPrefixMapping(beanClass)
    val ontClasses = SemachinaBinding.getOntClasses(getOntModel, beanClass)

    var individual = findIdStrategy(bean) match {
      case some: Some[IdStrategy] => semachina.createIndividual(null, ontClasses, false, some.get)
      case None => semachina.createIndividual(null, ontClasses, false, null)
    }

    for {
      property <- descriptor.properties
    }
    yield {
      val value: Any = property.get[Any](bean)
      if (value != null && value != None) {

        property.findAnnotation[Id].foreach {
          idAnnotation: Id =>
            val expandedURI = prefixMapping.expandPrefix(value.toString)
            individual =
              SemachinaIndividual(
                ResourceUtils.renameResource(individual, expandedURI).as(classOf[Individual]))
        }
      }

      property.findAnnotation[RdfProperty].foreach {
        rdfPropertyAnnotation: RdfProperty =>
          val expandedURI = prefixMapping.expandPrefix(rdfPropertyAnnotation.value())
          val rdfProperty = getOntModel.getProperty(expandedURI)
          toResource(individual, rdfProperty, rdfPropertyAnnotation, value)
      }
    }

    if (executeIdStrategy) {
      return individual.executeIdStrategy
    }
    return individual
  }


  protected def findIdStrategy(bean: Any): Option[IdStrategy] = {
    val beanClass = bean.getClass()
    for {
      property <- descriptorOf(beanClass).properties
      if (property.scalaType.erasure.isAssignableFrom(classOf[IdStrategy]))
    } yield {
      return Option(property.get(bean.asInstanceOf[AnyRef]).asInstanceOf[IdStrategy])
    }

    None
  }

  protected def toResource(
                            individual: SemachinaIndividual,
                            rdfProperty: Property,
                            rdfPropertyAnnotation: RdfProperty,
                            value: Any): Unit = {

    val rdfNodes: Iterable[RDFNode] = if (value == null) null
    else value match {

      // is it an option
      case option: Option[Any] => {
        option match {
          case some: Some[Any] => Seq[RDFNode](toRDFNode(some.get, rdfPropertyAnnotation))
          case _ => Seq.empty[RDFNode]
        }
      }
      // is it a scala iterable
      case iterable: Iterable[_] => {
        iterable.collect {
          case innerOption: Option[Any] => toRDFNode(innerOption.get, rdfPropertyAnnotation)
          case inner: Any => toRDFNode(inner, rdfPropertyAnnotation)
        }
      }
      // is it a java iterable
      case iterable: _root_.java.lang.Iterable[_] => {
        iterable.collect {
          case innerOption: Option[Any] => toRDFNode(innerOption.get, rdfPropertyAnnotation)
          case inner: Any => toRDFNode(inner, rdfPropertyAnnotation)
        }
      }
      // is it an array of any sort
      case array: Array[Any] => array.collect {
        case innerOption: Option[Any] => toRDFNode(innerOption.get, rdfPropertyAnnotation)
        case inner: Any => toRDFNode(inner, rdfPropertyAnnotation)
      }
      case any: Any => Seq[RDFNode](toRDFNode(value, rdfPropertyAnnotation))
    }

    if (rdfNodes != null && !rdfNodes.isEmpty) {
      //Need to consider containers when marshalling/unmarshalling
      rdfPropertyAnnotation.container match {
        case RdfContainer.Seq => {
          var seq: Container = getOntModel.createSeq()
          rdfNodes.foreach {
            rdfNode => seq = seq.add(rdfNode)
          }
          individual.set(rdfProperty, seq)
        }
        case RdfContainer.Alt => {
          var alt: Container = getOntModel.createAlt()
          rdfNodes.foreach {
            rdfNode => alt = alt.add(rdfNode)
          }
          individual.set(rdfProperty, alt)
        }
        case RdfContainer.Bag => {
          var bag: Container = getOntModel.createBag()
          rdfNodes.foreach {
            rdfNode => bag = bag.add(rdfNode)
          }
          individual.set(rdfProperty, bag)
        }
        case RdfContainer.RdfList => {
          var rdfList = getOntModel.createList()
          rdfNodes.foreach {
            rdfNode => rdfList = rdfList.`with`(rdfNode)
          }
          individual.set(rdfProperty, rdfList)
        }
        case RdfContainer.None => {
          individual.set(rdfProperty, rdfNodes)
        }
      }

      rdfNodes collect {
        case i: SemachinaIndividual => i.executeIdStrategy
      }
    }
    else if (rdfPropertyAnnotation.removeOnEmpty()) {
      individual.remove(rdfProperty)
    }
  }

  protected def toRDFNode(
                           value: Any,
                           rdfPropertyAnnotation: RdfProperty): RDFNode = {

    if (value.getClass.getAnnotation(classOf[RdfType]) != null) {
      return toIndividual(value.asInstanceOf[AnyRef], false).asInstanceOf[RDFNode]
    }
    else {
      return SemachinaBinding.toRDFNode(getOntModel, value, rdfPropertyAnnotation.dataTypeURI())
    }
  }
}