package org.semachina.jena.binder

import annotations.SemachinaBinding._
import org.semachina.jena.config.SemachinaConfiguration
import org.semachina.jena.dsl.SemachinaDSL._
import java.net.URI
import org.semachina.jena.ontology.naming.IdStrategy
import org.semachina.jena.config.SemachinaBuilder
import org.specs2.specification.Scope
import com.hp.hpl.jena.rdf.model.{Literal, Resource}
import com.hp.hpl.jena.ontology.Individual

@Prefixes(Array(
  new Prefix(prefix = "vcard", uri = "http://www.w3.org/2006/vcard/ns#"),
  new Prefix(prefix = "rdf", uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
  new Prefix(prefix = "me", uri = "http://re.su.me/sgopalan.owl#")
))
@RdfType(Array("vcard:VCard"))
case class VCard(
                  @Id val personURI: String = null,
                  @RdfProperty("vcard:fn") val fullName: String = null,
                  @RdfProperty("vcard:nickname") val nickName: Option[String] = None,
                  @RdfProperty("vcard:homeTel") val home: Resource = null,
                  @RdfProperty("vcard:mobileTel") val cell: Option[Resource] = None,
                  @RdfProperty("vcard:email") val emailAddress: List[Resource] = null,
                  @RdfProperty("vcard:adr") val homeAddress: String = null,
                  @RdfProperty("vcard:geo") val location: Location = null)


@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#"),
  new Prefix(prefix = "vcard", uri = "http://www.w3.org/2006/vcard/ns#"),
  new Prefix(prefix = "rdfs", uri = "http://www.w3.org/2000/01/rdf-schema#"),
  new Prefix(prefix = "me", uri = "http://re.su.me/sgopalan.owl#")
))
@RdfType(Array("vcard:Location"))
case class Location(
                     @Id val locationURI: URI = null,
                     val idStrategy: IdStrategy = null,
                     @RdfProperty("rdfs:label") val label: Literal = null,
                     @RdfProperty("vcard:street-address") val streetAddress: String = null,
                     @RdfProperty("vcard:postal-code") val postalCode: String = null,
                     @RdfProperty("vcard:country-name") val countryName: String = null,
                     @RdfProperty("vcard:latitude") val lat: Option[Float] = None,
                     @RdfProperty("vcard:longitude") val long: Option[Float] = None,
                     @RdfProperty("vcard:locality") val locality: String = null,
                     @RdfProperty("vcard:region") val region: String = null)

class BinderTest extends org.specs2.mutable.SpecificationWithJUnit {

  "Semachina IndividualBinder infrastructure" should {
    "MUST be able to bind a bean with a String member" in new model {
      implicit val model = newOntModel

      val vcard = new VCard(
        personURI = "http://re.su.me/sgopalan.owl#sgopalan",
        nickName = Option("Sri"))

      val binder = IndividualBinder.default(model)
      val individual = binder.toIndividual(vcard)
      individual.value[String]("vcard:nickname") must beEqualTo("Sri")
    }
    "MUST be able to bind a bean with a Option(String) member" in new model {
      implicit val model = newOntModel

      val vcard = new VCard(
        personURI = "http://re.su.me/sgopalan.owl#sgopalan",
        fullName = "Sri Gopalan")

      val binder = IndividualBinder.default(model)
      val individual = binder.toIndividual(vcard)

      individual.value[String]("vcard:fn") must beEqualTo("Sri Gopalan")
    }
    "MUST be able to bind a bean with a Resource member" in new model {
      implicit val model = newOntModel

      val homeResource = (("me:homeNumber-1") +& ("vcard:Home"))

      val vcard = new VCard(
        personURI = "http://re.su.me/sgopalan.owl#sgopalan",
        home = homeResource)

      val binder = IndividualBinder.default(model)
      val individual = binder.toIndividual(vcard)

      val telephoneList = individual.list("vcard:homeTel").toList
      telephoneList.contains(homeResource) must beTrue
    }
    "MUST be able to bind a bean with an Option(Resource) member" in new model {
      implicit val model = newOntModel

      val cellResource = Option(("me:mobileNumber-1") +&("vcard:Cell", "vcard:Pref"))

      val vcard = new VCard(
        personURI = "http://re.su.me/sgopalan.owl#sgopalan",
        cell = cellResource)

      val binder = IndividualBinder.default(model)
      val individual = binder.toIndividual(vcard)

      val telephoneList = individual.list("vcard:mobileTel").toList
      telephoneList.contains(cellResource.get) must beTrue
    }
    "build rdf type list (with prefix resolution) " in new model {

      implicit val model = newOntModel

      val locationIdStrategy = IdStrategy.newHierarchical("locations", {
        i: Individual => System.nanoTime.toString
      }, "^vcard:geo")

      val vcard = new VCard(
        personURI = "http://re.su.me/sgopalan.owl#sgopalan",
        fullName = "Sriram Gopalan",
        nickName = "Sri",
        home = (("me:homeNumber-1") +& ("vcard:Home")),
        cell = Option(("me:mobileNumber-1") +&("vcard:Cell", "vcard:Pref")),
        emailAddress = List(
          (("me:gmail-1") +& "vcard:Email"),
          (("me:gmail-2") +& "vcard:Email")
        ),
        homeAddress = "9 haypress road, cranbury nj",
        location = new Location(label = "Location" ^^@ ("en"))
      )
    }
    //
    //      //should evaluate binder and Id Strategies
    //      val binder  = IndividualBinder.default( model )
    //      val individual = binder.toIndividual( vcard )
    //
    //      model.write(System.out, "N3")
    //
    //      //should marshall back
    //      val bean = binder.toBean[VCard](individual)
    //
    //
    //
    //      individual must not beNull
    //    }
  }


  trait model extends Scope {
    val altEntry = classOf[BinderTest].getResource("/vcard.owl").toString
    SemachinaConfiguration
      .addAltEntry("http://www.w3.org/2006/vcard/ns" -> altEntry)
      .setNsPrefix("vcard" -> "http://www.w3.org/2006/vcard/ns#")

    def newOntModel = SemachinaBuilder()
      .addSubModelURI("http://www.w3.org/2006/vcard/ns")
      .build
  }

  val N3 = """
    @prefix dc:      <http://purl.org/dc/elements/1.1/> .
    @prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
    @prefix owl:     <http://www.w3.org/2002/07/owl#> .
    @prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
    @prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
    @prefix vcard:   <http://www.w3.org/2006/vcard/ns#> .

    <me:mobileNumber-1>
          a       vcard:Cell , vcard:Home , vcard:Pref .

    <me:gmail-1>
          a       vcard:Email .

    <me:gmail-2>
          a       vcard:Email .

    <http://re.su.me/sgopalan.owl#sgopalan>
          a       vcard:VCard ;
          vcard:adr "9 haypress road, cranbury nj"^^xsd:string ;
          vcard:email <me:gmail-1> , <me:gmail-2> ;
          vcard:fn "Sriram Gopalan"^^xsd:string ;
          vcard:geo
                  [ a       vcard:Location ;
                    rdfs:label "Location"@en
                  ] ;
          vcard:nickname "Sri"^^xsd:string ;
          vcard:tel <me:mobileNumber-1> ."""
}