package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Year;


public class YearAccessor extends DataAccessor<Year> {

	public YearAccessor(String typeURI) {
		super( typeURI, Year.class );
	}
	
	public Year parseLexicalForm(String lexicalForm) {
		return new Year( lexicalForm );
	}

	public String toLexicalForm(Year value) {
		return value.toString();
	}

}
