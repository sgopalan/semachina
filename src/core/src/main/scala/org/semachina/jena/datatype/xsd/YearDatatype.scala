package org.semachina.jena.datatype.xsd

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.Years
import org.joda.time.format.ISODateTimeFormat
import org.semachina.jena.datatype.SemachinaBaseDatatype

class YearDatatype extends SemachinaBaseDatatype[Years](
  XSD.gYear.getURI,
  { lexicalForm: String => Years.years(ISODateTimeFormat.year().parseDateTime( lexicalForm ).getYear ) },
  { cast: Years => ISODateTimeFormat.year().print(cast.getYears) }) {

  override def cannonicalise(value: AnyRef): Years = {
    if (value.isInstanceOf[Number]) {
      return Years.years( value.asInstanceOf[Number].intValue )
    }
    return super.cannonicalise(value)
  }
}