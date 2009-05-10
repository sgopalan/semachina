package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class DoubleAccessor extends DataAccessor<Double> {

	public DoubleAccessor(String uri) {
		super( uri, Double.class );
	}

	public Double parseLexicalForm(String lexicalForm) {
		return Double.valueOf( lexicalForm );
	}

	public String toLexicalForm(Double value) {
		return value.toString();
	}	
}