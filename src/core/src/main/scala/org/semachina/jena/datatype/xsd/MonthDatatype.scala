package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{Months, DateTimeFieldType}
import scala.collection.JavaConversions._
import org.semachina.jena.datatype.SemachinaBaseDatatype

object MonthDatatype {
  val monthFormat = ISODateTimeFormat.forFields( Seq( DateTimeFieldType.monthOfYear() ), true, true )
}

class MonthDatatype extends SemachinaBaseDatatype[Months] (
  XSD.gMonth.getURI,
  { lexicalForm: String => Months.months( MonthDatatype.monthFormat.parseDateTime( lexicalForm ).getMonthOfYear ) },
  { cast: Months => MonthDatatype.monthFormat.print( cast.getMonths ) }) {

  override def cannonicalise(value: AnyRef): Months = {
    if (value.isInstanceOf[Number]) {
      return Months.months( (value.asInstanceOf[Number]).intValue )
    }
    return super.cannonicalise(value)
  }
}