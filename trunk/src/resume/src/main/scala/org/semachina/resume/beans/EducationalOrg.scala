package org.semachina.resume.beans

import java.net.URI
import org.semachina.jena.binder.annotations.SemachinaBinding._

@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#"),
  new Prefix(prefix = "me", uri = "http://re.su.me/sgopalan.owl#")
))
@RdfType(Array("cv:EducationalOrg"))
case class EducationalOrg(
                           @Id var orgURI: URI = null,
                           @RdfProperty("cv:Name") var name: String = null,
                           @RdfProperty("cv:Locality") var locality: String = "") extends IdField {

  override def id = orgURI
}