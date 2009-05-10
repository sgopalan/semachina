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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Custom class for supporting XSD data type Entities
 * 
 * @author Davanum Srinivas <dims@yahoo.com>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#ENTITIES">XML Schema 3.3.12 ENTITIES</a>
 */
public class Entities 
	extends NCName {
	
    private List<Entity> entities = new ArrayList<Entity>();
    
    public Entities() {
        super();
    }
    /**
     * ctor for Entities
     * @exception IllegalArgumentException will be thrown if validation fails
     */
    public Entities (String stValue) throws IllegalArgumentException {
        setValue( stValue );
    }
    
    public void add(Entity entity) {
    	this.entities.add( entity );
    }
    
    public List<Entity> getEntities() {
    	return entities;
    }
    
	public void setValue(String lexicalForm) {
		StringTokenizer tokenizer = new StringTokenizer(lexicalForm);
        int count = tokenizer.countTokens();
        for(int i=0;i<count;i++){
            add( new Entity(tokenizer.nextToken() ) );
        }
	}
    /**
     * Entities can be equal without having identical ordering because
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
        if (object instanceof Entities) {
        	Entities that = (Entities)object;
            if (this.entities.size() == that.entities.size()) {
                Set ourSet = new HashSet(this.entities);
                Set theirSet = new HashSet(that.entities);
                return ourSet.equals(theirSet);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the sum of the hashcodes of the underlying entities, an
     * operation which is not sensitive to ordering.
     *
     * @return an <code>int</code> value
     */
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < entities.size(); i++) {
            hash += entities.get(i).hashCode();
        }
        return hash;
    }
    
    public String toString() {
		StringBuffer buf = new StringBuffer();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get( i );
            if (i > 0) buf.append(" ");
            buf.append(entity.toString());
        }
        return buf.toString();
    }   
}