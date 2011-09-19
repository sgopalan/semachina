package org.semachina.jena.config

import com.hp.hpl.jena.rdf.model.{RDFNode, ModelFactory}
import com.hp.hpl.jena.ontology.{OntModel, OntDocumentManager}
import java.net.URI
import com.hp.hpl.jena.enhanced.{Implementation, BuiltinPersonalities, Personality}
import com.hp.hpl.jena.shared.impl.JenaParameters
import com.hp.hpl.jena.datatypes.TypeMapper
import com.hp.hpl.jena.shared.PrefixMapping
import org.semachina.jena.enhanced.SemachinaImplementation
import org.semachina.jena.datatype.SemachinaBaseDatatype


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 4, 2011
 * Time: 1:06:06 PM
 * To change this template use File | Settings | File Templates.
 */

trait JenaConfiguration {

  val prefixMapping = ModelFactory.setDefaultModelPrefixes( PrefixMapping.Standard )

  val ontDocumentManager = OntDocumentManager.getInstance

  val typeMapper: TypeMapper = TypeMapper.getInstance

  val personality : Personality[RDFNode] = BuiltinPersonalities.model

  //include methods to configure Jena
  setEnableEagerLiteralValidation( true )
  setEnableSilentAcceptanceOfUnknownDatatypes( false )

  def setEnableEagerLiteralValidation(enable : Boolean) {
    JenaParameters.enableEagerLiteralValidation = enable
    this
  }

  def setEnableSilentAcceptanceOfUnknownDatatypes(enable : Boolean) = {
    JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = enable
    this
  }

  def registerImplementation[V <: RDFNode](impl : SemachinaImplementation[V] )(implicit m: Manifest[V]) :  JenaConfiguration =  {
    impl.supportedClasses.foreach { supportedClass : Class[_] => registerImplementation( supportedClass.asInstanceOf[Class[RDFNode]], impl ) }
    return this
  }


  def registerImplementation[V <: RDFNode](interface : Class[V], impl : Implementation ) = {
    personality.add( interface, impl )
    this
  }

  def getImplementation[V <: RDFNode](interface: Class[V]) = personality.getImplementation( interface )

  def registerDatatype[L <: Object](datatypeFactory: SemachinaBaseDatatype[L]) = {
    typeMapper.registerDatatype(datatypeFactory)
    this
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

    val dataType = new SemachinaBaseDatatype[L](typeURI, parser, lexer, validator)
    typeMapper.registerDatatype(dataType)
    this
  }

  def registerURI(uri: String,
                  prefix: String = null,
                  separator: String = "#",
                  altEntry: String = null ) = {

    //check if this is a valid URI
    val realURI = URI.create( uri )

    //set the prefix mapping
    if( prefix != null ) {
      val uriToPrefix = uri + separator
      prefixMapping.setNsPrefix( prefix, uriToPrefix )
    }

    //set the alt entry
    if( altEntry != null ) {
      ontDocumentManager.addAltEntry( uri, altEntry )
    }
    this
  }

  def getURI(uri : String) : String = {
    //establish the working URI
    //see if a prefix is given instead of full uri
    var workingURI = prefixMapping.getNsPrefixURI( uri )
    if( workingURI == null ) {
      workingURI = uri
    }
    else {
      workingURI = workingURI.substring(0, workingURI.length - 1)
    }
    return workingURI
  }

  def expandPrefix(shortURI : String) = prefixMapping.expandPrefix( shortURI )

  def shortForm(longURI : String) = prefixMapping.shortForm( longURI )

  def loadPrefix(prefix : String, ontModel : OntModel = null) : OntModel =
    load( getURI( prefix ), ontModel )

  def load(uri : String, ontModel: OntModel = null) : OntModel = {

    //establish working model
    var workingModel = ontModel
    if( ontModel == null ) {
      workingModel = ModelFactory.createOntologyModel
    }

    //load import and return model
    ontDocumentManager.loadImport( workingModel, uri )
    return workingModel
  }
}