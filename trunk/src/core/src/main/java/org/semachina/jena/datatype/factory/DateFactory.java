package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

import java.util.Date;

public class DateFactory extends SimpleRDFDatatypeImpl<Date> {

    public DateFactory() {
        super(XSD.date.getURI(), Date.class);
    }

    @Override
    public Date parseLexicalForm(String lexicalForm) throws Exception {
        return ISODateTimeFormat.date().parseDateTime(lexicalForm).toDate();
    }

    @Override
    public String toLexicalForm(Date cast) throws Exception {
        return ISODateTimeFormat.date().print(new DateTime(cast.getTime()));
    }

    @Override
    public Object cannonicalise(Object value) {
        if (value instanceof DateTime) {
            return ((DateTime) value).toDate();
        }
        return super.cannonicalise(value);
    }
}
