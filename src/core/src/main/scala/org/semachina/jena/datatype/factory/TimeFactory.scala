package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.LocalTime
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SimpleRDFDatatype

class TimeFactory extends SimpleRDFDatatype[LocalTime] (
  XSD.time.getURI,
  { lexicalForm: String => ISODateTimeFormat.timeParser.parseDateTime(lexicalForm).toLocalTime },
  { cast: LocalTime =>ISODateTimeFormat.timeParser.print(cast) })