package org.semachina.jena.binder.annotations.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker to identify the field/method that maps to rdf:ID during
 * the binding process.
 * <p/>
 * If a prefix is used, it should be defined by a <code>Prefix</code> annotation within
 * the same class
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
