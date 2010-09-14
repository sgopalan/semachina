package org.semachina.jena.core

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 9, 2010
 * Time: 7:47:33 PM
 * To change this template use File | Settings | File Templates.
 */


class StringLiteralWrapper(value: String) {
  def plainLit(lang: String = ""): ScalaLiteralImpl = {
    return ScalaLiteralImpl.createLiteral(literalString = value, lang = lang)
  }

  def typedLit(typeURI: String): ScalaLiteralImpl = {
    return ScalaLiteralImpl.createTypedLiteral(value, typeURI)
  }

  def parseLit(): ScalaLiteralImpl = {
    return ScalaLiteralImpl.parseTypedLiteral(value)
  }
}