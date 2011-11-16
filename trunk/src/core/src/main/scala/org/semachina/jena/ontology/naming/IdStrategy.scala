package org.semachina.jena.ontology.naming

import com.hp.hpl.jena.ontology.Individual
import org.springframework.web.util.UriTemplate
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import org.semachina.jena.ontology.SemachinaIndividual
import java.net.URLEncoder

object IdStrategy {
  def encodeString(str: String): String = {
    val lowerCase = str.toLowerCase
    val replaceSpaces = lowerCase.replace(' ', '_')
    val encoded = URLEncoder.encode(replaceSpaces, "UTF-8")
    return encoded
  }

  def default = new IdStrategy {
    def toId(resource: Individual) = resource.getURI
  }

  /**
   * http://patterns.dataincubator.org/book/patterned-uris.html
   * http://patterns.dataincubator.org/book/literal-keys.html
   * http://patterns.dataincubator.org/book/natural-keys.html
   * http://patterns.dataincubator.org/book/shared-keys.html
   * http://patterns.dataincubator.org/book/proxy-uris.html
   */
  def newPatterned(pattern: String, toUriVariables: (Individual, HashMap[String, AnyRef]) => Unit): IdStrategy = {

    return new IdStrategy {
      def toId(resource: Individual) = {
        val uriTemplate = new UriTemplate(pattern)

        val uriVariables = new HashMap[String, AnyRef]
        toUriVariables(resource, uriVariables)
        uriTemplate.expand(mapAsJavaMap(uriVariables)).toString
      }


    }
  }

  /**
   * http://patterns.dataincubator.org/book/url-slug.html
   *
   * Lowercase the string
   * Remove any special characters and punctuation that might require encoding in the URL
   * Replace spaces with a dash
   */
  def newLabelBased(
                     subCollection: String,
                     parent: SemachinaIndividual,
                     lang: String): IdStrategy = {

    val toId = {
      i: Individual => encodeString(i.getLabel(lang))
    }
    val toParent = {
      i: Individual => parent
    }
    return newHierarchical(subCollection, toId, toParent)
  }

  def newLabelBased(
                     subCollection: String,
                     parentPath: String,
                     lang: String): IdStrategy = {

    val toId = {
      i: Individual => encodeString(i.getLabel(lang))
    }
    return newHierarchical(subCollection, toId, parentPath: String)
  }

  def newLabelBased(
                     subCollection: String,
                     toParent: Individual => Individual,
                     lang: String): IdStrategy = {

    val toId = {
      i: Individual => encodeString(i.getLabel(lang))
    }
    return newHierarchical(subCollection, toId, toParent)
  }

  def newHierarchical(
                       subCollection: String,
                       toId: Individual => String,
                       parent: SemachinaIndividual): IdStrategy = {

    val toParent = {
      i: Individual => parent
    }
    newHierarchical(subCollection, toId, toParent)
  }


  def newHierarchical(
                       subCollection: String,
                       toId: Individual => String,
                       parentPath: String): IdStrategy = {

    val toParent = {
      i: Individual =>
        SemachinaIndividual(i).get(parentPath)
    }
    newHierarchical(subCollection, toId, toParent)
  }

  def newHierarchical(
                       subCollection: String,
                       toId: Individual => String,
                       toParent: Individual => Individual): IdStrategy = {

    val hierarchicalTemplate = "{parentURI}/{subCollection}/{id}"

    val toUriVariables = {
      (res: Individual, uriVariables: HashMap[String, AnyRef]) =>
      //set parentURI
        val parent = toParent(res)
        require(parent != null, "toParent() evaluates to null")
        uriVariables("parentURI") = parent.getURI

        //set sub collection
        uriVariables("subCollection") = subCollection

        //set id
        val id = toId(res)
        require(id != null, "id CANNOT resolve to null")
        uriVariables("id") = id
    }

    return newPatterned(hierarchicalTemplate, toUriVariables)

  }
}

trait IdStrategy {
  def toId(resource: Individual): String
}