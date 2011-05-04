package org.semachina.jena.impl;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.impl.IndividualImpl;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.path.PathEval;
import com.hp.hpl.jena.sparql.path.PathParser;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Map1;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import org.semachina.jena.SemachinaIndividual;
import org.semachina.jena.SemachinaOntModel;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 28, 2010
 * Time: 10:38:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemachinaIndividualImpl extends IndividualImpl implements SemachinaIndividual {

    /**
     * A Mapper to convert <oode>Statement</code> to <code>SemachinaIndividual</code>
     */
    protected static Map1<Statement, SemachinaIndividual> stmtToObjectConverter =
            new Map1<Statement, SemachinaIndividual>() {
                public SemachinaIndividual map1(Statement x) {
                    return (SemachinaIndividual) x.getObject().as(Individual.class);
                }
    };

    /**
     * A Mapper to convert <oode>Statement</code> to <code>Literal</code>
     */
    protected static Map1<Statement, Literal> stmtToLiteralConverter = new Map1<Statement, Literal>() {
        public Literal map1(Statement x) {
            return x.getLiteral();
        }
    };

    /**
     * A Mapper to convert <oode>RDFNode</code> to <code>SemachinaIndividual</code>
     */
    protected static Map1<RDFNode, SemachinaIndividual> nodeToObjectConverter =
            new Map1<RDFNode, SemachinaIndividual>() {
                public SemachinaIndividual map1(RDFNode x) {
                    return (SemachinaIndividual) x.as(Individual.class);
                }
    };


    /**
     * A Mapper to convert <oode>RDFNode</code> to <code>Literal</code>
     */
    protected static Map1<RDFNode, Literal> nodeToLiteralConverter = new Map1<RDFNode, Literal>() {
        public Literal map1(RDFNode x) {
            return x.asLiteral();
        }
    };

    public SemachinaIndividualImpl(Individual indiv) {
        super(indiv.asNode(), ( (EnhNode) indiv).getGraph() );
    }

    public SemachinaIndividualImpl(Node n, EnhGraph g) {
        super(n, g);
    }

    @Override
    public SemachinaOntModel getOntModel() {
        return (SemachinaOntModel) super.getOntModel();
    }

    @Override
    public NodeIterator path(String propPath) {
        Path path = PathParser.parse(propPath, (PrefixMapping) getOntModel() );
        return PathEval.walkForwards( getOntModel(), this, path );
    }

    @Override
    public SemachinaIndividual getObject(String propOrPath) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return getObject(ontProperty);
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return getObject( path );
    }

    @Override
    public SemachinaIndividual getObject(Path path) {
        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        try {
            if( iterator.hasNext() ) {
                return (SemachinaIndividual) iterator.next().as( Individual.class );
            }
        }
        finally {
            iterator.close();
        }
        return null;
    }

    @Override
    public SemachinaIndividual getObject(Property ontProperty) {
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
    public ExtendedIterator<SemachinaIndividual> listObjects(String propOrPath) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return listObjects(ontProperty);
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return listObjects( path );
    }

    @Override
    public ExtendedIterator<SemachinaIndividual> listObjects(Property ontProperty) {
        return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToObjectConverter));
    }

    @Override
    public ExtendedIterator<SemachinaIndividual> listObjects(Path path) {
        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        return WrappedIterator.create(iterator.mapWith(nodeToObjectConverter));
    }

    @Override
    public Literal getLiteral(String propOrPath) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return getLiteral(ontProperty);
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return getLiteral( path );
    }

    @Override
    public Literal getLiteral(Property ontProperty) {
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
    public Literal getLiteral(Path path) {
        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        try {
            if( iterator.hasNext() ) {
                return iterator.next().asLiteral();
            }
        }
        finally {
            iterator.close();
        }
        return null;
    }

    @Override
    public ExtendedIterator<Literal> listLiterals(String propOrPath) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return listLiterals(ontProperty);
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return listLiterals( path );
    }

    @Override
    public ExtendedIterator<Literal> listLiterals(Property ontProperty) {
        return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToLiteralConverter));
    }

    @Override
    public ExtendedIterator<Literal> listLiterals(Path path) {
        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        return WrappedIterator.create(iterator.mapWith(nodeToLiteralConverter));
    }


    @Override
    public <V> V getValue(String propOrPath, Class<V> clazz) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return getValue( ontProperty, clazz );
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return getValue( path, clazz );
    }

    @Override
    public <V> V getValue(Property ontProperty, Class<V> clazz) {
        RDFNode rdfNode = getPropertyValue(ontProperty);

        if (rdfNode == null) {
            return null;
        }
        else if (rdfNode.isLiteral()) {
            Object value = rdfNode.asLiteral().getValue();
            return clazz.cast( value );
        }

        throw new IllegalStateException(rdfNode + " is not a literal");
    }

    @Override
    public <V> V getValue(Path path, Class<V> clazz) {
        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        try {
            if( iterator.hasNext() ) {
                Object value = iterator.next().asLiteral().getValue();
                return clazz.cast( value );
            }
        }
        finally {
            iterator.close();
        }
        return null;
    }

    @Override
    public <V> V getValue(String propOrPath, Map1<Object,V> converter) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return getValue( ontProperty, converter );
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return getValue( path, converter );
    }

    @Override
    public <V> V getValue(Property ontProperty, Map1<Object,V> converter) {
        RDFNode rdfNode = getPropertyValue(ontProperty);

        if (rdfNode == null) {
            return null;
        }

        if (rdfNode.isLiteral()) {
            Object value = rdfNode.asLiteral().getValue();
            if( converter != null ) {
                return converter.map1(value);
            }
            return (V) value;
        }

        throw new IllegalStateException(rdfNode + " is not a literal");
    }

    @Override
    public <V> V getValue(Path path, Map1<Object,V> converter) {
        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        try {
            if( iterator.hasNext() ) {
                Object value = iterator.next().asLiteral().getValue();
                return converter.map1( value );
            }
        }
        finally {
            iterator.close();
        }
        return null;
    }

    @Override
    public <V> ExtendedIterator<V> listValues(String propOrPath, Class<V> clazz) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return listValues( ontProperty, clazz );
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return listValues( path, clazz );
    }

    @Override
    public <V> ExtendedIterator<V> listValues(Property ontProperty, final Class<V> clazz) {

        Map1<Statement, V> stmtToValueConverter = new Map1<Statement, V>() {
            public V map1(Statement x) {
                Object value =  x.getLiteral().getValue();
                return clazz.cast( value );
            }
        };

        return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToValueConverter));
    }

    @Override
    public <V> ExtendedIterator<V> listValues(Path path, final Class<V> clazz) {
        Map1<RDFNode, V> nodeToValueConverter = new Map1<RDFNode, V>() {
            public V map1(RDFNode x) {
                Object value =  x.asLiteral().getValue();
                return clazz.cast( value );
            }
        };

        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        return WrappedIterator.create(iterator.mapWith(nodeToValueConverter));
    }

    @Override
    public <V> ExtendedIterator<V> listValues(String propOrPath, Map1<Object,V> converter) {
        Property ontProperty = getOntModel().resolveProperty(propOrPath);

        if (ontProperty != null) {
          return listValues( ontProperty, converter );
        }

        Path path = PathParser.parse(propOrPath, (PrefixMapping) getOntModel() );
        return listValues( path, converter );
    }

    @Override
    public <V> ExtendedIterator<V> listValues(Property ontProperty, final Map1<Object, V> converter) {

        Map1<Statement, V> stmtToValueConverter = new Map1<Statement, V>() {
            public V map1(Statement x) {
                Object value =  x.getLiteral().getValue();
                if( converter != null ) {
                    return converter.map1( value );
                }
                return (V) value;
            }
        };

        return WrappedIterator.create(listProperties(ontProperty).mapWith(stmtToValueConverter));
    }

    @Override
    public <V> ExtendedIterator<V> listValues(Path path, final Map1<Object, V> converter) {
        Map1<RDFNode, V> nodeToValueConverter = new Map1<RDFNode, V>() {
            public V map1(RDFNode x) {
                Object value =  x.asLiteral().getValue();
                if( converter != null ) {
                    return converter.map1( value );
                }
                return (V) value;
            }
        };

        NodeIterator iterator = PathEval.walkForwards( getOntModel(), this, path );
        return WrappedIterator.create(iterator.mapWith(nodeToValueConverter));
    }

    @Override
    public SemachinaIndividual set(Property ontProperty, RDFNode value) {
        setPropertyValue(ontProperty, value);
        return this;
    }

    @Override
    public SemachinaIndividual setAll(Property ontProperty, Iterable<? extends RDFNode> values) {
        removeAll(ontProperty);
        if (values != null) {
            for (RDFNode value : values) {
                add(ontProperty, value);
            }
        }
        return this;
    }

    @Override
    public SemachinaIndividual add(Property ontProperty, RDFNode value) {
        addProperty(ontProperty, value);
        return this;
    }

    @Override
    public SemachinaIndividual addAll(Property ontProperty, Iterable<? extends RDFNode> values) {
        if (values != null) {
            for (RDFNode value : values) {
                add(ontProperty, value);
            }
        }
        return this;
    }

    @Override
    public SemachinaIndividual remove(Property ontProperty, RDFNode value) {
        removeProperty(ontProperty, value);
        return this;
    }

    @Override
    public SemachinaIndividual removeThese(Property ontProperty, Iterable<? extends RDFNode> values) {
        if (values != null) {
            for (RDFNode value : values) {
                remove(ontProperty, value);
            }
        }
        return this;
    }
}