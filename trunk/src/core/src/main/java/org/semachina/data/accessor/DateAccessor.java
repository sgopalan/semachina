package org.semachina.data.accessor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.semachina.core.DataAccessor;




public class DateAccessor extends DataAccessor<Date> {

    private static SimpleDateFormat zulu = 
        new SimpleDateFormat("yyyy-MM-dd");
                          //  0123456789 0 123456789

    private static Calendar calendar = Calendar.getInstance();
	
	public DateAccessor(String uri) {
		super( uri, Date.class );
	}

	/**
	 * @see org.apache.axis.encoding.ser.DateDeserializer#makeValue(String)
	 */
	public Date parseLexicalForm(String lexicalForm) {
        Date result;
        boolean bc = false;
        
        // validate fixed portion of format
        if ( lexicalForm != null ) {
            if (lexicalForm.length() < 10)
                throw new NumberFormatException(
                        "bad date" );   
                		//Messages.getMessage("badDate00"));
    
            if (lexicalForm.charAt(0) == '+')
            	lexicalForm = lexicalForm.substring(1);

            if (lexicalForm.charAt(0) == '-') {
            	lexicalForm = lexicalForm.substring(1);
                bc = true;
            }

            if (lexicalForm.charAt(4) != '-' || lexicalForm.charAt(7) != '-')
                throw new NumberFormatException(
                        "bad date" );                        
                		//Messages.getMessage("badDate00"));
            
        }
        
        synchronized (calendar) {
            // convert what we have validated so far
            try {
                result = zulu.parse(lexicalForm == null ? null :
                                    (lexicalForm.substring(0,10)) );
            } catch (Exception e) {
                throw new NumberFormatException(e.toString());
            }
            
            // support dates before the Christian era
            if (bc) {
                calendar.setTime((Date)result);
                calendar.set(Calendar.ERA, GregorianCalendar.BC);
                result = calendar.getTime();
            }
            
        }
        return result;
	}

	/**
	 * @see org.apache.axis.encoding.ser.DateSerializer#getValueAsString(Object, SerializationContext)
	 */
	public String toLexicalForm(Date value) {
        StringBuffer buf = new StringBuffer();
        synchronized (calendar) {
            if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
                buf.append("-");
                calendar.setTime((Date)value);
                calendar.set(Calendar.ERA, GregorianCalendar.AD);
                value = calendar.getTime();
            }
            buf.append(zulu.format((Date)value));
        }
        return buf.toString();
	}	
}