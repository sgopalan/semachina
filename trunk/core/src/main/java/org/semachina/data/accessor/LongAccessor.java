package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class LongAccessor extends DataAccessor<Long> {

	public LongAccessor(String uri) {
		super( uri, Long.class );
	}

	public Long parseLexicalForm(String lexicalForm) {
		return Long.valueOf( lexicalForm );
	}

	public String toLexicalForm(Long value) {
		return value.toString();
	}	
}