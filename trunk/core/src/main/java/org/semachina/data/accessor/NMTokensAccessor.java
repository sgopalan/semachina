package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.NMTokens;


public class NMTokensAccessor extends DataAccessor<NMTokens> {

	public NMTokensAccessor(String typeURI) {
		super( typeURI, NMTokens.class );
	}
	
	public NMTokens parseLexicalForm(String lexicalForm) {
		return new NMTokens( lexicalForm );
	}

	public String toLexicalForm(NMTokens value) {
		return value.toString();
	}

}
