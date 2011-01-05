package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.semachina.jena.datatype.types.Day;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

public class DayFactory extends SimpleRDFDatatypeImpl<Day> {
    public DayFactory() {
        super(XSD.gDay.getURI(), Day.class);
    }

    @Override
    public Day parseLexicalForm(String lexicalForm) throws Exception {
        return new Day(lexicalForm);
    }

    @Override
    public String toLexicalForm(Day cast) throws Exception {
        return cast.toString();
    }

    @Override
    public Object cannonicalise(Object value) {
        if (value instanceof Number) {
            return new Day(((Number) value).intValue());
        }
        return super.cannonicalise(value);
    }
}
