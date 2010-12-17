package org.semachina.jena.impl;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Node;
import org.semachina.jena.TypedDatatypeProperty;
import com.hp.hpl.jena.ontology.impl.DatatypePropertyImpl;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 8:33:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SemachinaTypedDatatypePropertyImpl<V> extends DatatypePropertyImpl implements TypedDatatypeProperty<V> {

    public SemachinaTypedDatatypePropertyImpl(Node n, EnhGraph g) {
        super(n, g);
    }
}
