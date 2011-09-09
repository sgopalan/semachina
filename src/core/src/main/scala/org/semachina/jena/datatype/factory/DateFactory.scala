package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SimpleRDFDatatype
import java.util.Date

class DateFactory extends SimpleRDFDatatype[Date](
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