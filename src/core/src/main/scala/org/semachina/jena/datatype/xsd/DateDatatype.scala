package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import java.util.Date
import org.semachina.jena.datatype.SemachinaBaseDatatype

class DateDatatype extends SemachinaBaseDatatype[Date](
  XSD.date.getURI,
  { lexicalForm: String  => ISODateTimeFormat.date.parseDateTime(lexicalForm).toDate },
  { cast: Date => ISODateTimeFormat.date.print(new DateTime(cast.getTime) ) } ) {

  override def cannonicalise(value: AnyRef): Date = {
    if (value.isInstanceOf[LocalDate]) {
      return (value.asInstanceOf[LocalDate]).toDateTimeAtStartOfDay.toDate
    }
    else if (value.isInstanceOf[DateTime]) {
      return (value.asInstanceOf[DateTime]).toDate
    }
    return super.cannonicalise(value)
  }
}