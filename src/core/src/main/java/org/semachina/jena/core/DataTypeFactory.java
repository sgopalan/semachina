package org.semachina.jena.core;

public interface DataTypeFactory<C> {

	public C parseLexicalForm(String lexicalForm) throws Exception;

	public String toLexicalForm(C cast) throws Exception;
}
