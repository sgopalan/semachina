package org.semachina.jena.features.larq3;

import com.hp.hpl.jena.ontology.OntModel;

import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.apache.jena.larq.IndexBuilderModel;
import org.apache.jena.larq.IndexBuilderString;
import org.apache.jena.larq.IndexLARQ;
import org.apache.jena.larq.LARQ;

import org.apache.jena.larq.pfunction.textMatch;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
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

    private String textMatchURI = "http://jena.hpl.hp.com/ARQ/property#TextMatch3";

    private Directory fsd;

    private IndexBuilderModel ib;

    private IndexLARQ index;

    private SemachinaOntModel ontModel;

    public Larq3Feature(Directory fsd) {
        this( fsd, null );
    }

    public Larq3Feature(Directory fsd, String textMatchURI) {
        this.fsd = fsd;

        if( textMatchURI != null ) {
           this.textMatchURI = textMatchURI;
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void init(SemachinaOntModel ontModel, org.semachina.jena.SemachinaFactory factory) {
        this.ontModel = ontModel;

        if (fsd == null) {
            throw new IllegalStateException("LARQ directory should not be null");
        }


        //register function...
        PropertyFunctionRegistry registry  = PropertyFunctionRegistry.get();
        if( !registry.isRegistered( this.textMatchURI ) ) {
            registry.put( this.textMatchURI, textMatch.class );
        }

        ib = new IndexBuilderString();

        index = ib.getIndex();
        LARQ.setDefaultIndex(index);
    }

    @Override
    public void close() throws IOException {
        //    logger.info("started close larq")

        if( index != null ) {
            index.close();
        }

        if (fsd != null) {
            fsd.close();
        }
    }

    public void reindex() throws Exception {
        index(getOntModel().listStatements());
    }

    //should this be synchronized
    public void index(StmtIterator statements) throws Exception {
        IndexWriter indexWriter = new IndexWriter(fsd, new IndexWriterConfig(Version.LUCENE_31, new SimpleAnalyzer(Version.LUCENE_31) ));
        ib = new IndexBuilderString(indexWriter);
        ib.indexStatements(statements);
        ib.closeWriter();

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

    protected OntModel getOntModel() {
        return ontModel;
    }
}
