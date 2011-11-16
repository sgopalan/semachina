package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{Months, DateTimeFieldType}
import scala.collection.JavaConversions._
import org.semachina.jena.datatype.SemachinaBaseDatatype
import org.semachina.jena.binder.ObjectBinder

object MonthDatatype {
  val monthFormat =
    ISODateTimeFormat.forFields(Seq(DateTimeFieldType.monthOfYear()), true, true)
}

class MonthDatatype extends SemachinaBaseDatatype[Months](
  typeURI = XSD.gMonth.getURI,
  parser = {
    lexicalForm: String =>
      Months.months(MonthDatatype.monthFormat.parseDateTime(lexicalForm).getMonthOfYear)
  },
  lexer = {
    cast: Months => MonthDatatype.monthFormat.print(cast.getMonths)
  }) {

  addObjectBinder(
    ObjectBinder[Number, Months]({
      number: Number => Months.months(number.intValue)
    }))
}