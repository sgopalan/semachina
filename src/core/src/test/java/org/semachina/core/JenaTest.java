package org.semachina.core;



import org.junit.Test;
import org.semachina.jena.core.OWLBean;
import org.semachina.jena.core.OWLFactory;
import org.semachina.jena.core.OWLModel;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class JenaTest {

    public static final String DB_URL = "jdbc:derby:jenatest;create=true";
    public static final String DB_USER = "";
    public static final String DB_PASSWD = "";
    public static final String DB = "Derby";
    public static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    
    
    // Static variables
    //////////////////////////////////

    // database connection parameters, with defaults
    private static String s_dbURL = DB_URL;
    private static String s_dbUser = DB_USER;
    private static String s_dbPw = DB_PASSWD;
    private static String s_dbType = DB;
    private static String s_dbDriver = DB_DRIVER;

    // if true, reload the data
    private static boolean s_reload = true;


    public ModelMaker createModelMaker(boolean cleanDB) {
        try {
            // Create database connection
            IDBConnection conn  = new DBConnection( s_dbURL, s_dbUser, s_dbPw, s_dbType );

            // do we need to clean the database?
            if (cleanDB) {
                conn.cleanDB();
            }

            // Create a model maker object
            return ModelFactory.createModelRDBMaker( conn );
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit( 1 );
        }

        return null;
    }

//    public OntModelSpec getModelSpec( ModelMaker maker ) {
//        OntModelSpec spec = PelletReasonerFactory.THE_SPEC;
//        spec.setImportModelMaker( maker );
//        return spec;
//    }

	@Test
	public void testJena() throws Exception {
		
		//Jena config
		BuiltinPersonalities.model.add( Individual.class, OWLBean.factory );
		
		
		JenaTest jt = new JenaTest();
		
		//ModelMaker maker = jt.getRDBMaker( true );
        // use the ont doc mgr to map from a generic URN to a local source file
		OntDocumentManager ontDocManager = OntDocumentManager.getInstance();
		ontDocManager.addAltEntry( "http://boozallen.com/soa/metamodel.owl", ClassLoader.getSystemResource( "metamodel.owl" ).toString() );
		ontDocManager.addAltEntry( "http://dcgs.enterprise.spfg/20080324.owl" , ClassLoader.getSystemResource( "des-example.owl" ).toString() );
        
		OWLModel m = OWLFactory.createOWLModel();
		
		
		//OntModel m = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, ModelFactory.createDefaultModel() ); //jt.getModelSpec( maker ));

	    m.setNsPrefix("bah", "http://boozallen.com/soa/metamodel.owl");
	    m.setNsPrefix("des", "http://dcgs.enterprise.spfg/20080324.owl");
		
	    //m.qnameFor();
		
        m.read( ClassLoader.getSystemResource( "metamodel.owl" ).toString() );
        m.read( ClassLoader.getSystemResource( "des-example.owl" ).toString() );
		
        m.prepare();
        
        //m.write( System.out );

        
        //String uri = m.expandPrefix( "bah:narrower" );
        //String hasDoubleUri = m.expandPrefix( "des:hasDouble" );
        
        
        //ObjectProperty narrower = m.getObjectProperty( uri );      
        //ObjectProperty broader = m.getObjectProperty("http://boozallen.com/soa/metamodel.owl#broader");
        //DatatypeProperty hasDouble = m.getDatatypeProperty( hasDoubleUri );
        
        long start = System.nanoTime();
        
        OntProperty hasDouble = m.getOWLProperty( "des:hasDouble" );

        long resolveFind = System.nanoTime() - start;
        
        start = System.nanoTime();
        
        OntProperty hasDouble0 = m.getOWLProperty( "des:hasDouble" );

        long resolveFind1 = System.nanoTime() - start;
        
        
        start = System.nanoTime();
        
        OntProperty hasDouble1 = m.getDatatypeProperty( "http://dcgs.enterprise.spfg/20080324.owl#hasDouble" );

        long find = System.nanoTime() - start;
        
        start = System.nanoTime();
        
        OntProperty hasDouble2 = m.getDatatypeProperty( "des#hasDouble" );

        long find2 = System.nanoTime() - start;
        
        
        System.out.println( "ResolveFind: " + resolveFind + " ResolveFind1: " + resolveFind1 + " find: " + find + " find2: " + find2 );
        
		for( ExtendedIterator i = hasDouble.listRange(); i.hasNext();  ) {
			OntClass range = (OntClass) i.next();
			System.out.println( range );
		}
        
        
        OntClass portfolio = m.getOntClass( "http://boozallen.com/soa/metamodel.owl#Portfolio" );
              
        ExtendedIterator i = portfolio.listInstances();
        while ( i.hasNext() ) {
        	OWLBean indiv = (OWLBean) i.next();
        	print( 0, indiv, "bah:narrower" );
        }
        System.out.println( "-------------------" );
        Individual serviceProfile = m.getIndividual("des:Data_Service_Reference_Architecture");
        
        if( serviceProfile == null ) {
        	System.out.println( "wrong uri");
        }
        else {
        	print( 0, serviceProfile, "bah:broader" );
        }
        
        System.out.println( "-------------------" );
        serviceProfile = m.getIndividual("http://dcgs.enterprise.spfg/20080324.owl#Data_Service_Reference_Architecture");
        
        if( serviceProfile == null ) {
        	System.out.println( "wrong uri");
        }
        else {
        	print( 0, serviceProfile, "bah:broader" );
        }
        
	}
	
	static void print(int levels, Individual head, String p) {
		StringBuilder str = new StringBuilder();
		for( int t = 0; t < levels; t++ ) {
			str.append( "\t" );
		}
		int inner = levels + 1;
		
		ExtendedIterator j = head.listOntClasses(true);
		if( j == null ) {
			return;
		}
		
		for( ; j.hasNext(); ) {
			OntClass clazz = (OntClass) j.next();
			str.append( clazz.getLocalName() );
			if( j.hasNext() ) {
				str.append( ", ");
			}
		}
		
		str.append( ": " + head.getURI() );
        
		System.out.println( str.toString() );
		
		OWLModel m = ( (OWLBean) head ).getOWLModel();
		
		OWLBean wrap = (OWLBean) ( (OWLBean) head ).get( "bah:narrower" );
		
		NodeIterator i = head.listPropertyValues( m.getOWLProperty( p ) );
        while ( i.hasNext() ) {
        	Individual indiv = (Individual) i.nextNode().as( Individual.class );     
    		print( inner, indiv, p );
        }
	}
	
	
}
