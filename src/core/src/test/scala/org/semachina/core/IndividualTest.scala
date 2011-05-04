package org.semachina.core

import org.junit._
import Assert._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.sdb.sql.{JDBC, SDBConnection}
import com.hp.hpl.jena.sdb.store.{LayoutType, DatabaseType}
import com.hp.hpl.jena.sdb.{StoreDesc, Store, SDBFactory}
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.semachina.config.AppConfig
import com.hp.hpl.jena.ontology.{ProfileRegistry, OntModelSpec}
import org.semachina.jena.{ReadWriteContext, SemachinaIndividual, SemachinaOntModel}
import org.semachina.jena.impl.{SimpleReadWriteContext, SemachinaOntModelImpl}
import com.hp.hpl.jena.vocabulary.XSD

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Aug 28, 2010
 * Time: 8:09:56 PM
 * To change this template use File | Settings | File Templates.
 */


object IndividualTest {
  @BeforeClass
  def setup: Unit = {
    val ctx = new AnnotationConfigApplicationContext(classOf[AppConfig]);
  }

  @AfterClass
  def teardown: Unit = {

  }
}

/**
 * Vocabulary definitions from file:vocabularies/dublin-core_11.xml
 * @author Auto -generated by schemagen on 13 May 2003 08:51
 */

class IndividualTest {
  val NL = System.getProperty("line.separator")


  def createSDBModel: Store = {
    var storeDesc: StoreDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.H2)
    JDBC.loadDriverH2
    var jdbcURL: String = "jdbc:h2:mem:test"

    // Passing null for user and password causes them to be extracted
    // from the environment variables SDB_USER and SDB_PASSWORD
    var conn: SDBConnection = new SDBConnection(jdbcURL, null, null)

    // Make store from connection and store description.
    var store: Store = SDBFactory.connectStore(conn, storeDesc)
    store.getTableFormatter.create
    return store
  }

  @Test
  def testTransaction : Unit = {
    val ontModel = new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG))
    ontModel.read("http://purl.org/dc/elements/1.1/")
    ontModel.read("http://www.w3.org/TR/owl-guide/wine.rdf")


    val context = new SimpleReadWriteContext() {
      def execute(it: SemachinaOntModel) {
        val title = it.getOntProperty("http://purl.org/dc/elements/1.1/title")
        val ontClass = it.resolveOntClass( "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Wine" )
        val me = it.createIndividual( "http://example.com/sri", ontClass )
        me.set( title, it.createTypedLiteral("My wine", XSD.xstring.getURI ) )
      }
    }

    ontModel.safeWrite(context)


    ontModel.write( System.out )
  }

  //@Test
  def testTransactionFail = {
    val store = createSDBModel
    var sdbModel: Model = SDBFactory.connectDefaultModel(store)
    val ontModel = new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG), sdbModel)

    ontModel.read("http://purl.org/dc/elements/1.1/")
    val title = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/title")
    val description = ontModel.getOntProperty("http://purl.org/dc/elements/1.1/description")
    println(description)
    try {
      ontModel -> {
        it: Model =>
          val r1: Resource = it.createResource("http://example.org/book#1")
          r1.addProperty(title, "SPARQL - the book")
                  .addProperty(description, "A book about SPARQL")

          throw new RuntimeException("Failure");
      }
    }
    catch {
      case e: Exception => {
      }
    }

    assertFalse(sdbModel.containsResource(ResourceFactory.createResource("http://example.org/book#1")))
    store.close
  }

}
