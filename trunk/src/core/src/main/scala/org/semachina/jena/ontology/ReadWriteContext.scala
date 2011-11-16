package org.semachina.jena.ontology

import com.hp.hpl.jena.ontology.OntModel

/**
 * Transactional context for OntModel transactions
 */
object ReadWriteContext {
  def apply(transaction: OntModel => Unit): ReadWriteContext = new ReadWriteContext {
    def execute(transactModel: OntModel) : Unit = transaction(transactModel)
  }
}

trait ReadWriteContext {
  def execute(transactModel: OntModel)
}