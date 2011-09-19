package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{ Days, DateTimeFieldType }
import scala.collection.JavaConversions._
import org.semachina.jena.datatype.SemachinaBaseDatatype

object DayDatatype {
  val dayFormat = ISODateTimeFormat.forFields( Seq( DateTimeFieldType.dayOfMonth() ), true, true )
}

class DayDatatype extends SemachinaBaseDatatype[Days] (
  XSD.gDay.getURI,
  { lexicalForm: String => Days.days(DayDatatype.dayFormat.parseDateTime( lexicalForm ).getDayOfMonth) },
  { cast: Days => DayDatatype.dayFormat.print( cast.getDays ) }) {

  override def cannonicalise(value: AnyRef): Days = {
    if (value.isInstanceOf[Number]) {
      return Days.days((value.asInstanceOf[Number]).intValue)
    }
    return super.cannonicalise(value)
  }
}