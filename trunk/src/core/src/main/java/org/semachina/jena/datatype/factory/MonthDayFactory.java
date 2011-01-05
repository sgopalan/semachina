package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.semachina.jena.datatype.types.MonthDay;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

public class MonthDayFactory extends SimpleRDFDatatypeImpl<MonthDay> {
    public MonthDayFactory() {
        super(XSD.gMonthDay.getURI(), MonthDay.class);
    }

    @Override
    public MonthDay parseLexicalForm(String lexicalForm) throws Exception {
        return new MonthDay(lexicalForm);
    }

    @Override
    public String toLexicalForm(MonthDay cast) throws Exception {
        return cast.toString();
    }
}
