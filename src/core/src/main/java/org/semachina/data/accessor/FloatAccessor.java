package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class FloatAccessor extends DataAccessor<Float> {

	public FloatAccessor(String uri) {
		super( uri, Float.class );
	}

	public Float parseLexicalForm(String lexicalForm) {
		return Float.valueOf( lexicalForm );
	}

	public String toLexicalForm(Float value) {
		return value.toString();
	}	
}