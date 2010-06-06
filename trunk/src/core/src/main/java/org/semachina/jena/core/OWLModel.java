package org.semachina.jena.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OWLModel extends OntModelImpl {
	
	public OWLModel(OntModelSpec spec, Model model) {
		super(spec, model);
	}

	public OWLModel(OntModelSpec spec) {
		super(spec);
	}
	
	private Map<String, Property> resolvedProperties = 
		new ConcurrentHashMap<String, Property>();
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLModel#getOWLBeans(java.lang.String)
	 */
	public Collection<OWLBean> getOWLBeans(String classURI) {
		String uri = this.expandPrefix( classURI );		
		OntClass ontClass = this.getOntClass( uri );
		
		Collection<OWLBean> beans = new ArrayList<OWLBean>();
		
		if( ontClass != null ) {
			ExtendedIterator<?> i = ontClass.listInstances();
			while( i.hasNext() ) {
				Individual indiv = (Individual) i.next();
				beans.add( OWLFactory.objectify( indiv ) );
			}
		}
		return beans;
	}
	
	public OWLBean getOWLBean(String uri) {
		String fullUri = this.expandPrefix( uri );
		return (OWLBean) this.getIndividual( fullUri );
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLModel#ask(java.lang.String)
	 */
	public boolean ask(String sparql) {	
		Query query = createQuery( sparql );
		
		if( !query.isAskType() ) {
			throw new IllegalArgumentException( "Must be SPARQL ask query" );
		}
		
        QueryExecution qexec = QueryExecutionFactory.create( query, this ) ;
    
        try {
        	this.enterCriticalSection( Lock.READ );
        	return qexec.execAsk() ;     	
        }
        finally {
        	this.leaveCriticalSection();
            qexec.close() ;
        }		
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLModel#describe(java.lang.String)
	 */
	public OWLModel describe(String sparql) {

		Query query = createQuery( sparql );
		
		if( !query.isDescribeType() ) {
			throw new IllegalArgumentException( "Must be SPARQL describe query" );
		}
		
        QueryExecution qexec = QueryExecutionFactory.create( query, this ) ;
    
        OWLModel owlModel = null;
        
        try {
        	this.enterCriticalSection( Lock.READ );
        	
        	Model resultModel = qexec.execDescribe() ;
        	owlModel = new OWLModel( this.m_spec, resultModel );

        }
        finally {
        	this.leaveCriticalSection();
            qexec.close() ;
        }		
		return owlModel;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLModel#construct(java.lang.String)
	 */
	public OWLModel construct(String sparql) {
		
		Query query = createQuery( sparql );
		
		if( !query.isConstructType() ) {
			throw new IllegalArgumentException( "Must be SPARQL construct query" );
		}
		
        QueryExecution qexec = QueryExecutionFactory.create( query, this ) ;
    
    
        OWLModel owlModel = null;
        
        try {
        	this.enterCriticalSection( Lock.READ );
        	
        	Model resultModel = qexec.execConstruct() ;
        	owlModel = new OWLModel( this.m_spec, resultModel );

        }
        finally {
        	this.leaveCriticalSection();
            qexec.close() ;
        }		
		return owlModel;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLModel#select(java.lang.String)
	 */
	public List<Map<String, Object>> select(String sparql) {
        
		List<Map<String, Object>> results = 
			new ArrayList<Map<String, Object>>();
		
		Query query = createQuery( sparql );
		
		if( !query.isSelectType() ) {
			throw new IllegalArgumentException( "Must be SPARQL select query" );
		}
		
        QueryExecution qexec = QueryExecutionFactory.create( query, this ) ;
    
            
        try {
        	this.enterCriticalSection( Lock.READ );
        	
            // Assumption: it's a SELECT query.
            ResultSet rs = qexec.execSelect() ;
            
            // The order of results is undefined. 
            while ( rs.hasNext() ) {
            	
                QuerySolution rb = rs.nextSolution() ;
                
                Map<String, Object> result = new HashMap<String, Object>();
                
                Iterator<?> i = rb.varNames();
                while( i.hasNext() ) {
                	String varName = (String) i.next();
                	
                	RDFNode node = rb.get( varName );
                	Object value = null;
                	if( node.isLiteral() ) {
                		Literal literal = node.as( Literal.class );
                		value = literal.getValue();
                	}
                	else {
                		value = OWLFactory.objectify( node );
                	}
                	
                	result.put( varName, value );
                }
                
                results.add( result );
            }
        }
        finally {
        	this.leaveCriticalSection();
            qexec.close() ;
        }		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLModel#select(java.lang.String, org.semachina.jena.core.ResultsHandler)
	 */
	public void select(String sparql, ResultsHandler handler) {

		if( handler == null ) {
			return;
		}

		Query query = createQuery( sparql );
		
		if( !query.isSelectType() ) {
			throw new IllegalArgumentException( "Must be SPARQL select query" );
		}
		
        QueryExecution qexec = QueryExecutionFactory.create( query, this ) ;
    
        try {
        	this.enterCriticalSection( Lock.READ );
        	
            // Assumption: it's a SELECT query.
            ResultSet rs = qexec.execSelect() ;
            
            // The order of results is undefined. 
            while ( rs.hasNext() ) {
            	
                QuerySolution rb = rs.nextSolution() ;
                
                Map<String, Object> result = new HashMap<String, Object>();
                
                Iterator<?> i = rb.varNames();
                while( i.hasNext() ) {
                	String varName = (String) i.next();
                	
                	RDFNode node = rb.get( varName );
                	Object value = null;
                	if( node.isLiteral() ) {
                		Literal literal = node.as( Literal.class );
                		value = literal.getValue();
                	}
                	else {
                		value = OWLFactory.objectify( node );
                	}
                	
                	result.put( varName, value );
                }
                
               handler.process( result );
            }
        }
        finally {
        	this.leaveCriticalSection();
            qexec.close() ;
        }		
	}
	
	protected Query createQuery(String sparql) {
		Query query = new Query() ;
		query.getPrefixMapping().withDefaultMappings( this );
        query = QueryFactory.parse(query, sparql, null, Syntax.defaultSyntax) ;
        return query;
	}
	
	
	protected String expandProperty(String property) {
		
		String uri = super.expandPrefix( property );
	
		if( uri == null ) {
			throw new IllegalArgumentException( "URI cannot be evaluated as null: " + property );
		}
		return uri;
	}
	

	

	public OntProperty getOWLProperty(String property) {
		Property p = this.resolvedProperties.get( property );
		if( p != null ) {
			return (OntProperty) p;
		}
		
		String uri = expandProperty( property );
		
		OntProperty ontProperty = super.getOntProperty( uri );
		
		if( ontProperty == null ) {
			throw new IllegalArgumentException( 
					"There is no Property for: " + 
					property + " (" + uri + ") " );
		}
		
		if( !property.equals( uri ) ) {
			this.resolvedProperties.put( property, ontProperty );
		}
		
		return ontProperty;		
	}
	
	public Literal parseLiteral(String literalString) {
		String[] literalParts = literalString.split( "\\^\\^" );
		if( literalParts.length == 2 ) {
			
			if( TypeMapper.getInstance().getTypeByName( literalParts[1] ) != null ) {
				return createTypedLiteral( literalParts[0], literalParts[1] );
			}
		}

		return createLiteral( literalString );
	}
	
}