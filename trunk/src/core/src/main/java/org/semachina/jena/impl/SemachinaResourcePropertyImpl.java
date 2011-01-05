package org.semachina.jena.impl;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.impl.OntPropertyImpl;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 8:33:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SemachinaResourcePropertyImpl extends OntPropertyImpl {

    public SemachinaResourcePropertyImpl(Node n, EnhGraph g) {
        super(n, g);
    }
}
