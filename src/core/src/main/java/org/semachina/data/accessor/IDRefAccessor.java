package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.IDRef;


public class IDRefAccessor extends DataAccessor<IDRef> {

	public IDRefAccessor(String uri) {
		super( uri, IDRef.class );
	}
	
	public IDRef parseLexicalForm(String lexicalForm) {
		return new IDRef( lexicalForm );
	}

	public String toLexicalForm(IDRef value) {
		return value.toString();
	}
}