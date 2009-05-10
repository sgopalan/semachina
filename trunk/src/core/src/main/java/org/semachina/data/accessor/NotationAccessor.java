package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Notation;


public class NotationAccessor extends DataAccessor<Notation> {

	public NotationAccessor(String typeURI) {
		super( typeURI, Notation.class );
	}
	
	public Notation parseLexicalForm(String lexicalForm) {
		//return new Notation( lexicalForm );
		return null;
	}

	public String toLexicalForm(Notation value) {
		return value.toString();
	}

}
