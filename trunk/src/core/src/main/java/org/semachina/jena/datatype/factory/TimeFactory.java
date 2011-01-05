package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

public class TimeFactory extends SimpleRDFDatatypeImpl<LocalTime> {
    public TimeFactory() {
        super(XSD.time.getURI(), LocalTime.class);
    }

    @Override
    public LocalTime parseLexicalForm(String lexicalForm) throws Exception {
        DateTime dt = ISODateTimeFormat.timeParser().parseDateTime(lexicalForm);
        return dt.toLocalTime();
    }

    @Override
    public String toLexicalForm(LocalTime cast) throws Exception {
        return ISODateTimeFormat.timeParser().print(cast);
    }
}
