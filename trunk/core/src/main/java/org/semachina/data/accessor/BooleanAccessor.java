package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class BooleanAccessor extends DataAccessor<Boolean> {

	public BooleanAccessor(String uri) {
		super( uri, Boolean.class );
	}

	public Boolean parseLexicalForm(String lexicalForm) {
		return Boolean.valueOf( lexicalForm );
	}

	public String toLexicalForm(Boolean value) {
		return value.toString();
	}	
}