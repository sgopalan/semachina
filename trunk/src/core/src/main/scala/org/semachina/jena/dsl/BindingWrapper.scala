package org.semachina.jena.dsl

import org.semachina.jena.binder.IndividualBinder
import com.hp.hpl.jena.ontology.OntModel

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 11/15/11
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */

class BindingWrapper(bean: AnyRef, ontModel: OntModel) {
  def & = IndividualBinder.default(ontModel) toIndividual (bean)
}