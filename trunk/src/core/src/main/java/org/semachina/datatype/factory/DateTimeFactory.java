package org.semachina.datatype.factory;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.semachina.jena.impl.SimpleDataTypeFactory;

public class DateTimeFactory extends SimpleDataTypeFactory<DateTime> {
    @Override
    public DateTime parseLexicalForm(String lexicalForm)throws Exception{
            return ISODateTimeFormat.dateTime().parseDateTime(lexicalForm);
    }

    @Override
    public String toLexicalForm(DateTime cast)throws Exception{
            return ISODateTimeFormat.dateTime().print(cast);
    }
}