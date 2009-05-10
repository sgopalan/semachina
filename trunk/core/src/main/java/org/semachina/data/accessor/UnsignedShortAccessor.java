package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.UnsignedShort;


public class UnsignedShortAccessor extends DataAccessor<UnsignedShort> {

	public UnsignedShortAccessor(String typeURI) {
		super( typeURI, UnsignedShort.class );
	}
	
	public UnsignedShort parseLexicalForm(String lexicalForm) {
		return new UnsignedShort( lexicalForm );
	}

	public String toLexicalForm(UnsignedShort value) {
		return value.toString();
	}

}
