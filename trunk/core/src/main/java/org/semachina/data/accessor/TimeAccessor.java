package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Time;


public class TimeAccessor extends DataAccessor<Time> {

	public TimeAccessor(String typeURI) {
		super( typeURI, Time.class );
	}
	
	public Time parseLexicalForm(String lexicalForm) {
		return new Time( lexicalForm );
	}

	public String toLexicalForm(Time value) {
		return value.toString();
	}

}
