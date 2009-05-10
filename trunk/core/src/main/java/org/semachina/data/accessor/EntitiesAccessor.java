package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Entities;


public class EntitiesAccessor extends DataAccessor<Entities> {

	public EntitiesAccessor(String uri) {
		super( uri, Entities.class );
	}
	
	public Entities parseLexicalForm(String lexicalForm) {
		return new Entities( lexicalForm );
	}

	public String toLexicalForm(Entities value) {
		return value.toString();
	}

}
