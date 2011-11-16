package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SemachinaBaseDatatype
import java.util.Date
import org.semachina.jena.binder.ObjectBinder
import org.joda.time.{LocalTime, DateTime}

class TimeDatatype extends SemachinaBaseDatatype[LocalTime](
  typeURI = XSD.time.getURI,
  parser = {
    lexicalForm: String =>
      ISODateTimeFormat.timeParser.parseDateTime(lexicalForm).toLocalTime
  },
  lexer = {
    cast: LocalTime => ISODateTimeFormat.timeParser.print(cast)
  }) {

  addObjectBinder(ObjectBinder[Date, LocalTime]({
    date: Date => new LocalTime(date)
  }))

  addObjectBinder(
    ObjectBinder[DateTime, LocalTime]({
      dateTime: DateTime => dateTime.toLocalTime
    }))
}