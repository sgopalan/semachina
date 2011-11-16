package org.semachina.jena.features.larq3

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.StmtIterator
import org.semachina.jena.features.Feature
import org.apache.jena.larq.pfunction.textMatch
import org.apache.jena.larq.{LARQ, IndexBuilderString, IndexLARQ, IndexBuilderModel}
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.{RAMDirectory, Directory}
import org.apache.lucene.index.IndexWriter.MaxFieldLength
import org.semachina.jena.config.SemachinaConfiguration

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

class Larq3Feature(fsd: Directory = new RAMDirectory,
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

    val indexWriter =
      new IndexWriter(fsd, new StandardAnalyzer(LARQ.LUCENE_VERSION), MaxFieldLength.UNLIMITED)

    SemachinaConfiguration.registerPropertyFunction(textMatchURI, classOf[textMatch])

    ib = new IndexBuilderString(indexWriter)
    ib.closeWriter()

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
    var indexWriter =
      new IndexWriter(fsd, new StandardAnalyzer(LARQ.LUCENE_VERSION), MaxFieldLength.UNLIMITED)
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