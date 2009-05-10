package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.NonNegativeInteger;


public class NonNegativeIntegerAccessor extends DataAccessor<NonNegativeInteger> {

	public NonNegativeIntegerAccessor(String typeURI) {
		super( typeURI, NonNegativeInteger.class );
	}
	
	public NonNegativeInteger parseLexicalForm(String lexicalForm) {
		return new NonNegativeInteger( lexicalForm );
	}

	public String toLexicalForm(NonNegativeInteger value) {
		return value.toString();
	}

}
