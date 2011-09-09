package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.semachina.jena.datatype.SimpleRDFDatatype
import org.semachina.jena.datatype.types.Day

class DayFactory extends SimpleRDFDatatype[Day] (
  XSD.gDay.getURI,
  { lexicalForm: String => new Day(lexicalForm) }) {

  override def cannonicalise(value: AnyRef): Day = {
    if (value.isInstanceOf[Number]) {
      return new Day((value.asInstanceOf[Number]).intValue)
    }
    return super.cannonicalise(value)
  }
}