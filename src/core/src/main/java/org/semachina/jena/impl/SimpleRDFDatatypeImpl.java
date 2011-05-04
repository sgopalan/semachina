package org.semachina.jena.impl;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.util.iterator.Map1;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Dec 17, 2010
 * Time: 10:53:32 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class SimpleRDFDatatypeImpl<L> extends BaseDatatype {

    private Class<L> javaClass;

    private Map<Class, Map1<Object, L>> converters;

    public SimpleRDFDatatypeImpl(String typeURI, Class<L> javaClass) {
        this(typeURI, javaClass, new HashMap<Class, Map1<Object, L>>() );
    }

    public SimpleRDFDatatypeImpl(String typeURI, Class<L> javaClass, Map<Class, Map1<Object, L>> converters) {
        super(typeURI);
        this.javaClass = javaClass;
        this.converters = converters;
    }

    public L parse(String lexicalForm) {
        if (lexicalForm == null) {
            return null;
        }
        try {
            return parseLexicalForm(lexicalForm);
        } catch (Exception e) {
            throw new DatatypeFormatException(lexicalForm, this, e.getMessage());
        }
    }

    public String unparse(Object valueForm) {
        if (valueForm == null) {
            throw new NullPointerException("valueForm cannot be null");
        }

        try {

            if (getJavaClass().isInstance(valueForm)) {
                return toLexicalForm(getJavaClass().cast(valueForm));
            }
        } catch (Exception e) {
            throw new DatatypeFormatException(valueForm.toString(), this, e.getMessage());
        }

        throw new IllegalArgumentException("Must be of type: " + getJavaClass() + ".  Not: " + valueForm.getClass().toString());
    }

    /**
     * Returns the java class which is used to represent value
     * instances of this datatype.
     */
    public Class<L> getJavaClass() {
        return javaClass;
    }


    abstract public L parseLexicalForm(String lexicalForm) throws Exception;

    public String toLexicalForm(L cast) throws Exception {
        return cast.toString();
    }


    public boolean isValidValue(Object valueForm) {
        Object value = this.cannonicalise(valueForm);
        // Default to brute force
        return isValid(unparse(value));
    }

    @Override
    public Object cannonicalise(Object value) {
        Map1<Object, L> converter = converters.get( value.getClass() );
        if( converter != null ) {
            return converter.map1( value );
        }

        return super.cannonicalise(value);
    }
}
