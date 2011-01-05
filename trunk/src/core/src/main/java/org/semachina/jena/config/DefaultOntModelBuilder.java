package org.semachina.jena.config;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.lucene.store.Directory;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semachina.jena.OntModelBuilder;
import org.semachina.jena.SemachinaOntModel;
import org.semachina.jena.features.larq3.Larq3Feature;
import org.semachina.jena.features.pellet.PelletFeature;
import org.semachina.jena.impl.SemachinaOntModelImpl;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 12/10/10
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultOntModelBuilder implements OntModelBuilder {
    private Model base = ModelFactory.createDefaultModel();

    private OntModelSpec spec = OntModelSpec.getDefaultSpec(ProfileRegistry.OWL_LANG);

    private boolean usePellet;

    private boolean useLarq3;

    private Directory directory;

    @Override
    public OntModelBuilder create(Model base) {
        this.base = base;
        return this;
    }

    @Override
    public OntModelBuilder withOntSpec(OntModelSpec spec) {
        this.spec = spec;
        return this;
    }

    @Override
    public OntModelBuilder withPellet() {
        this.usePellet = true;
        this.spec = PelletReasonerFactory.THE_SPEC;
        return this;
    }

    @Override
    public OntModelBuilder withLarq3(Directory directory) {
        this.useLarq3 = true;
        this.directory = directory;
        return this;
    }

    @Override
    public SemachinaOntModel done() throws Exception {

        SemachinaOntModel ontModel = new SemachinaOntModelImpl(spec, base);
        if (usePellet) {
            ontModel.addFeature(new PelletFeature());
        }

        if (useLarq3) {
            ontModel.addFeature(new Larq3Feature(directory));
        }

        return ontModel;
    }
}
