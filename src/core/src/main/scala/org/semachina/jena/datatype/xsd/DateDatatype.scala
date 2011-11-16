package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import java.util.Date
import org.semachina.jena.datatype.SemachinaBaseDatatype
import org.semachina.jena.binder.ObjectBinder

class DateDatatype extends SemachinaBaseDatatype[Date](
  typeURI = XSD.date.getURI,
  parser = {
    lexicalForm: String => ISODateTimeFormat.date.parseDateTime(lexicalForm).toDate
  },
  lexer = {
    cast: Date => ISODateTimeFormat.date.print(new DateTime(cast.getTime))
  }) {

  addObjectBinder(
    ObjectBinder[LocalDate, Date]({
      localDate: LocalDate =>
        localDate.toDateTimeAtStartOfDay.toDate
    }))

  addObjectBinder(ObjectBinder[DateTime, Date]({
    dateTime: DateTime => dateTime.toDate
  }))
}