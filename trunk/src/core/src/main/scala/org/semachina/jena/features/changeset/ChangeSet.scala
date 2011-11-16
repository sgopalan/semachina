package org.semachina.jena.features.changeset

import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.Statement
import javax.validation.constraints.NotNull
import java.util.Date
import org.semachina.jena.binder.annotations.SemachinaBinding._
import org.semachina.jena.ontology.naming.IdStrategy

@Prefixes(Array(
  new Prefix(prefix = "cs", uri = "http://purl.org/vocab/changeset/schema#")
))
@RdfType(Array("cs:ChangeSet"))
class ChangeSet(
                 @Id val id: String,
                 val idStrategy: IdStrategy = null,
                 @NotNull @RdfProperty("cs:subjectOfChange") val subjectOfChange: Resource,
                 @NotNull @RdfProperty("cs:createdDate") val createdDate: Date,
                 @RdfProperty("cs:creatorName") val creatorName: Option[String],
                 @RdfProperty("cs:changeReason") val changeReason: Option[String],
                 @NotNull @RdfProperty("cs:addition") val addition: scala.List[Statement],
                 @NotNull @RdfProperty("cs:removal") val removal: scala.List[Statement]) {
}