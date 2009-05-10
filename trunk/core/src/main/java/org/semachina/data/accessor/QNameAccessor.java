package org.semachina.data.accessor;

import javax.xml.namespace.QName;

import org.semachina.core.DataAccessor;




public class QNameAccessor extends DataAccessor<QName> {

	public QNameAccessor(String typeURI) {
		super( typeURI, QName.class );
	}
	
	public QName parseLexicalForm(String lexicalForm) {
		return QName.valueOf( lexicalForm );
	}

	public String toLexicalForm(QName value) {
		return value.toString();
	}

}
