package org.semachina.jena;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 8:44:25 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SemachinaIndividual extends Individual {

    SemachinaIndividual getObject(String propOrPath);

    SemachinaIndividual getObject(Property ontProperty);

    SemachinaIndividual getObject(Path path);

    ExtendedIterator<SemachinaIndividual> listObjects(String propOrPath);

    ExtendedIterator<SemachinaIndividual> listObjects(Property ontProperty);

    ExtendedIterator<SemachinaIndividual> listObjects(Path path);

    Literal getLiteral(String propOrPath);

    Literal getLiteral(Property ontProperty);

    Literal getLiteral(Path path);

    ExtendedIterator<Literal> listLiterals(String propOrPath);

    ExtendedIterator<Literal> listLiterals(Property ontProperty);

    ExtendedIterator<Literal> listLiterals(Path path);

    <V> V getValue(String propOrPath, Class<V> clazz);

    <V> V getValue(Property ontProperty, Class<V> clazz);

    <V> V getValue(Path path, Class<V> clazz);

    <V> V getValue(String propOrPath, Map1<Object,V> converter);

    <V> V getValue(Property ontProperty, Map1<Object,V> converter);

    <V> V getValue(Path path, Map1<Object,V> converter);

    <V> ExtendedIterator<V> listValues(String propOrPath, Class<V> clazz);

    <V> ExtendedIterator<V> listValues(Property ontProperty, Class<V> clazz);

    <V> ExtendedIterator<V> listValues(Path path, Class<V> clazz);

    <V> ExtendedIterator<V> listValues(String propOrPath, Map1<Object,V> converter);

    <V> ExtendedIterator<V> listValues(Property ontProperty, Map1<Object,V> converter);

    <V> ExtendedIterator<V> listValues(Path path, Map1<Object,V> converter);

    NodeIterator path(String propPath);

    SemachinaIndividual set(Property ontProperty, RDFNode value);

    SemachinaIndividual setAll(Property ontProperty, Iterable<? extends RDFNode> values);

    SemachinaIndividual add(Property ontProperty, RDFNode value);

    SemachinaIndividual addAll(Property ontProperty, Iterable<? extends RDFNode> values);

    SemachinaIndividual remove(Property ontProperty, RDFNode value);

    SemachinaIndividual removeThese(Property ontProperty, Iterable<? extends RDFNode> values);
}
