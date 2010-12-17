package org.semachina.jena.impl

import java.lang.String
import com.hp.hpl.jena.graph.impl.LiteralLabel
import com.hp.hpl.jena.datatypes.{TypeMapper, RDFDatatype, DatatypeFormatException}
import org.semachina.jena.DataTypeFactory
/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jul 24, 2010
 * Time: 5:30:09 PM
 * To change this template use File | Settings | File Templates.
 */
class ScalaDataTypeFactory[L <: Object](lexer: L => String, parser: String => L, validator: L => Boolean )
        extends DataTypeFactory[L] {
  def toLexicalForm(cast: L) = lexer( cast )

  def parseLexicalForm(lexicalForm: String) = parser( lexicalForm )

  def isValidValue(valueForm: L) = validator( valueForm )

  def hasValidator = validator != null
}




