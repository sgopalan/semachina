package org.semachina.jena.binder.annotations

import _root_.java.net.URI
import annotation.target.field
import com.hp.hpl.jena.shared.PrefixMapping
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl
import com.hp.hpl.jena.ontology.{OntModel, Individual}
import org.semachina.jena.ontology.SemachinaOntModel
import scala.collection.JavaConversions._
import com.hp.hpl.jena.vocabulary.XSD
import reflect.BeanProperty
import com.hp.hpl.jena.rdf.model.{Statement, RDFNode}
import com.hp.hpl.jena.datatypes.TypeMapper

/**
 * Helper class to leverage <code>org.semachina.jena.binder.annotations.java</code> within a
 * scala environment
 */
object SemachinaBinding {

  /**
   * Instructs the compiler to attach the annotation to the field and to create getter/setter
   * methods (for the benefit of binder various implementations)
   */
  type Id = java.Id@field @BeanProperty

  /**
   * Instructs the compiler to attach the annotation to the field and to create getter/setter
   * methods (for the benefit of binder various implementations)
   */
  type RdfProperty = java.RdfProperty@field @BeanProperty

  /**
   * Instructs the compiler to attach the annotation to the field and to create getter/setter
   * methods (for the benefit of binder various implementations)
   */
  type PropertyPath = java.PropertyPath@field @BeanProperty

  /**Shortcut to allow import of all annotations in one statement */
  type RdfType = java.RdfType

  /**Shortcut to allow import of all annotations in one statement */
  type Prefix = java.Prefix

  /**Shortcut to allow import of all annotations in one statement */
  type Prefixes = java.Prefixes

  /**
   * @returns PrefixMapping defined by <code>Prefix</code> annotations in class
   */
  def getPrefixMapping(c: Class[_]): PrefixMapping = {
    var prefixes: Prefixes = c.getAnnotation(classOf[Prefixes])
    if (prefixes != null) {
      var prefixMapping: PrefixMapping = new PrefixMappingImpl
      prefixMapping.setNsPrefixes(PrefixMapping.Standard)
      for (prefix <- prefixes.value()) {
        prefixMapping.setNsPrefix(prefix.prefix(), prefix.uri())
      }
      return prefixMapping
    }
    return PrefixMapping.Standard
  }

  /**
   * Checks if the individual to map and the target bean share the same RDF Type definitions
   *
   * @param individual The <code>Individual</code> to check
   * @param beanClass The class of the bean to map to
   * @param prefixMapping The PrefixMapping to expand any prefixed URIs
   *
   * @throws Exception if individual does not contain at least one of the <code>OntClass<code>
   *   annotated within the bean class
   */
  def checkRDFType(individual: Individual, beanClass: Class[_], prefixMapping: PrefixMapping) {
    val ontClasses = getOntClasses(individual.getOntModel, beanClass)
    ontClasses.foreach {
      ontClass =>
        if (individual.hasOntClass(ontClass)) return
    }

    require(
      false,
      "Individual " + individual.getId + " MUST have one of type(s):" + ontClasses.mkString)
  }

  /**
   * Get the annotated <code>OntClass</code>es from the given bean class
   *
   * @param ontModel The ontModel to resolve <code>OntClass</code> with
   * @param beanClass The class of the bean to map to
   * @return A Collection of <code>OntClass</code> defined by the <code>RdfType<code> annotation
   */
  def getOntClasses(ontModel: OntModel, beanClass: Class[_]) = {
    var rdfType = beanClass.getAnnotation(classOf[RdfType])
    SemachinaOntModel(ontModel).resolveOntClasses(rdfType.value().toList)
  }

  /**
   * Translate a given value into an RDF Node
   *
   * @param ontModel The ontModel to create <code>RDFNode</code> from
   * @param value The value to map to a <code>RDFNode</code>
   * @param dataTypeURI (Optional) The dataTypeURI to help map object to a <code>Literal</code>
   * @return <code>RDFNode</code> the corresponds the value
   */
  def toRDFNode(ontModel: OntModel, value: Any, dataTypeURI: String = null): RDFNode = {
    def semachina = SemachinaOntModel(ontModel)

    //assume it is a literal if dataTypeURI is set
    if (dataTypeURI != null && !"".equals(dataTypeURI)) {
      return semachina.resolveTypedLiteral(value, dataTypeURI)
    }

    return value match {
      case stmt: Statement => ontModel.createReifiedStatement(stmt)
      case r: RDFNode => r
      case s: String => semachina.resolveTypedLiteral(s, XSD.xstring.getURI)
      case uri: URI => semachina.resolveTypedLiteral(uri.toString, XSD.xstring.getURI)
      case other: AnyRef => {
        val dataType = TypeMapper.getInstance().getTypeByClass(other.getClass)
        require(dataType != null, "There MUST be a registered data type for: " + other.getClass)
        semachina.getOntModel.createTypedLiteral(other, dataType)
      }
    }
  }
}