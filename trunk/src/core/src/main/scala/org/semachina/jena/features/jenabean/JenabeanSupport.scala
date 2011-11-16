package org.semachina.jena.features.jenabean

import reflect.BeanProperty
import annotation.target.field
import com.hp.hpl.jena.ontology.{OntModel, Individual}
import org.semachina.jena.features.Feature
import thewebsemantic.{TypeWrapper, Bean2RDF, RDF2Bean}
import org.semachina.jena.dsl.SemachinaDSL._

/**
 * Support for JenaBeans RDF to Object bindings
 */
 object JenabeanSupport {
  type Id = thewebsemantic.Id@field @BeanProperty
  type RdfProperty = thewebsemantic.RdfProperty@field @BeanProperty
  type Transient = thewebsemantic.Transient@field @BeanProperty
  val KEY: String = "jenabean-feature"

  implicit def toJenabeanSupport(ontModel: OntModel): JenabeanSupport = {
    val jenabeanSupport = new JenabeanSupport
    jenabeanSupport.init(ontModel)
    jenabeanSupport
  }
}

class JenabeanSupport extends Feature {

  val key: String = JenabeanSupport.KEY
  var ontModel: OntModel = null
  var beanReader: RDF2Bean = null
  var beanWriter: Bean2RDF = null

  def addOrUpdate(obj: AnyRef, saveDeep: Boolean = true) =
    if (saveDeep) beanWriter.saveDeep(obj) else beanWriter.save(obj)

  def getBean[V <: Object](uri: AnyRef)(implicit m: Manifest[V]) = beanReader.load(m.erasure, uri)

  def toIndividual[V <: Object](bean: V) = {
    val id = TypeWrapper.`type`(bean).id(bean)
    ontModel.findByURIAs[Individual](ontModel.expandPrefix(id))
  }

  def init(ontModel: OntModel) {
    this.ontModel = ontModel
    this.beanReader = new RDF2Bean(ontModel, false)
    this.beanWriter = new Bean2RDF(ontModel, false)
  }

  def close {}
}