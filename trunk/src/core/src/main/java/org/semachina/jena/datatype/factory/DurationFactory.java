package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

public class DurationFactory extends SimpleRDFDatatypeImpl<Period> {
    public DurationFactory() {
        super(XSD.duration.getURI(), Period.class);
    }

    @Override
    public Period parseLexicalForm(String lexicalForm) throws Exception {
        return ISOPeriodFormat.standard().parsePeriod(lexicalForm);
    }

    @Override
    public String toLexicalForm(Period cast) throws Exception {
        return ISOPeriodFormat.standard().print(cast);
    }
}
