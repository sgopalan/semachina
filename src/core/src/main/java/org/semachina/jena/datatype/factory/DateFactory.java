package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.vocabulary.XSD;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

import java.util.Date;
import java.util.Map;

public class DateFactory extends SimpleRDFDatatypeImpl<Date> {

    public DateFactory() {
        super(XSD.date.getURI(), Date.class);
    }

    public DateFactory(Map<Class, Map1<Object, Date>> converters) {
        super(XSD.date.getURI(), Date.class, converters);
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
        if (value instanceof LocalDate) {
            return ((LocalDate) value).toDateTimeAtStartOfDay().toDate();
        }
        else if (value instanceof DateTime) {
            return ((DateTime) value).toDate();
        }
        return super.cannonicalise(value);
    }
}
