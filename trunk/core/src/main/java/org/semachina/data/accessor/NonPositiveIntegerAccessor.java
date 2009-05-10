package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.NonPositiveInteger;


public class NonPositiveIntegerAccessor extends DataAccessor<NonPositiveInteger> {

	public NonPositiveIntegerAccessor(String typeURI) {
		super( typeURI, NonPositiveInteger.class );
	}
	
	public NonPositiveInteger parseLexicalForm(String lexicalForm) {
		return new NonPositiveInteger( lexicalForm );
	}

	public String toLexicalForm(NonPositiveInteger value) {
		return value.toString();
	}

}
