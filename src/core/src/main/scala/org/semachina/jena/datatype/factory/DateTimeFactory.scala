package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SimpleRDFDatatype
import java.util.Date

class DateTimeFactory extends SimpleRDFDatatype[DateTime] (
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