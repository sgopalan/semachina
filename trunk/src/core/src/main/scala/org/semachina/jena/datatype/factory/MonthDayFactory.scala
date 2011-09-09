package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.semachina.jena.datatype.SimpleRDFDatatype
import org.semachina.jena.datatype.types.MonthDay

class MonthDayFactory extends SimpleRDFDatatype[MonthDay] (
  XSD.gMonthDay.getURI,
  { lexicalForm: String => new MonthDay(lexicalForm) })