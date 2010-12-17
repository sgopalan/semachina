package org.semachina.jena;

import com.hp.hpl.jena.query.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Nov 30, 2010
 * Time: 9:08:48 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ResultSetHandler {

    void handle(ResultSet rs);
}
