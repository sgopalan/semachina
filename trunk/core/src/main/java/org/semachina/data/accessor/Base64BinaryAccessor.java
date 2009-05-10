package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Base64Binary;


public class Base64BinaryAccessor extends DataAccessor<Base64Binary> {

	public Base64BinaryAccessor(String uri) {
		super( uri, Base64Binary.class );
	}

	public Base64Binary parseLexicalForm(String lexicalForm) {
		return new Base64Binary( lexicalForm );
	}
	
	public String toLexicalForm(Base64Binary value) {
		return value.toString();
	}
}
