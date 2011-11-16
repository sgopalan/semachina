package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.format.ISODateTimeFormat
import java.util.Date
import org.semachina.jena.datatype.SemachinaBaseDatatype
import org.semachina.jena.binder.ObjectBinder
import org.joda.time.{DateTime, LocalDate}

class DateTimeDatatype extends SemachinaBaseDatatype[DateTime](
  typeURI = XSD.dateTime.getURI,
  parser = {
    lexicalForm: String => ISODateTimeFormat.dateTime.parseDateTime(lexicalForm)
  },
  lexer = {
    cast: DateTime => ISODateTimeFormat.dateTime.print(cast)
  }) {

  addObjectBinder(
    ObjectBinder[LocalDate, DateTime]({
      localDate: LocalDate => localDate.toDateTimeAtStartOfDay
    }))

  addObjectBinder(ObjectBinder[Date, DateTime]({
    date: Date => new DateTime(date.getTime)
  }))
}