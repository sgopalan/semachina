package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

import java.util.Date;

public class DateTimeFactory extends SimpleRDFDatatypeImpl<DateTime> {

    public DateTimeFactory() {
        super(XSD.dateTime.getURI(), DateTime.class);
    }

    @Override
    public DateTime parseLexicalForm(String lexicalForm) throws Exception {
        return ISODateTimeFormat.dateTime().parseDateTime(lexicalForm);
    }

    @Override
    public String toLexicalForm(DateTime cast) throws Exception {
        return ISODateTimeFormat.dateTime().print(cast);
    }

    @Override
    public Object cannonicalise(Object value) {
        if (value instanceof Date) {
            return new DateTime(((Date) value).getTime());
        }
        return super.cannonicalise(value);
    }
}
