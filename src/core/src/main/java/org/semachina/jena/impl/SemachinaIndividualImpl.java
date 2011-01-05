package org.semachina.jena.impl;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import org.semachina.jena.SemachinaIndividual;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 28, 2010
 * Time: 10:38:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemachinaIndividualImpl extends IndividualImpl implements SemachinaIndividual {

    public static Map1<Statement, SemachinaIndividual> stmtToObjectConverter = new Map1<Statement, SemachinaIndividual>() {
        public SemachinaIndividual map1(Statement x) {
            return (SemachinaIndividual) x.getObject().as(Individual.class);
        }
    };

    public static Map1<Statement, Literal> stmtToLiteralConverter = new Map1<Statement, Literal>() {
        public Literal map1(Statement x) {
            return x.getLiteral();
        }
    };

    public SemachinaIndividualImpl(Node n, EnhGraph g) {
        super(n, g);
    }

    @Override
    public SemachinaIndividual getObject(OntProperty ontProperty) {
        RDFNode rdfNode = getPropertyValue(ontProperty);

        if (rdfNode == null) {
            return null;
        }

        if (rdfNode.isResource()) {
            return (SemachinaIndividual) rdfNode.as(Individual.class);
        }

        throw new IllegalStateException(rdfNode + " is not an object");
    }

    @Override
    public ExtendedIterator<SemachinaIndividual> listObjects(OntProperty ontProperty) {
        return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToObjectConverter));
    }

    @Override
    public Literal getLiteral(OntProperty ontProperty) {
        RDFNode rdfNode = getPropertyValue(ontProperty);

        if (rdfNode == null) {
            return null;
        }

        if (rdfNode.isLiteral()) {
            return rdfNode.asLiteral();
        }

        throw new IllegalStateException(rdfNode + " is not a literal");
    }

    @Override
    public ExtendedIterator<Literal> listLiterals(OntProperty ontProperty) {
        return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToLiteralConverter));
    }


    @Override
    public <V> V getValue(OntProperty ontProperty) {
        RDFNode rdfNode = getPropertyValue(ontProperty);

        if (rdfNode == null) {
            return null;
        }

        if (rdfNode.isLiteral()) {
            return (V) rdfNode.asLiteral().getValue();
        }

        throw new IllegalStateException(rdfNode + " is not a literal");
    }

    @Override
    public <V> ExtendedIterator<V> listValues(OntProperty ontProperty) {

        Map1<Statement, V> stmtToValueConverter = new Map1<Statement, V>() {
            public V map1(Statement x) {
                return (V) x.getLiteral().getValue();
            }
        };

        return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToValueConverter));
    }

    @Override
    public SemachinaIndividual set(OntProperty ontProperty, RDFNode value) {
        if (ontProperty.isDatatypeProperty() && !value.isLiteral()) {
            throw new IllegalArgumentException();
        }

        if (ontProperty.isObjectProperty() && !value.isResource()) {
            throw new IllegalArgumentException();
        }

        setPropertyValue(ontProperty, value);
        return this;
    }

    @Override
    public SemachinaIndividual setAll(OntProperty ontProperty, Iterable<? extends RDFNode> values) {
        removeAll(ontProperty);
        if (values != null) {
            for (RDFNode value : values) {
                add(ontProperty, value);
            }
        }
        return this;
    }

    @Override
    public SemachinaIndividual add(OntProperty ontProperty, RDFNode value) {
        if (ontProperty.isDatatypeProperty() && !value.isLiteral()) {
            throw new IllegalArgumentException();
        }

        if (ontProperty.isObjectProperty() && !value.isResource()) {
            throw new IllegalArgumentException();
        }
        addProperty(ontProperty, value);
        return this;
    }

    @Override
    public SemachinaIndividual addAll(OntProperty ontProperty, Iterable<? extends RDFNode> values) {
        if (values != null) {
            for (RDFNode value : values) {
                add(ontProperty, value);
            }
        }
        return this;
    }

    @Override
    public SemachinaIndividual remove(OntProperty ontProperty, RDFNode value) {
        removeProperty(ontProperty, value);
        return this;
    }

    @Override
    public SemachinaIndividual removeThese(OntProperty ontProperty, Iterable<? extends RDFNode> values) {
        if (values != null) {
            for (RDFNode value : values) {
                remove(ontProperty, value);
            }
        }
        return this;
    }
}