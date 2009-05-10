package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.UnsignedByte;


public class UnsignedByteAccessor extends DataAccessor<UnsignedByte> {

	public UnsignedByteAccessor(String typeURI) {
		super( typeURI, UnsignedByte.class );
	}
	
	public UnsignedByte parseLexicalForm(String lexicalForm) {
		return new UnsignedByte( lexicalForm );
	}

	public String toLexicalForm(UnsignedByte value) {
		return value.toString();
	}

}
