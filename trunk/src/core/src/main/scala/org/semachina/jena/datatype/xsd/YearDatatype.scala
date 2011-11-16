package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.Years
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SemachinaBaseDatatype
import org.semachina.jena.binder.ObjectBinder

class YearDatatype extends SemachinaBaseDatatype[Years](
  typeURI = XSD.gYear.getURI,
  parser = {
    lexicalForm: String =>
      Years.years(ISODateTimeFormat.year().parseDateTime(lexicalForm).getYear)
  },
  lexer = {
    cast: Years => ISODateTimeFormat.year().print(cast.getYears)
  }) {

  addObjectBinder(
    ObjectBinder[Number, Years]({
      number: Number => Years.years(number.intValue)
    }))
}