package org.semachina.jena;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 12/11/10
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SemachinaFactory {
    TypeMapper getTypeMapper();

    PrefixMapping getPrefixMapping();

    OntDocumentManager getDocManager();

    SemachinaOntModel createOntologyModel();

    SemachinaOntModel createOntologyModel(OntModelSpec ontModelSpec);

    SemachinaOntModel createOntologyModel(Model base);

    SemachinaOntModel createOntologyModel(OntModelSpec ontModelSpec, Model base);

    void initDatatypes() throws Exception;

    void initImplementationClasses();

}
