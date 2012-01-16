package org.semachina.resume.beans


import java.net.URI
import org.semachina.jena.binder.annotations.SemachinaBinding._


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 10/4/11
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */

@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#"),
  new Prefix(prefix = "vcard", uri = "http://www.w3.org/2006/vcard/ns#"),
  new Prefix(prefix = "rdfs", uri = "http://www.w3.org/2000/01/rdf-schema#")
))
@RdfType(Array("vcard:Location"))
class Location extends IdField {
  @Id var locationURI: URI = null
  @RdfProperty("rdfs:label") var label: String = null
  @RdfProperty("vcard:street-address") var streetAddress: String = null
  @RdfProperty("vcard:postal-code") var postalCode: String = null
  @RdfProperty("vcard:country-name") var countryName: String = null
  @RdfProperty("vcard:latitude") var lat: Float = 0f
  @RdfProperty("vcard:longitude") var long: Float = 0f
  @RdfProperty("vcard:locality") var locality: String = null
  @RdfProperty("vcard:region") var region: String = null;

  override def id = locationURI
}