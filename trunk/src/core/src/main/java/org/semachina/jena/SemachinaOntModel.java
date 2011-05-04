package org.semachina.jena;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import org.semachina.jena.features.Feature;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 8:49:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SemachinaOntModel extends OntModel {

    SemachinaIndividual createIndividual(String newUri, Iterable<OntClass> clazzes);

    SemachinaIndividual createIndividual(String newUri, Iterable<OntClass> clazzes, boolean isUnique);

    SemachinaIndividual createIndividual(String uri, OntClass cls);

    SemachinaIndividual createIndividual(String uri, OntClass cls, boolean isUnique);

    String expandURI(String property);

    OntClass resolveOntClass(String uri);

    Collection<OntClass> resolveOntClasses(Collection<String> uris);

    Individual resolveIndividual(String uri);

    Property resolveProperty(String property);

    ObjectProperty resolveObjectProperty(String property);

    DatatypeProperty resolveDatatypeProperty(String property);

    OntProperty resolveOntProperty(String property);

    Literal parseTypedLiteral(String literalString);

    RDFDatatype resolveRDFDatatype(String typeURI) throws DatatypeFormatException;

    void safeRead(final ReadWriteContext command) throws Exception;

    void safeWrite(final ReadWriteContext command) throws Exception;

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
