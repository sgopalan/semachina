package org.semachina.jena.config.features.larq3

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.StmtIterator
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry
import org.apache.jena.larq.IndexBuilderModel
import org.apache.jena.larq.IndexBuilderString
import org.apache.jena.larq.IndexLARQ
import org.apache.jena.larq.LARQ
import org.apache.jena.larq.pfunction.textMatch
import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.Directory
import org.apache.lucene.util.Version
import org.semachina.jena.config.features.Feature
import java.io.IOException

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Dec 1, 2010
 * Time: 7:11:49 PM
 * To change this template use File | Settings | File Templates.
 */

object Larq3Feature {
  val KEY: String = "larq3-feature"
}

class Larq3Feature(fsd: Directory,
                   textMatchURI: String = "http://jena.hpl.hp.com/ARQ/property#TextMatch3") extends Feature {

  val key: String = Larq3Feature.KEY

  private var ib: IndexBuilderModel = null
  private var index: IndexLARQ = null
  private var ontModel: OntModel = null

  override def init(ontModel: OntModel): Unit = {
    this.ontModel = ontModel
    if (fsd == null) {
      throw new IllegalStateException("LARQ directory should not be null")
    }
    var registry: PropertyFunctionRegistry = PropertyFunctionRegistry.get
    if (!registry.isRegistered(this.textMatchURI)) {
      registry.put(this.textMatchURI, classOf[textMatch])
    }
    ib = new IndexBuilderString
    index = ib.getIndex
    LARQ.setDefaultIndex(index)
  }

  override def close: Unit = {
    if (index != null) {
      index.close
    }
    if (fsd != null) {
      fsd.close
    }
  }

  def reindex: Unit = {
    index(getOntModel.listStatements)
  }

  def index(statements: StmtIterator): Unit = {
    var indexWriter: IndexWriter = new IndexWriter(fsd, new IndexWriterConfig(Version.LUCENE_31, new SimpleAnalyzer(Version.LUCENE_31)))
    ib = new IndexBuilderString(indexWriter)
    ib.indexStatements(statements)
    ib.closeWriter
    var oldIndex: IndexLARQ = index
    index = ib.getIndex
    LARQ.setDefaultIndex(index)
    if (oldIndex != null) {
      oldIndex.close
    }
  }

  protected def getOntModel: OntModel = {
    return ontModel
  }
}