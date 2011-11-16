package org.semachina.resume

import org.junit.Test
import org.junit.Assert._
import org.semachina.jena.dsl.SemachinaDSL._
import java.util.Date
import com.hp.hpl.jena.vocabulary.{RDFS, RDF}
import org.mindswap.pellet.jena.PelletReasonerFactory
import org.semachina.resume.web.controller.IndexController
import com.hp.hpl.jena.ontology.Individual
import scala.collection.JavaConversions._
import org.semachina.jena.config.SemachinaConfiguration
import com.hp.hpl.jena.rdf.model._
import org.springframework.context.annotation.Bean
import web.config.WebConfig
import web.{EducationalOrgService, CompanyService, ModelService, ResumeService}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 2/5/11
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */

class TestResume {

  @Test
  def testController() : Unit = {

    val config = new WebConfig()
    val resumeService = config.newResumeService//config.resumeService

    println( resumeService.getResume( "me:sgopalan_cv" ) )

    val controller = new IndexController( resumeService )

    val mv = controller.resume
    implicit val resumeModel = mv.getModel.get("resumeModel")
    val resume = mv.getModel.get("resume").asInstanceOf[Individual]

    val me = resume.get("cv:aboutPerson")

//    val valueProp = resumeModel.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value").as(classOf[OntProperty])

    var schools = resume.list("cv:hasEducation")//.sort{ _.value[Date]("cv:eduStartDate").get compareTo _.value[Date]("cv:eduStartDate").get }
    //var schools = resume.list("cv:hasEducation").sort((e1, e2) => (e1.value[Date]("cv:eduStartDate").get.compareTo(e2.value[Date]("cv:eduStartDate").get)))

    for( edu <- schools ) {
      println( "Degree type: " + edu.get("cv:degreeType").listLabels(null).toList() )
    }

    var start = System.currentTimeMillis
    println( "full name: "+ resume.get("cv:aboutPerson").value[String]("vcard:fn") )
    println( "full name time: " + (System.currentTimeMillis - start)/1000f )

    start = System.currentTimeMillis
    println( "phone number: " + resume.get("cv:aboutPerson").get("vcard:tel").value[String]("rdf:value") )
    println( "phone number time: " + (System.currentTimeMillis - start)/1000f )

    start = System.currentTimeMillis
    println( "email: " + me.get("vcard:email").value[String]("rdf:value") )
    println( "email time: " + (System.currentTimeMillis - start)/1000f )

    start = System.currentTimeMillis
    println( "full name: "+ resume.value[String]("cv:aboutPerson/vcard:fn"))
    println( "full name path time: " + (System.currentTimeMillis - start)/1000f )

    start = System.currentTimeMillis
    println( "full name: "+ resume.value[String]("cv:aboutPerson/vcard:fn") )
    println( "full name path time: " + (System.currentTimeMillis - start)/1000f )

    start = System.currentTimeMillis
    println( "phone number: " + resume.value[String]("cv:aboutPerson/vcard:tel/rdf:value"))
    println( "phone number path time: " + (System.currentTimeMillis - start)/1000f )

    start = System.currentTimeMillis
    println( "email: " + me.value[String]("vcard:email/rdf:value"))
    println( "email path time: " + (System.currentTimeMillis - start)/1000f )


    println( me.value[String]("vcard:email/rdf:value"))
    println( me.value[String]("vcard:email/vcard:fn").getOrElse("N/A"))

    println( me.value[String]("vcard:adr/rdfs:label") )
    println( me.value[String]("vcard:tel/rdf:value") )
    println( me.value[String]("vcard:email/rdf:value") )

    //println( "bachelor: " + &("base:EduBachelor").getLabel(null))

    me.getOntModel.write( System.out, "N3", null )



    //ModelFactory.createDefaultModel()

    println("printing: " + asString (me) )
  }


  def asString( obj : RDFNode ) : String = {

    return obj.visitWith( new RDFVisitor() {
      def visitLiteral(l: Literal) = l.toString().replaceAll( "//^//^.+", "" )

      def visitURI(r: Resource, uri: String) = r.getLocalName()

      def visitBlank(r: Resource, id: AnonId) = r.getLocalName()
    }).asInstanceOf[String]
  }
}