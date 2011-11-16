package org.semachina.jena.config.impl

import org.semachina.jena.config.{SemachinaConfiguration, SemachinaProfile}
import org.semachina.jena.ontology.impl.SemachinaIndividualImpl
import com.hp.hpl.jena.graph.Node
import com.hp.hpl.jena.enhanced.EnhGraph
import com.hp.hpl.jena.ontology.Individual
import org.semachina.jena.datatype.xsd._
import com.hp.hpl.jena.vocabulary.{RDFS, RDF}
import org.semachina.jena.enhanced.SemachinaImplementation

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 10/26/11
 * Time: 8:34 AM
 * To change this template use File | Settings | File Templates.
 */

object DefaultSemachinaProfile extends SemachinaProfile {
  def apply(configuration: SemachinaConfiguration): Unit = {

    //register Implementation -->  SemachinaIndividualImpl
    configuration
      .registerImplementation(
      SemachinaImplementation[SemachinaIndividualImpl](
        (node: Node, eg: EnhGraph) => new SemachinaIndividualImpl(node, eg, None),
        "",
        classOf[Individual])
    )

      //register new data types
      .registerDatatype(new DateTimeDatatype)
      .registerDatatype(new DateDatatype)
      .registerDatatype(new DayDatatype)
      .registerDatatype(new DurationDatatype)
      .registerDatatype(new MonthDayDatatype)
      .registerDatatype(new TimeDatatype)
      .registerDatatype(new YearDatatype)
      .registerDatatype(new YearMonthDatatype)

      //register rdf and rdfs (in case we reference it)
      .addAltEntry(
      "http://www.w3.org/1999/02/22-rdf-syntax-ns" -> getClass.getResource("/vocabularies/rdf-schema.rdf").toString)

      .setNsPrefix("rdf" -> RDF.getURI)

      .addAltEntry(
      "http://www.w3.org/1999/02/22-rdf-syntax-ns" -> getClass.getResource("/vocabularies/rdf-schema.rdf").toString)

      .setNsPrefix("rdfs" -> RDFS.getURI)
  }
}