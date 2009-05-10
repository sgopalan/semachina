package org.semachina.data.accessor;

import java.math.BigDecimal;

import org.semachina.core.DataAccessor;




public class DecimalAccessor extends DataAccessor<BigDecimal> {

	public DecimalAccessor(String uri) {
		super( uri, BigDecimal.class );
	}

	public BigDecimal parseLexicalForm(String lexicalForm) {
		return new BigDecimal( lexicalForm );
	}

	public String toLexicalForm(BigDecimal value) {
		return value.toString();
	}	
}