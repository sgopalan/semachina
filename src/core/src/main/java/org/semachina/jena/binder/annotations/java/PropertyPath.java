package org.semachina.jena.binder.annotations.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definition of a Jena ARQ Property Path that will be processed when binding
 * RDF to Object
 * <p/>
 * {@link http://jena.sourceforge.net/ARQ/property_paths.html ARQ - Property Paths}
 */
@SuppressWarnings({"ALL"})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyPath {

    /**
     * @return The property path string to evaluate.  If a prefix is used,
     *         it should be defined by a <code>Prefix</code> annotation within
     *         the same class
     */
    String value();

    /**
     * @return (Optional) Describe the datatypeURI of the intended result
     */
    String dataTypeURI() default "";
}