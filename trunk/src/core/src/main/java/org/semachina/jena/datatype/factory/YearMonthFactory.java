package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.semachina.jena.datatype.types.YearMonth;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

public class YearMonthFactory extends SimpleRDFDatatypeImpl<YearMonth> {
    public YearMonthFactory() {
        super(XSD.gYearMonth.getURI(), YearMonth.class);
    }

    @Override
    public YearMonth parseLexicalForm(String lexicalForm) throws Exception {
        return new YearMonth(lexicalForm);
    }

    @Override
    public String toLexicalForm(YearMonth cast) throws Exception {
        return cast.toString();
    }
}
