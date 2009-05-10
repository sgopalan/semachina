package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.Language;


public class LanguageAccessor extends DataAccessor<Language> {

	public LanguageAccessor(String uri) {
		super( uri, Language.class );
	}
	
	public Language parseLexicalForm(String lexicalForm) {
		return new Language( lexicalForm );
	}

	public String toLexicalForm(Language value) {
		return value.toString();
	}

}
