package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.NormalizedString;


public class NormalizedStringAccessor extends DataAccessor<NormalizedString> {

	public NormalizedStringAccessor(String typeURI) {
		super( typeURI, NormalizedString.class );
	}
	
	public NormalizedString parseLexicalForm(String lexicalForm) {
		return new NormalizedString( lexicalForm );
	}

	public String toLexicalForm(NormalizedString value) {
		return value.toString();
	}

}
