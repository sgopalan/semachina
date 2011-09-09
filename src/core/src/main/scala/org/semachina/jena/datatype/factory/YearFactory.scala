package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.semachina.jena.datatype.types.Year
import org.semachina.jena.datatype.SimpleRDFDatatype

class YearFactory extends SimpleRDFDatatype[Year](
  XSD.gYear.getURI,
  { lexicalForm: String => new Year( lexicalForm ) } ) {

  override def cannonicalise(value: AnyRef): Year = {
    if (value.isInstanceOf[Number]) {
      return new Year((value.asInstanceOf[Number]).intValue)
    }
    return super.cannonicalise(value)
  }
}