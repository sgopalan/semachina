package org.semachina.jena.features.larq

import org.semachina.jena.features.larq3.Larq3Feature
import org.openjena.atlas.io.IndentedWriter
import org.junit.Test
import org.semachina.jena.ontology.impl.SemachinaOntModelImpl
import com.hp.hpl.jena.ontology.{ProfileRegistry, OntModelSpec}
import com.hp.hpl.jena.rdf.model.Resource

import org.semachina.jena.dsl.SemachinaDSL._
import scala.collection.JavaConversions._
import org.semachina.jena.query.SemachinaQuerySolution


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 10/12/11
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */

class LarqFeatureTest {
  //extends SpecificationWithJUnit("Larq Feature Specification") {
  val NL = System.getProperty("line.separator")

  def createModel: SemachinaOntModelImpl = {
    implicit val ontModel = new
        SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG))
    ontModel.read("http://purl.org/dc/elements/1.1/")
    val title = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/title")
    val description = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/description")


    ontModel.transact {
      writeModel =>

        val r1: Resource = writeModel.createResource("http://example.org/book#1")

        r1.addProperty(title, "SPARQL - the book")
          .addProperty(description, "A book about SPARQL")
    }

    return ontModel;
  }

  @Test
  def testSparqlLARQ3 = {
    // Create the data.
    // This wil be the background (unnamed) graph in the dataset.
    implicit val model = createModel

    val larq3Feature = new Larq3Feature()
    model.addFeature(larq3Feature)
    larq3Feature.reindex

    // Query string.
    var query: String =
      "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
        "PREFIX :    <http://example/> " +
        "PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#> " +
        "SELECT * { ?lit pf:TextMatch3 'SPARQL'. }"

    query.toQuery.serialize(new IndentedWriter(System.out, true));

    val closure = {
      soln: SemachinaQuerySolution =>
        var str = ""
        var varnames = soln.getResultVars.toList
        varnames.foreach {
          name: String => println(name + ": " + soln.getLiteral(name) + ", ")
        }
    }

    model.select(query, closure, null)
    model.close
  }
}