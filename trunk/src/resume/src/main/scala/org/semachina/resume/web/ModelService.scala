package org.semachina.resume.web

import com.hp.hpl.jena.vocabulary.{RDFS, RDF}
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntDocumentManager
import com.hp.hpl.jena.rdf.model.ModelFactory
import org.semachina.jena.config.SemachinaConfiguration
import org.mindswap.pellet.jena.PelletReasonerFactory
import org.semachina.jena.config.SemachinaBuilder

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 3/30/11
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */

class ModelService {

  val baseModel: OntModel = createBaseModel()

  private def createBaseModel(): OntModel = {

    var base = getClass.getResource("/base.owl")
    var cv = getClass.getResource("/cv.owl")
    var vcard = getClass.getResource("/vcard.owl")
    var rdf = getClass.getResource("/vocabularies/rdf-schema.rdf")

    var m = SemachinaBuilder(spec = PelletReasonerFactory.THE_SPEC).build

    OntDocumentManager.getInstance().addAltEntry("http://www.w3.org/1999/02/22-rdf-syntax-ns", rdf.toString)
    OntDocumentManager.getInstance().addAltEntry("http://kaste.lv/~captsolo/semweb/resume/base.owl", base.toString)
    OntDocumentManager.getInstance().addAltEntry("http://kaste.lv/~captsolo/semweb/resume/cv.owl", cv.toString)
    OntDocumentManager.getInstance().addAltEntry("http://www.w3.org/2006/vcard/ns", vcard.toString)

    var fileManager =  OntDocumentManager.getInstance().getFileManager()
    m.read( "http://kaste.lv/~captsolo/semweb/resume/base.owl" )
    m.read( "http://kaste.lv/~captsolo/semweb/resume/cv.owl")
    m.read( "http://www.w3.org/2006/vcard/ns" )
    m.read( "http://www.w3.org/1999/02/22-rdf-syntax-ns" )
    //m.read( RDFS.getURI )

    m.setNsPrefix("rdf", RDF.getURI)
    m.setNsPrefix("rdfs", RDFS.getURI)
    m.setNsPrefix("base", "http://kaste.lv/~captsolo/semweb/resume/base.owl#")
    m.setNsPrefix("cv", "http://kaste.lv/~captsolo/semweb/resume/cv.owl#")
    m.setNsPrefix("me", "http://re.su.me/sgopalan.owl#")
    m.setNsPrefix("vcard", "http://www.w3.org/2006/vcard/ns#")

    return m
  }
}