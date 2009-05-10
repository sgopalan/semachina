package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;



public class ByteAccessor extends DataAccessor<Byte> {

	public ByteAccessor(String uri) {
		super( uri, Byte.class );
	}

	public Byte parseLexicalForm(String lexicalForm) {
		return Byte.valueOf( lexicalForm );
	}

	public String toLexicalForm(Byte value) {
		return value.toString();
	}	
}