package org.semachina.jena.ontology

import com.hp.hpl.jena.ontology.OntModel
import org.semachina.jena.features.Feature
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import _root_.java.io.{InputStream, Reader}
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl
import com.hp.hpl.jena.rdf.model.Model

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 8/28/11
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */

trait OntModelConfiguration {

  protected lazy val features = new HashMap[String, Feature]

  def getOntModel: OntModel

  def addFeature(feature: Feature): Unit = {
    var featureKey: String = feature.getKey
    if (features.containsKey(featureKey)) {
      throw new IllegalArgumentException("key already exists: " + featureKey)
    }
    feature.init(getOntModel)
    features(featureKey) = feature
  }

  def getFeature(featureKey: String): Feature = {
    if (features == null || !features.containsKey(featureKey)) {
      throw new IllegalArgumentException("feature not implemented: " + featureKey)
    }
    return features(featureKey)
  }

  protected def withPrefixMappings(read: (() => Model)): Model = {
    //make sure reading does not override existing prefixes
    val prefixMapping = new PrefixMappingImpl()
    prefixMapping.setNsPrefixes(getOntModel)
    val readModel = read()
    readModel.setNsPrefixes(prefixMapping)
    readModel
  }

  /**Add the RDF statements from an XML document.
   * Uses content negotiation to request appropriate mime types.
   *
   * <p>See {@link Model} for a description of how to traverse a firewall.</p>
   * @return this model
   * @param url of the document containing the RDF statements.

   */
  def readWithPrefixes(url: String): Model = {
    val read = () => getOntModel.read(url)
    return withPrefixMappings(read)
  }

  /**Add statements from an RDF/XML serialization.
   * @param in the source of the RDF/XML

  @param base the base uri to be used when converting relative
         URI's to absolute URI's. (Resolving relative URIs and fragment IDs is done
         by prepending the base URI to the relative URI/fragment.) If there are no
         relative URIs in the source, this argument may safely be <code>null</code>.
         If the base is the empty string, then relative URIs <i>will be retained in
         the model</i>. This is typically unwise and will usually generate errors
         when writing the model back out.

   * @return the current model
   */
  def readWithPrefixes(in: InputStream, base: String): Model = {
    val read = () => getOntModel.read(in, base)
    withPrefixMappings(read)
  }

  /**Add RDF statements represented in language <code>lang</code> to the model.
   * <br />Predefined values for <code>lang</code> are "RDF/XML", "N-TRIPLE",
   * "TURTLE" (or "TTL") and "N3".
   * <code>null</code> represents the default language, "RDF/XML".
   * "RDF/XML-ABBREV" is a synonym for "RDF/XML".
   * <br />
   *
   * @return this model

  @param base the base uri to be used when converting relative
	     URI's to absolute URI's. (Resolving relative URIs and fragment IDs is done
	     by prepending the base URI to the relative URI/fragment.) If there are no
	     relative URIs in the source, this argument may safely be <code>null</code>.
	     If the base is the empty string, then relative URIs <i>will be retained in
	     the model</i>. This is typically unwise and will usually generate errors
	     when writing the model back out.

   * @param lang the langauge of the serialization <code>null</code>
   * selects the default
   * @param in the source of the input serialization
   */
  def readWithPrefixes(in: InputStream, base: String, lang: String): Model = {
    val read = () => getOntModel.read(in, base, lang)
    withPrefixMappings(read)
  }

  /**Using this method is often a mistake.
   * Add statements from an RDF/XML serialization.
   * It is generally better to use an InputStream if possible.
   * {@link Model#read(InputStream,String)}, otherwise there is a danger of a
   * mismatch between the character encoding of say the FileReader and the
   * character encoding of the data in the file.
   * @param reader the source of the RDF/XML

  @param base the base uri to be used when converting relative
         URI's to absolute URI's. (Resolving relative URIs and fragment IDs is done
         by prepending the base URI to the relative URI/fragment.) If there are no
         relative URIs in the source, this argument may safely be <code>null</code>.
         If the base is the empty string, then relative URIs <i>will be retained in
         the model</i>. This is typically unwise and will usually generate errors
         when writing the model back out.

   * * @return the current model
   */
  def readWithPrefixes(reader: Reader, base: String): Model = {
    val read = () => getOntModel.read(reader, base)
    withPrefixMappings(read)
  }

  /**
   * Add statements from a serializion in language <code>lang</code> to the
   * model.
   * <br />Predefined values for <code>lang</code> are "RDF/XML", "N-TRIPLE",
   * "TURTLE" (or "TTL") and "N3".
   * <code>null</code> represents the default language, "RDF/XML".
   * "RDF/XML-ABBREV" is a synonym for "RDF/XML".
   * <br />
   *
   * <p>See {@link Model} for a description of how to traverse a firewall.</p>
   * @param url a string representation of the url to read from
   * @param lang the language of the serialization

   * @return this model
   */
  def readWithPrefixes(url: String, lang: String): Model = {
    val read = () => getOntModel.read(url, lang)
    withPrefixMappings(read)
  }

  /**Using this method is often a mistake.
   * Add RDF statements represented in language <code>lang</code> to the model.
   * <br />Predefined values for <code>lang</code> are "RDF/XML", "N-TRIPLE",
   * "TURTLE" (or "TTL") and "N3".
   * <code>null</code> represents the default language, "RDF/XML".
   * "RDF/XML-ABBREV" is a synonym for "RDF/XML".
   * <br />
   * It is generally better to use an InputStream if possible.
   * {@link Model#read(InputStream,String)}, otherwise there is a danger of a
   * mismatch between the character encoding of say the FileReader and the
   * character encoding of the data in the file.
   * @return this model

  @param base the base uri to be used when converting relative
         URI's to absolute URI's. (Resolving relative URIs and fragment IDs is done
         by prepending the base URI to the relative URI/fragment.) If there are no
         relative URIs in the source, this argument may safely be <code>null</code>.
         If the base is the empty string, then relative URIs <i>will be retained in
         the model</i>. This is typically unwise and will usually generate errors
         when writing the model back out.

   * @param lang the langauge of the serialization <code>null</code>
   * selects the default
   * @param reader the source of the input serialization
   */
  def readWithPrefixes(reader: Reader, base: String, lang: String): Model = {
    val read = () => getOntModel.read(reader, base, lang)
    withPrefixMappings(read)
  }

  /**
  Read into this model the RDF at <code>url</code>, using
        <code>baseURI</code> as the base URI if it is non-null. The RDF is assumed
        to be RDF/XML unless <code>lang</code> is non-null, in which case it names
        the language to be used. Answer this model.
   */
  def readWithPrefixes(url: String, base: String, lang: String): Model = {
    val read = () => getOntModel.read(url, base, lang)
    withPrefixMappings(read)
  }

  def closeFeatures: Unit = {
    if (features != null) {
      for (feature <- features.values) {
        try {
          feature.close
        }
        catch {
          case e: Exception => {
            e.printStackTrace
          }
        }
      }
    }
  }
}