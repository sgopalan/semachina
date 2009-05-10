package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Token;


public class TokenAccessor extends DataAccessor<Token> {

	public TokenAccessor(String typeURI) {
		super( typeURI, Token.class );
	}
	
	public Token parseLexicalForm(String lexicalForm) {
		return new Token( lexicalForm );
	}

	public String toLexicalForm(Token value) {
		return value.toString();
	}

}
