package org.semachina.jena.sdb

import org.junit._

import com.hp.hpl.jena.vocabulary.{RDFS, RDF}
import com.hp.hpl.jena.rdf.model.ModelFactory
import org.semachina.jena.dsl.SemachinaDSL._
import com.hp.hpl.jena.ontology.OntDocumentManager
import org.semachina.jena.config.SemachinaBuilder

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 4/2/11
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */

class SDBTest {

  @Test
  def testSDB(): Unit = {
    def sdbConnector = new SDBConnector("layout2/index",
      "jdbc:h2:db/database_rdf",
      "org.h2.Driver",
      "h2",
      "sa",
      "",
      false)

    sdbConnector.deleteSDBStore
    sdbConnector.tryToCreateSDBStore

    val model = sdbConnector.getModel(null)

    println("SDB: ")
    model.write(System.out, "N3")

    var base = getClass.getResource("/base.owl")
    var cv = getClass.getResource("/cv.owl")
    var vcard = getClass.getResource("/vcard.owl")

    OntDocumentManager.getInstance().addAltEntry("http://kaste.lv/~captsolo/semweb/resume/base.owl", base.toString)
    OntDocumentManager.getInstance().addAltEntry("http://kaste.lv/~captsolo/semweb/resume/cv.owl", cv.toString)
    OntDocumentManager.getInstance().addAltEntry("http://www.w3.org/2006/vcard/ns", vcard.toString)

    var m = ModelFactory.createDefaultModel
    var fileManager = OntDocumentManager.getInstance().getFileManager()
    m = fileManager.readModel(m, "http://kaste.lv/~captsolo/semweb/resume/base.owl")
    m = fileManager.readModel(m, "http://kaste.lv/~captsolo/semweb/resume/cv.owl")
    m = fileManager.readModel(m, "http://www.w3.org/2006/vcard/ns")
    m.read(RDF.getURI)
    m.read(RDFS.getURI)
    m.setNsPrefix("rdf", RDF.getURI)
    m.setNsPrefix("rdfs", RDFS.getURI)
    m.setNsPrefix("base", "http://kaste.lv/~captsolo/semweb/resume/base.owl#")
    m.setNsPrefix("cv", "http://kaste.lv/~captsolo/semweb/resume/cv.owl#")
    m.setNsPrefix("vcard", "http://www.w3.org/2006/vcard/ns#")
    m.setNsPrefix("me", "http://www.ontModelAdapter.org/re.su.me/sgopalan.owl#")




    implicit val ontBase = SemachinaBuilder(base = m).build
    ontBase.addSubModel(model)

    val myResume = "me:sgopalan_cv" +& "cv:CV"

    ontBase.removeSubModel(model)

    //
    //
    model.add(ontBase)

    println("SDB: ")
    model.write(System.out, "N3")
  }


}