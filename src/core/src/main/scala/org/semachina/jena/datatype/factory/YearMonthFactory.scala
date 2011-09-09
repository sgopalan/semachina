package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.semachina.jena.datatype.types.YearMonth
import org.semachina.jena.datatype.SimpleRDFDatatype


class YearMonthFactory extends SimpleRDFDatatype[YearMonth](
  XSD.gYearMonth.getURI,
  { lexicalForm: String => new YearMonth(lexicalForm) } )