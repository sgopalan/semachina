package org.semachina.jena.config;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.Personality;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.JenaParameters;
import org.semachina.jena.*;
import org.semachina.jena.datatype.factory.*;
import org.semachina.jena.impl.*;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 30, 2010
 * Time: 8:50:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultSemachinaFactory implements SemachinaFactory {

    private TypeMapper typeMapper = TypeMapper.getInstance();

    private PrefixMapping prefixMapping;

    private OntDocumentManager docManager;

    private Personality<RDFNode> model;

    public DefaultSemachinaFactory() throws Exception {

        //static settings
        JenaParameters.enableEagerLiteralValidation = true;
        JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = false;

        //configuration references
        this.prefixMapping = PrefixMapping.Factory.create();
        this.prefixMapping.setNsPrefixes(PrefixMapping.Standard);
        this.docManager = OntDocumentManager.getInstance();
        this.model = BuiltinPersonalities.model;

        //install Semachina classes and datatypes
        initImplementationClasses();
        initDatatypes();
    }

    public void initImplementationClasses() {
        model.add(Individual.class,
                new DefaultImplementationImpl<SemachinaIndividualImpl>(SemachinaIndividualImpl.class, Individual.class) {
                    @Override
                    protected SemachinaIndividualImpl create(Node node, EnhGraph eg) {
                        return new SemachinaIndividualImpl(node, eg);
                    }
                });
        model.add(SemachinaIndividual.class,
                new DefaultImplementationImpl<SemachinaIndividualImpl>(SemachinaIndividualImpl.class, Individual.class) {
                    @Override
                    protected SemachinaIndividualImpl create(Node node, EnhGraph eg) {
                        return new SemachinaIndividualImpl(node, eg);
                    }
                });
        model.add(OntClass.class,
                new DefaultImplementationImpl<SemachinaOntClassImpl>(SemachinaOntClassImpl.class,
                        "It does not have rdf:type owl:Class or equivalent", OntClass.class) {
                    @Override
                    protected SemachinaOntClassImpl create(Node node, EnhGraph eg) {
                        return new SemachinaOntClassImpl(node, eg);
                    }
                });
        model.add(SemachinaOntClass.class,
                new DefaultImplementationImpl<SemachinaOntClassImpl>(SemachinaOntClassImpl.class,
                        "It does not have rdf:type owl:Class or equivalent", OntClass.class) {
                    @Override
                    protected SemachinaOntClassImpl create(Node node, EnhGraph eg) {
                        return new SemachinaOntClassImpl(node, eg);
                    }
                });
    }

    public void initDatatypes() throws Exception {
        typeMapper.registerDatatype(new DateTimeFactory());
        typeMapper.registerDatatype(new DateFactory());
        typeMapper.registerDatatype(new DayFactory());
        typeMapper.registerDatatype(new DurationFactory());
        typeMapper.registerDatatype(new MonthDayFactory());
        typeMapper.registerDatatype(new TimeFactory());
        typeMapper.registerDatatype(new YearFactory());
        typeMapper.registerDatatype(new YearMonthFactory());
    }


    @Override
    public TypeMapper getTypeMapper() {
        return typeMapper;
    }

    @Override
    public PrefixMapping getPrefixMapping() {
        return prefixMapping;
    }

    @Override
    public OntDocumentManager getDocManager() {
        return docManager;
    }


    /**
     * <PROP>
     * Answer a new ontology model which will process in-memory models of
     * ontologies expressed the default ontology language (OWL).
     * The default document manager
     * will be used to load the ontology's included documents.
     * </PROP>
     * <PROP><strong>Note:</strong>The default model chosen for OWL, RDFS and DAML+OIL
     * includes a weak reasoner that includes some entailments (such to
     * transitive closure on the sub-class and sub-property hierarchies). Users
     * who want either no inference at all, or alternatively
     * more complete reasoning, should use
     * one of the other <code>createOntologyModel</code> methods that allow the
     * preferred OntModel specification to be stated.</PROP>
     *
     * @return A new ontology model
     */
    @Override
    public SemachinaOntModel createOntologyModel() {
        try {
            return new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG));
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    @Override
    public SemachinaOntModel createOntologyModel(OntModelSpec ontModelSpec) {
        try {
            return new SemachinaOntModelImpl(ontModelSpec);
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    @Override
    public SemachinaOntModel createOntologyModel(Model base) {
        try {
            return new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG), base);
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    @Override
    public SemachinaOntModel createOntologyModel(OntModelSpec ontModelSpec, Model base) {
        try {
            return new SemachinaOntModelImpl(ontModelSpec, base);
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }


//  /**
//   * <PROP>
//   * Answer a new ontology model which will process in-memory models of
//   * ontologies expressed the default ontology language (OWL).
//   * The default document manager
//   * will be used to load the ontology's included documents.
//   * </PROP>
//   *
//   * @param spec An ontology model specification that defines the language and reasoner to use
//   * @param maker A model maker that is used to get the initial store for the ontology (unless
//   * the base model is given),
//   * and createIndividual addtional stores for the models in the imports closure
//   * @param base The base model, which contains the contents of the ontology to be processed
//   * @return A new ontology model
//   * @see OntModelSpec
//   */
//  public SemachinaOntModel createOntologyModel(ModelMaker maker, Model base) {
//    return new SemachinaOntModelImpl(maker, base);
//  }
//
//
//  /**
//   * <PROP>
//   * Answer a new ontology model, constructed according to the given ontology model specification,
//   * and starting with the ontology data in the given model.
//   * </PROP>
//   *
//   * @param spec An ontology model specification object, that will be used to construct the ontology
//   * model with different options of ontology language, reasoner, document manager and storage model
//   * @param base An existing model to treat to an ontology model, or null.
//   * @return A new ontology model
//   * @see OntModelSpec
//   */
//  public SemachinaOntModel createOntologyModel(Model base) {
//    return new SemachinaOntModelImpl(base);
//  }


}