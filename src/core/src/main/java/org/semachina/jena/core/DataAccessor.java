/**
 * 
 */
package org.semachina.jena.core;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.impl.LiteralLabel;

/**
 * @author sgopalan
 * @param <L>
 *
 */
public class DataAccessor<L> implements RDFDatatype {

	private String _typeURI;
	
	private Class<L> _javaClass;
	
	private DataTypeFactory<L> _factory;
	
	public DataAccessor(String typeURI, Class<L> javaClass, DataTypeFactory<L> factory) throws Exception {
		_typeURI = typeURI;
		_javaClass = javaClass;
		_factory = factory;
	}
	
    /**
     * Return the URI which is the label for this datatype
     */
    public String getURI() {
        return _typeURI;
    }
       
	public L parse(String lexicalForm) throws DatatypeFormatException {
		if( lexicalForm == null ) {
			return null;
		}
		
		try {
			return parseLexicalForm( lexicalForm );
		} 
		catch (Exception e) {
			throw new DatatypeFormatException( lexicalForm, 
											   this, 
											   e.getMessage() );
		}
	}
	
	public String unparse(Object value) {
		try {
			if( getJavaClass().isInstance( value ) ) {
				return toLexicalForm( getJavaClass().cast( value ) );
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException( "Must be of type: " 
				+ getJavaClass() 
				+ ".  Not: " + value.getClass() );
	}
    
	/**
     * Test whether the given string is a legal lexical form
     * of this datatype.
     */
    public boolean isValid(String lexicalForm) {
        try {
            parseLexicalForm( lexicalForm );
            return true;
        } 
        catch (Exception e) {
            return false;
        }
    }    
    
    /**
     * Test whether the given LiteralLabel is a valid instance
     * of this datatype. This takes into accound typing information
     * as well as lexical form - for example an xsd:string is
     * never considered valid as an xsd:integer (even if it is
     * lexically legal like "1").
     */
    public boolean isValidLiteral(LiteralLabel lit) {
        // default is that only literals with the same type are valid
        return equals( lit.getDatatype() );
    }
     
    /**
     * Test whether the given object is a legal value form
     * of this datatype.
     */
    public boolean isValidValue(Object valueForm) {
        // Default to brute force
        return isValid( unparse( valueForm ) );
    }
    
    /**
     * Compares two instances of values of the given datatype.
     * This default requires value and datatype equality.
     */
    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
        return value1.getDatatype() == value2.getDatatype()
             && value1.getValue().equals(value2.getValue());
    }
    
    /**
         Default implementation of getHashCode() delegates to the default from
         the literal label.
    */
    public int getHashCode( LiteralLabel lit ) {
        return lit.getDefaultHashcode();
    }
    
    /**
     * Helper function to compare language tag values
     */
    public boolean langTagCompatible(LiteralLabel value1, LiteralLabel value2) {
        if (value1.language() == null) {
            return (value2.language() == null || value2.language().equals(""));
        } 
        else {
            return value1.language().equalsIgnoreCase(value2.language());
        }
    }
    
    /**
     * Returns the java class which is used to represent value
     * instances of this datatype.
     */
    public Class<L> getJavaClass() {
        return _javaClass;
    }
    
    /**
     * Cannonicalise a java Object value to a normal form.
     * Primarily used in cases such as xsd:integer to reduce
     * the Java object representation to the narrowest of the Number
     * subclasses to ensure that indexing of typed literals works. 
     */
    public L cannonicalise( Object value ) {
        if( getJavaClass().isInstance( value ) ) {
        	return getJavaClass().cast( value );
        }
    	return null;
    }
    
    /**
     * Returns an object giving more details on the datatype.
     * This is type system dependent. In the case of XSD types
     * this will be an instance of 
     * <code>org.apache.xerces.impl.xs.psvi.XSTypeDefinition</code>.
     */
    public Object extendedTypeDefinition() {
        return null;
    }
    
    /**
     * Normalization. If the value is narrower than the current data type
     * (e.g. value is xsd:date but the time is xsd:datetime) returns
     * the narrower type for the literal. 
     * If the type is narrower than the value then it may normalize
     * the value (e.g. set the mask of an XSDDateTime)
     * Currently only used to narrow gener XSDDateTime objects
     * to the minimal XSD date/time type.
     * @param value the current object value
     * @param dt the currently set data type
     * @return a narrower version of the datatype based on the actual value range
     */
    public RDFDatatype normalizeSubType(Object value, RDFDatatype dt) {
        return this; // default is no narrowing
    }
    
    /**
     * Display format
     */
    public String toString() {
        return "Datatype[" + getURI()
              + (getJavaClass() == null ? "" : " -> " + getJavaClass())
              + "]";
    }
	
	public String getTypeURI() {
		return _typeURI;
	}
		
	public L parseLexicalForm(String lexicalForm) throws Exception {
		return _factory.parseLexicalForm( lexicalForm );
	}

	public String toLexicalForm(L cast) throws Exception {
		return _factory.toLexicalForm( cast );
	}
}