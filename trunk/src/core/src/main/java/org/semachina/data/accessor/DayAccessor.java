package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Day;


public class DayAccessor extends DataAccessor<Day> {

	public DayAccessor(String uri) {
		super( uri, Day.class );
	}

	public Day parseLexicalForm(String lexicalForm) {
		return new Day( lexicalForm );
	}

	public String toLexicalForm(Day value) {
		return value.toString();
	}	
}