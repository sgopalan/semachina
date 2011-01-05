package org.semachina.jena;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 8:47:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SemachinaOntClass extends OntClass {

    SemachinaOntModel getOntModel();

    SemachinaIndividual createUniqueIndividual(String uri);

    SemachinaIndividual createIndividual();

    SemachinaIndividual createIndividual(String uri);
}
