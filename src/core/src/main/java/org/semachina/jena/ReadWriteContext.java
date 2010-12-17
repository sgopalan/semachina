package org.semachina.jena;

import java.io.File;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 12/10/10
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReadWriteContext {

    Map<String, String> getNsPrefixes();

    Map<String, File> getAltEntries();

    void execute(SemachinaOntModel transactModel);
}
