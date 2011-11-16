package org.semachina.resume.beans

import java.net.URI
import org.semachina.jenabean.JenabeanSupport._
import thewebsemantic.{Prefix, Prefixes, RdfType}

@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#"),
  new Prefix(prefix = "me", uri = "http://re.su.me/sgopalan.owl#")
))
@RdfType("cv:Company")
class Company (
  @Id var companyURI: URI = null,
  @RdfProperty("cv:Name") var name: String = null,
  @RdfProperty("cv:Locality") var locality: String = null,
  @RdfProperty("cv:Country") var country: String = null,
  @RdfProperty("cv:Industry") var industry: java.util.Collection[String] = null,
  @RdfProperty("cv:URL") var url: String = null,
  @RdfProperty("cv:Notes") var notes: String = "") extends IdField {

  override def id = companyURI
}