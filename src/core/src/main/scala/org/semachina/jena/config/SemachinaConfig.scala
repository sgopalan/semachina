package org.semachina.jena.config

import com.hp.hpl.jena.shared.PrefixMapping
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.semachina.jena.impl.scala.ScalaRDFDatatype
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry
import org.apache.jena.larq.pfunction.textMatch


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jul 30, 2010
 * Time: 8:41:13 AM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaConfig extends DefaultSemachinaFactory {

  def registerDatatype[L <: Object](
          typeURI: String,
          javaClass: Class[L] = null,
          lexer: L => String = {(it: L) => it.toString},
          parser: String => L,
          validator: L => Boolean = null)(implicit m: Manifest[L]) = {

    var clazz = javaClass
    if (clazz == null) {
      clazz = m.erasure.asInstanceOf[Class[L]]
    }

    val dataType = new ScalaRDFDatatype[L](typeURI, clazz, lexer, parser, validator)
    getTypeMapper.registerDatatype(dataType)
  }
}