package org.semachina.resume.beans

import org.joda.time.LocalDate
import com.hp.hpl.jena.ontology.{OntModel, Individual}
import java.net.URI
import thewebsemantic.{RdfType, Prefixes, Prefix}
import org.semachina.jenabean.JenabeanSupport._
import com.hp.hpl.jena.rdf.model.Resource

@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#")
))
@RdfType("cv:Education")
class EducationEntry(
  @Id var entryURI : URI = null,
  @RdfProperty("cv:degreeType") var degreeType : Resource = null,
  @RdfProperty("cv:studiedIn") var studiedIn : Resource = null,
  @RdfProperty("cv:eduDescription") var eduDescription : String = null,
  @RdfProperty(value="cv:eduStartDate", dataTypeURI="xsd:date") var eduStartDate : LocalDate = null,
  @RdfProperty(value="cv:eduGradDate", dataTypeURI ="xsd:date") var eduGradDate : LocalDate = null,
  @RdfProperty("cv:eduMajor") var eduMajor : String = null,
  @RdfProperty("cv:eduMinor") var eduMinor : String = null) extends IdField {

  override def id = entryURI

}