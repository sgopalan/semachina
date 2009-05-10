package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.URI;
import org.semachina.data.URI.MalformedURIException;


public class URIAccessor extends DataAccessor<URI> {

	public URIAccessor(String typeURI) {
		super( typeURI, URI.class );
	}
	
	public URI parseLexicalForm(String lexicalForm) throws MalformedURIException {
		return new URI( lexicalForm );
	}

	public String toLexicalForm(URI value) {
		return value.toString();
	}

}
