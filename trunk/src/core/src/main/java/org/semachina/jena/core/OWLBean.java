package org.semachina.jena.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OWLBean extends IndividualImpl {
		
	public OWLBean(Node node, EnhGraph graph) {
		super( node, graph );
	}

	public OWLBean(EnhNode indiv) {
		this( indiv.asNode(),  indiv.getGraph() );
	}

	protected String getTypeURI(OntProperty property) {
		String uri = null;
		if( property.isObjectProperty() ) {
			return null;
		}
		else if( property.isDatatypeProperty() ) {
			OntResource range = property.getRange();	
			uri = RDFS.Literal.getURI();
			if( range != null ) {
				uri = range.getURI();
			}
		}
		else {
			throw new IllegalArgumentException( "Not supported: " + property );
		}
		
		return uri;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#get(java.lang.String)
	 */
	public Object get(String property) {
		OntProperty ontProperty = this.getOntModel().getOntProperty( property );

		RDFNode rdf = getPropertyValue( ontProperty );
		return marshall( rdf );
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#getAll(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<?> getAll(String property) {
		
		OntProperty ontProperty = this.getOntModel().getOntProperty( property );
		Collection results = new ArrayList();
		
		NodeIterator nodeIterator = listPropertyValues( ontProperty );
		
		List rdfs = nodeIterator.toList();
		int size = rdfs.size();
		
		for( int i = 0; i < size; i++ ) {
			RDFNode rdf = (RDFNode) rdfs.get( i );
			results.add( marshall( rdf ) );
		}
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#add(java.lang.String, java.lang.Object)
	 */
	public OWLBean add(String property, Object value) {
		OntProperty ontProperty = getOWLModel().getOWLProperty( property );
			
		if( value != null ) {
			String typeURI = getTypeURI( ontProperty );
			RDFNode rdf = unmarshall( typeURI, value );
			addProperty( ontProperty, rdf );
		}

		return this;
	}

	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#add(java.lang.String, java.lang.String)
	 */
	public OWLBean add(String property, String literal) {
		OntProperty ontProperty = getOWLModel().getOWLProperty( property );
		
		
		if( literal != null ) {
			try {
				String typeURI = getTypeURI( ontProperty );
				
				RDFNode rdf = unmarshall( typeURI, literal );
				if( rdf != null ) {
					addProperty( ontProperty, rdf );
				}
			} 
			catch (Exception e) {
				throw new RuntimeException( e.getMessage(), e );
			}	
		}

		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#set(java.lang.String, java.lang.Object)
	 */
	public OWLBean set(String property, Object value) {
		OntProperty ontProperty = getOWLModel().getOWLProperty( property );
		
		if( value != null ) {
			String typeURI = getTypeURI( ontProperty );
			RDFNode rdf = unmarshall( typeURI, value );
			if( rdf != null ) {
				setPropertyValue( ontProperty, rdf );
			}
		}

		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#set(java.lang.String, java.lang.String)
	 */
	public OWLBean set(String property, String literal) {
		OntProperty ontProperty = getOWLModel().getOWLProperty( property );
		
		if( literal != null ) {
			
			try {
				String typeURI = getTypeURI( ontProperty );
				RDFNode rdf = unmarshall( typeURI, literal );
				if( rdf != null ) {
					setPropertyValue( ontProperty, rdf );
				}
			} 
			catch (Exception e) {
				throw new RuntimeException( e.getMessage(), e );
			}
			
		}

		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#remove(java.lang.String)
	 */
	public void remove(String property) {
		OntProperty ontProperty = getOWLModel().getOWLProperty( property );

		RDFNode rdf = getPropertyValue( ontProperty );
		if( rdf != null ) {
			this.removeProperty( ontProperty, rdf );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#remove(java.lang.String, java.lang.Object)
	 */
	public void remove(String property, Object value) {
		OntProperty ontProperty = getOWLModel().getOWLProperty( property );
		
		if( value == null ) {
			String typeURI = getTypeURI( ontProperty );
			RDFNode rdf = unmarshall( typeURI, value );
			if( rdf != null ) {
				this.removeProperty( ontProperty, rdf );
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.jena.core.OWLBean#removeAll(java.lang.String)
	 */
	public void removeAll(String property) {
		Property p = getOWLModel().getOWLProperty( property );
		removeAll( p );
	}

	protected Object marshall(RDFNode rdf) {
		if( rdf == null ) {
			return null;
		}
		
		if( rdf.isLiteral() ) {
			Literal literal = (Literal) rdf.as( Literal.class );
			return literal.getValue();
		}
		else {
			return OWLFactory.objectify( rdf );
		}
	}
	
	protected RDFNode unmarshall(String typeURI, Object value) {
		if( typeURI == null ) {
			if( value instanceof RDFNode ) {
				return (RDFNode) value;
			}
			throw new IllegalStateException( "data accessor is null, and value is of type: " + value.getClass() );
		}
		
		Literal literal = 
			getOntModel()
				   .createTypedLiteral( value, typeURI );
		
		return literal;
	}
	

	public OWLModel getOWLModel() {
		return (OWLModel) this.getOntModel();
	}
	
}