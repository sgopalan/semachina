package org.semachina.jena.impl

import java.lang.String
/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jul 24, 2010
 * Time: 5:30:09 PM
 * To change this template use File | Settings | File Templates.
 */
class ScalaRDFDatatype[L <: Object](typeURI: String,
                                    javaClass: Class[L],
                                    lexer: L => String,
                                    parser: String => L,
                                    validator: L => Boolean)
        extends SimpleRDFDatatypeImpl(typeURI, javaClass) {
  def this(typeURI: String, javaClass: Class[L], lexer: L => String, parser: String => L) =
    this (typeURI, javaClass, lexer, parser, null);


  override def toLexicalForm(cast: L) = lexer(cast);

  override def parseLexicalForm(lexicalForm: String) = parser(lexicalForm);

  override def isValidValue(valueForm: Object): Boolean = {
    if (validator != null) {
      return validator(valueForm.asInstanceOf[L])
    }
    return super.isValidValue(valueForm);

  }
}
