package org.semachina.jena.impl;

import org.semachina.jena.ReadWriteContext;

import java.io.File;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 12/11/10
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class SimpleReadWriteContext implements ReadWriteContext {

    @Override
    public Map<String, String> getNsPrefixes() {
        return null;
    }

    @Override
    public Map<String, File> getAltEntries() {
        return null;
    }

}
