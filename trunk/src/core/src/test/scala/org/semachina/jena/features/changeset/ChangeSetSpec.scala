package org.semachina.jena.features.changeset

import org.junit.Test
import com.hp.hpl.jena.rdf.model.{Statement, Resource}
import java.util.{Date, ArrayList}
import org.semachina.jena.dsl.SemachinaDSL._
import com.hp.hpl.jena.util.MonitorModel
import scala.collection.JavaConversions._
import org.semachina.jena.ontology.SemachinaOntModel
import org.semachina.jena.ontology.naming.IdStrategy
import com.hp.hpl.jena.ontology.{Individual, OntModel}
import collection.mutable.HashMap
import java.text.SimpleDateFormat
import org.semachina.jena.binder.IndividualBinder
import org.semachina.jena.config.SemachinaBuilder


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 5/11/11
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */

class ChangeSetSpec {

  def createModel: OntModel with SemachinaOntModel = {
    implicit val ontModel = SemachinaBuilder().build

    ontModel.read("http://purl.org/dc/elements/1.1/")
    ontModel.read("http://vocab.org/changeset/schema.rdf")

    val title = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/title")
    ontModel.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/")
    ontModel.setNsPrefix("ex", "http://example.org/book#")
    ontModel.setNsPrefix("cs", "http://purl.org/vocab/changeset/schema#");

    ontModel.transact {
      writeModel =>

        val r1: Resource = writeModel.createResource("http://example.org/book#1")

        r1.addProperty(title, "SPARQL - the book")
    }

    return ontModel;
  }


  @Test
  def testChangeSet(): Unit = {
    implicit val model = createModel
    val monitor = new MonitorModel(model)
    monitor.snapshot()

    val resource = model("ex:1")

    val title = model.getOntProperty("http://purl.org/dc/elements/1.1/title")
    val description = model.getOntProperty("http://purl.org/dc/elements/1.1/description")
    resource.addProperty(description, "A book about SPARQL1")
    resource.addProperty(description, "A book about SPARQL2")
    resource.addProperty(description, "A book about SPARQL3")

    resource.removeProperty(title, resource.getPropertyValue(title))

    val additions = new ArrayList[Statement]
    val removals = new ArrayList[Statement]
    monitor.snapshot(additions, removals)


    val additionsByURI = additions.toList.groupBy {
      stmt => stmt.getSubject.getURI
    }
    val removalsByURI = removals.toList.groupBy {
      stmt => stmt.getSubject.getURI
    }


    val changeModel = SemachinaBuilder().build
    changeModel.setNsPrefix("cs", "http://purl.org/vocab/changeset/schema#")
    changeModel.setNsPrefix("ex", "http://example.org/book#")
    changeModel.read("http://vocab.org/changeset/schema.rdf")

    val changeTime = new Date()

    val strategy = IdStrategy.newPatterned(
    "http://www.semachina.org/{subject}/{date}", {
      (res: Individual, uriVariables: HashMap[String, AnyRef]) =>
      //set sub collection
        uriVariables("subject") = IdStrategy.encodeString(res.value[String]("cs:changeReason"))
        uriVariables("date") = new SimpleDateFormat("yyyy/MM/dd") format res.value[Date]("cs:createdDate")
    })

    val changeSet = new ChangeSet(
      null,
      strategy,
      resource,
      changeTime,
      Option("sgopalan"),
      Option("Everything Changes"),
      additions.toList,
      removals.toList)

    val binder = IndividualBinder.default(changeModel)
    val indiv = binder.toIndividual(changeSet)

    val boundChangeSet = binder.toBean[ChangeSet](indiv)

    changeModel.write(System.out, "N3")
  }
}