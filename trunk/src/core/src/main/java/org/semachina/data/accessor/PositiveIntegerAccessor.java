package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.PositiveInteger;


public class PositiveIntegerAccessor extends DataAccessor<PositiveInteger> {

	public PositiveIntegerAccessor(String typeURI) {
		super( typeURI, PositiveInteger.class );
	}
	
	public PositiveInteger parseLexicalForm(String lexicalForm) {
		return new PositiveInteger( lexicalForm );
	}

	public String toLexicalForm(PositiveInteger value) {
		return value.toString();
	}

}
