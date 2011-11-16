package org.semachina.jena.binder.annotations.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes the property, literal data type URI (optional), and
 * whether if the object should be serialized in a RDF Container
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RdfProperty {

    /**
     * @return The uri of the RDF Property.  If a prefix is used, it should be defined by a
     *         <code>Prefix</code> annotation within the same class
     */
    String value() default "";

    /**
     * @return (Optional) Describe the datatypeURI of the intended result. If a prefix is used,
     *         it should be defined by a <code>Prefix</code> annotation within the same class
     */
    String dataTypeURI() default "";

    /**
     * @return (Optional) The RDF Container (or Collection) type that the associated values
     *         should be wrapped in / extracted from
     */
    RdfContainer container() default RdfContainer.None;

    boolean removeOnEmpty() default true;

    String objectTypeURI() default "";
}
