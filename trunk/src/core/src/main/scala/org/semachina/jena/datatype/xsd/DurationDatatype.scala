package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.Period
import org.joda.time.format.ISOPeriodFormat
import org.semachina.jena.datatype.SemachinaBaseDatatype

class DurationDatatype extends SemachinaBaseDatatype[Period](
  typeURI = XSD.duration.getURI,
  parser = {
    lexicalForm: String => ISOPeriodFormat.standard.parsePeriod(lexicalForm)
  },
  lexer = {
    cast: Period => ISOPeriodFormat.standard.print(cast)
  })