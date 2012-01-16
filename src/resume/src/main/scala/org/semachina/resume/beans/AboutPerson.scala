package org.semachina.resume.beans

import com.hp.hpl.jena.rdf.model.Resource
import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._
import com.google.code.geocoder.model.{GeocodeResponse, GeocoderRequest}
import com.google.code.geocoder.{Geocoder, GeocoderRequestBuilder}
import java.net.URI
import org.semachina.jena.binder.annotations.SemachinaBinding._
import com.hp.hpl.jena.ontology.{OntClass, OntModel}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 10/3/11
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */

object AboutPerson {
  val geocoder: Geocoder = new Geocoder

  def apply(
             personURI: String,
             birthPlace: String,
             citizenship: List[String],
             nationality: String,
             noOfChildren: Integer,
             isMale: Boolean,
             hasLicense: Boolean,
             maritalStatus: String,
             fullName: String,
             nickName: String,
             homeAddress: String,
             phone: String,
             emailAddress: String)(implicit ontModel: OntModel) = {


    val id = URI.create(personURI.!)
    val address = currentAddress(id, homeAddress)

    val mobile = (("me:mobileNumber-" + System.currentTimeMillis()) +&("vcard:Home", "vcard:Cell", "vcard:Pref"))
    mobile set("rdf:value", phone)

    val email = (("me:gmail-" + System.currentTimeMillis()) +& "vcard:Email")
    email set("rdf:value", emailAddress ^^ "xsd:string")



    val aboutPerson = new AboutPerson(
      personURI = id,
      birthPlace = birthPlace,
      citizenship = citizenship,
      nationality = nationality,
      noOfChildren = noOfChildren,
      gender = (if (isMale) "base:Male".& else "base:Female".&),
      hasLicense = (if (hasLicense) "base:True".& else "base:False".&),
      maritalStatus = maritalStatus.&,
      fullName = fullName,
      nickName = nickName,
      tel = mobile,
      emailAddress = email,
      homeAddress = address._2,
      location = address._2)

    aboutPerson.types.add("cv:Person".$)
    aboutPerson.types.add("vcard:VCard".$)
    aboutPerson
  }

  def currentAddress(personURI: URI, address: String): Pair[String, Location] = {
    var requestBuilder = new GeocoderRequestBuilder
    var geocoderRequest: GeocoderRequest = requestBuilder
      .setAddress(address)
      .setLanguage("en")
      .getGeocoderRequest

    var geocoderResponse: GeocodeResponse = AboutPerson.geocoder.geocode(geocoderRequest)

    if (geocoderResponse.getResults.isEmpty) {
      return (address -> null)
    }
    var result = geocoderResponse.getResults.get(0)


    val homeAddress = result.getFormattedAddress

    val location = new Location()
    location.locationURI = personURI.resolve("/homeAddress")
    location.label = result.getFormattedAddress
    location.lat = result.getGeometry.getLocation.getLat.toEngineeringString.toFloat
    location.long = result.getGeometry.getLocation.getLng.toEngineeringString.toFloat

    var streetNumber: String = null
    var route: String = null

    for (comp <- result.getAddressComponents) {
      for (compTypeCode <- comp.getTypes) {

        compTypeCode match {
          case "street_number" => streetNumber = comp.getLongName
          case "route" => route = comp.getLongName
          case "locality" => location.locality = comp.getLongName
          case "administrative_area_level_1" => location.region = comp.getLongName
          case "postal_code" => location.postalCode = comp.getLongName
          case "country" => location.countryName = comp.getLongName
          case _ => {}
        }
      }
    }
    if (route != null && streetNumber != null) {
      location.streetAddress = (streetNumber + " " + route)
    }

    return (homeAddress -> location)
  }
}

@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#"),
  new Prefix(prefix = "vcard", uri = "http://www.w3.org/2006/vcard/ns#"),
  new Prefix(prefix = "rdf", uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
))
@RdfType(Array("cv:Person"))
class AboutPerson(
                   @Id var personURI: URI = null,
                   @RdfProperty("rdf:type") var types: java.util.Collection[OntClass] = new java.util.ArrayList[OntClass],
                   @RdfProperty("cv:birthPlace") var birthPlace: String = null,
                   @RdfProperty("cv:hasCitizenship") var citizenship: java.util.Collection[String] = null,
                   @RdfProperty("cv:hasNationality") var nationality: String = null,
                   @RdfProperty("cv:noOfChildren") var noOfChildren: Integer = 0,
                   @RdfProperty("cv:gender") var gender: Resource = null,
                   @RdfProperty("cv:hasDriversLicense") var hasLicense: Resource = null,
                   @RdfProperty("cv:maritalStatus") var maritalStatus: Resource = null,
                   @RdfProperty("vcard:fn") var fullName: String = null,
                   @RdfProperty("vcard:nickname") var nickName: String = null,
                   @RdfProperty("vcard:tel") var tel: Resource = null,
                   @RdfProperty("vcard:email") var emailAddress: Resource = null,
                   @RdfProperty("vcard:adr") var homeAddress: Location = null,
                   @RdfProperty("vcard:geo") var location: Location = null) extends IdField {

  override def id = personURI
}