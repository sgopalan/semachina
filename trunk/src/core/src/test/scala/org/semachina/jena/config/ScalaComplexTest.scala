package org.semachina.jena.config

import org.junit.Test
import org.junit.Assert._


import com.hp.hpl.jena.rdf.model.{RDFNode, Model}
import com.hp.hpl.jena.enhanced.Personality
import com.hp.hpl.jena.ontology.OntModelSpec

//import org.scalatest.matchers.MustMatchers
//import razie.base.scripting._
//import razie.scripster._

class ScalaComplexTest {
  //extends MustMatchers {
  val SS = """
import com.hp.hpl.jena.enhanced.{Implementation, BuiltinPersonalities, Personality}
import org.semachina.jena.ontology.impl.SemachinaOntModelImpl
import org.semachina.jena.ontology.SemachinaOntModel
import com.hp.hpl.jena.ontology.{ProfileRegistry, OntModelSpec, OntModel, OntDocumentManager}
import com.hp.hpl.jena.rdf.model.{Model, RDFNode, ModelFactory}
import org.semachina.jena.config.JenaBuilderContext

  def createOntologyModel(jenaContext : JenaBuilderContext): OntModel with SemachinaOntModel = {
    try {
      return new SemachinaOntModelImpl(
        jenaContext.ontSpec,
        jenaContext.baseModel,
        jenaContext.personality)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e.getMessage, e)
      }
    }
  }

  val ontModel = createOntologyModel(jenaContext)
"""

  @Test def testdef1 = {


    // simple, one time, expression
    //    val ctx = ScalaScriptContext()
    //    ctx.set("jenaContext", new JenaBuilderContext(personality = BuiltinPersonalities.model))
    //    val value = ScalaScript (SS).eval(ctx).getOrThrow.asInstanceOf[OntModel]
    val value = new Object
    assertNotNull(value)
  }


}

class JenaBuilderContext(
                          val ontSpec: OntModelSpec = OntModelSpec.OWL_MEM,
                          val baseModel: Model = null,
                          val personality: Personality[RDFNode])