package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.NegativeInteger;


public class NegativeIntegerAccessor extends DataAccessor<NegativeInteger> {

	public NegativeIntegerAccessor(String typeURI) {
		super( typeURI, NegativeInteger.class );
	}
	
	public NegativeInteger parseLexicalForm(String lexicalForm) {
		return new NegativeInteger( lexicalForm );
	}

	public String toLexicalForm(NegativeInteger value) {
		return value.toString();
	}

}
