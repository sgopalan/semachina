package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.semachina.jena.datatype.types.Month;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

public class MonthFactory extends SimpleRDFDatatypeImpl<Month> {
    public MonthFactory() {
        super(XSD.gMonth.getURI(), Month.class);
    }

    @Override
    public Month parseLexicalForm(String lexicalForm) throws Exception {
        return new Month(lexicalForm);
    }

    @Override
    public String toLexicalForm(Month cast) throws Exception {
        return cast.toString();
    }

    @Override
    public Object cannonicalise(Object value) {
        if (value instanceof Number) {
            return new Month(((Number) value).intValue());
        }
        return super.cannonicalise(value);
    }
}
