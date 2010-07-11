package org.semachina.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.semachina.core.db.DBModelMakerFactory;
import org.semachina.jena.core.ModelMakerFactory;
import org.semachina.jena.core.OWLBean;
import org.semachina.jena.core.OWLFactory;
import org.semachina.jena.core.OWLModel;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.ModelMaker;

public class Main {
	
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
		
		
		String foafBase = "http://xmlns.com/foaf/0.1/";
		
		OWLFactory.addNsPrefix( "foaf", foafBase );
		
		ModelMaker maker = modelMakerFactory.createModelMaker(true);
		
		OWLModel m = OWLFactory.createOWLModel( /** maker */ );
		
        for (Iterator i = m.listClasses(); i.hasNext(); ) {
            OntClass c = (OntClass) i.next();
            System.out.println( "Class " + c.getURI() );
        }
		
		m.enterCriticalSection( false );
		
		//first load foaf for class hierarchy
		m.read("http://xmlns.com/foaf/spec/index.rdf");
		
		// now load some foaf off the web
		m.read("http://richard.cyganiak.de/foaf.rdf#cygri");
		m.read("http://www.deri.ie/fileadmin/scripts/foaf.php?id=316");
		
		m.leaveCriticalSection();
		
        for (Iterator i = m.listClasses(); i.hasNext(); ) {
            OntClass c = (OntClass) i.next();
            System.out.println( "Class " + c.getURI() );
        }
		
		// now write the model in XML form to a file
		//m.write(System.out);
		
		m.close();
		
		maker = modelMakerFactory.createModelMaker(false);
		m = OWLFactory.createOWLModel( /** maker */ );
		
        for (Iterator i = m.listClasses(); i.hasNext(); ) {
            OntClass c = (OntClass) i.next();
            System.out.println( "Class " + c.getURI() );
        }
		
		Collection<OWLBean> groups = m.getOWLBeans( "foaf:Group" );
		Collection<OWLBean> people = m.getOWLBeans( "foaf:Person" );
		Collection<OWLBean> agents = m.getOWLBeans( "foaf:Agent" );

		
		//m.writeAll(System.out, "N3", null); System.exit(0);
		System.out.println("There are " + people.size() + " People in this graph.");
		System.out.println("There are " + groups.size() + " Groups in this graph.");
		System.out.println("There are " + agents.size() + " Agents in this graph. (people + groups)");
		
		for (OWLBean p : people) {
			OWLBean owlBean = (OWLBean) p.get( "foaf:weblog");
			if( owlBean != null ) {
				System.out.println( "name: " + owlBean.getURI() );
			}
			
		}
		
		System.out.println( m.select( "select ?x where { $y  foaf:name ?x }" ) );
		
	}

}
