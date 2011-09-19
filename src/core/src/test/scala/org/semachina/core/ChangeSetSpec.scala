package org.semachina.core

import org.junit.Test
import org.semachina.jena.ontology.impl.SemachinaOntModelImpl
import com.hp.hpl.jena.rdf.model.{Statement, Resource}
import java.util.{Date, ArrayList}
import org.semachina.jena.SemachinaDSL._
import com.hp.hpl.jena.util.{PrintUtil, MonitorModel}
import scala.collection.JavaConversions._
import com.hp.hpl.jena.ontology.{OntModel, ProfileRegistry, OntModelSpec}
import org.semachina.jena.config.SemachinaFactory
import org.semachina.jena.ontology.SemachinaOntModel
import org.semachina.jena.features.changeset.ChangeSet


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 5/11/11
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */

class ChangeSetSpec {


  def createModel: OntModel with SemachinaOntModel = {
    implicit val ontModel =
        SemachinaFactory.createOntologyModel(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG))

    ontModel.read("http://purl.org/dc/elements/1.1/")
    ontModel.read("http://vocab.org/changeset/schema.rdf")

    val title = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/title")
    ontModel.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/")
    ontModel.setNsPrefix("ex", "http://example.org/book#")
    ontModel.setNsPrefix("cs", "http://purl.org/vocab/changeset/schema#");

    ontModel.write( System.out, "N3" );

    ontModel.safeWrite {
      writeModel =>

        val r1: Resource = writeModel.createResource("http://example.org/book#1")

        r1.addProperty(title, "SPARQL - the book")
    }

    return ontModel;
  }


  @Test
  def testChangeSet() : Unit = {
    implicit val model = createModel
    val monitor = new MonitorModel( model )
    monitor.snapshot()

    val resource = model("ex:1")

    val title = model.getOntProperty("http://purl.org/dc/elements/1.1/title")
    val description = model.getOntProperty("http://purl.org/dc/elements/1.1/description")
    resource.addProperty(description, "A book about SPARQL1")
    resource.addProperty(description, "A book about SPARQL2")
    resource.addProperty(description, "A book about SPARQL3")

    resource.removeProperty( title, resource.getPropertyValue( title ) )

    val additions = new ArrayList[Statement]
    val removals = new ArrayList[Statement]
    monitor.snapshot( additions, removals )

    val changeModel =
        SemachinaFactory.createOntologyModel(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG))
    changeModel.setNsPrefix("ex", "http://example.org/book#")
    changeModel.read("http://vocab.org/changeset/schema.rdf")

    val changeTime = new Date()

    val changeSet = new ChangeSet( "ex:changeSet-" + changeTime.getTime(), resource, changeTime, "sgopalan", "Everything Changes" )
    changeSet.getAdditions.addAll( additions )
    changeSet.getRemovals.addAll( removals )
    changeSet.create( changeModel )

//    val changeSet1 = changeModel.createIndividual("ex:changeSet-" + changeTime.getTime(), "http://purl.org/vocab/changeset/schema#ChangeSet" )
//    changeSet1 set ("cs:subjectOfChange" -> resource )
//    changeSet1 set ("cs:changeReason" -> "Everything Changes")
//    changeSet1 set ("cs:createdDate" -> changeTime )
//    changeSet1 set ("cs:creatorName" -> "sgopalan" )
//
//    val reifiedAdditions = additions collect { case s:Statement => changeModel.createReifiedStatement( s ) }
//    val reifiedRemovals = removals collect { case s:Statement => changeModel.createReifiedStatement( s ) }
//    changeSet1 set ("cs:addition" -> reifiedAdditions )
//    changeSet1 set ("cs:removal" -> reifiedRemovals )

    changeModel.write( System.out, "N3")
  }
}