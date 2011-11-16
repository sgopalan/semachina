package org.semachina.jena.binder.annotations.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lists the RDF Types of the mapped object
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RdfType {
    /**
     * @return The uri(s) of the RDF Type(s) associated with this class.  If a prefix is used,
     *         it should be defined within a <code>Prefix</code> annotation within the same class
     */
    public abstract String[] value();
}
