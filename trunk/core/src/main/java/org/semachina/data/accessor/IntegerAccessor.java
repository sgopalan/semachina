package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class IntegerAccessor extends DataAccessor<Integer> {

	public IntegerAccessor(String uri) {
		super( uri, Integer.class );
	}
	
	public Integer parseLexicalForm(String lexicalForm) {
		return Integer.valueOf( lexicalForm );
	}

	public String toLexicalForm(Integer value) {
		return value.toString();
	}

}
