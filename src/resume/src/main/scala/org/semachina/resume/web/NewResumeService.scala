package org.semachina.resume.web

import com.google.code.geocoder.{GeocoderRequestBuilder, Geocoder}
import com.google.code.geocoder.model.{GeocodeResponse, GeocoderRequest}
import org.joda.time.LocalDate
import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._

import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.Individual
import org.semachina.resume.beans.{Resume, AboutPerson, EducationEntry, WorkHistory}
import java.util.{Date}
import org.semachina.jenabean.JenabeanSupport._

import java.net.URI



/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 3/30/11
 * Time: 7:27 PM
 * To change this template use File | Settings | File Templates.
 */

class NewResumeService(modelService : ModelService, companyService:CompanyService, edOrgService:EducationalOrgService) {

  val geocoder: Geocoder = new Geocoder

  val infoModel : OntModel = createModel()

  def createModel() : OntModel = {
    return modelService.baseModel
  }

  createResumeAll( infoModel )

  def getResume(id:String) = infoModel( id )

  def baseBoolean(bool:Boolean)(implicit transactionModel:OntModel) = { if(bool) "base:True".& else "base:False".&}

  def createResumeAll(implicit transactionModel:OntModel) = {

    //create CV
    //val myResume = addOrUpdateResume(
    val myResume = new Resume(
      URI.create("me:sgopalan_cv".!),
      "Sri Gopalan - Full Resume",
      "I want a great job",
      "Sriram Gopalan",
      false,
      true,
      new Date() )

    myResume.aboutPerson = AboutPerson(
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

      var educationEntries = new java.util.ArrayList[EducationEntry]()


    //Create CMU BS Degree
    myResume.addEducationEntry(
        new EducationEntry(
          URI.create("me:CMU_BS".!),
          "base:EduBachelor".&,
          "me:CMU".&,
          "Electrical and Computer Engineering (focus in Engineering Design / VLSI )",
          new LocalDate( 1996, 8, 17 ),
          new LocalDate( 1999, 12, 17 ),
          "Electrical and Computer Engineering",
          "Business Administration" ) )

    //Create CMU MS Degree
    myResume.addEducationEntry(
        new EducationEntry(
          URI.create("me:CMU_MS".!),
          "base:EduMaster".&,
          "me:CMU".&,
          "Electrical and Computer Engineering",
          new LocalDate( 2000, 1, 13 ),
          new LocalDate( 2000, 5, 21 ),
          "Electrical and Computer Engineering" ) )

    //Create UVA Certificate Degree
    myResume.addEducationEntry(
        new EducationEntry(
          URI.create("me:UVA_Professional".!),
          "base:EduProfessional".&,
          "me:UVA".&,
          "Graduate Certificate in Technology Leadership",
          new LocalDate( 2005, 8, 13 ),
          new LocalDate( 2007, 5, 21 ),
          "Graduate Certificate in Technology Leadership" ) )

      var workHistory = new java.util.ArrayList[WorkHistory]()
      myResume.workHistory = workHistory

    myResume.addWorkHistory(
        new WorkHistory(
          URI.create("me:ML1997".!),
          "base:Student".&,
          "me:MerrillLynch".&,
          baseBoolean(false),
          "base:Intern".&,
          new LocalDate( 1997, 6, 2 ),
          new LocalDate( 1997, 8, 8 ),
          "Sandbox implementation of third-party tools on Windows NT 4.0; Developed Departmental Intranet Site;",
          "Summer Intern",
          0 ) )

    myResume.addWorkHistory(
        new WorkHistory(
          URI.create("me:DigitalLightwave1998".!),
          "base:Student".&,
          "me:DigitalLightwave".&,
          baseBoolean(false),
          "base:Intern".&,
          new LocalDate( 1998, 5, 25 ),
          new LocalDate( 1998, 8, 7 ),
          "Prototype FPGA implemention of SONET Protocols",
          "Summer Intern",
          0 ) )

    myResume.addWorkHistory(
        new WorkHistory(
          URI.create("me:Mitre1999".!),
          "base:Student".&,
          "me:MITRE".&,
          baseBoolean(false),
          "base:Intern".&,
          new LocalDate( 1999, 5, 31 ),
          new LocalDate( 1999, 8, 4 ),
          "Co-authored research paper on benefits of software-based implementation of phase-locked loop in software radios.  Performed analysis and verification of test data from an advanced prototype Global Positioning System (GPS).  Reduced run-time of analysis scripts by 66%.  Offered full-time position as Hardware Engineer.",
          "Summer Intern",
          0 ) )

    myResume.addWorkHistory(
        new WorkHistory(
          URI.create("me:Sapient2000".!),
          "base:EntryLvl".&,
          "me:Sapient".&,
          baseBoolean(false),
          "base:Employee".&,
          new LocalDate( 2000, 8, 7 ),
          new LocalDate( 2001, 7, 2 ),
          "Developed, supported, maintained and evolved business and mission critical applications in roles including technology strategy, project management and application development.",
          "Associate",
          0 ) )

    myResume.addWorkHistory(
        new WorkHistory(
          URI.create("me:PEC2002".!),
          "base:EntryLvl".&,
          "me:PEC".&,
          baseBoolean(false),
          "base:Employee".&,
          new LocalDate( 2002, 6, 1 ),
          new LocalDate( 2004, 9, 3 ),
          "Develop systems utilizing Enterprise Java, AJAX, web services, and Oracle technologies, executing all aspects of the system development life cycle including requirements analysis, system design, application development and testing.",
          "J2EE/Java Senior Web Application Developer",
          2 ) )


    myResume.addWorkHistory(
        new WorkHistory(
          URI.create("me:BoozAllenHamilton2004".!),
          "base:MidCareer".&,
          "me:BoozAllenHamilton".&,
          baseBoolean(false),
          "base:Employee".&,
          new LocalDate( 2004, 9, 5 ),
          new LocalDate( 2008, 12, 31 ),
          "Provides architecture and design services to clients in the government market. Designs and blueprints SOA-based architectures and solutions. Manages and facilitates the preparation and communication of the programs SOA Strategy, Roadmap and Charter.  Manages and facilitates training and stakeholder understanding of the SOA Reference Architecture. Supports the facilitation of Communities of Interest (COIs). Recruits, leads, and manages small technical and architectural project teams.",
          "Enterprise Service Oriented Architecture (SOA) Architect",
          10 ) )

    myResume.addWorkHistory(
        new WorkHistory(
          URI.create("me:TwoSigma2010".!),
          "base:MidCareer".&,
          "me:TwoSigma".&,
          baseBoolean(true),
          "base:Employee".&,
          new LocalDate( 2009, 1, 5 ),
          new LocalDate,
          "Project Management Program, Compliance Automation Program, Corporate Technology Program, New Business Initiatives",
          "Technology Strategy Analyst / Project Manager / Knowledge Manager",
          10 ) )

    transactionModel.addOrUpdate( myResume ).as(classOf[Individual])
  }




}
