package org.semachina.jena.impl;

import org.semachina.jena.DataTypeFactory;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 12/11/10
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDataTypeFactory<C> implements DataTypeFactory<C> {
    @Override
    public C parseLexicalForm(String lexicalForm) throws Exception {
        return (C) lexicalForm;
    }

    @Override
    public String toLexicalForm(C cast) throws Exception {
        return cast.toString();
    }

    @Override
    public boolean isValidValue(C valueForm) {
        return false;
    }

    @Override
    public boolean hasValidator() {
        return false;
    }
}
