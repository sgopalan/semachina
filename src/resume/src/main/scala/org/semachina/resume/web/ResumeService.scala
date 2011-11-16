package org.semachina.resume.web

import com.google.code.geocoder.{GeocoderRequestBuilder, Geocoder}
import com.google.code.geocoder.model.{GeocodeResponse, GeocoderRequest}
import com.hp.hpl.jena.query.{QuerySolutionMap, QuerySolution, ResultSet}
import org.joda.time.LocalDate
import java.util.Date
import com.hp.hpl.jena.vocabulary.{RDFS, RDF}

import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.Individual

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 3/30/11
 * Time: 7:27 PM
 * To change this template use File | Settings | File Templates.
 */

class ResumeService(modelService : ModelService, companyService:CompanyService, edOrgService:EducationalOrgService) {

  val geocoder: Geocoder = new Geocoder

  val infoModel : OntModel = createModel()

  def createModel() : OntModel = {

    return modelService.baseModel
  }

  createResumeAll( infoModel )

  def getResume(id:String) = infoModel( id )

  def addOrUpdateEducationEntry(
      resume : Individual,
      entryURI : String,
      degreeType : String,
      studiedIn : String,
      eduDescription : String,
      eduStartDate : LocalDate,
      eduGradDate : LocalDate,
      eduMajor : String,
      eduMinor : String = null )(implicit transactionModel:OntModel) = {

    val cmuBS = entryURI.&.getOrElse { entryURI +& "cv:Education" }
    cmuBS
      .set( "cv:degreeType", degreeType.& )
      .set( "cv:studiedIn", studiedIn.& )
      .set( "cv:eduDescription", eduDescription )
      .set( "cv:eduStartDate", (eduStartDate ^^ "xsd:date") )
      .set( "cv:eduGradDate", (eduGradDate ^^ "xsd:date") )
      .set( "cv:eduMajor", eduMajor )

    if( eduMinor != null) { cmuBS set ("cv:eduMinor", eduMinor)}

        //link CMU BS to Resume
    resume add ( "cv:hasEducation", cmuBS )
    resume
  }

  def addOrUpdateWorkHistory(
      resume:Individual,
      workHistoryURI : String,
      careerLevel : String,
      employedIn : String,
      isCurrent : Boolean,
      jobType : String,
      startDate : LocalDate,
      endDate : LocalDate,
      jobDescription : String,
      jobTitle : String,
      numSubordinates: Integer = 0)(implicit transactionModel:OntModel) = {

    val workHistory = workHistoryURI.&.getOrElse{ workHistoryURI +& "cv:WorkHistory" }

    val company = employedIn.&
    workHistory
      .set( "cv:careerLevel", careerLevel.& )
      .set( "cv:employedIn",  employedIn.& )
      .set( "cv:isCurrent", { if(isCurrent) "base:True".& else "base:False".&} )
      .set( "cv:jobType", jobType.& )
      .set( "cv:startDate", (startDate ^^ "xsd:date" ) )
      .set( "cv:endDate", (endDate ^^ "xsd:date" ) )
      .set( "cv:jobDescription", jobDescription )
      .set( "cv:jobTitle", jobTitle )
      .set( "cv:numSubordinates", numSubordinates ^^ "xsd:integer" )

    resume add ("cv:hasWorkHistory", workHistory )
    resume
  }

   def addOrUpdatePerson(
      resume : Individual,
      personURI: String,
      birthPlace:String,
      citizenship : List[String],
      nationality : String,
      noOfChildren : Integer,
      isMale : Boolean,
      hasLicense : Boolean,
      maritalStatus : String,
      fullName : String,
      nickName : String,
      currentAddress : String,
      phone : String,
      emailAddress : String)(implicit transactionModel:OntModel) = {

    val person = personURI.&.getOrElse { personURI +& ("cv:Person", "vcard:VCard") }

    if(birthPlace != null) { person set ( "cv:birthPlace", birthPlace ) }
    if(citizenship != null) { person set ( "cv:hasCitizenship", citizenship ) }
    if(nationality != null) { person set ( "cv:hasNationality", nationality ^^ "xsd:string" ) }
    if(noOfChildren != null) { person set ( "cv:noOfChildren", noOfChildren^^ "xsd:integer" ) }

    person set ( "cv:gender", { if(isMale) "base:Male".&  else "base:Female".&  } )
    person set ( "cv:hasDriversLicense", { if(hasLicense) "base:True".& else "base:False".& } )

    if(maritalStatus != null) { person set ( "cv:maritalStatus", maritalStatus.& ) }
    if(fullName != null) { person set ( "vcard:fn", fullName ) }
    if(nickName != null) { person set ( "vcard:nickname", nickName ) }

    createVCard( person, currentAddress, phone, emailAddress )

    //link CV to Person
    resume set ("cv:aboutPerson", person )
    resume
  }

  def createVCard(indiv: Individual, address:String, mobile:String, email:String) : Unit = {

    implicit var model : OntModel = indiv.getOntModel()

    var requestBuilder = new GeocoderRequestBuilder
    var geocoderRequest: GeocoderRequest = requestBuilder
        .setAddress(address)
        .setLanguage("en")
        .getGeocoderRequest

    var geocoderResponse: GeocodeResponse = geocoder.geocode(geocoderRequest)

    if( geocoderResponse.getResults.isEmpty ) {
      return
    }
    var result = geocoderResponse.getResults.get(0)


    indiv add ("vcard:tel", ("me:mobileNumber" +& ("vcard:Home", "vcard:Cell", "vcard:Pref" ) ) )
    "me:mobileNumber".& set ("rdf:value", mobile )

    indiv add ("vcard:email", ("me:gmail" +& "vcard:Email") )
    "me:gmail".& set ("rdf:value", email )

    indiv add ("vcard:adr", ("me:homeAddress" +& "vcard:Home") )
    "me:homeAddress".& set ("rdfs:label", result.getFormattedAddress)

    indiv add ("vcard:geo", ("me:homeLocation" +& "vcard:Location")  )
    "me:homeLocation".&
      .add ( "vcard:latitude", result.getGeometry.getLocation.getLat.toEngineeringString.toFloat )
      .add ( "vcard:longitude", result.getGeometry.getLocation.getLng.toEngineeringString.toFloat )

    var streetNumber :String = null
    var route : String = null

    for( comp <- result.getAddressComponents ) {
      for (compTypeCode <- comp.getTypes ) {

        compTypeCode match {
          case "street_number" => streetNumber = comp.getLongName
          case "route" => route = comp.getLongName
          case "locality" => "me:homeAddress".& set ("vcard:locality", comp.getLongName )
          case "administrative_area_level_1" => "me:homeAddress".& set ("vcard:region", comp.getLongName )
          case "postal_code" => "me:homeAddress".& set ("vcard:postal-code", comp.getLongName )
          case "country" => "me:homeAddress".& set ("vcard:country-name", comp.getLongName )
          case _ => {}
        }
      }
    }
    if( route != null && streetNumber != null ) {
      "me:homeAddress".& set ("vcard:street-address", (streetNumber + " " + route) )
    }
  }

    def addOrUpdateResume(
                   resumeURI: String,
                   title : String,
                   description : String,
                   copyright : String,
                   isActive : Boolean,
                   isConfidential:Boolean,
                   lastUpdate:Date)(implicit transactionModel:OntModel): Individual = {

    val resume = resumeURI.&.getOrElse { resumeURI +& "cv:CV"}

    //create CV
    resume
      .set ( "cv:cvCopyright", copyright )
      .set ( "cv:cvDescription", description )
      .set ( "cv:cvIsActive", isActive )
      .set (  "cv:cvIsConfidential", isConfidential )
      .set (  "cv:cvTitle", title )
      .set (  "cv:lastUpdate", lastUpdate )

    return resume
  }

  def createResumeAll(implicit transactionModel:OntModel): Individual = {
    //create CV
    val myResume = addOrUpdateResume(
      "me:sgopalan_cv",
      "Sri Gopalan - Full Resume",
      "I want a great job",
      "Sriram Gopalan",
      false,
      true,
      new Date() )

    val me =  addOrUpdatePerson(
        myResume,
        "me:sgopalan",
        "Edmonton, Alberta Canada",
        List("Canada", "US"),
        "Canada",
        0,
        true,
        true,
        "base:Single",
        "Sriram Gopalan",
        "Sri",
        "9 haypress road, cranbury nj",
        "703-395-4057",
        "sri.gopalan@gmail.com")

    //Create CMU BS Degree
    val cmuBS = addOrUpdateEducationEntry(
        myResume,
        "me:CMU_BS",
        "base:EduBachelor",
        "me:CMU",
        "Electrical and Computer Engineering (focus in Engineering Design / VLSI )",
        new LocalDate( 1996, 8, 17 ),
        new LocalDate( 1999, 12, 17 ),
        "Electrical and Computer Engineering",
        "Business Administration" )

    //Create CMU MS Degree
    val cmuMS = addOrUpdateEducationEntry(
        myResume,
        "me:CMU_MS",
        "base:EduMaster",
        "me:CMU",
        "Electrical and Computer Engineering",
        new LocalDate( 2000, 1, 13 ),
        new LocalDate( 2000, 5, 21 ),
        "Electrical and Computer Engineering" )

    //Create UVA Certificate Degree
    val uvaProfessional = addOrUpdateEducationEntry(
        myResume,
        "me:UVA_Professional",
        "base:EduProfessional",
        "me:UVA",
        "Graduate Certificate in Technology Leadership",
        new LocalDate( 2005, 8, 13 ),
        new LocalDate( 2007, 5, 21 ),
        "Graduate Certificate in Technology Leadership" )

    val merrillLynchEntry = addOrUpdateWorkHistory(
        myResume,
        "me:ML1997",
        "base:Student",
        "me:MerrillLynch",
        false,
        "base:Intern",
        new LocalDate( 1997, 6, 2 ),
        new LocalDate( 1997, 8, 8 ),
        "Sandbox implementation of third-party tools on Windows NT 4.0; Developed Departmental Intranet Site;",
        "Summer Intern",
        0
    )

    val digitalLightwaveEntry = addOrUpdateWorkHistory(
        myResume,
        "me:DigitalLightwave1998",
        "base:Student",
        "me:DigitalLightwave",
        false,
        "base:Intern",
        new LocalDate( 1998, 5, 25 ),
        new LocalDate( 1998, 8, 7 ),
        "Prototype FPGA implemention of SONET Protocols",
        "Summer Intern",
        0
    )

    val mitreEntry = addOrUpdateWorkHistory(
        myResume,
        "me:Mitre1999",
        "base:Student",
        "me:MITRE",
        false,
        "base:Intern",
        new LocalDate( 1999, 5, 31 ),
        new LocalDate( 1999, 8, 4 ),
        "Co-authored research paper on benefits of software-based implementation of phase-locked loop in software radios.  Performed analysis and verification of test data from an advanced prototype Global Positioning System (GPS).  Reduced run-time of analysis scripts by 66%.  Offered full-time position as Hardware Engineer.",
        "Summer Intern",
        0 )

    val sapientEntry = addOrUpdateWorkHistory(
        myResume,
        "me:Sapient2000",
        "base:EntryLvl",
        "me:Sapient",
        false,
        "base:Employee",
        new LocalDate( 2000, 8, 7 ),
        new LocalDate( 2001, 7, 2 ),
        "Developed, supported, maintained and evolved business and mission critical applications in roles including technology strategy, project management and application development.",
        "Associate",
        0 )

    val pecEntry = addOrUpdateWorkHistory(
        myResume,
        "me:PEC2002",
        "base:EntryLvl",
        "me:PEC",
        false,
        "base:Employee",
        new LocalDate( 2002, 6, 1 ),
        new LocalDate( 2004, 9, 3 ),
        "Develop systems utilizing Enterprise Java, AJAX, web services, and Oracle technologies, executing all aspects of the system development life cycle including requirements analysis, system design, application development and testing.",
        "J2EE/Java Senior Web Application Developer",
        2 )


    val bahEntry = addOrUpdateWorkHistory(
        myResume,
        "me:BoozAllenHamilton2004",
        "base:MidCareer",
        "me:BoozAllenHamilton",
        false,
        "base:Employee",
        new LocalDate( 2004, 9, 5 ),
        new LocalDate( 2008, 12, 31 ),
        "Provides architecture and design services to clients in the government market. Designs and blueprints SOA-based architectures and solutions. Manages and facilitates the preparation and communication of the programs SOA Strategy, Roadmap and Charter.  Manages and facilitates training and stakeholder understanding of the SOA Reference Architecture. Supports the facilitation of Communities of Interest (COIs). Recruits, leads, and manages small technical and architectural project teams.",
        "Enterprise Service Oriented Architecture (SOA) Architect",
        10 )

    val twoSigmaEntry = addOrUpdateWorkHistory(
        myResume,
        "me:TwoSigma2010",
        "base:MidCareer",
        "me:TwoSigma",
        true,
        "base:Employee",
        new LocalDate( 2009, 1, 5 ),
        new LocalDate,
        "Project Management Program, Compliance Automation Program, Corporate Technology Program, New Business Initiatives",
        "Technology Strategy Analyst / Project Manager / Knowledge Manager",
        10 )

    return myResume
  }


}