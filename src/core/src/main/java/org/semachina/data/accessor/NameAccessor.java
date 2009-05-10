package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Name;


public class NameAccessor extends DataAccessor<Name> {

	public NameAccessor(String typeURI) {
		super( typeURI, Name.class );
	}
	
	public Name parseLexicalForm(String lexicalForm) {
		return new Name( lexicalForm );
	}

	public String toLexicalForm(Name value) {
		return value.toString();
	}

}
