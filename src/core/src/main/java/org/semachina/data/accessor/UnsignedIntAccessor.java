package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.UnsignedInt;


public class UnsignedIntAccessor extends DataAccessor<UnsignedInt> {

	public UnsignedIntAccessor(String typeURI) {
		super( typeURI, UnsignedInt.class );
	}
	
	public UnsignedInt parseLexicalForm(String lexicalForm) {
		return new UnsignedInt( lexicalForm );
	}

	public String toLexicalForm(UnsignedInt value) {
		return value.toString();
	}

}
