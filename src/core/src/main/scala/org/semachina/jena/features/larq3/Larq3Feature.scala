package org.semachina.jena.features.larq3

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.StmtIterator
import org.semachina.jena.features.Feature
import org.apache.jena.larq.pfunction.textMatch
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.{RAMDirectory, Directory}
import org.apache.lucene.index.IndexWriter.MaxFieldLength
import org.semachina.jena.config.SemachinaConfiguration
import org.apache.jena.larq._

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
    SemachinaConfiguration.registerPropertyFunction(textMatchURI, classOf[textMatch])

    ib = getIndexBuilder()

  }

  def closeIndex: Unit = {
    if (ib != null) {
      ib.closeWriter();
      ib = null
      ontModel.unregister(ib)
    }

    if (index != null) {
      index.close
    }
  }

  override def close: Unit = {
    closeIndex

    if (fsd != null) {
      fsd.close
    }
  }

  def reindex: Unit = {
    closeIndex

    index(getOntModel.listStatements)
  }

  def index(statements: StmtIterator): Unit = {
    //check ib
    ib = getIndexBuilder()
    ib.indexStatements(statements)
    ib.flushWriter()
  }

  protected def getIndexBuilder(): IndexBuilderModel = {
    if( ib != null ) {
      return ib
    }

    val indexWriter = IndexWriterFactory.create(fsd)

    ib = new IndexBuilderSubject(indexWriter)
    setIndex(ib)
    ontModel.register(ib)
    return ib
  }

  protected def setIndex(indexBuilder: IndexBuilderModel) = {
    var oldIndex = this.index

    this.index = indexBuilder.getIndex
    LARQ.setDefaultIndex(index)

    if (oldIndex != null) {
      oldIndex.close
    }
  }

  protected def getOntModel: OntModel = {
    return ontModel
  }
}