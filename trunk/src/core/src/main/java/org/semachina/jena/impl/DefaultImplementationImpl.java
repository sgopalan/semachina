package org.semachina.jena.impl;

import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Profile;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 29, 2010
 * Time: 6:56:25 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DefaultImplementationImpl<V extends EnhNode> extends Implementation {

    private Class<V> implClass;

    private String convertFailedMessage = "";

    private Class<?>[] supportedClasses = null;

    public DefaultImplementationImpl(Class<V> implClass, String convertFailedMessage, Class<?>... supportedClasses) {

        if( supportedClasses == null || supportedClasses.length == 0 ) {
            throw new IllegalArgumentException("There needs to be at least one supported class");
        }

        this.implClass = implClass;
        this.supportedClasses = supportedClasses;

        if( convertFailedMessage != null) {
            this.convertFailedMessage = convertFailedMessage;
        }
    }

    public DefaultImplementationImpl(Class<V> implClass, Class<?>... supportedClasses) {
        this( implClass, null, supportedClasses );
    }


    @Override
    public EnhNode wrap(Node node, EnhGraph eg) {
        if (canWrap(node, eg)) {          
            return create( node, eg );
      }
      else {
        throw new ConversionException("Cannot convert node " + node + " to " + implClass + ".\n" + convertFailedMessage );
      }
    }

    @Override
    public boolean canWrap(Node node, EnhGraph eg) {
        // node will support being an OntClass facet if it has rdf:type owl:Class or equivalent
        Profile profile = (eg instanceof OntModel) ? ((OntModel) eg).getProfile() : null;
        if(profile != null ) {
            for( Class<?> clazz : supportedClasses) {
                if( ! profile.isSupported( node, eg, clazz ) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected abstract V create(Node node, EnhGraph eg);
}
