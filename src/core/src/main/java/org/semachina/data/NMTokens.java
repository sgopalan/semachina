/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semachina.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Custom class for supporting XSD data type NMTokens
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 */
public class NMTokens 
	extends NCName {
	
    private List<NMToken> tokens = new ArrayList<NMToken>();

    public NMTokens() {
        super();
    }
    /**
     * ctor for NMTokens
     * @exception IllegalArgumentException will be thrown if validation fails
     */
    public NMTokens (String stValue) throws IllegalArgumentException {
        setValue(stValue);
    }

    public void setValue(String stValue) {
        StringTokenizer tokenizer = new StringTokenizer(stValue);
        int count = tokenizer.countTokens();
        for(int i=0;i<count;i++){
            add( new NMToken(tokenizer.nextToken() ) );
        }
    }

    public String toString() {
        return toLexicalForm();
    }

    /**
     * NMTokens can be equal without having identical ordering because
     * they represent a set of references.  Hence we have to compare
     * values here as a set, not a list.
     *
     * @param object an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;        // succeed quickly, when possible
        }
        if (object instanceof NMTokens) {
            NMTokens that = (NMTokens)object;
            if (this.tokens.size() == that.tokens.size()) {
                Set ourSet = new HashSet(this.tokens);
                Set theirSet = new HashSet(that.tokens);
                return ourSet.equals(theirSet);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the sum of the hashcodes of the underlying tokens, an
     * operation which is not sensitive to ordering.
     *
     * @return an <code>int</code> value
     */
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < tokens.size(); i++) {
            hash += tokens.get(i).hashCode();
        }
        return hash;
    }
    
    public void add(NMToken token) {
    	this.tokens.add( token );
    }
    
    public List<NMToken> getNMTokens() {
    	return this.tokens;
    }
    
	public String toLexicalForm() {
		StringBuffer buf = new StringBuffer();
        for (int i = 0; i < tokens.size(); i++) {
            NMToken token = tokens.get( i );
            if (i > 0) buf.append(" ");
            buf.append(token.toString());
        }
        return buf.toString();
	}   
}