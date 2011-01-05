package org.semachina.jena;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import org.semachina.jena.features.Feature;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 8:49:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SemachinaOntModel extends OntModel {

    String expandURI(String property);

    OntClass expandToOntClass(String uri);

    Individual expandToIndividual(String uri);

    ObjectProperty expandToObjectProperty(String property);

    DatatypeProperty expandToDatatypeProperty(String property);

    <T> TypedDatatypeProperty<T> expandToTypedDatatypeProperty(String property);

    ResourceProperty expandToResourceProperty(String property);

    OntProperty expandToOntProperty(String property);

    Literal parseTypedLiteral(String literalString);

    RDFDatatype toRDFDatatype(String typeURI) throws DatatypeFormatException;

    void read(final ReadWriteContext command) throws Exception;

    void write(final ReadWriteContext command) throws Exception;

    SemachinaIndividual createIndividual(String newUri, Iterable<OntClass> clazzes);

    SemachinaIndividual createIndividual(OntClass cls);

    SemachinaIndividual createIndividual(String uri, OntClass cls);

    boolean ask(String sparql, QuerySolution initialBindings);

    boolean ask(Query query, QuerySolution initialBindings);

    SemachinaOntModel describe(String sparql, QuerySolution initialBindings);

    SemachinaOntModel describe(Query query, QuerySolution initialBindings);

    SemachinaOntModel construct(String sparql, QuerySolution initialBindings);

    SemachinaOntModel construct(Query query, QuerySolution initialBindings);

    void select(String sparql, ResultSetHandler handler, QuerySolution initialBindings);

    void select(Query query, ResultSetHandler handler, QuerySolution initialBindings);

    void addFeature(Feature feature) throws Exception;

    <T extends Feature> T getFeature(String featureKey);

    org.semachina.jena.SemachinaFactory getFactory();
}
