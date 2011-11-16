package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.format.ISODateTimeFormat
import scala.collection.JavaConversions._
import org.semachina.jena.datatype.SemachinaBaseDatatype
import org.semachina.jena.binder.ObjectBinder
import org.joda.time.{Days, DateTimeFieldType}

object DayDatatype {
  val dayFormat = ISODateTimeFormat.forFields(Seq(DateTimeFieldType.dayOfMonth()), true, true)
}

class DayDatatype extends SemachinaBaseDatatype[Days](
  typeURI = XSD.gDay.getURI,
  parser = {
    lexicalForm: String =>
      Days.days(DayDatatype.dayFormat.parseDateTime(lexicalForm).getDayOfMonth)
  },
  lexer = {
    cast: Days => DayDatatype.dayFormat.print(cast.getDays)
  }) {

  addObjectBinder(
    ObjectBinder[Number, Days]({
      number: Number => Days.days(number.intValue)
    }))
}