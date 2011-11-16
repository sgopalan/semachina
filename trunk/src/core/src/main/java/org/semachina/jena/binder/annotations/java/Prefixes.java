package org.semachina.jena.binder.annotations.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wrapper to hold multiple Prefix Mappings (captured by <code>Prefix</code>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Prefixes {

    /**
     * @return The wrapped <code>Prefix</code>(s)
     */
    public Prefix[] value() default {};
}
