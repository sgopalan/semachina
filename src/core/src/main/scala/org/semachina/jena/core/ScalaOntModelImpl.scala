package org.semachina.jena.core

import com.hp.hpl.jena.ontology.impl.OntModelImpl
import com.weiglewilczek.slf4s.Logging
import org.mindswap.pellet.jena.{PelletReasonerFactory}
import com.hp.hpl.jena.rdf.model.{Statement, ModelMaker, Model}
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl
import org.apache.lucene.store.{Directory, RAMDirectory}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Aug 9, 2010
 * Time: 7:41:56 PM
 * To change this template use File | Settings | File Templates.
 */

class ScalaOntModelImpl(model: Model, protected var fsd: Directory)
        extends OntModelImpl(PelletReasonerFactory.THE_SPEC, model)
                with ScalaOntModel
                with PelletReasoning
                with Logging
                with LarqTrait
                with JenaBeanTrait {
  override protected def processUpdates(adds: java.util.ArrayList[Statement], dels: java.util.ArrayList[Statement]) = {
    val validity = classify(true)

    if (validity.isValid) {
      index(new StmtIteratorImpl(adds.iterator))
      //removeIndex( dels) <-- not supported right now
    }
  }

  override def close() = {
    closeHooks.foreach(hook => hook())
    super.close
  }
}

object ScalaOntModelImpl {
  def apply(model: Model = PelletReasonerFactory.THE_SPEC.createBaseModel): ScalaOntModelImpl = {
    return apply(model, new RAMDirectory())
  }

  def apply(maker: ModelMaker, model: Model): ScalaOntModelImpl = {
    return apply(maker, model, new RAMDirectory())
  }

  def apply(model: Model, directory: Directory): ScalaOntModelImpl = {
    val ontModel = new ScalaOntModelImpl(model, directory)
    ontModel.init
    return ontModel
  }

  def apply(maker: ModelMaker, model: Model, directory: Directory): ScalaOntModelImpl = {
    val ontModel = new ScalaOntModelImpl(model, directory)
    ontModel.getSpecification.setBaseModelMaker(maker)
    ontModel.init
    return ontModel
  }

}