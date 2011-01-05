package org.semachina.jena.impl;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.shared.Lock;
import org.semachina.jena.*;
import org.semachina.jena.config.DefaultSemachinaFactory;
import org.semachina.jena.features.Feature;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 28, 2010
 * Time: 9:50:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemachinaOntModelImpl extends OntModelImpl implements SemachinaOntModel {

    protected Map<String, Feature> features = null;

    protected SemachinaFactory factory = new DefaultSemachinaFactory();

    public SemachinaOntModelImpl(OntModelSpec spec, Model model) {
        super(spec, model);
    }

    public SemachinaOntModelImpl(OntModelSpec spec) {
        super(spec);
    }

    public void setFactory(SemachinaFactory factory) {
        this.factory = factory;
    }


    public SemachinaIndividual createIndividual(OntClass cls) {
        return createOntResource(SemachinaIndividual.class, cls, null);
    }

    public SemachinaIndividual createIndividual(String uri, OntClass cls) {
        return createOntResource(SemachinaIndividual.class, cls, uri);
    }

    @Override
    public SemachinaIndividual createIndividual(String newURI, Iterable<OntClass> clazzes) {
        String expandedURI = expandPrefix(newURI);

        if (containsResource(getResource(expandedURI)) || clazzes.iterator() == null) {
            throw new IllegalArgumentException();
        }

        SemachinaIndividual indiv = null;

        for (Iterator<OntClass> i = clazzes.iterator(); i.hasNext();) {
            if (indiv == null) {
                indiv = createIndividual(expandedURI, i.next());
            }
            else {
                indiv.addRDFType(i.next());
            }
        }

        return indiv;
    }


    @Override
    public String expandURI(String property) {
        String uri = expandPrefix(property);
        if (uri == null) {
            throw new IllegalArgumentException("URI cannot be evaluated as null: " + property);
        }
        return uri;
    }

    @Override
    public OntClass expandToOntClass(String uri) {
        String expandedURI = expandPrefix(uri);

        OntClass ontClass = getOntClass(expandedURI);
        if (ontClass == null) {
            throw new IllegalArgumentException("There is no OntClass for: " + uri);
        }

        return ontClass;
    }

    @Override
    public Individual expandToIndividual(String uri) {
        String expandedURI = expandPrefix(uri);
        Individual indiv = getIndividual(expandedURI);
        if (indiv == null) {
            throw new IllegalArgumentException("There is no Individual for: " + uri);
        }

        return indiv;
    }

    @Override
    public ObjectProperty expandToObjectProperty(String property) {
        String uri = expandPrefix(property);
        ObjectProperty ontProperty = getObjectProperty(uri);
        if (ontProperty == null) {
            throw new IllegalArgumentException("There is no Property for: " + uri + " (" + uri + ") ");
        }

        return ontProperty;
    }

    @Override
    public DatatypeProperty expandToDatatypeProperty(String property) {
        String uri = expandPrefix(property);
        DatatypeProperty ontProperty = getDatatypeProperty(uri);
        if (ontProperty == null) {
            throw new IllegalArgumentException("There is no Property for: " + property + " (" + uri + ") ");
        }

        return ontProperty;
    }

    @Override
    public <T> TypedDatatypeProperty<T> expandToTypedDatatypeProperty(String property) {
        String uri = expandPrefix(property);
        DatatypeProperty ontProperty = getDatatypeProperty(uri);
        if (ontProperty == null) {
            throw new IllegalArgumentException("There is no Property for: " + property + " (" + uri + ") ");
        }

        return (TypedDatatypeProperty<T>) ontProperty;
    }

    @Override
    public ResourceProperty expandToResourceProperty(String property) {
        String uri = expandPrefix(property);
        ResourceProperty ontProperty = (ResourceProperty) findByURIAs(uri, ResourceProperty.class);
        if (ontProperty == null) {
            throw new IllegalArgumentException("There is no Property for: " + property + " (" + uri + ") ");
        }

        return ontProperty;
    }

    @Override
    public OntProperty expandToOntProperty(String property) {
        String uri = expandPrefix(property);
        OntProperty ontProperty = getOntProperty(uri);
        if (ontProperty == null) {
            throw new IllegalArgumentException("There is no Property for: " + property + " (" + uri + ") ");
        }

        return ontProperty;
    }

    @Override
    public RDFDatatype toRDFDatatype(String typeURI) throws DatatypeFormatException {
        String expanded = expandPrefix(typeURI);
        TypeMapper typeMapper = TypeMapper.getInstance();
        RDFDatatype dType = typeMapper.getTypeByName(expanded);
        if (dType == null) {
            StringBuilder message = new StringBuilder();
            message.append(typeURI + " does not match to a valid RDFDataype. Valid URIs include: ");
            for (Iterator<RDFDatatype> i = typeMapper.listTypes(); i.hasNext();) {
                RDFDatatype otherDType = i.next();
                message.append("\t" + otherDType.getURI() + " with class: " + otherDType.getJavaClass() + "\n");
            }

            throw new DatatypeFormatException(message.toString());
        }
        return dType;
    }

    @Override
    public Literal createTypedLiteral(Object value, String typeURI) {
        String expandedURI = expandPrefix(typeURI);
        return super.createTypedLiteral(value, expandedURI);
    }

    @Override
    public Literal parseTypedLiteral(String literalString) {
        String[] literalParts = literalString.split("\\^\\^");
        if (literalParts.length == 2) {
            String expandedURI = expandURI(literalParts[1]);
            RDFDatatype dtype = TypeMapper.getInstance().getSafeTypeByName(expandedURI);
            if (dtype != null) {
                return createTypedLiteral(literalParts[0], dtype);
            }
        }
        return new LiteralImpl(Node.createLiteral(literalString, "", false), null);
    }


    @Override
    public void read(final ReadWriteContext command) throws Exception {
        com.hp.hpl.jena.shared.Command wrapped = new com.hp.hpl.jena.shared.Command() {
            @Override
            public Object execute() {
                SemachinaOntModel transactModel = getFactory().createOntologyModel();
                transactModel.addSubModel(SemachinaOntModelImpl.this);


                try {
                    command.execute(transactModel);
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return true;
            }
        };

        wrapped.execute();
    }

    @Override
    public void write(final ReadWriteContext command) throws Exception {

        com.hp.hpl.jena.shared.Command wrapped = new com.hp.hpl.jena.shared.Command() {
            @Override
            public Object execute() {
                SemachinaOntModel transactModel = getFactory().createOntologyModel();
                transactModel.addSubModel(SemachinaOntModelImpl.this);


                try {
                    command.execute(transactModel);
                    removeSubModel(SemachinaOntModelImpl.this);
                    add(transactModel);

                    rebind();
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return true;
            }
        };

        try {
            enterCriticalSection(Lock.WRITE);
            TransactionHandler handler = getGraph().getTransactionHandler();
            if (handler.transactionsSupported()) {
                handler.executeInTransaction(wrapped);
            } else {
                wrapped.execute();
            }
        }
        finally {
            leaveCriticalSection();
        }
    }

    @Override
    public boolean ask(String sparql, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        return ask(query, initialBindings);
    }

    @Override
    public boolean ask(Query query, QuerySolution initialBindings) {
        if (!query.isAskType()) {
            throw new IllegalArgumentException("Must be SPARQL ask query");
        }

        QueryExecution qexec = createQueryExecution(query, initialBindings);

        try {
            prepareQueryExecution(qexec);
            enterCriticalSection(Lock.READ);
            return qexec.execAsk();
        }
        finally {
            leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
    }

    @Override
    public SemachinaOntModel describe(String sparql, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        return describe(query, initialBindings);
    }

    @Override
    public SemachinaOntModel describe(Query query, QuerySolution initialBindings) {

        if (!query.isDescribeType()) {
            throw new IllegalArgumentException("Must be SPARQL describe query");
        }

        QueryExecution qexec = createQueryExecution(query, initialBindings);

        SemachinaOntModel owlModel = null;

        try {
            prepareQueryExecution(qexec);
            enterCriticalSection(Lock.READ);

            Model resultModel = qexec.execDescribe();
            owlModel = new SemachinaOntModelImpl(getSpecification(), resultModel);
        }
        finally {
            leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
        return owlModel;
    }

    @Override
    public SemachinaOntModel construct(String sparql, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        return construct(query, initialBindings);
    }


    @Override
    public SemachinaOntModel construct(Query query, QuerySolution initialBindings) {

        if (!query.isConstructType()) {
            throw new IllegalArgumentException("Must be SPARQL construct query");
        }

        QueryExecution qexec = createQueryExecution(query, initialBindings);

        SemachinaOntModel owlModel = null;

        try {
            prepareQueryExecution(qexec);
            enterCriticalSection(Lock.READ);

            Model resultModel = qexec.execConstruct();
            owlModel = new SemachinaOntModelImpl(getSpecification(), resultModel);

        }
        finally {
            leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
        return owlModel;
    }

    @Override
    public void select(String sparql, ResultSetHandler handler, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        select(query, handler, initialBindings);
    }


    @Override
    public void select(Query query, ResultSetHandler handler, QuerySolution initialBindings) {

        if (handler == null) {
            return;
        }

        if (!query.isSelectType()) {
            throw new IllegalArgumentException("Must be SPARQL select query");
        }

        QueryExecution qexec = createQueryExecution(query, initialBindings);

        try {
            prepareQueryExecution(qexec);
            enterCriticalSection(Lock.READ);

            ResultSet rs = qexec.execSelect();

            handler.handle(rs);

        }
        finally {
            leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
    }

    public void addFeature(Feature feature) throws Exception {

        if (features == null) {
            features = new HashMap<String, Feature>();
        }

        String featureKey = feature.getKey();

        if (features.containsKey(featureKey)) {
            throw new IllegalArgumentException("key already exists: " + featureKey);
        }

        feature.init(this, factory);


        features.put(featureKey, feature);
    }

    public <T extends Feature> T getFeature(String featureKey) {
        if (features == null || !features.containsKey(featureKey)) {
            throw new IllegalArgumentException("feature not implemented: " + featureKey);
        }

        return (T) features.get(featureKey);
    }

    public SemachinaFactory getFactory() {
        return factory;
    }

    protected QueryExecution createQueryExecution(Query query, QuerySolution initialBindings) {
        QueryExecution qexec = QueryExecutionFactory.create(query, this);

        if (initialBindings != null) {
            qexec.setInitialBinding(initialBindings);
        }

        return qexec;
    }

    protected void prepareQueryExecution(QueryExecution qexec) {
    }

    protected void closeQueryExecution(QueryExecution queryExec) {
    }

    protected Query createQuery(String sparql) {
        Query query = new Query();
        query.getPrefixMapping().withDefaultMappings(this);
        query = QueryFactory.parse(query, sparql, null, Syntax.defaultSyntax);
        return query;
    }

    @Override
    public void close() {
        if (features != null) {
            for (Feature feature : features.values()) {
                try {
                    feature.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        super.close();
    }
}
