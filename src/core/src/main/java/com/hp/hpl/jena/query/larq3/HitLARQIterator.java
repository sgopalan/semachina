package com.hp.hpl.jena.query.larq3;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class HitLARQIterator implements Iterator<HitLARQ> {

    private IndexReader reader;

    private Iterator<ScoreDoc> scoreDocs;

    public HitLARQIterator(IndexReader reader, Iterator<ScoreDoc> scoreDocs) {
        this.reader = reader;
        this.scoreDocs = scoreDocs;
    }

    public boolean hasNext() {
        return this.scoreDocs.hasNext();
    }

    public HitLARQ next() {
        ScoreDoc scoreDoc = this.scoreDocs.next();
        try {
            Document document = this.reader.document( scoreDoc.doc );
            HitLARQ hitLarq = new HitLARQ( document, scoreDoc.score, scoreDoc.doc );
            return hitLarq;
        }
        catch (IOException io) {
           throw new ARQLuceneException("search", io) ;
        }
    }

    public void remove() {
        this.scoreDocs.remove();
    }
}
