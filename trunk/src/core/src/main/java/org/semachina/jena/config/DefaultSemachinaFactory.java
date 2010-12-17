package org.semachina.jena.config;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.Personality;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.shared.PrefixMapping;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.semachina.jena.*;
import org.semachina.jena.impl.*;

import com.hp.hpl.jena.vocabulary.XSD;
import org.semachina.datatype.types.Time;
import org.semachina.datatype.utils.XMLDateUtils;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 30, 2010
 * Time: 8:50:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultSemachinaFactory implements org.semachina.jena.SemachinaFactory {

    private TypeMapper typeMapper = TypeMapper.getInstance();

    private PrefixMapping prefixMapping;

    private OntDocumentManager docManager;

    private Personality<RDFNode> model;

    public DefaultSemachinaFactory() {
        this.prefixMapping = PrefixMapping.Factory.create();
        this.prefixMapping.setNsPrefixes(PrefixMapping.Standard);
        this.docManager = OntDocumentManager.getInstance();
        this.model = BuiltinPersonalities.model;
    }

    public void initImplementationClasses() {
        model.add(Individual.class,
                new DefaultImplementationImpl<SemachinaIndividualImpl>(SemachinaIndividualImpl.class, Individual.class) {
                    @Override
                    protected SemachinaIndividualImpl create(Node node, EnhGraph eg) {
                        return new SemachinaIndividualImpl( node, eg );
                    }
                });
        model.add(SemachinaIndividual.class,
                new DefaultImplementationImpl<SemachinaIndividualImpl>(SemachinaIndividualImpl.class, Individual.class) {
                    @Override
                    protected SemachinaIndividualImpl create(Node node, EnhGraph eg) {
                        return new SemachinaIndividualImpl( node, eg );
                    }
                });
        model.add(OntClass.class,
                new DefaultImplementationImpl<SemachinaOntClassImpl>(SemachinaOntClassImpl.class,
                        "It does not have rdf:type owl:Class or equivalent", OntClass.class) {
                    @Override
                    protected SemachinaOntClassImpl create(Node node, EnhGraph eg) {
                        return new SemachinaOntClassImpl( node, eg );
                    }
                });
        model.add(SemachinaOntClass.class,
                new DefaultImplementationImpl<SemachinaOntClassImpl>(SemachinaOntClassImpl.class,
                        "It does not have rdf:type owl:Class or equivalent", OntClass.class) {
                    @Override
                    protected SemachinaOntClassImpl create(Node node, EnhGraph eg) {
                        return new SemachinaOntClassImpl( node, eg );
                    }
                });
        model.add(DatatypeProperty.class,
                new DefaultImplementationImpl<SemachinaTypedDatatypePropertyImpl> (
                        SemachinaTypedDatatypePropertyImpl.class, DatatypeProperty.class) {
                    @Override
                    protected SemachinaTypedDatatypePropertyImpl<Object> create(Node node, EnhGraph eg) {
                        return new SemachinaTypedDatatypePropertyImpl<Object>( node, eg );
                    }
                });
        model.add(ResourceProperty.class,
                new DefaultImplementationImpl<SemachinaResourcePropertyImpl> (
                        SemachinaResourcePropertyImpl.class, ResourceProperty.class) {
                    @Override
                    protected SemachinaResourcePropertyImpl create(Node node, EnhGraph eg) {
                        return new SemachinaResourcePropertyImpl( node, eg );
                    }
                });
    }

    public void initDatatypes() throws Exception {
        typeMapper.registerDatatype(
            new RDFDatatypeImpl(
                    XSD.dateTime.getURI(),
                    DateTime.class,
                    new SimpleDataTypeFactory<DateTime>() {
                        @Override
                        public DateTime parseLexicalForm(String lexicalForm) throws Exception {
                            return ISODateTimeFormat.dateTime().parseDateTime(lexicalForm);
                        }

                        @Override
                        public String toLexicalForm(DateTime cast) throws Exception {
                            return ISODateTimeFormat.dateTime().print(cast);
                        }
                    }));

        typeMapper.registerDatatype(
            new RDFDatatypeImpl(
                    XSD.date.getURI(),
                    Date.class,
                    new SimpleDataTypeFactory<Date>() {
                        @Override
                        public Date parseLexicalForm(String lexicalForm) throws Exception {
                            return XMLDateUtils.parseDate(lexicalForm);
                        }

                        @Override
                        public String toLexicalForm(Date cast) throws Exception {
                            return XMLDateUtils.toLexicalDate(cast);
                        }
                    }));

        typeMapper.registerDatatype(
            new RDFDatatypeImpl(
                    XSD.time.getURI(),
                    Time.class,
                    new SimpleDataTypeFactory<Time>() {
                        @Override
                        public Time parseLexicalForm(String lexicalForm) throws Exception {
                            return new Time(lexicalForm);
                        }
                    }));


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
   * @return A new ontology model
   */
  @Override
  public SemachinaOntModel createOntologyModel() {
    return new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG));
  }

  @Override
  public SemachinaOntModel createOntologyModel(OntModelSpec ontModelSpec) {
    return new SemachinaOntModelImpl(ontModelSpec);
  }

  @Override
  public SemachinaOntModel createOntologyModel(Model base) {
    return new SemachinaOntModelImpl(OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_DL_LANG), base);
  }

  @Override
  public SemachinaOntModel createOntologyModel(OntModelSpec ontModelSpec, Model base) {
    return new SemachinaOntModelImpl(ontModelSpec, base);
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
//   * and create addtional stores for the models in the imports closure
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


    @Override
    public RDFDatatype toRDFDatatype(String typeURI) throws DatatypeFormatException {
        String expanded = prefixMapping.expandPrefix(typeURI);
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

    /**
     * Build a typed literal from its value form.
     *
     * @param value the value of the literal
     * @param dType the type of the literal, null for old style "typed" literals
     */
    @Override
    public Literal createTypedLiteral(Object value, RDFDatatype dType) {
        LiteralLabel ll = LiteralLabelFactory.create(value, "", dType);
        return new LiteralImpl(Node.createLiteral(ll), null);
    }

    /**
     * Build a typed literal label from its value form using
     * whatever datatype is currently registered to the the default
     * representation for this java class. No language tag is supplied.
     *
     * @param value the literal value to encapsulate
     */
    @Override
    public Literal createTypedLiteral(Object value) {
        RDFDatatype dType = typeMapper.getTypeByValue(value);
        return createTypedLiteral(value, dType);
    }


    @Override
    public Literal createTypedLiteral(Object value, String typeURI) {
        String expandedURI = prefixMapping.expandPrefix(typeURI);

        RDFDatatype dt = typeMapper.getSafeTypeByName(expandedURI);
        LiteralLabel ll = null;
        if (value instanceof String) {
            ll = LiteralLabelFactory.createLiteralLabel(String.class.cast(value), "", dt);
        } else {
            ll = LiteralLabelFactory.create(value, "", dt);
        }

        return new LiteralImpl(Node.createLiteral(ll), null);
    }

    @Override
    public Literal createLiteral(String literalString, String lang) {
        if (lang == null) {
            lang = "";
        }
        return new LiteralImpl(Node.createLiteral(literalString, lang, false), null);
    }

    @Override
    public Literal parseTypedLiteral(String literalString) {
        String[] literalParts = literalString.split("\\^\\^");
        if (literalParts.length == 2) {
            RDFDatatype dtype = typeMapper.getSafeTypeByName(literalParts[1]);
            if (dtype != null) {
                return createTypedLiteral(literalParts[0], dtype);
            }
        }
        return new LiteralImpl(Node.createLiteral(literalString, "", false), null);
    }
}