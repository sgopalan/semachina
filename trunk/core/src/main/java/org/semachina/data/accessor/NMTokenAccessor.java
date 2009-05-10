package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.NMToken;


public class NMTokenAccessor extends DataAccessor<NMToken> {

	public NMTokenAccessor(String typeURI) {
		super( typeURI, NMToken.class );
	}
	
	public NMToken parseLexicalForm(String lexicalForm) {
		return new NMToken( lexicalForm );
	}

	public String toLexicalForm(NMToken value) {
		return value.toString();
	}

}
