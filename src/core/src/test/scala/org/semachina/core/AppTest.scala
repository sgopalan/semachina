package org.semachina.core

import org.junit._
import Assert._

import com.hp.hpl.jena.rdf.model.{Literal}
import org.semachina.jena.JenaExtension._
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.vocabulary.{RDFS, XSD}
import com.hp.hpl.jena.datatypes.{DatatypeFormatException}
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.semachina.jena.core.OWLFactory
import org.semachina.config.AppConfig


object AppTest {
  @BeforeClass
  def setup: Unit = {
    val ctx = new AnnotationConfigApplicationContext(classOf[AppConfig]);

  }
}

class AppTest {
  @Test
  def testBase64() = {
    var value: Array[Byte] = getClass.toString.getBytes
    //var value: Base64 = new Base64(bytes)

    var literal: Literal = value.typedLit(XSD.base64Binary.getURI)

    println("Literal: " + literal.toString);
    assertNotNull(literal)
    assertTrue(literal.toString.endsWith("^^http://www.w3.org/2001/XMLSchema#base64Binary"))
    println("Class " + literal.getValue.getClass)
    assertEquals(literal.getValue, value)
  }

  @Test
  def testNegativeInteger() = {

    var value = -100

    var literal: Literal = value.typedLit(XSD.negativeInteger.getURI)
    println("Literal: " + literal.toString);
    assertNotNull(literal)
    var literalValue: Number = literal()
    assertEquals(value, literalValue);
  }

  @Test(expected = classOf[DatatypeFormatException])
  def testNegativeIntegerFail() = {
    var value = 0
    var literal: Literal = value.typedLit("xsd:negativeInteger")
    println("Literal: " + literal.toString);
    assertNotNull(literal)
    assertEquals(value, literal());
  }


  @Test
  def testBoolean() = {
    var bool: Boolean = true

    var boolLiteral: Literal = bool.typedLit();
    println("Literal: " + boolLiteral.toString);
    assertNotNull(boolLiteral)
    assertTrue(boolLiteral.toString.endsWith("^^http://www.w3.org/2001/XMLSchema#boolean"))

    assertEquals(boolLiteral, boolLiteral.toString.parseLit());

    println("Value: " + boolLiteral() + " Class " + boolLiteral.getValue.getClass)
    assertEquals(boolLiteral[Boolean](), bool)

    if (boolLiteral()) {
      println("auto cast");
    }

  }

  @Test
  def testJena() = {
    //BuiltinPersonalities.model.add(classOf[Individual], OWLBean.factory)
    //var jt: JenaTest = new JenaTest

    var metamodel = getClass.getResource("/metamodel.owl")
    var des = getClass.getResource("/des-example.owl")

    assertNotNull(metamodel)
    assertNotNull(des)

    addAltEntry("http://boozallen.com/soa/metamodel.owl", metamodel.toString)
    addAltEntry("http://dcgs.enterprise.spfg/20080324.owl", des.toString)




    implicit var m: OntModel = OWLFactory.createOntologyModel()
    m.setNsPrefix("rdfs", RDFS.getURI)
    m.setNsPrefix("bah", "http://boozallen.com/soa/metamodel.owl#")
    m.setNsPrefix("des", "http://dcgs.enterprise.spfg/20080324.owl#")

    m.read(metamodel.toString)
    m.read(des.toString)

    m.prepare

    var start: Long = System.nanoTime
    var hasDouble: OntProperty = "bah:narrowerLayer"
    var resolveFind: Long = System.nanoTime - start

    start = System.nanoTime
    var hasDouble0: OntProperty = "des:hasDouble"
    var resolveFind1: Long = System.nanoTime - start

    start = System.nanoTime
    var hasDouble1: OntProperty = "http://dcgs.enterprise.spfg/20080324.owl#hasDouble"
    var find: Long = System.nanoTime - start

    start = System.nanoTime
    var hasDouble2: OntProperty = "des:hasDouble"
    var find2: Long = System.nanoTime - start
    System.out.println("ResolveFind: " + resolveFind / 1000000000d + " ResolveFind1: " + resolveFind1 / 1000000000d + " find: " + find / 1000000000d + " find2: " + find2 / 1000000000d)

    var classes = new scala.collection.mutable.HashSet[OntClass]()

    m.listClasses() {c => classes += c}

    def ontClass: OntClass = "bah:Portfolio"
    ontClass.listIndividuals {println}

    //for some reason m.getIndividual("http://dcgs.enterprise.spfg/20080324.owl#DESPortfolio")  doesn't work
    val indiv: Individual = "des:DESPortfolio"
    indiv.listProperties() {p => println("P: " + p)}

    val common: Individual = "http://dcgs.enterprise.spfg/20080324.owl#FunctionalLayer"

    println(indiv + ("bah:narrowerLayer", common))
    indiv.listProperties() {p => println("P: " + p)}

    println(indiv - ("bah:narrowerLayer", common))
    indiv.listProperties() {p => println("P: " + p)}

    indiv("rdfs:label") = "Hello".plainLit("en");
    println("apply: " + indiv("rdfs:label"))

  }
}


