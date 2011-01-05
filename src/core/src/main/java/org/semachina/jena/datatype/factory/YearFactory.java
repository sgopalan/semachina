package org.semachina.jena.datatype.factory;

import com.hp.hpl.jena.vocabulary.XSD;
import org.semachina.jena.datatype.types.Year;
import org.semachina.jena.impl.SimpleRDFDatatypeImpl;

public class YearFactory extends SimpleRDFDatatypeImpl<Year> {
    public YearFactory() {
        super(XSD.gYear.getURI(), Year.class);
    }

    @Override
    public Year parseLexicalForm(String lexicalForm) throws Exception {
        return new Year(lexicalForm);
    }

    @Override
    public String toLexicalForm(Year cast) throws Exception {
        return cast.toString();
    }

    @Override
    public Object cannonicalise(Object value) {
        if (value instanceof Number) {
            return new Year(((Number) value).intValue());
        }
        return super.cannonicalise(value);
    }
}
