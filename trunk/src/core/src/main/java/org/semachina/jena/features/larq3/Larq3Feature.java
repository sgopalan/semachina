package org.semachina.jena.features.larq3;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.larq3.IndexBuilderModel;
import com.hp.hpl.jena.query.larq3.IndexBuilderString;
import com.hp.hpl.jena.query.larq3.IndexLARQ;
import com.hp.hpl.jena.query.larq3.LARQ;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import org.apache.lucene.store.Directory;
import org.semachina.jena.SemachinaOntModel;
import org.semachina.jena.features.Feature;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Dec 1, 2010
 * Time: 7:11:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Larq3Feature implements Feature {

    public static String KEY = "larq3-feature";

    private Directory fsd;

    private IndexBuilderModel ib;

    private IndexLARQ index;

    private SemachinaOntModel ontModel;

    public Larq3Feature(Directory fsd) {
        this.fsd = fsd;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void init(SemachinaOntModel ontModel, org.semachina.jena.SemachinaFactory factory) {
        this.ontModel = ontModel;

        if( fsd == null ) {
            throw new IllegalStateException( "LARQ directory should not be null" );
        }

        ib = new IndexBuilderString( fsd );

        index = ib.getIndex();
        LARQ.setDefaultIndex(index);
    }

    @Override
    public void close() throws IOException {
    //    logger.info("started close larq")

        if (fsd != null) {
          fsd.close();
        }
    }

    public void initLarq() {
//    logger.info("started init larq")

    if( fsd == null ) {
      throw new IllegalStateException( "LARQ directory should not be null" );
    }

    ib = new IndexBuilderString( fsd );

    index = ib.getIndex();
    LARQ.setDefaultIndex(index);
  }

  public void reindex() {
      index(getOntModel().listStatements());
  }

  //should this be synchronized
  public void index(StmtIterator statements) {

    ib = new IndexBuilderString(fsd);
    ib.indexStatements(statements);

    //set the old index
    IndexLARQ oldIndex = index;

    //set the new index
    index = ib.getIndex();
    LARQ.setDefaultIndex(index);

    //close the old index
    if (oldIndex != null) {
      oldIndex.close();
    }
  }

  public void closeLarq() throws IOException {
    //logger.info("started close larq")

    if (fsd != null) {
      fsd.close();
    }
  }

    protected OntModel getOntModel() {
        return ontModel;
    }
}
