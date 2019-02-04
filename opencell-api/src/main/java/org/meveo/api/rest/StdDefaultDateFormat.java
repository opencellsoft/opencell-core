package org.meveo.api.rest;

import java.util.Calendar;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class StdDefaultDateFormat extends StdDateFormat {
    
    private static final long serialVersionUID = 1L;

    static {
        Calendar defCal = Calendar.getInstance(TimeZone.getDefault());
        
        DATE_FORMAT_RFC1123.setCalendar(defCal);
        DATE_FORMAT_ISO8601.setCalendar(defCal);
        DATE_FORMAT_ISO8601_Z.setCalendar(defCal);
        DATE_FORMAT_PLAIN.setCalendar(defCal);
    }

}
