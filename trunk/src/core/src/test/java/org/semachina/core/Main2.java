package org.semachina.core;

import java.io.IOException;
import java.util.Collection;

import org.semachina.core.db.DBModelMakerFactory;
import org.semachina.jena.core.ModelMakerFactory;
import org.semachina.jena.core.OWLBean;
import org.semachina.jena.core.OWLFactory;
import org.semachina.jena.core.OWLModel;
import org.semachina.xml.types.Day;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.rdf.model.ModelMaker;

public class Main2 {
	
    public static final String DB_URL = "jdbc:derby:jenatest;create=true";
    public static final String DB_USER = "";
    public static final String DB_PASSWD = "";
    public static final String DB = "Derby";

	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		//OWLFactory.init();

		ModelMakerFactory modelMakerFactory = 
			new DBModelMakerFactory( DB_URL, DB_USER, DB_PASSWD, DB );
		
		
		OntDocumentManager ontDocManager = OntDocumentManager.getInstance();
		ontDocManager.addAltEntry( "http://semachina.org/test-data.owl", "file:test.owl" );

        
		String testBase = "http://semachina.org/test-data.owl#";
		
		OWLFactory.addNsPrefix( "test", testBase );
		
		ModelMaker maker = null;//modelMakerFactory.createModelMaker(true);
		
		OWLModel m = OWLFactory.createOWLModel( maker );
		
		//first load foaf for class hierarchy
		m.read("file:test.owl");
	
		Collection<OWLBean> beans = m.getOWLBeans( "test:TestClass" );
		for( OWLBean bean : beans ) {
			Day gDay = (Day) bean.get( "test:getGDay" );
			if( gDay != null ) {
				System.out.println( gDay );
			}
			bean.set( "test:getGDay", "---12" );
			System.out.println( bean.get( "test:getGDay" ) );
			
			gDay = new Day( 29 );
			bean.set( "test:getGDay", gDay );
			System.out.println( bean.get( "test:getGDay" ) );
			
			
		}
		
	}

}
