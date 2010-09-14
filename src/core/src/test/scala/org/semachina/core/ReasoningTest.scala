package org.semachina.core

import org.junit._
import org.semachina.jena.JenaExtension._
import org.mindswap.pellet.jena.PelletReasonerFactory
import com.hp.hpl.jena.rdf.model.{ModelFactory, Resource}
import com.hp.hpl.jena.ontology._
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.semachina.jena.core.OWLFactory
import org.mindswap.pellet.PelletOptions
import org.semachina.config.AppConfig

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Aug 30, 2010
 * Time: 8:35:43 PM
 * To change this template use File | Settings | File Templates.
 */

object ReasoningTest {
  @BeforeClass
  def setup: Unit = {
    val ctx = new AnnotationConfigApplicationContext(classOf[AppConfig]);
  }

  @AfterClass
  def teardown: Unit = {

  }
}

class ReasoningTest {
  def createModel: OntModel = {
    val ontModel = OWLFactory.createOntologyModel
    ontModel.read("http://purl.org/dc/elements/1.1/")
    val title = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/title")
    val description = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/description")


    val r1: Resource = ontModel.createResource("http://example.org/book#1")
    //    val r2: Resource = ontModel.createResource("http://example.org/book#2")

    r1.addProperty(title, "SPARQL - the book")
            .addProperty(description, "A book about SPARQL")

    //    r2.addProperty(DC.title, "Advanced techniques for SPARQL")

    return ontModel;
  }

  @Test
  def deductionModelTest = {

    // ontology that will be used
    val ont: String = "http://www.mindswap.org/2004/owl/mindswappers#"

    val defaultModel = ModelFactory.createDefaultModel

    // load the ontology with its imports and no reasoning


    implicit val model: OntModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC)
    PelletOptions.RETURN_DEDUCTIONS_GRAPH = true
    defaultModel.read(ont)

    model add defaultModel

    // load the model to the reasoner
    model.rebind


    val diffModel = model difference defaultModel

    val deductionModel = model.getDeductionsModel

    println("difference between diffModel and deductionModel")
    (diffModel difference deductionModel).write(System.out, "N3");
    println("done\n\n\n")
    // create property and resources to query the reasoner
    val Person: OntClass = "http://xmlns.com/foaf/0.1/Person"
    val workHomepage: OntProperty = "http://xmlns.com/foaf/0.1/workInfoHomepage"
    val foafName: OntProperty = "http://xmlns.com/foaf/0.1/name"

    // get all instances of Person class



    Person.listIndividuals {
      ind =>
        var name: String = ind.getLiteral(foafName).getString
        var rdftype: Resource = ind.getRDFType
        var homepage: Resource = ind.getPropertyValue(workHomepage).asInstanceOf[Resource]
        System.out.println("Name: " + name)
        System.out.println("Type: " + rdftype.getLocalName)
        if (homepage == null) System.out.println("Homepage: Unknown")
        else System.out.println("Homepage: " + homepage)
        System.out.println
    }
    System.out.println("done")
  }
}