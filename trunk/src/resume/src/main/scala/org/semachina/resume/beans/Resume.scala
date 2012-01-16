package org.semachina.resume.beans

import java.util.Date
import java.net.URI
import org.semachina.jena.binder.annotations.SemachinaBinding._

@Prefixes(Array(
  new Prefix(prefix = "cv", uri = "http://kaste.lv/~captsolo/semweb/resume/cv.owl#")
))
@RdfType(Array("cv:CV"))
class Resume(
              @Id var resumeURI: URI = null,
              @RdfProperty("cv:cvTitle") var title: String = null,
              @RdfProperty("cv:cvDescription") var description: String = null,
              @RdfProperty("cv:cvCopyright") var copyright: String = null,
              @RdfProperty("cv:cvIsActive") var isActive: Boolean = true,
              @RdfProperty("cv:cvIsConfidential") var isConfidential: Boolean = true,
              @RdfProperty("cv:lastUpdate") var lastUpdate: Date = null,
              @RdfProperty("cv:aboutPerson") var aboutPerson: AboutPerson = null,
              @RdfProperty("cv:hasEducation") var educationEntries: java.util.Collection[EducationEntry] = new java.util.ArrayList[EducationEntry](),
              @RdfProperty("cv:hasWorkHistory") var workHistory: java.util.Collection[WorkHistory] = new java.util.ArrayList[WorkHistory]()) extends IdField {

  def addEducationEntry(entry: EducationEntry) = educationEntries.add(entry)

  def addWorkHistory(entry: WorkHistory) = workHistory.add(entry)

  override def id = resumeURI
}