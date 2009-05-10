package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Month;


public class MonthAccessor extends DataAccessor<Month> {

	public MonthAccessor(String typeURI) {
		super( typeURI, Month.class );
	}

	public Month parseLexicalForm(String lexicalForm) {
		return new Month( lexicalForm );
	}

	public String toLexicalForm(Month value) {
		return value.toLexicalForm();
	}

}
