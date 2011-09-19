package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.LocalTime
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SemachinaBaseDatatype

class TimeDatatype extends SemachinaBaseDatatype[LocalTime] (
  XSD.time.getURI,
  { lexicalForm: String => ISODateTimeFormat.timeParser.parseDateTime(lexicalForm).toLocalTime },
  { cast: LocalTime =>ISODateTimeFormat.timeParser.print(cast) })