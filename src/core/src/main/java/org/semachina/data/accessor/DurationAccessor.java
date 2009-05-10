package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Duration;


public class DurationAccessor extends DataAccessor<Duration> {

	public DurationAccessor(String uri) {
		super( uri, Duration.class );
	}
	
	public Duration parseLexicalForm(String lexicalForm) {
		return new Duration( lexicalForm );
	}

	public String toLexicalForm(Duration value) {
		return value.toString();
	}

}
