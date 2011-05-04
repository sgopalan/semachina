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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.MonitorModel;
import org.semachina.jena.*;
import org.semachina.jena.config.DefaultSemachinaFactory;
import org.semachina.jena.features.Feature;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 28, 2010
 * Time: 9:50:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SemachinaOntModelImpl extends OntModelImpl implements SemachinaOntModel {

    protected Map<String, Feature> features = null;

    protected SemachinaFactory factory;

    public SemachinaOntModelImpl(OntModelSpec spec, Model model) throws Exception {
        super(spec, model);
        factory = new DefaultSemachinaFactory();
    }

    public SemachinaOntModelImpl(OntModelSpec spec) throws Exception {
        super(spec);
        factory = new DefaultSemachinaFactory();
    }

    public void setFactory(SemachinaFactory factory) {
        this.factory = factory;
    }


    protected SemachinaOntModel getOntModel() {
        return this;
    }

    @Override
    public SemachinaIndividual createIndividual(String newURI, OntClass cls) {
        return createIndividual(newURI, cls, true);
    }

    @Override
    public SemachinaIndividual createIndividual(String newURI, OntClass cls, boolean isUnique) {
        String expandedURI = expandURI(newURI);

        if (isUnique) {
            Individual indiv = getIndividual(expandedURI);
            if (indiv != null) {
                throw new IllegalArgumentException("Individual " + expandedURI + " exists: " + indiv);
            }
        }

        return createOntResource(SemachinaIndividual.class, cls, expandedURI);
    }

    @Override
    public SemachinaIndividual createIndividual(String newURI, Iterable<OntClass> clazzes) {
        return createIndividual(newURI, clazzes, true);
    }

    @Override
    public SemachinaIndividual createIndividual(String newURI, Iterable<OntClass> clazzes, boolean isUnique) {
        String expandedURI = expandURI(newURI);
        SemachinaIndividual indiv = null;
        boolean first = true;
        for (Iterator<OntClass> i = clazzes.iterator(); i.hasNext();) {
            if (first) {
                first = false;
                indiv = createIndividual(newURI, i.next(), isUnique);
            }
            indiv.addRDFType(i.next());
        }
        return indiv;
    }

    @Override
    public Individual resolveIndividual(String uri) {
        String indivURI = expandURI(uri);
        return getIndividual(indivURI);
    }

    @Override
    public String expandURI(String uri) {
        String expandedURI = expandPrefix(uri);
        if (expandedURI == null) {
            throw new IllegalArgumentException("URI cannot be evaluated as null: " + uri);
        }
        return expandedURI;
    }

    @Override
    public OntClass resolveOntClass(String uri) {
        String expandedURI = expandURI(uri);
        OntClass ontClass = getOntClass(expandedURI);
        return ontClass;
    }

    @Override
    public Collection<OntClass> resolveOntClasses(Collection<String> uris) {
        Collection<OntClass> clazzes = new ArrayList<OntClass>();
        for (Iterator<String> i = uris.iterator(); i.hasNext();) {
            clazzes.add(resolveOntClass(i.next()));
        }

        return clazzes;
    }

    @Override
    public ObjectProperty resolveObjectProperty(String property) {
        String uri = expandURI(property);
        ObjectProperty ontProperty = getObjectProperty(uri);
        return ontProperty;
    }

    @Override
    public DatatypeProperty resolveDatatypeProperty(String property) {
        String uri = expandURI(property);
        DatatypeProperty ontProperty = getDatatypeProperty(uri);
        return ontProperty;
    }

    @Override
    public Property resolveProperty(String property) {
        String uri = expandURI(property);
        //DO NOT USE getProperty().  This will create a property regardless if it exists or not.
        //This method will only return a property if it exists in the model
        Property ontProperty = (Property) findByURIAs(uri, Property.class);
        return ontProperty;
    }

    @Override
    public OntProperty resolveOntProperty(String property) {
        String uri = expandURI(property);
        OntProperty ontProperty = getOntProperty(uri);
        return ontProperty;
    }

    @Override
    public RDFDatatype resolveRDFDatatype(String typeURI) throws DatatypeFormatException {
        String expanded = expandURI(typeURI);
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
        String expandedURI = expandURI(typeURI);
        return createTypedLiteral(value, expandedURI);
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


    @Override
    public void close() {
        if (features != null) {
            for (Feature feature : features.values()) {
                try {
                    feature.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        super.close();
    }

    public void safeRead(final ReadWriteContext command) throws Exception {
        com.hp.hpl.jena.shared.Command wrapped = new com.hp.hpl.jena.shared.Command() {
            @Override
            public Object execute() {
                SemachinaOntModel transactModel = getFactory().createOntologyModel();
                transactModel.addSubModel(getOntModel());

                try {
                    command.execute(transactModel);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return true;
            }
        };

        wrapped.execute();
    }

    public void safeWrite(final ReadWriteContext command) throws Exception {

        com.hp.hpl.jena.shared.Command wrapped = new com.hp.hpl.jena.shared.Command() {
            @Override
            public Object execute() {
                try {
                    MonitorModel monitorModel = new MonitorModel( getOntModel() );
                    List<Statement> additions = new ArrayList<Statement>();
                    List<Statement> deletions = new ArrayList<Statement>();

                    SemachinaOntModel transact = factory.createOntologyModel( OntModelSpec.OWL_MEM, monitorModel );
                    monitorModel.snapshot();
                    command.execute( transact );
                    monitorModel.snapshot( additions, deletions );

                    getOntModel().rebind();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return true;
            }
        };

        try {
            getOntModel().enterCriticalSection(Lock.WRITE);
            TransactionHandler handler = getOntModel().getGraph().getTransactionHandler();
            if (handler.transactionsSupported()) {
                handler.executeInTransaction(wrapped);
            } else {
                wrapped.execute();
            }
        } finally {
            getOntModel().leaveCriticalSection();
        }
    }

    public boolean ask(String sparql, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        return ask(query, initialBindings);
    }

    public boolean ask(Query query, QuerySolution initialBindings) {
        if (!query.isAskType()) {
            throw new IllegalArgumentException("Must be SPARQL ask query");
        }

        QueryExecution qexec = createQueryExecution(query, initialBindings);

        try {
            prepareQueryExecution(qexec);
            getOntModel().enterCriticalSection(Lock.READ);
            return qexec.execAsk();
        } finally {
            getOntModel().leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
    }

    public SemachinaOntModel describe(String sparql, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        return describe(query, initialBindings);
    }

    public SemachinaOntModel describe(Query query, QuerySolution initialBindings) {

        if (!query.isDescribeType()) {
            throw new IllegalArgumentException("Must be SPARQL describe query");
        }

        QueryExecution qexec = createQueryExecution(query, initialBindings);

        SemachinaOntModel owlModel = null;

        try {
            prepareQueryExecution(qexec);
            getOntModel().enterCriticalSection(Lock.READ);

            Model resultModel = qexec.execDescribe();
            owlModel = getFactory().createOntologyModel(getOntModel().getSpecification(), resultModel);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            getOntModel().leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
        return owlModel;
    }

    public SemachinaOntModel construct(String sparql, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        return construct(query, initialBindings);
    }


    public SemachinaOntModel construct(Query query, QuerySolution initialBindings) {

        if (!query.isConstructType()) {
            throw new IllegalArgumentException("Must be SPARQL construct query");
        }

        QueryExecution qexec = createQueryExecution(query, initialBindings);

        SemachinaOntModel owlModel = null;

        try {
            prepareQueryExecution(qexec);
            getOntModel().enterCriticalSection(Lock.READ);

            Model resultModel = qexec.execConstruct();
            owlModel = getFactory().createOntologyModel(getOntModel().getSpecification(), resultModel);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            getOntModel().leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
        return owlModel;
    }

    public void select(String sparql, ResultSetHandler handler, QuerySolution initialBindings) {
        Query query = createQuery(sparql);
        select(query, handler, initialBindings);
    }


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
            getOntModel().enterCriticalSection(Lock.READ);

            ResultSet rs = qexec.execSelect();

            handler.handle(rs);

        } finally {
            getOntModel().leaveCriticalSection();
            closeQueryExecution(qexec);
            qexec.close();
        }
    }

    protected QueryExecution createQueryExecution(Query query, QuerySolution initialBindings) {
        QueryExecution qexec = QueryExecutionFactory.create(query, getOntModel());

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
        query.getPrefixMapping().withDefaultMappings(getOntModel());
        query = QueryFactory.parse(query, sparql, null, Syntax.defaultSyntax);
        return query;
    }
}
