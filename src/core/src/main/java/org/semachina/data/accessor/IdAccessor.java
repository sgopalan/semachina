package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Id;


public class IdAccessor extends DataAccessor<Id> {

	public IdAccessor(String uri) {
		super( uri, Id.class );
	}
	
	public Id parseLexicalForm(String lexicalForm) {
		return new Id( lexicalForm );
	}

	public String toLexicalForm(Id value) {
		return value.toString();
	}

}
