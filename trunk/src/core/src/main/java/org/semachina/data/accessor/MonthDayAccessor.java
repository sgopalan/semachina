package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.MonthDay;


public class MonthDayAccessor extends DataAccessor<MonthDay> {

	public MonthDayAccessor(String uri) {
		super( uri, MonthDay.class );
	}
	
	public MonthDay parseLexicalForm(String lexicalForm) {
		return new MonthDay( lexicalForm );
	}

	public String toLexicalForm(MonthDay value) {
		return value.toLexicalForm();
	}

}
