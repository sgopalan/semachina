package org.semachina.jena.features.pellet

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.RDFNode
import com.hp.hpl.jena.rdf.model.Statement
import com.hp.hpl.jena.util.iterator.ExtendedIterator
import com.hp.hpl.jena.vocabulary.RDF
import org.mindswap.pellet.PelletOptions
import org.mindswap.pellet.jena.BuiltinTerm
import org.mindswap.pellet.jena.PelletInfGraph
import org.mindswap.pellet.utils.progress.ProgressMonitor
import scala.collection.JavaConversions._
import org.semachina.jena.utils.ExtendedIteratorWrapper._
import org.semachina.jena.features.Feature

object PelletFeature {
  val KEY: String = "pellet-feature"
}

class PelletFeature extends Feature {

  val key: String = PelletFeature.KEY
  protected var pelletModel: OntModel = null
  protected var pelletGraph: PelletInfGraph = null

  override def init(pelletModel: OntModel): Unit = {
    this.pelletModel = pelletModel
    PelletOptions.USE_TRACING = true
    PelletOptions.USE_ANNOTATION_SUPPORT = true
    PelletOptions.SAMPLING_RATIO = 0
    pelletGraph = pelletModel.getGraph.asInstanceOf[PelletInfGraph]
  }

  override def close: Unit = {
  }

  def classify(progressBar: ProgressMonitor): Unit = {
    checkAndResolveInconsistency(progressBar)
    if (progressBar != null) {
      pelletGraph.getKB.getTaxonomyBuilder.setProgressMonitor(progressBar)
    }
    pelletGraph.classify
  }

  private def checkAndResolveInconsistency(progressBar: ProgressMonitor): Unit = {
    if (progressBar != null) {
      progressBar.setProgressMessage("Consistency checking")
    }
    while (!pelletGraph.isConsistent) {
      var explanation: Model = pelletGraph.explainInconsistency
      System.out.println("Data is inconsistent:")
      explanation.setNsPrefixes(pelletModel)
      explanation.write(System.out, "TTL")
      explanation.listStatements.foreach { stmt =>
        if (isIndividualAssertion(stmt)) {
          System.out.println("Remove statement: " + stmt.asTriple.toString(pelletModel))
          var subModels: ExtendedIterator[OntModel] = pelletModel.listSubModels
          while (subModels.hasNext) {
            subModels.next.remove(stmt)
          }
        }
      }
      pelletGraph.getLoader.setKB(pelletGraph.getKB)
      pelletModel.rebind
    }
    if (progressBar != null) {
      progressBar.taskFinished
    }
  }

  private def isIndividualAssertion(stmt: Statement): Boolean = {
    var p: Property = stmt.getPredicate
    var o: RDFNode = stmt.getResource
    return !isBuiltinTerm(p) || !isBuiltinTerm(o) && (p == RDF.`type`)
  }

  private def isBuiltinTerm(r: RDFNode): Boolean = {
    return BuiltinTerm.find(r.asNode) != null
  }
}