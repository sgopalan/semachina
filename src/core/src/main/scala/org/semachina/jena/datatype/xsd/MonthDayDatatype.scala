package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTimeFieldType, MonthDay}
import scala.collection.JavaConversions._
import org.semachina.jena.datatype.SemachinaBaseDatatype

object MonthDayDatatype {
  val monthDayFormat = ISODateTimeFormat.forFields(Seq(DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()), true, true)
}

class MonthDayDatatype extends SemachinaBaseDatatype[MonthDay](
  typeURI = XSD.gMonthDay.getURI,
  parser = {
    lexicalForm: String =>
      new MonthDay(MonthDayDatatype.monthDayFormat.parseDateTime(lexicalForm))
  },
  lexer = {
    cast: MonthDay => MonthDayDatatype.monthDayFormat.print(cast)
  })