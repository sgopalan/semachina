package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.UnsignedLong;


public class UnsignedLongAccessor extends DataAccessor<UnsignedLong> {

	public UnsignedLongAccessor(String typeURI) {
		super( typeURI, UnsignedLong.class );
	}
	
	public UnsignedLong parseLexicalForm(String lexicalForm) {
		return new UnsignedLong( lexicalForm );
	}

	public String toLexicalForm(UnsignedLong value) {
		return value.toString();
	}

}
