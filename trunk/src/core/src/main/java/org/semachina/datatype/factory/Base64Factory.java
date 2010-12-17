package org.semachina.datatype.factory;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.semachina.datatype.types.Base64;
import org.semachina.jena.impl.SimpleDataTypeFactory;

public class Base64Factory extends SimpleDataTypeFactory<Base64> {
    @Override
    public Base64 parseLexicalForm(String lexicalForm)throws Exception{
            return new Base64(lexicalForm);
    }

    @Override
    public String toLexicalForm(Base64 cast)throws Exception{
            return cast.toString();
    }
}