package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Entity;


public class EntityAccessor extends DataAccessor<Entity> {

	public EntityAccessor(String uri) {
		super( uri, Entity.class );
	}
	
	public Entity parseLexicalForm(String lexicalForm) {
		return new Entity( lexicalForm );
	}

	public String toLexicalForm(Entity value) {
		return value.toString();
	}
}
