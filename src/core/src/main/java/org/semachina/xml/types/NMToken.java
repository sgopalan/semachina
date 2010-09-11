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
package org.semachina.xml.types;

//import org.apache.axis.utils.Messages;
import org.semachina.xml.utils.XMLChar;

/**
 * Custom class for supporting XSD data type NMToken
 *
 * NMTOKEN represents the NMTOKEN attribute type from
 * [XML 1.0(Second Edition)]. The value space of NMTOKEN
 * is the set of tokens that match the Nmtoken production
 * in [XML 1.0 (Second Edition)].
 * The base type of NMTOKEN is token.
 * @author Chris Haddad <chaddad@cobia.net>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#nmtoken">XML Schema 3.3.4</a>
 * @see org.apache.axis.encoding.NMToken (Axis 1.4)
 */
public class NMToken extends Token {

    public NMToken() {
        super();
    }

    /**
     * ctor for NMToken
     * @exception IllegalArgumentException will be thrown if validation fails
     */
    public NMToken(String stValue) throws IllegalArgumentException {
        try {
            setValue(stValue);
        }
        catch (IllegalArgumentException e) {
            // recast normalizedString exception as token exception
            throw new IllegalArgumentException(
            	"Invalid Nmtoken" +
//            	Messages.getMessage("badNmtoken00") + 
                "data=[" +
                stValue + "]");
        }
    }

    /**
     *
     * validate the value against the xsd definition
     * Nmtoken    ::=    (NameChar)+
     * NameChar    ::=     Letter | Digit | '.' | '-' | '_' | ':' | CombiningChar | Extender
     */
    public static boolean isValid(String stValue) {
        int scan;

        for (scan=0; scan < stValue.length(); scan++) {
          if (XMLChar.isName(stValue.charAt(scan)) == false)
            return false;
        }

        return true;
    }
}