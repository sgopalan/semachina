package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.NCName;


public class NCNameAccessor extends DataAccessor<NCName> {

	public NCNameAccessor(String typeURI) {
		super( typeURI, NCName.class );
	}
	
	public NCName parseLexicalForm(String lexicalForm) {
		return new NCName( lexicalForm );
	}

	public String toLexicalForm(NCName value) {
		return value.toString();
	}

}
