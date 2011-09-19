package org.semachina.jena.ontology

import com.hp.hpl.jena.ontology.OntModel
import java.io.File
import java.util.Map

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 12/10/10
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */

object ReadWriteContext {
  def apply(transaction : OntModel => Unit) : ReadWriteContext = new ReadWriteContext {
    def execute(transactModel: OntModel)  = transaction( transactModel )
  }
}

trait ReadWriteContext {
  def execute(transactModel: OntModel): Unit
}