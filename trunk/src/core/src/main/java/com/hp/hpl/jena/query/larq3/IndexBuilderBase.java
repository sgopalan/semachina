/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.query.larq3;

import java.io.File ;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader ;
import org.apache.lucene.index.IndexWriter ;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory ;
import org.apache.lucene.store.FSDirectory ;
import org.apache.lucene.store.RAMDirectory ;
import org.apache.lucene.util.Version;

/** Root class for index creation.
 *  
 * @author Andy Seaborne
 */

public class IndexBuilderBase implements IndexBuilder 
{
    private Directory dir = null ;

    // Use this for incremental indexing?
    //private IndexModifier modifier ;

    //private IndexWriter indexWriter = null ;
    //private IndexReader indexReader = null ;

    //private boolean isClosed ;

    /** Create an in-memory index */
    
    public IndexBuilderBase()
    {
        this( new RAMDirectory() );
    }
    
    /** Manage a Lucene index that has already been created */
    
    public IndexBuilderBase(Directory dir)
    {
        this.dir = dir;
        makeIndex() ;
    }
    
    /** Create an on-disk index */
    
    public IndexBuilderBase(File fileDir)
    {
        try {
            dir = FSDirectory.open(fileDir);
            makeIndex() ;
        } catch (Exception ex)
        { throw new ARQLuceneException("IndexBuilderLARQ", ex) ; }
        
    }
    
    /** Create an on-disk index */

    public IndexBuilderBase(String fileDir)
    {
        this( new File( fileDir ) );
    }

    private void makeIndex()
    {
        try {
            IndexWriter writer = createIndexWriter();
            Document doc = new Document();
            writer.addDocument(doc);
            writer.close();
        }
        catch (Exception ex)
        {
            throw new ARQLuceneException("IndexBuilderLARQ", ex) ;
        }
    }

    protected IndexWriter createIndexWriter() {
        try {
            return new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_31, new SimpleAnalyzer(Version.LUCENE_31) ) );
        }
        catch (Exception ex)
        {
            throw new ARQLuceneException("IndexBuilderLARQ", ex) ;
        }
    }
    
    protected IndexReader createIndexReader()
    {
        // Always return a new reader.  Write may have changed.
        try {
            return IndexReader.open(dir) ;
        } catch (Exception e) { throw new ARQLuceneException("getIndexReader", e) ; }
    }

    protected void closeIndexWriter(IndexWriter writer) {
        try {
            writer.optimize();
            writer.close();
        }
        catch (Exception ex)
        {
            throw new ARQLuceneException("IndexBuilderLARQ", ex) ;
        }               
    }

    /** Get a search index used by LARQ */
    
    public IndexLARQ getIndex()
    {
    	// In Lucene, an index reader sees the index at a point in time.
    	// This wil not see later updates.
        //ARQ 2.2 : no longer close the index.  closeForWriting() ;
        return new IndexLARQ(createIndexReader()) ;
    }
    

}

/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
