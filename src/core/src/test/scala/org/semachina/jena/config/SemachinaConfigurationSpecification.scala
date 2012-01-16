package org.semachina.jena.config

import org.specs2._
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.util.iterator.ExtendedIterator
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.datatypes.RDFDatatype
import org.semachina.jena.enhanced.SemachinaImplementation
import com.hp.hpl.jena.enhanced.{EnhNode, EnhGraph}
import org.semachina.jena.datatype.SemachinaBaseDatatype
import org.semachina.jena.datatype.SemachinaBaseDatatype._
import org.semachina.jena.binder.ObjectBinder
import com.hp.hpl.jena.shared.impl.JenaParameters

class SemachinaConfigurationSpecification extends mutable.SpecificationWithJUnit {

  val baseURI = "http://www.w3.org/2006/vcard/ns#"
  val vcardPrefix = "vcardNS"
  val entry = "http://www.w3.org/2006/vcard/ns"
  val altEntry = SemachinaConfiguration.getResource("/vcard.owl").toString
  val shortURI = vcardPrefix + ":VCard"
  val longURI = "http://www.w3.org/2006/vcard/ns#VCard"

  "SemachinaConfiguration functionality" should {
    "register prefixes / expand prefixes for URIs" in {
      SemachinaConfiguration.reloadDefault
      SemachinaConfiguration.setNsPrefix(vcardPrefix -> baseURI)
      val uri = SemachinaConfiguration expandPrefix shortURI
      uri must beEqualTo(longURI)
    }
    "register prefixes / short form URIs " in {
      SemachinaConfiguration.reloadDefault
      SemachinaConfiguration.setNsPrefix(vcardPrefix -> baseURI)
      val uri = SemachinaConfiguration shortForm longURI
      uri must beEqualTo(shortURI)
    }
    "register alt entries for URIs" in {
      SemachinaConfiguration.reloadDefault
      SemachinaConfiguration.addAltEntry(entry -> altEntry)
      val storedEntry =
        OntDocumentManager.getInstance.getFileManager.getLocationMapper.getAltEntry(entry)
      storedEntry must beEqualTo(altEntry)
    }
    "register custom implementations for Jena interfaces" in {
      SemachinaConfiguration.reloadDefault
      val dummyImplemention =
        SemachinaImplementation[DummyIndividual](
          (node: Node, eg: EnhGraph) => new DummyIndividual(node, eg),
          "",
          classOf[Individual])

      //set the implementation
      SemachinaConfiguration.registerImplementation(dummyImplemention)

      //get the implementation
      val retrievedFactory = SemachinaConfiguration.getImplementation(classOf[Individual])

      retrievedFactory must beEqualTo(dummyImplemention)
    }
    "register custom datatype" in {
      SemachinaConfiguration.reloadDefault

      val dummyDatatype = new DummyDatatype
      SemachinaConfiguration.registerDatatype(dummyDatatype)

      val retrievedDatatype =
        SemachinaConfiguration.typeMapper.getTypeByName("http://example.com#SSN")

      retrievedDatatype must beEqualTo(dummyDatatype)
    }
    "set Jena Parameters true" in {
      SemachinaConfiguration.reloadDefault
      SemachinaConfiguration
        .setEnableEagerLiteralValidation(true)
        .setEnablePlainLiteralSameAsString(true)
        .setEnableSilentAcceptanceOfUnknownDatatypes(true)
        .setEnableWhitespaceCheckingOfTypedLiterals(true)
        .setEnableFilteringOfHiddenInfNodes(true)
        .setEnableOWLRuleOverOWLRuleWarnings(true)
        .setDisableBNodeUIDGeneration(true)

      JenaParameters.enableEagerLiteralValidation must beTrue
      JenaParameters.enablePlainLiteralSameAsString must beTrue
      JenaParameters.enableSilentAcceptanceOfUnknownDatatypes must beTrue
      JenaParameters.enableWhitespaceCheckingOfTypedLiterals must beTrue
      JenaParameters.enableFilteringOfHiddenInfNodes must beTrue
      JenaParameters.enableOWLRuleOverOWLRuleWarnings must beTrue
      JenaParameters.disableBNodeUIDGeneration must beTrue
    }
    "set Jena Parameters false" in {
      SemachinaConfiguration.reloadDefault
      SemachinaConfiguration
        .setEnableEagerLiteralValidation(false)
        .setEnablePlainLiteralSameAsString(false)
        .setEnableSilentAcceptanceOfUnknownDatatypes(false)
        .setEnableWhitespaceCheckingOfTypedLiterals(false)
        .setEnableFilteringOfHiddenInfNodes(false)
        .setEnableOWLRuleOverOWLRuleWarnings(false)
        .setDisableBNodeUIDGeneration(false)

      JenaParameters.enableEagerLiteralValidation must beFalse
      JenaParameters.enablePlainLiteralSameAsString must beFalse
      JenaParameters.enableSilentAcceptanceOfUnknownDatatypes must beFalse
      JenaParameters.enableWhitespaceCheckingOfTypedLiterals must beFalse
      JenaParameters.enableFilteringOfHiddenInfNodes must beFalse
      JenaParameters.enableOWLRuleOverOWLRuleWarnings must beFalse
      JenaParameters.disableBNodeUIDGeneration must beFalse
    }
  }
}

class SocialSecurityNumber(val ssn: String)

class DummyDatatype extends SemachinaBaseDatatype[SocialSecurityNumber](
  typeURI = "http://example.com#SSN",
  parser = {
    lexicalForm: String => new SocialSecurityNumber(lexicalForm)
  },
  lexer = {
    cast: SocialSecurityNumber => cast.ssn
  }) {

  addObjectBinder(
    ObjectBinder[Number, SocialSecurityNumber]({
      number: Number => new SocialSecurityNumber(number.toString)
    }))
}

class DummyIndividual(n: Node, g: EnhGraph) extends EnhNode(n, g) with Individual {
  def getId: AnonId = null

  def inModel(m: Model): Resource = null

  def hasURI(uri: String): Boolean = false

  def getURI: String = ""

  def getNameSpace: String = ""

  def getLocalName: String = ""

  def getRequiredProperty(p: Property): Statement = null

  def getProperty(p: Property): Statement = null

  def listProperties(p: Property): StmtIterator = null

  def listProperties(): StmtIterator = null

  def addLiteral(p: Property, o: Boolean): Resource = null

  def addLiteral(p: Property, o: Long): Resource = null

  def addLiteral(p: Property, o: Char): Resource = null

  def addLiteral(value: Property, d: Double): Resource = null

  def addLiteral(value: Property, d: Float): Resource = null

  def addLiteral(p: Property, o: AnyRef): Resource = null

  def addLiteral(p: Property, o: Literal): Resource = null

  def addProperty(p: Property, o: String): Resource = null

  def addProperty(p: Property, o: String, l: String): Resource = null

  def addProperty(p: Property, lexicalForm: String, datatype: RDFDatatype): Resource = null

  def addProperty(p: Property, o: RDFNode): Resource = null

  def hasProperty(p: Property): Boolean = false

  def hasLiteral(p: Property, o: Boolean): Boolean = false

  def hasLiteral(p: Property, o: Long): Boolean = false

  def hasLiteral(p: Property, o: Char): Boolean = false

  def hasLiteral(p: Property, o: Double): Boolean = false

  def hasLiteral(p: Property, o: Float): Boolean = false

  def hasLiteral(p: Property, o: AnyRef): Boolean = false

  def hasProperty(p: Property, o: String): Boolean = false

  def hasProperty(p: Property, o: String, l: String): Boolean = false

  def hasProperty(p: Property, o: RDFNode): Boolean = false

  def removeProperties(): Resource = null

  def removeAll(p: Property): Resource = null

  def begin(): Resource = null

  def abort(): Resource = null

  def commit(): Resource = null

  def getPropertyResourceValue(p: Property): Resource = null

  def getOntModel: OntModel = null

  def getProfile: Profile = null

  def isOntLanguageTerm: Boolean = false

  def setSameAs(res: Resource) {}

  def addSameAs(res: Resource) {}

  def getSameAs: OntResource = null

  def listSameAs(): ExtendedIterator[_ <: Resource] = null

  def isSameAs(res: Resource): Boolean = false

  def removeSameAs(res: Resource) {}

  def setDifferentFrom(res: Resource) {}

  def addDifferentFrom(res: Resource) {}

  def getDifferentFrom: OntResource = null

  def listDifferentFrom(): ExtendedIterator[_ <: Resource] = null

  def isDifferentFrom(res: Resource): Boolean = false

  def removeDifferentFrom(res: Resource) {}

  def setSeeAlso(res: Resource) {}

  def addSeeAlso(res: Resource) {}

  def getSeeAlso: Resource = null

  def listSeeAlso(): ExtendedIterator[RDFNode] = null

  def hasSeeAlso(res: Resource): Boolean = false

  def removeSeeAlso(res: Resource) {}

  def setIsDefinedBy(res: Resource) {}

  def addIsDefinedBy(res: Resource) {}

  def getIsDefinedBy: Resource = null

  def listIsDefinedBy(): ExtendedIterator[RDFNode] = null

  def isDefinedBy(res: Resource): Boolean = false

  def removeDefinedBy(res: Resource) {}

  def setVersionInfo(info: String) {}

  def addVersionInfo(info: String) {}

  def getVersionInfo: String = ""

  def listVersionInfo(): ExtendedIterator[String] = null

  def hasVersionInfo(info: String): Boolean = false

  def removeVersionInfo(info: String) {}

  def setLabel(label: String, lang: String) {}

  def addLabel(label: String, lang: String) {}

  def addLabel(label: Literal) {}

  def getLabel(lang: String): String = ""

  def listLabels(lang: String): ExtendedIterator[RDFNode] = null

  def hasLabel(label: String, lang: String): Boolean = false

  def hasLabel(label: Literal): Boolean = false

  def removeLabel(label: String, lang: String) {}

  def removeLabel(label: Literal) {}

  def setComment(comment: String, lang: String) {}

  def addComment(comment: String, lang: String) {}

  def addComment(comment: Literal) {}

  def getComment(lang: String): String = ""

  def listComments(lang: String): ExtendedIterator[RDFNode] = null

  def hasComment(comment: String, lang: String): Boolean = false

  def hasComment(comment: Literal): Boolean = false

  def removeComment(comment: String, lang: String) {}

  def removeComment(comment: Literal) {}

  def setRDFType(cls: Resource) {}

  def addRDFType(cls: Resource) {}

  def getRDFType: Resource = null

  def getRDFType(direct: Boolean): Resource = null

  def listRDFTypes(direct: Boolean): ExtendedIterator[Resource] = null

  def hasRDFType(ontClass: Resource, direct: Boolean): Boolean = false

  def hasRDFType(ontClass: Resource): Boolean = false

  def removeRDFType(cls: Resource) {}

  def hasRDFType(uri: String): Boolean = false

  def getCardinality(p: Property): Int = 0

  def setPropertyValue(property: Property, value: RDFNode) {}

  def getPropertyValue(property: Property): RDFNode = null

  def listPropertyValues(property: Property): NodeIterator = null

  def removeProperty(property: Property, value: RDFNode) {}

  def remove() {}

  def asProperty(): OntProperty = null

  def asAnnotationProperty(): AnnotationProperty = null

  def asObjectProperty(): ObjectProperty = null

  def asDatatypeProperty(): DatatypeProperty = null

  def asIndividual(): Individual = null

  def asClass(): OntClass = null

  def asOntology(): Ontology = null

  def asDataRange(): DataRange = null

  def asAllDifferent(): AllDifferent = null

  def isProperty: Boolean = false

  def isAnnotationProperty: Boolean = false

  def isObjectProperty: Boolean = false

  def isDatatypeProperty: Boolean = false

  def isIndividual: Boolean = false

  def isClass: Boolean = false

  def isOntology: Boolean = false

  def isDataRange: Boolean = false

  def isAllDifferent: Boolean = false

  def getModel: Model = null

  def visitWith(rv: RDFVisitor): AnyRef = null

  def asResource(): Resource = null

  def asLiteral(): Literal = null

  def setOntClass(cls: Resource) {}

  def addOntClass(cls: Resource) {}

  def getOntClass: OntClass = null

  def getOntClass(direct: Boolean): OntClass = null

  def listOntClasses[T <: OntClass](direct: Boolean): ExtendedIterator[T] = null

  def hasOntClass(ontClass: Resource, direct: Boolean): Boolean = false

  def hasOntClass(ontClass: Resource): Boolean = false

  def hasOntClass(uri: String): Boolean = false

  def removeOntClass(ontClass: Resource) {}
}