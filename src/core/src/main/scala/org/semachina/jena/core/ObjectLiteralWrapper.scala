package org.semachina.jena.core

import com.hp.hpl.jena.datatypes.RDFDatatype

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 9, 2010
 * Time: 7:46:21 PM
 * To change this template use File | Settings | File Templates.
 */

class ObjectLiteralWrapper(value: Any) {
  def typedLit(dType: RDFDatatype): ScalaLiteralImpl = {
    if (dType == null) {
      throw new IllegalArgumentException("RDFDatatype cannot be null");
    }

    return ScalaLiteralImpl.createTypedLiteral(value, dType)
  }

  def typedLit(): ScalaLiteralImpl = {
    return ScalaLiteralImpl.createTypedLiteral(value)
  }
}
