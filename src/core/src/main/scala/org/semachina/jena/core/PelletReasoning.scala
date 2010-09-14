package org.semachina.jena.core

import org.mindswap.pellet.PelletOptions
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.util.iterator.ExtendedIterator
import com.hp.hpl.jena.reasoner.ValidityReport
import org.mindswap.pellet.jena.{BuiltinTerm, PelletInfGraph}
import com.hp.hpl.jena.rdf.model._

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 6, 2010
 * Time: 11:52:09 AM
 * To change this template use File | Settings | File Templates.
 */

object PelletReasoning {
  def initPellet = {
    PelletOptions.USE_TRACING = true
    PelletOptions.USE_ANNOTATION_SUPPORT = true
    PelletOptions.SAMPLING_RATIO = 0
  }
}

trait PelletReasoning extends OntModel {
  protected var pelletGraph: PelletInfGraph = getGraph.asInstanceOf[PelletInfGraph]

  private val RDF_TYPE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");

  //  def initData(subModel: Model): Unit = {
  //    addSubModel(subModel)
  //    classify(true)
  //  }

  protected def classify(resolveInconsistency: Boolean = false): ValidityReport = {
    if (resolveInconsistency) {
      checkAndResolveInconsistency
    }

    //this is to reset the KB.  I guess it only listens to adds, not deletes.
    pelletGraph.getLoader.setKB(pelletGraph.getKB)
    rebind

    //look at pellet tutorial for ideas on how to integrate a progress bar
    //var progressMonitor: ClassificationMonitor = new ClassificationMonitor(progressBar)
    //pelletGraph.getKB.getTaxonomyBuilder.setProgressMonitor(progressMonitor)
    pelletGraph.classify

    var validity = validate
    return validity
  }

  protected def checkAndResolveInconsistency: Unit = {
    while (!pelletGraph.isConsistent) {
      var explanation: Model = pelletGraph.explainInconsistency
      System.out.println("Data is inconsistent:")
      explanation.setNsPrefixes(this)
      explanation.write(System.out, "TTL")

      var stmts: ExtendedIterator[Statement] = explanation.listStatements
      try {
        while (stmts.hasNext) {
          var stmt = stmts.next
          if (isIndividualAssertion(stmt)) {
            System.out.println("Remove statement: " + stmt.asTriple.toString(this))
            var subModels: ExtendedIterator[OntModel] = listSubModels
            while (subModels.hasNext) {
              subModels.next.remove(stmt)
            }
          }
        }
      }
      finally {
        stmts.close
      }
    }
  }

  private def isIndividualAssertion(stmt: Statement): Boolean = {
    var p: Property = stmt.getPredicate
    var o: RDFNode = stmt.getResource
    return !isBuiltinTerm(p) || !isBuiltinTerm(o) && p.equals(RDF_TYPE)
  }


  private def isBuiltinTerm(r: RDFNode): Boolean = {
    return BuiltinTerm.find(r.asNode) != null
  }

}