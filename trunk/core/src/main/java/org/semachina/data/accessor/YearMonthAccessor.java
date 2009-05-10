package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.YearMonth;


public class YearMonthAccessor extends DataAccessor<YearMonth> {

	public YearMonthAccessor(String typeURI) {
		super( typeURI, YearMonth.class );
	}
	
	public YearMonth parseLexicalForm(String lexicalForm) {
		return new YearMonth( lexicalForm );
	}

	public String toLexicalForm(YearMonth value) {
		return value.toString();
	}

}
