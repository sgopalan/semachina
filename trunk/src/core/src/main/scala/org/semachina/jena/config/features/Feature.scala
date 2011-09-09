package org.semachina.jena.config.features

import com.hp.hpl.jena.ontology.OntModel

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Dec 8, 2010
 * Time: 8:34:38 AM
 * To change this template use File | Settings | File Templates.
 */
trait Feature {
  val key: String

  def getKey() : String = {
    return key
  }

  def init(ontModel: OntModel): Unit

  def close: Unit
}