package org.semachina.jena.config

import com.hp.hpl.jena.shared.PrefixMapping
import com.hp.hpl.jena.shared.impl.JenaParameters
import org.semachina.jena.impl._
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry
import com.hp.hpl.jena.query.larq3.library.TextMatch


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jul 30, 2010
 * Time: 8:41:13 AM
 * To change this template use File | Settings | File Templates.
 */

object SemachinaConfig {
  /**
  A PrefixMapping built on Standard with some extras
   */
  val PREFIX: PrefixMapping = PrefixMapping.Factory.create
          .setNsPrefixes(PrefixMapping.Standard)

  val factory = new DefaultSemachinaFactory()

  def init(): Unit = {

    //register function... This should happen in config, not here
    PropertyFunctionRegistry.get().put("http://jena.hpl.hp.com/ARQ/property#textMatch3", classOf[TextMatch])


    JenaParameters.enableEagerLiteralValidation = true
    JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = false

    factory.initImplementationClasses
    factory.initDatatypes
  }

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

    //should move this out
    factory.getTypeMapper.registerDatatype(dataType);
  }
}