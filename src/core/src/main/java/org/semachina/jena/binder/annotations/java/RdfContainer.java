package org.semachina.jena.binder.annotations.java;

/**
 * Enum of the RDF Container modes a <code>RdfProperty</code> could take
 * <p/>
 * {@link http://www.w3.org/TR/rdf-schema/#ch_alt Alt}
 * {@link http://www.w3.org/TR/rdf-schema/#ch_bag Bag}
 * {@link http://www.w3.org/TR/rdf-schema/#ch_seq Seq}
 * <p/>
 * Technically, a list is not a Container, but a Collection
 * {@link http://www.w3.org/TR/rdf-schema/#ch_list List}
 */
@SuppressWarnings({"ALL"})
public enum RdfContainer {
    None, Alt, Bag, Seq, RdfList
}
