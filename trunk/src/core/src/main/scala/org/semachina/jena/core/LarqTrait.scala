package org.semachina.jena.core

import org.apache.lucene.index.IndexWriter
import org.apache.lucene.util.Version
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.store.{Directory}
import com.hp.hpl.jena.query.larq.{IndexLARQ, LARQ, IndexBuilderString, IndexBuilderModel}
import com.hp.hpl.jena.rdf.model.{StmtIterator}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Aug 28, 2010
 * Time: 10:20:41 PM
 * To change this template use File | Settings | File Templates.
 */

trait LarqTrait extends ArqTrait {

  //add larq to close hooks
  addInitHook {initLarq}
  addCloseHook {closeLarq}

  protected var fsd: Directory

  protected var ib: IndexBuilderModel = null

  protected var indexWriter: IndexWriter = null

  protected var index: IndexLARQ = null

  def initLarq() = {
    logger.info("started init larq")
    indexWriter = new IndexWriter(fsd, new StandardAnalyzer(Version.LUCENE_24), true, IndexWriter.MaxFieldLength.UNLIMITED)
    ib = new IndexBuilderString(indexWriter)

    index = ib.getIndex
    LARQ.setDefaultIndex(index)
  }

  def reindex(): Unit = index(listStatements)

  //should this be synchronized
  def index(statements: StmtIterator): Unit = {
    if (indexWriter == null) {
      throw new IllegalStateException("Directory has not be initialized")
    }
    ib = new IndexBuilderString(indexWriter)
    ib.indexStatements(statements)
    ib.flushWriter
    indexWriter.commit

    //set the old index
    var oldIndex = index

    //set the new index
    index = ib.getIndex
    LARQ.setDefaultIndex(index);

    //close the old index
    if (oldIndex != null) {
      oldIndex.close
    }
  }


  def closeLarq() = {
    logger.info("started close larq")
    if (ib != null) {
      ib.closeWriter
    }

    if (indexWriter != null) {
      indexWriter.close(true)
    }

    if (fsd != null) {
      fsd.close
    }
  }

}