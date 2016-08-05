package btd.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {

    /** The date pattern that is used for conversion. Change as you wish. */
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String HOUR_PATTERN = "mm";
    private static final String MINUTE_PATTERN = "mm";

    /** The date formatter. */
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(DATE_PATTERN);
    
    /** The HOUR formatter. */
    private static final DateTimeFormatter HOUR_FORMATTER = 
            DateTimeFormatter.ofPattern(HOUR_PATTERN);
    
    /** The MINUTE formatter. */
    private static final DateTimeFormatter MINUTE_FORMATTER = 
            DateTimeFormatter.ofPattern(MINUTE_PATTERN);

    /**
     * Returns the given date as a well formatted String. The above defined 
     * {@link DateUtil#DATE_PATTERN} is used.
     * 
     * @param date the date to be returned as a string
     * @return formatted string
     */
    public static String format(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    /**
     * Converts a String in the format of the defined {@link DateUtil#DATE_PATTERN} 
     * to a {@link LocalDate} object.
     * 
     * Returns null if the String could not be converted.
     * 
     * @param dateString the date as String
     * @return the date object or null if it could not be converted
     */
    public static LocalDateTime parse(String dateString) {
    	if (dateString != null && dateString != "" && dateString != "null") {
	    	try {
	            return DATE_FORMATTER.parse(dateString, LocalDateTime::from);
	        } catch (DateTimeParseException e) {
	            return null;
	        }
    	} else
        	return null;
    }
    
    /**
     * Returns HOUR from the given date as a well formatted String. The above defined 
     * {@link DateUtil#DATE_PATTERN} is used.
     * 
     * @param date the date to be returned as a string
     * @return formatted string
     */
    public static String getHour(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return HOUR_FORMATTER.format(date);
    }
    
    /**
     * Returns MINUTES from the given date as a well formatted String. The above defined 
     * {@link DateUtil#DATE_PATTERN} is used.
     * 
     * @param date the date to be returned as a string
     * @return formatted string
     */
    public static String getMinutes(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return MINUTE_FORMATTER.format(date);
    }

    /**
     * Checks the String whether it is a valid date.
     * 
     * @param dateString
     * @return true if the String is a valid date
     */
    public static boolean validDate(String dateString) {
        // Try to parse the String.
        return DateTimeUtil.parse(dateString) != null;
    }
}