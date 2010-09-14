package org.semachina.jena.core

import com.hp.hpl.jena.datatypes.{TypeMapper, RDFDatatype}
import com.hp.hpl.jena.graph.impl.{LiteralLabelFactory, LiteralLabel}
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl
import com.hp.hpl.jena.enhanced.{EnhNode, Implementation, EnhGraph}
import com.hp.hpl.jena.rdf.model.{LiteralRequiredException, Literal}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Aug 10, 2010
 * Time: 11:05:19 PM
 * To change this template use File | Settings | File Templates.
 */
class ScalaLiteralImpl(n: Node, m: EnhGraph) extends LiteralImpl(n, m) {
  def apply[C](): C = {
    return getValue.asInstanceOf[C]
  }

}



object ScalaLiteralImpl {
  val factory: Implementation = new Implementation {
    def wrap(n: Node, eg: EnhGraph): EnhNode = {
      if (!n.isLiteral) throw new LiteralRequiredException(n)
      return new ScalaLiteralImpl(n, eg)
    }

    def canWrap(n: Node, eg: EnhGraph): Boolean = {
      return n.isLiteral
    }
  }

  def apply(n: Node, m: EnhGraph) = new ScalaLiteralImpl(n, m);

  def apply(lit: Literal) = new ScalaLiteralImpl(lit.asNode, lit.getModel.asInstanceOf[EnhGraph])

  /**
   * Build a typed literal from its value form.
   *
   * @param value the value of the literal
   * @param dtype the type of the literal, null for old style "typed" literals
   */
  def createTypedLiteral(value: Any, dType: RDFDatatype): ScalaLiteralImpl = {
    var ll: LiteralLabel = LiteralLabelFactory.create(value, "", dType)
    return ScalaLiteralImpl(Node.createLiteral(ll), null)
  }

  /**
   * Build a typed literal label from its value form using
   * whatever datatype is currently registered as the the default
   * representation for this java class. No language tag is supplied.
   * @param value the literal value to encapsulate
   */
  def createTypedLiteral(value: Any): ScalaLiteralImpl = {
    var dType: RDFDatatype = TypeMapper.getInstance.getTypeByValue(value);
    return createTypedLiteral(value, dType);
  }

  /**
   * Build a typed literal from its lexical form. The
   * lexical form will be parsed now and the value stored. If
   * the form is not legal this will throw an exception.
   *
   * @param lex the lexical form of the literal
   * @param typeURI the uri of the type of the literal, null for old style "typed" literals
   * @throws DatatypeFormatException if lex is not a legal form of dtype
   */
  def createTypedLiteral(lex: String, typeURI: String): ScalaLiteralImpl = {
    var dt: RDFDatatype = TypeMapper.getInstance.getSafeTypeByName(typeURI)
    var ll: LiteralLabel = LiteralLabelFactory.createLiteralLabel(lex, "", dt)
    return ScalaLiteralImpl(Node.createLiteral(ll), null)
  }

  def createLiteral(literalString: String, lang: String = ""): ScalaLiteralImpl = {
    return ScalaLiteralImpl(Node.createLiteral(literalString, lang, false), null)
  }

  def parseTypedLiteral(literalString: String): ScalaLiteralImpl = {
    var literalParts: Array[String] = literalString.split("\\^\\^")
    if (literalParts.length == 2) {
      val dtype = TypeMapper.getInstance.getSafeTypeByName(literalParts(1))
      if (dtype != null) {
        return createTypedLiteral(literalParts(0), dtype)
      }
    }
    return ScalaLiteralImpl(Node.createLiteral(literalString, "", false), null)
  }
}