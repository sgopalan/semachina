package org.semachina.jena.ontology

import com.hp.hpl.jena.ontology.OntModel
import org.semachina.jena.features.Feature
import java.util.HashMap
import scala.collection.JavaConversions._


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/28/11
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */

trait OntModelConfiguration {

  protected lazy val features: java.util.Map[String, Feature] = new HashMap[String, Feature]

  def getOntModel: OntModel

  def addFeature(feature: Feature): Unit = {
    var featureKey: String = feature.getKey
    if (features.containsKey(featureKey)) {
      throw new IllegalArgumentException("key already exists: " + featureKey)
    }
    feature.init(getOntModel)
    features.put(featureKey, feature)
  }

  def getFeature(featureKey: String): Feature = {
    if (features == null || !features.containsKey(featureKey)) {
      throw new IllegalArgumentException("feature not implemented: " + featureKey)
    }
    return features.get(featureKey)
  }

  def closeFeatures: Unit = {
    if (features != null) {
      for (feature <- features.values) {
        try {
          feature.close
        }
        catch {
          case e: Exception => {
            e.printStackTrace
          }
        }
      }
    }
  }
}