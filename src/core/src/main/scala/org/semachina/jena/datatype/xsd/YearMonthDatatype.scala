package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.YearMonth
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SemachinaBaseDatatype

class YearMonthDatatype extends SemachinaBaseDatatype[YearMonth](
  typeURI = XSD.gYearMonth.getURI,
  parser = {
    lexicalForm: String =>
      new YearMonth(ISODateTimeFormat.yearMonth().parseDateTime(lexicalForm))
  })