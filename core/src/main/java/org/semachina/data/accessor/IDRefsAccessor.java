package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.IDRefs;


public class IDRefsAccessor extends DataAccessor<IDRefs> {

	public IDRefsAccessor(String uri) {
		super( uri, IDRefs.class );
	}
	
	public IDRefs parseLexicalForm(String lexicalForm) {
		return new IDRefs( lexicalForm );
	}

	public String toLexicalForm(IDRefs value) {
		return value.toLexicalForm();
	}

}
