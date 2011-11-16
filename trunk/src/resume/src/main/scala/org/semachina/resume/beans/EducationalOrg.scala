package org.semachina.resume.beans

import java.net.URI
import thewebsemantic.{Prefix, Prefixes, RdfType }
import org.semachina.jenabean.JenabeanSupport._

@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#"),
  new Prefix(prefix = "me", uri = "http://re.su.me/sgopalan.owl#")
))
@RdfType("cv:EducationalOrg")
class EducationalOrg (
  @Id var orgURI : URI = null,
  @RdfProperty("cv:Name") var name : String = null,
  @RdfProperty("cv:Locality") var locality : String = "") extends IdField {

  override def id = orgURI
}