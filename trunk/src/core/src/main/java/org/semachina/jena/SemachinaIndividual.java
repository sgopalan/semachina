package org.semachina.jena;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 8:44:25 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SemachinaIndividual extends Individual {

    SemachinaIndividual getObject(OntProperty ontProperty);

    ExtendedIterator<SemachinaIndividual> listObjects(OntProperty ontProperty);

    Literal getLiteral(OntProperty ontProperty);

    ExtendedIterator<Literal> listLiterals(OntProperty ontProperty);

    <V> V getValue(OntProperty ontProperty);

    <V> ExtendedIterator<V> listValues(OntProperty ontProperty);

    SemachinaIndividual set(OntProperty ontProperty, RDFNode value);

    SemachinaIndividual setAll(OntProperty ontProperty, Iterable<? extends RDFNode> values);

    SemachinaIndividual add(OntProperty ontProperty, RDFNode value);

    SemachinaIndividual addAll(OntProperty ontProperty, Iterable<? extends RDFNode> values);

    SemachinaIndividual remove(OntProperty ontProperty, RDFNode value);

    SemachinaIndividual removeThese(OntProperty ontProperty, Iterable<? extends RDFNode> values);
}
