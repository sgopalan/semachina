package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class StringAccessor extends DataAccessor<String> {

	public StringAccessor(String uri) {
		super( uri, String.class );
	}

	public String parseLexicalForm(String lexicalForm) {
		return String.valueOf( lexicalForm );
	}

	public String toLexicalForm(String value) {
		return value.toString();
	}	
}