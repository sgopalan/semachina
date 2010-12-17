package org.semachina.datatype.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class XMLDateUtils {

	public static Date parseDate(String lexicalForm) {		        
		SimpleDateFormat zulu = new SimpleDateFormat( "yyyy-MM-dd" );
		Calendar calendar = Calendar.getInstance();
		
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
        
        // convert what we have validated so far
        try {
            result = zulu.parse(lexicalForm == null ? null :
                                (lexicalForm.substring(0,10)) );
        } 
        catch (Exception e) {
            throw new RuntimeException( e.toString(), e );
        }
        
        // support dates before the Christian era
        if (bc) {
            calendar.setTime( result );
            calendar.set( Calendar.ERA, GregorianCalendar.BC );
            result = calendar.getTime();
        }
        
        return result;
	}

	public static String toLexicalDate(Date value) {
		SimpleDateFormat zulu = new SimpleDateFormat( "yyyy-MM-dd" );
		Calendar calendar = Calendar.getInstance();
		
        StringBuffer buf = new StringBuffer();
        if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
            buf.append("-");
            calendar.setTime((Date)value);
            calendar.set(Calendar.ERA, GregorianCalendar.AD);
            value = calendar.getTime();
        }
        buf.append(zulu.format((Date)value));
        return buf.toString();
	}
	
	public static String toLexicalDateTime(Date value) {
    	SimpleDateFormat zulu =
    	       new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	                         //  0123456789 0 123456789

    	zulu.setTimeZone(TimeZone.getTimeZone("GMT"));

    	return zulu.format(value);
	}
	
	public static Date parseDateTime(String lexicalForm) {
		SimpleDateFormat zulu =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //  0123456789 0 123456789

        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));

    	
    	Calendar calendar = Calendar.getInstance();
        Date date;
        boolean bc = false;

        // validate fixed portion of format
        if (lexicalForm == null || lexicalForm.length() == 0) {
            throw new NumberFormatException(
            		"Invalid date/time" );
//                    Messages.getMessage("badDateTime00"));
        }
        if (lexicalForm.charAt(0) == '+') {
            lexicalForm = lexicalForm.substring(1);
        }
        if (lexicalForm.charAt(0) == '-') {
            lexicalForm = lexicalForm.substring(1);
            bc = true;
        }
        if (lexicalForm.length() < 19) {
            throw new NumberFormatException(
    		"Invalid date/time" );
//            Messages.getMessage("badDateTime00"));
        }
        if (lexicalForm.charAt(4) != '-' || lexicalForm.charAt(7) != '-' ||
                lexicalForm.charAt(10) != 'T') {
            throw new NumberFormatException(
            		"Invalid date");
//            		Messages.getMessage("badDate00"));
        }
        if (lexicalForm.charAt(13) != ':' || lexicalForm.charAt(16) != ':') {
            throw new NumberFormatException(
            		"Invalid time" );
//            		Messages.getMessage("badTime00"));
        }
        // convert what we have validated so far
        try {
        	date = zulu.parse(lexicalForm.substring(0, 19) + ".000Z");
        } 
        catch (Exception e) {
            throw new NumberFormatException(e.toString());
        }
        int pos = 19;

        // parse optional milliseconds
        if (pos < lexicalForm.length() && lexicalForm.charAt(pos) == '.') {
            int milliseconds = 0;
            int start = ++pos;
            while (pos < lexicalForm.length() &&
                    Character.isDigit(lexicalForm.charAt(pos))) {
                pos++;
            }
            String decimal = lexicalForm.substring(start, pos);
            if (decimal.length() == 3) {
                milliseconds = Integer.parseInt(decimal);
            } else if (decimal.length() < 3) {
                milliseconds = Integer.parseInt((decimal + "000")
                        .substring(0, 3));
            } else {
                milliseconds = Integer.parseInt(decimal.substring(0, 3));
                if (decimal.charAt(3) >= '5') {
                    ++milliseconds;
                }
            }

            // add milliseconds to the current date
            date.setTime(date.getTime() + milliseconds);
        }

        // parse optional timezone
        if (pos + 5 < lexicalForm.length() &&
                (lexicalForm.charAt(pos) == '+' || (lexicalForm.charAt(pos) == '-'))) {
            if (!Character.isDigit(lexicalForm.charAt(pos + 1)) ||
                    !Character.isDigit(lexicalForm.charAt(pos + 2)) ||
                    lexicalForm.charAt(pos + 3) != ':' ||
                    !Character.isDigit(lexicalForm.charAt(pos + 4)) ||
                    !Character.isDigit(lexicalForm.charAt(pos + 5))) {
                throw new NumberFormatException(
                		"Invalid timezone" );
//                        Messages.getMessage("badTimezone00"));
            }
            int hours = (lexicalForm.charAt(pos + 1) - '0') * 10
                    + lexicalForm.charAt(pos + 2) - '0';
            int mins = (lexicalForm.charAt(pos + 4) - '0') * 10
                    + lexicalForm.charAt(pos + 5) - '0';
            int milliseconds = (hours * 60 + mins) * 60 * 1000;

            // subtract milliseconds from current date to obtain GMT
            if (lexicalForm.charAt(pos) == '+') {
                milliseconds = -milliseconds;
            }
            date.setTime(date.getTime() + milliseconds);
            pos += 6;
        }
        if (pos < lexicalForm.length() && lexicalForm.charAt(pos) == 'Z') {
            pos++;
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        if (pos < lexicalForm.length()) {
            throw new NumberFormatException(
            		"Unexpected characters" );
//            		Messages.getMessage("badChars00"));
        }
        calendar.setTime(date);

        // support dates before the Christian era
        if (bc) {
            calendar.set(Calendar.ERA, GregorianCalendar.BC);
        }

        return calendar.getTime();
	}
	
}
