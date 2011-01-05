package org.semachina.jena;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.lucene.store.Directory;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Dec 2, 2010
 * Time: 8:14:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OntModelBuilder {

    OntModelBuilder create(Model base);

    OntModelBuilder withOntSpec(OntModelSpec spec);

    OntModelBuilder withPellet();

    OntModelBuilder withLarq3(Directory directory);

    SemachinaOntModel done() throws Exception;
}
