package org.semachina.jena

import com.hp.hpl.jena.util.{iterator => jena}

import features.larq3.Larq3Feature
import features.pellet.PelletFeature
import impl._
import impl.SemachinaOntClassImpl
import java.{lang => jl, util => ju}
import com.hp.hpl.jena.ontology._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query._
import scala.collection.JavaConversions._
import com.hp.hpl.jena.datatypes.{DatatypeFormatException, RDFDatatype, TypeMapper}
import wrapper._
import org.semachina.jena.config.OWLFactory._
import com.hp.hpl.jena.enhanced.{EnhNode, BuiltinPersonalities}

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Jun 29, 2010
 * Time: 8:37:05 AM
 * To change this template use File | Settings | File Templates.
 */


object JenaExtension {

  /**
   * Implicitly converts a Jena <code>ExtendedIteratorWrapper</code> to a Scala <code>ExtendedIteratorWrapper</code>.
   * The returned Scala <code>Iterator</code> is backed by the provided Java
   * <code>Iterator</code> and any side-effects of using it via the Scala interface will
   * be visible via the Java interface and vice versa.
   * <PROP>
   * If the Java <code>Iterator</code> was previously obtained from an implicit or
   * explicit call of <code>asIterator(scala.collection.Iterator)</code> then the original
   * Scala <code>Iterator</code> will be returned.
   *
   * @param i The <code>Iterator</code> to be converted.
   * @return A Scala <code>Iterator</code> view of the argument.
   */
  implicit def extendedIterator2Wrapper[A](i: jena.ExtendedIterator[A]): ExtendedIteratorWrapper[A] = ExtendedIteratorWrapper(i)

  implicit def extendedWrapper2Iterator[A](wrapper: ExtendedIteratorWrapper[A]): jena.ExtendedIterator[A] = wrapper.i

  implicit def toSemachinaOntModel(ontModel:OntModel) = ontModel.asInstanceOf[SemachinaOntModel]

  implicit def toSemachinaIndividual(indiv:Individual) = indiv.asInstanceOf[SemachinaIndividual]

  implicit def toScalaIndividual(i: Individual) = new {
    val indiv = i.asInstanceOf[SemachinaIndividual]

    def / (ontProperty : ObjectProperty) = new ObjectValueWrapper( indiv, ontProperty )
    def / (ontProperty : ResourceProperty) = new ResourceValueWrapper( indiv, ontProperty )
    def /[V](ontProperty : TypedDatatypeProperty[V]) = new DataValueWrapper[V]( indiv, ontProperty )
  }

  implicit def toScalaOntModel(ontModel: SemachinaOntModel) = new {
    val model = ontModel

    def select(query: Query, resultHandler: (ResultSet, QuerySolution) => Unit, initialBindings: QuerySolution) : Unit = {
      val resultSetHandler : ResultSetHandler = prepareHandler( resultHandler )
      model.select(query, resultSetHandler, initialBindings)
    }

    def select(sparql: String, resultHandler: (ResultSet, QuerySolution) => Unit, initialBindings: QuerySolution) : Unit = {
      val resultSetHandler : ResultSetHandler = prepareHandler( resultHandler )
      model.select(sparql, resultSetHandler, initialBindings)
    }

    def doRead(closure: SemachinaOntModel => Unit ) = {
      val command = new SimpleReadWriteContext() {
        def execute(transactionModel: SemachinaOntModel) = closure( transactionModel )
      }

      model.read( command )
    }

    def doWrite(closure: SemachinaOntModel => Unit ) = {
      val command = new SimpleReadWriteContext() {
        def execute(transactionModel: SemachinaOntModel) = closure( transactionModel )
      }

      model.write( command )
    }

    protected def prepareHandler(resultHandler: (ResultSet, QuerySolution) => Unit): ResultSetHandler = {
      if( resultHandler == null ) {
        throw new IllegalArgumentException( "resultHandler cannot be null" )
      }

      val handler = new ResultSetHandler {
        def handle(rs: ResultSet): Unit = {
          rs.foreach {soln => resultHandler(rs, soln)}
        }
      }
      return handler
    }
  }
  implicit def toScalaOntClassImpl(ontClass: OntClass): SemachinaOntClass = ontClass.asInstanceOf[SemachinaOntClass]

  implicit def toScalaLiteral(rdfNode: RDFNode): Literal = rdfNode.asLiteral

  implicit def toQuerySolution(map: Map[String, RDFNode]): QuerySolution = {
    var initialBindings: QuerySolutionMap = new QuerySolutionMap
    map.foreach {kv => initialBindings.add(kv._1, kv._2)}
    return initialBindings
  }

  implicit def toURIWrapper(uri:String)(implicit ontModel: SemachinaOntModel) = new {

    def withTypes( clazzesStr : String* ) : Tuple2[String, Iterable[OntClass]] = {
      val clazzes = clazzesStr.collect{ case clazz:String => ontModel.expandToOntClass( clazz ) }
      ( uri -> clazzes )
    }

    def obj = ontModel.expandToOntProperty(uri).asObjectProperty

    def res = ontModel.expandToOntProperty(uri).as(classOf[ResourceProperty])

    def data : TypedDatatypeProperty[String]  = {
      val ontProperty = ontModel.expandToDatatypeProperty(uri)
      new ScalaDatatypePropertyImpl[String](ontProperty.asInstanceOf[EnhNode], { it:Literal => it.getString })
    }

    def data[V](convert : Literal => V ) = {
      val ontProperty = ontModel.expandToDatatypeProperty(uri)
      new ScalaDatatypePropertyImpl[V](ontProperty.asInstanceOf[EnhNode], convert )
    }

    def @: = ontModel.getFactory.createLiteral(uri, "")
    def @: (lang:String) = ontModel.getFactory.createLiteral(uri, lang)

    def toQuery(): Query = {
      var query: Query = new Query
      query.getPrefixMapping.withDefaultMappings(ontModel)
      query = QueryFactory.parse(query, uri, null, Syntax.defaultSyntax)
      return query
    }
  }

  implicit def toURIIteratorWrapper(uris:Iterable[String])(implicit ontModel: SemachinaOntModel) = new {

    def obj = uris.collect { case uri : String => ontModel.expandToOntProperty(uri).asObjectProperty }

    def res = uris.collect { case uri : String => ontModel.expandToOntProperty(uri).as(classOf[ResourceProperty]) }

    def data = uris.collect { case uri : String =>
      val ontProperty = ontModel.expandToDatatypeProperty(uri)
      new ScalaDatatypePropertyImpl[Literal](ontProperty.asInstanceOf[EnhNode], { it:Literal => it } )
    }

    def data[V](convert : Literal => V ) = {
      uris.collect { case uri : String =>
        val ontProperty = ontModel.expandToDatatypeProperty(uri)
        new ScalaDatatypePropertyImpl[V](ontProperty.asInstanceOf[EnhNode], convert )
      }
    }
  }

//  implicit def toValueWrapper(value:Any)(implicit ontModel: OntModel) = new ValueWrapper( value, ontModel )
  implicit def toValueWrapper(value:Any)(implicit ontModel: SemachinaOntModel) = new {
    val factory = ontModel.getFactory
    def ^( dtype: String ) = factory.createTypedLiteral( value, dtype )
    def ^( dtype: RDFDatatype ) = factory.createTypedLiteral( value, dtype )
    def ^% = factory.parseTypedLiteral( value.toString )
  }

  implicit def toLarq3Feature(implicit ontModel: SemachinaOntModel) =
    ontModel.getFeature[Larq3Feature]( Larq3Feature.KEY )

  implicit def toPelletFeature(implicit ontModel: SemachinaOntModel) =
    ontModel.getFeature[PelletFeature]( PelletFeature.KEY )

  def addAltEntry(docURI: String, locationURL: String): Unit =
    OntDocumentManager.getInstance.addAltEntry(docURI, locationURL)

  //get class
  def $ (uri : String )(implicit ontModel: SemachinaOntModel) = ontModel.expandToOntClass( uri )
  def $ (uris : String* )(implicit ontModel: SemachinaOntModel) =
    uris.collect { case uri : String => ontModel.expandToOntClass(uri) }

  //get object
  def & (node:RDFNode): SemachinaIndividual = {
    if( node == null) {
      return None.asInstanceOf[SemachinaIndividual]
    }
    return node.as(classOf[Individual]).asInstanceOf[SemachinaIndividual]
  }
  def & (uri: String)(implicit ontModel: SemachinaOntModel) = ontModel.expandToIndividual( uri )
  def & (uris: String*)(implicit ontModel:SemachinaOntModel)=
    uris.collect { case uri : String => ontModel.expandToIndividual(uri) }

  //create object
  def & (uriAndClazzes : Tuple2[String, Iterable[OntClass]])(implicit ontModel: SemachinaOntModel) =
    ontModel.create( uriAndClazzes._1, asIterable( uriAndClazzes._2 ) )

  //createIndividual( uriAndClazzes )( ontModel )

  def as[V](node:RDFNode, convert : (Literal => V) = { lit:Literal => lit.getValue.asInstanceOf[V] } ) : V = {
    if( node == null) {
      return None.asInstanceOf[V]
    }
    return convert( node.asLiteral )
  }
}