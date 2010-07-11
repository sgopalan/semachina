/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.semachina.jena.db.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semachina.jena.db.Data;
import org.semachina.jena.db.Database;
import org.semachina.jena.db.Results;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.core.DataSourceImpl;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LarqIndexedDatabaseTest {

	private LarqIndexedDatabase indexedDb;
	private Database db;
	private File ldir;

	@Before
	public void setUp() throws Exception {
		if( indexedDb != null ) {
			indexedDb.close();
		}
		db = new FileDatabase( new File( this.getClass().getClassLoader().getResource( "empty.n3" ).getPath() ), 
							   new File( this.getClass().getClassLoader().getResource( "graphs/test" ).getPath() ) );
		ldir = new File(this.getClass().getClassLoader().getResource( "." ).getPath() ,"larq-test");
	
		if (!ldir.exists())  {
			if (!ldir.mkdirs()) {
				throw new Error("Can't create " + ldir);
			}
		}
		
		ldir.deleteOnExit();
		indexedDb = new LarqIndexedDatabase(db,ldir.getAbsolutePath());
	}

//	private void remove(File file) {
//		if (!file.exists()) return;
//		if (!file.isDirectory()) { 
//			boolean isDeleted = file.delete(); 
//			return; 
//		}
//		// Directory: remove contents, then self
//		for (File subFile: file.listFiles()) {
//			remove(subFile);
//		}
//		file.delete();
//	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testBasics() throws IOException {
		assertEquals("Files were indexed",ResourceFactory.createResource("http://example.com/foo/1"),find("tester"));
		// reopen!
		indexedDb.close();
		indexedDb = new LarqIndexedDatabase(db,ldir.getAbsolutePath(),false,false);
		assertEquals("Files were still indexed",ResourceFactory.createResource("http://example.com/foo/1"),find("tester"));
		indexedDb.close();
	}
	
	@Test
	public void testReindex() throws IOException {
		db = new SimpleDB(); // FileDatabase is immutable, it seems
		Model m = ModelFactory.createDefaultModel();
		m.add(m.createResource("http://example.com/index-orig"), RDFS.label, "loseme");
		db.addModel("http://example.com/index-orig", m);
		indexedDb = new LarqIndexedDatabase(db,ldir.getAbsolutePath());
		assertNull("Starting state is correct", find("reindexme"));
		assertNotNull("Starting state is correct", find("loseme"));
		// go behind larq index's back
		m = ModelFactory.createDefaultModel();
		m.add(m.createResource("http://example.com/index"), RDFS.label, "reindexme");
		db.deleteModel("http://example.com/index-orig", null);
		db.addModel("http://example.com/unindexed", m);
		assertNull("Not indexed yet", find("reindexme"));
		assertNotNull("Not indexed yet", find("loseme"));
		indexedDb.reindex();
		assertEquals("Now indexed", ResourceFactory.createResource("http://example.com/index"), find("reindexme"));
		assertNull("Forgot lost model", find("loseme"));
	}
	
	@Test
	public void testAddModel() {
		assertNull("Unique ain't there", find("unique"));
		Model m = ModelFactory.createDefaultModel();
		m.add(m.createResource("http://example.com/unique"), RDFS.label, "unique");
		indexedDb.addModel("http://example.com/foo", m);
		assertEquals("Unique is there", ResourceFactory.createResource("http://example.com/unique"), find("unique"));
		m = ModelFactory.createDefaultModel();
		m.add(m.createResource("http://example.com/unique2"), RDFS.label, "unique2");
		indexedDb.addModel("http://example.com/bar", m);
		assertEquals("Unique is still there", ResourceFactory.createResource("http://example.com/unique"), find("unique"));
		assertEquals("Unique2 is now there", ResourceFactory.createResource("http://example.com/unique2"), find("unique2"));
	}
	
	@Ignore
	@Test
	public void testDeleteAll() {
		
	}
	
	@Ignore
	@Test
	public void testDeleteModel() {
		
	}
	
	@Ignore
	@Test
	public void testUpdateProperty() {
	}
	
	private Resource find(String string) {
		String query = "SELECT ?res { ?res <http://jena.hpl.hp.com/ARQ/property#textMatch> \"" + string + "\" }";
		Results result = indexedDb.executeSelectQuery(query, null);
		ResultSet realRes = result.getResults();
		Resource res = (realRes.hasNext()) ?
			realRes.nextSolution().getResource("res") :
		    null ;
		//result.close();
		return res;
	}
	
	private static class SimpleDB implements Database {
		
		private DataSource dataset = new DataSourceImpl();
		private Context context;
		
		public boolean addModel(String uri, Model model) {
			dataset.addNamedModel(uri, model);
			return true;
		}

		public boolean deleteAll(String uri) {
			return false;
		}

		public boolean deleteModel(String uri, Model model) {
			dataset.removeNamedModel(uri);
			return true;
		}

		public Model executeConstructQuery(String sparql,
				QuerySolution initialBindings) {
			return null;
		}

		public Model executeConstructQuery(Query query,
				QuerySolution initialBindings) {
			return null;
		}

		public Results executeSelectQuery(String sparql,
				QuerySolution initialBindings) {
			QueryExecution qef = QueryExecutionFactory.create(sparql, dataset);
			if (context != null) qef.getContext().setAll(context);
			return new Results(qef.execSelect(), qef, getData());
		}

		public Data getData() {
			return new Data() {
				public void close() {}
				public Dataset getDataset() { return null; }
				public Model getModel(String uri) { return null; }
				public Context getContext() { return null; }
			};
		}

		public Model getUpdateModel() {
			return null;
		}

		public boolean updateProperty(String uri, String resourceUri,
				Property property, RDFNode value) {
			return false;
		}

		public void setQueryContext(Symbol indexKey, Object value) {
			if (context == null) context = new Context();
			context.set(indexKey, value);
		}

		public Context getQueryContext() {
			return context;
		}

                public void setRules(String rulesFile) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
		
	}
}
