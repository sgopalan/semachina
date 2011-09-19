package org.semachina.jena.datatype

import com.hp.hpl.jena.util.iterator.Map1
import com.hp.hpl.jena.datatypes.BaseDatatype
import com.hp.hpl.jena.datatypes.DatatypeFormatException
import javax.validation.constraints.NotNull
import java.util.{Collections, HashMap, Map}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/30/11
 * Time: 10:26 PM
 * To change this template use File | Settings | File Templates.
 */

class SemachinaBaseDatatype[L <: Object](@NotNull typeURI : String,
                                     javaClass: Class[L],
                                     parser: String => L,
                                     lexer: L => String,
                                     validator: L => Boolean,
                                     converters: Map[Class[_], Map1[Object, L]])
        extends BaseDatatype(typeURI) {

  def this(typeURI: String,
           parser: String => L,
           lexer: L => String = null,
           validator: L => Boolean = null,
           converters: Map[Class[_], Map1[AnyRef, L]] = new HashMap[Class[_], Map1[Object, L]])
          (implicit m: scala.reflect.Manifest[L]) =
    this( typeURI, m.erasure.asInstanceOf[Class[L]], parser, lexer, validator, converters )

  def toLexicalForm(cast: L) = if(lexer != null) lexer(cast) else cast.toString

  def parseLexicalForm(lexicalForm: String) = parser(lexicalForm);

  override def isValidValue(valueForm: Object): Boolean = {
    if (validator != null) {
      return validator(valueForm.asInstanceOf[L])
    }

    var value = this.cannonicalise(valueForm)
    return isValid(unparse(value))
  }

  override def parse(@NotNull lexicalForm: String): L = {
    try {
      return parseLexicalForm(lexicalForm)
    }
    catch {
      case e: Exception => {
        throw new DatatypeFormatException(lexicalForm, this, e.getMessage)
      }
    }
  }

  override def unparse(@NotNull valueForm: Object): String = {
    try {
      if (getJavaClass.isInstance(valueForm)) {
        return toLexicalForm(getJavaClass.cast(valueForm))
      }
    }
    catch {
      case e: Exception => {
        throw new DatatypeFormatException(valueForm.toString, this, e.getMessage)
      }
    }
    throw new IllegalArgumentException("Must be of type: " + getJavaClass + ".  Not: " + valueForm.getClass.toString )
  }

  /**
   * Returns the java class which is used to represent value
   * instances of this datatype.
   */
  override def getJavaClass: Class[L] = javaClass

  override def cannonicalise(value: AnyRef): L = {
    var converter: Map1[Object, L] = converters.get(value.getClass)
    if (converter != null) {
      return converter.map1(value)
    }

    if( javaClass.isAssignableFrom( value.getClass ) ) {
      return value.asInstanceOf[L]
    }

    throw new DatatypeFormatException("Must be able to cannonicalize: " + javaClass + " " + value )
  }
}