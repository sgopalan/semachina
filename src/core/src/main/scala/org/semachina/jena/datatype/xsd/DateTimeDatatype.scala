package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.util.Date
import org.semachina.jena.datatype.SemachinaBaseDatatype

class DateTimeDatatype extends SemachinaBaseDatatype[DateTime] (
  XSD.dateTime.getURI,
  { lexicalForm: String => ISODateTimeFormat.dateTime.parseDateTime(lexicalForm) },
  { cast: DateTime => ISODateTimeFormat.dateTime.print(cast) } ) {

  override def cannonicalise(value: AnyRef): DateTime = {
    if (value.isInstanceOf[Date]) {
      return new DateTime((value.asInstanceOf[Date]).getTime)
    }
    return super.cannonicalise(value)
  }
}