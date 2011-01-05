package org.semachina.jena.impl;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.impl.OntClassImpl;
import org.semachina.jena.SemachinaIndividual;
import org.semachina.jena.SemachinaOntClass;
import org.semachina.jena.SemachinaOntModel;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 28, 2010
 * Time: 10:15:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemachinaOntClassImpl extends OntClassImpl implements SemachinaOntClass {
//
//     public static Implementation FACTORY =
//            new DefaultImplementationImpl(
//                    SemachinaOntClassImpl.class, null, "Does not have rdf:type owl:Class or equivalent");

    public SemachinaOntClassImpl(Node n, EnhGraph g) {
        super(n, g);
    }

    public SemachinaOntClassImpl(OntClass clazz) {
        super(clazz.asNode(), (EnhGraph) clazz.getOntModel().getGraph());
    }

    @Override
    public SemachinaOntModel getOntModel() {
        return (SemachinaOntModel) super.getOntModel();
    }

    @Override
    public SemachinaIndividual createIndividual() {
        return getOntModel().createIndividual(this);
    }

    @Override
    public SemachinaIndividual createIndividual(String uri) {
        return getOntModel().createIndividual(uri, this);
    }

    @Override
    public SemachinaIndividual createUniqueIndividual(String uri) {
        String expandedURI = getOntModel().expandPrefix(uri);

        if (getOntModel().containsResource(getOntModel().getResource(expandedURI))) {
            throw new IllegalArgumentException();
        }

        return createIndividual(expandedURI);
    }
}
