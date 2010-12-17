package org.semachina.jena.features;

import org.semachina.jena.SemachinaOntModel;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Dec 8, 2010
 * Time: 8:34:38 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Feature {

    String getKey();
    
    void init(SemachinaOntModel ontModel, org.semachina.jena.SemachinaFactory factory) throws Exception;

    void close() throws Exception;
}
