package org.semachina.jena

import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.ontology.OntClass
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.rdf.model.ReifiedStatement
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.Statement
import org.semachina.jena.config.SemachinaConfig
import org.semachina.jena.impl.scala.SemachinaIndividualAdapter
import javax.validation.constraints.NotNull
import java.util.ArrayList
import java.util.Collection
import java.util.Date
import java.util.List
import scala.collection.JavaConversions._
import org.semachina.jena.wrapper.ExtendedIteratorWrapper._
import org.semachina.jena.config.SemachinaConfig._
/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 6/7/11
 * Time: 10:19 PM
 * To change this template use File | Settings | File Templates.
 */
object ChangeSet {
  private var CS_URI: String = "http://purl.org/vocab/changeset/schema#"
}

class ChangeSet {
  def this(@NotNull id: String, @NotNull subjectOfChange: Resource, @NotNull createdDate: Date, creatorName: String, changeReason: String) {
    this ()
    this.id = id
    this.subjectOfChange = subjectOfChange
    this.createdDate = createdDate
    this.creatorName = creatorName
    this.changeReason = changeReason
  }

  def getId: String = {
    return id
  }

  def getSubjectOfChange: Resource = {
    return subjectOfChange
  }

  def getChangeReason: String = {
    return changeReason
  }

  def getCreatedDate: Date = {
    return createdDate
  }

  def getCreatorName: String = {
    return creatorName
  }

  def getAdditions: Collection[Statement] = {
    return addition
  }

  def getRemovals: Collection[Statement] = {
    return removal
  }

  def create(implicit model: OntModel): Individual = {
    if (addition.isEmpty && removal.isEmpty) {
      return null
    }
    var extended: SemachinaOntModelTrait = SemachinaConfig.toSemachinaOntModelTrait(model)
    var csClass: OntClass = model.getOntClass(ChangeSet.CS_URI + "ChangeSet")
    var i: Individual = model.createIndividual(extended.expandURI(getId.toString), csClass)
    var changeSet: SemachinaIndividualAdapter = SemachinaIndividualAdapter(i)
    changeSet.set(ChangeSet.CS_URI + "subjectOfChange", getSubjectOfChange)
    changeSet.set(ChangeSet.CS_URI + "createdDate", model.createTypedLiteral(getCreatedDate))
    if (!getAdditions.isEmpty) {
      var reifiedAdditions: List[ReifiedStatement] = new ArrayList[ReifiedStatement]
      for (stmt <- getAdditions) {
        reifiedAdditions.add(model.createReifiedStatement(stmt))
      }
      changeSet.set(ChangeSet.CS_URI + "addition", reifiedAdditions)
    }
    if (!getRemovals.isEmpty) {
      var reifiedRemovals: List[ReifiedStatement] = new ArrayList[ReifiedStatement]
      for (stmt <- getRemovals) {
        reifiedRemovals.add(model.createReifiedStatement(stmt))
      }
      changeSet.set(ChangeSet.CS_URI + "removal", reifiedRemovals)
    }
    if (getChangeReason != null) {
      changeSet.set(ChangeSet.CS_URI + "changeReason", (getChangeReason ^^ "xsd:string") )
    }
    if (getCreatorName != null) {
      changeSet.set(ChangeSet.CS_URI + "creatorName", (getCreatorName ^^ "xsd:string") )
    }
    return i
  }

  private var id: String = null
  private var subjectOfChange: Resource = null
  private var changeReason: String = null
  private var createdDate: Date = null
  private var creatorName: String = null
  private var addition: List[Statement] = new ArrayList[Statement]
  private var removal: List[Statement] = new ArrayList[Statement]
}