package org.semachina.jena.binder.annotations.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Prefix Mapping that allows any URI references in other binder annotations
 * in the same class to use the short form instead of full qualification
 * <p/>
 * For example: xs:string vs. http://www.w3.org/2001/XMLSchema#string
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prefix {

    /**
     * @return The prefix for the URI
     */
    String prefix();

    /**
     * @return The URI to prefix
     */
    String uri();
}
