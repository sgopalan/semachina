package org.semachina.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OWLBean extends IndividualImpl {
	
    /**
     * A factory for generating Individual facets from nodes in enhanced graphs.
     * Note: should not be invoked directly by user code: use
     * {@link com.hp.hpl.jena.rdf.model.RDFNode#as as()} instead.
     */
    public static Implementation factory = new Implementation() {
        public EnhNode wrap( Node n, EnhGraph eg ) {
            if (canWrap( n, eg )) {
                return new OWLBean( n, eg );
            }
            else {
                throw new ConversionException( "Cannot convert node " + n.toString() + " to Individual");
            }
        }

        public boolean canWrap( Node node, EnhGraph eg ) {
            // node will support being an Individual facet if it is a URI node or bNode
            Profile profile = (eg instanceof OntModel) ? ((OntModel) eg).getProfile() : null;
            return (profile != null)  &&  profile.isSupported( node, eg, Individual.class );
        }
    };
	
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
	 * @see org.semachina.core.OWLBean#get(java.lang.String)
	 */
	public Object get(String property) {
		OntProperty ontProperty = getOntProperty( property );

		RDFNode rdf = getPropertyValue( ontProperty );
		return marshall( rdf );
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.core.OWLBean#getAll(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<?> getAll(String property) {
		
		OntProperty ontProperty = getOntProperty( property );
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
	 * @see org.semachina.core.OWLBean#add(java.lang.String, java.lang.Object)
	 */
	public OWLBean add(String property, Object value) {
		OntProperty ontProperty = getOntProperty( property );
			
		if( value != null ) {
			String typeURI = getTypeURI( ontProperty );
			RDFNode rdf = unmarshall( typeURI, value );
			addProperty( ontProperty, rdf );
		}

		return this;
	}

	/* (non-Javadoc)
	 * @see org.semachina.core.OWLBean#add(java.lang.String, java.lang.String)
	 */
	public OWLBean add(String property, String literal) {
		DatatypeProperty ontProperty = getDataPropertyInternal( property );
		
		
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
	 * @see org.semachina.core.OWLBean#set(java.lang.String, java.lang.Object)
	 */
	public OWLBean set(String property, Object value) {
		OntProperty ontProperty = getOntProperty( property );
		
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
	 * @see org.semachina.core.OWLBean#set(java.lang.String, java.lang.String)
	 */
	public OWLBean set(String property, String literal) {
		DatatypeProperty ontProperty = getDataPropertyInternal( property );
		
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
	 * @see org.semachina.core.OWLBean#remove(java.lang.String)
	 */
	public void remove(String property) {
		OntProperty ontProperty = getOntProperty( property );

		RDFNode rdf = getPropertyValue( ontProperty );
		if( rdf != null ) {
			this.removeProperty( ontProperty, rdf );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.core.OWLBean#remove(java.lang.String, java.lang.Object)
	 */
	public void remove(String property, Object value) {
		OntProperty ontProperty = getOntProperty( property );
		
		if( value == null ) {
			String typeURI = getTypeURI( ontProperty );
			RDFNode rdf = unmarshall( typeURI, value );
			if( rdf != null ) {
				this.removeProperty( ontProperty, rdf );
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.core.OWLBean#removeAll(java.lang.String)
	 */
	public void removeAll(String property) {
		Property p = getOntProperty( property );
		removeAll( p );
	}

	protected String expandProperty(String property) {
		String uri = getOntModel().expandPrefix( property );
	
		if( uri == null ) {
			throw new IllegalArgumentException( "URI cannot be evaluated as null: " + property );
		}
		return uri;
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.core.OWLBean#getObjectPropertyInternal(java.lang.String)
	 */
	public ObjectProperty getObjectPropertyInternal(String property) {
		String uri = expandProperty( property );
		
		ObjectProperty ontProperty = 
			getOntModel().getObjectProperty( uri );
		
		if( ontProperty == null ) {
			throw new IllegalArgumentException( 
					"There is no Property for: " + 
					property + " (" + uri + ") " );
		}
		
		return ontProperty;		
	}
	
	/* (non-Javadoc)
	 * @see org.semachina.core.OWLBean#getDataPropertyInternal(java.lang.String)
	 */
	public DatatypeProperty getDataPropertyInternal(String property) {
		String uri = expandProperty( property );
		
		DatatypeProperty ontProperty = 
			getOntModel().getDatatypeProperty( uri );
		
		if( ontProperty == null ) {
			throw new IllegalArgumentException( 
					"There is no Property for: " + 
					property + " (" + uri + ") " );
		}
		
		return ontProperty;		
	}
	
	protected OntProperty getOntProperty(String property) {
		String uri = expandProperty( property );
		
		OntProperty ontProperty = 
			getOntModel().getOntProperty( uri );
		
		if( ontProperty == null ) {
			throw new IllegalArgumentException( 
					"There is no Property for: " + 
					property + " (" + uri + ") " );
		}
		
		return ontProperty;		
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
			return objectify( rdf );
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
	
	public static OWLBean objectify(RDFNode rdf) {
		if( rdf != null && rdf.canAs( Individual.class ) ) {
			Individual indiv = (Individual) rdf.as( Individual.class );
			if( indiv instanceof OWLBean ) {
				return (OWLBean) indiv;
			}
			else if( indiv instanceof EnhNode ) {
				return new OWLBean( (EnhNode) indiv );
			}
			else {
				return new OWLBean( indiv.asNode(), null );
			}
		}
		return null;	
	}
}