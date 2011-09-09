package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.semachina.jena.datatype.SimpleRDFDatatype
import org.semachina.jena.datatype.types.Month

class MonthFactory extends SimpleRDFDatatype[Month] (
  XSD.gMonth.getURI,
  { lexicalForm: String => new Month(lexicalForm) }) {

  override def cannonicalise(value: AnyRef): Month = {
    if (value.isInstanceOf[Number]) {
      return new Month((value.asInstanceOf[Number]).intValue)
    }
    return super.cannonicalise(value)
  }
}