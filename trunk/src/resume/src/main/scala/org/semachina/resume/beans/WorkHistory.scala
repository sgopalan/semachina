package org.semachina.resume.beans


import org.joda.time.LocalDate

import java.net.URI
import thewebsemantic.{RdfType, Prefixes, Prefix}
import org.semachina.jenabean.JenabeanSupport._
import com.hp.hpl.jena.rdf.model.Resource


@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#")
))
@RdfType("cv:WorkHistory")
class WorkHistory (
    @Id var workHistoryURI : URI = null,
    @RdfProperty("cv:careerLevel") var careerLevel : Resource = null,
    @RdfProperty("cv:employedIn") var employedIn : Resource = null,
    @RdfProperty("cv:isCurrent") var isCurrent : Resource = null,
    @RdfProperty("cv:jobType") var jobType : Resource = null,
    @RdfProperty(value="cv:startDate", dataTypeURI="xsd:date") var startDate : LocalDate = null,
    @RdfProperty(value="cv:endDate", dataTypeURI="xsd:date") var endDate : LocalDate = null,
    @RdfProperty("cv:jobDescription") var jobDescription : String = null,
    @RdfProperty("cv:jobTitle") var jobTitle : String = null,
    @RdfProperty("cv:numSubordinates") var numSubordinates: Integer = 0) extends IdField {

  override def id = workHistoryURI
}