package org.semachina.data.accessor;

import org.semachina.core.DataAccessor;
import org.semachina.data.HexBinary;


public class HexBinaryAccessor extends DataAccessor<HexBinary> {

	public HexBinaryAccessor(String uri) {
		super( uri, HexBinary.class );
	}
	
	public HexBinary parseLexicalForm(String lexicalForm) {
		return new HexBinary( lexicalForm );
	}

	public String toLexicalForm(HexBinary value) {
		return value.toString();
	}

}
