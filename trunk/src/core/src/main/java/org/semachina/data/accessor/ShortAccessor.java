package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class ShortAccessor extends DataAccessor<Short> {

	public ShortAccessor(String uri) {
		super( uri, Short.class );
	}

	public Short parseLexicalForm(String lexicalForm) {
		return Short.valueOf( lexicalForm );
	}

	public String toLexicalForm(Short value) {
		return value.toString();
	}	
}