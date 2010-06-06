/*
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.semachina.jena.db;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * A utility class for passing the results so that the execution can be closed
 * later
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Results {

    // The wrapped result set
    private ResultSet resultSet = null;

    // The query execution to close
    private QueryExecution queryExecution  = null;

    // The data to query
    private Data data = null;

    // True if closed
    private boolean closed = false;

    /**
     * Creates a new Results
     * @param resultSet The result set
     * @param queryExecution The query execution
     * @param data The data queried
     */
    public Results(ResultSet resultSet, QueryExecution queryExecution,
            Data data) {
        this.resultSet = resultSet;
        this.queryExecution = queryExecution;
        this.data = data;
    }

    /**
     * Gets the result set of the query
     * @return The result set
     */
    public ResultSet getResults() {
        return resultSet;
    }

    /**
     * Frees resources
     */
    public void close() {
        if (!closed) {
            closed = true;
            queryExecution.close();
            data.close();
        }
    }

    /**
     *
     * @see java.lang.Object#finalize()
     */
    protected void finalize() {
        close();
    }
}
