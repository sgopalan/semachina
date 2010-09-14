package org.semachina.jena.core

import java.lang.String
import com.hp.hpl.jena.graph.impl.LiteralLabel
import com.hp.hpl.jena.datatypes.{TypeMapper, RDFDatatype, DatatypeFormatException}

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
                                    validator: L => Boolean) extends RDFDatatype {
  def this(typeURI: String, javaClass: Class[L], lexer: L => String, parser: String => L) =
    this (typeURI, javaClass, lexer, parser, null);

  /**
   * Return the URI which is the label for this datatype
   */
  def getURI: String = {
    return typeURI
  }

  def parse(lexicalForm: String): L = {
    if (lexicalForm == null) {
      return None.asInstanceOf[L]
    }
    try {
      return parser(lexicalForm)
    }
    catch {
      case e: Exception => {
        throw new DatatypeFormatException(lexicalForm, this, e.getMessage)
      }
    }
  }

  def unparse(valueForm: AnyRef): String = {
    if (valueForm == null) {
      throw new NullPointerException("valueForm cannot be null");
    }

    try {

      //special number exception
      var validate = suitableNumber(valueForm)

      if (getJavaClass.isInstance(validate)) {
        return lexer(getJavaClass.cast(validate))
      }
    }
    catch {
      case e: Exception => {
        throw new DatatypeFormatException(valueForm.toString, this, e.getMessage)
      }
    }
    throw new IllegalArgumentException("Must be of type: " + getJavaClass + ".  Not: " + valueForm.getClass.toString)
  }

  /**
   * This is needed because PreDef has an implicit conversion that seems to
   * change all Numbers in to Integers.  In the case where we need a Long, this
   * causes issues.
   */
  def suitableNumber(valueForm: AnyRef): Any = {
    //special number exception
    var validate = valueForm;
    if (classOf[Number].isAssignableFrom(getJavaClass) &&
            valueForm.isInstanceOf[Number]) {

      val clazzName: String = getJavaClass.getName
      var numberValue: Number = valueForm.asInstanceOf[Number]

      var cannonicalValue: Number = clazzName match {
        case "java.lang.Byte" => numberValue.byteValue
        case "java.lang.Double" => numberValue.doubleValue
        case "java.lang.Float" => numberValue.floatValue
        case "java.lang.Integer" => numberValue.intValue
        case "java.lang.Long" => numberValue.longValue
        case "java.lang.Short" => numberValue.shortValue
      }

      if (cannonicalValue != null) {
        return cannonicalValue
      }

      return numberValue;
    }

    return valueForm
  }

  /**
   * Test whether the given string is a legal lexical form
   * of this datatype.
   */
  def isValid(lexicalForm: String): Boolean = {
    try {
      if (lexicalForm != null && parse(lexicalForm) == null) {
        return false
      }
      return true
    }
    catch {
      case e: Exception => {
        return false
      }
    }
  }


  /**
   * Test whether the given LiteralLabel is a valid instance
   * of this datatype. This takes into accound typing information
   * as well as lexical form - for example an xsd:string is
   * never considered valid as an xsd:integer (even if it is
   * lexically legal like "1").
   */
  def isValidLiteral(lit: LiteralLabel): Boolean = {
    return equals(lit.getDatatype)
  }


  /**
   * Test whether the given object is a legal value form
   * of this datatype.
   */
  def isValidValue(valueForm: AnyRef): Boolean = {
    if (validator != null) {

      //special number exception
      var validate = suitableNumber(valueForm)
      return validator(validate.asInstanceOf[L])
    }
    return isValid(unparse(valueForm))
  }


  /**
   * Compares two instances of values of the given datatype.
   * This default requires value and datatype equality.
   */
  def isEqual(value1: LiteralLabel, value2: LiteralLabel): Boolean = {
    return value1.getDatatype == value2.getDatatype && value1.getValue.equals(value2.getValue)
  }


  /**
  Default implementation of getHashCode() delegates to the default from
  the literal label.
   */
  def getHashCode(lit: LiteralLabel): Int = {
    return lit.getDefaultHashcode
  }


  /**
   * Helper function to compare language tag values
   */
  def langTagCompatible(value1: LiteralLabel, value2: LiteralLabel): Boolean = {
    if (value1.language == null) {
      return (value2.language == null || value2.language.equals(""))
    }
    else {
      return value1.language.equalsIgnoreCase(value2.language)
    }
  }


  /**
   * Returns the java class which is used to represent value
   * instances of this datatype.
   */
  def getJavaClass: Class[L] = {
    return javaClass
  }


  /**
   * Cannonicalise a java Object value to a normal form.
   * Primarily used in cases such as xsd:integer to reduce
   * the Java object representation to the narrowest of the Number
   * subclasses to ensure that indexing of typed literals works.
   */
  def cannonicalise(valueForm: AnyRef): L = {

    val validate = suitableNumber(valueForm);

    if (getJavaClass.isInstance(validate)) {
      return getJavaClass.cast(validate)
    }
    return None.asInstanceOf[L]
  }

  /**
   * Returns an object giving more details on the datatype.
   * This is type system dependent. In the case of XSD types
   * this will be an instance of
   * <code>org.apache.xerces.impl.xs.psvi.XSTypeDefinition</code>.
   */
  def extendedTypeDefinition: Object = {
    return null
  }

  /**
   * Normalization. If the value is narrower than the current data type
   * (e.g. value is xsd:date but the time is xsd:datetime) returns
   * the narrower type for the literal.
   * If the type is narrower than the value then it may normalize
   * the value (e.g. set the mask of an XSDDateTime)
   * Currently only used to narrow gener XSDDateTime objects
   * to the minimal XSD date/time type.
   * @param value the current object value
   * @param dt the currently set data type
   * @return a narrower version of the datatype based on the actual value range
   */
  def normalizeSubType(value: Any, dt: RDFDatatype): RDFDatatype = {
    return this
  }

  /**
   * Display format
   */
  override def toString: String = {
    return "Datatype[" + getURI + (if (getJavaClass == null) "" else " -> " + getJavaClass) + "]"
  }
}

object ScalaRDFDatatype {
  implicit def apply[L <: Object](typeURI: String,
                                  javaClass: Class[L] = null,
                                  lexer: L => String = {(it: L) => it.toString},
                                  parser: String => L,
                                  validator: L => Boolean = null)(implicit m: Manifest[L]) = {
    var clazz = javaClass
    if (clazz == null) {
      clazz = m.erasure.asInstanceOf[Class[L]]
    }

    val dataType = new ScalaRDFDatatype[L](typeURI, clazz, lexer, parser, validator)

    //should move this out
    TypeMapper.getInstance.registerDatatype(dataType);
  }
}