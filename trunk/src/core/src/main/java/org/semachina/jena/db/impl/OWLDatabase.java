package org.semachina.jena.db.impl;

import org.semachina.jena.db.Data;
import org.semachina.jena.db.DataException;
import org.semachina.jena.db.Database;
import org.semachina.jena.db.Results;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;

public class OWLDatabase implements Database {

	@Override
	public boolean addModel(String uri, Model model) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteAll(String uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteModel(String uri, Model model) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Model executeConstructQuery(String sparql,
			QuerySolution initialBindings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model executeConstructQuery(Query query,
			QuerySolution initialBindings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Results executeSelectQuery(String sparql,
			QuerySolution initialBindings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Data getData() throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getQueryContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getUpdateModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setQueryContext(Symbol indexKey, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean updateProperty(String uri, String resourceUri,
			Property property, RDFNode value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRules(String rulesFile) {
		// TODO Auto-generated method stub
		
	}

}
