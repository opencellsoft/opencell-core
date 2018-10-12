/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
public class DateUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

    private static long lastTime = System.currentTimeMillis() / 1000;

    public static String DATE_PATTERN = "yyyy-MM-dd";
    public static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'hh:mm:ssXXX";
    private static final String START_DATE_DELIMITER = "[";
    private static final String END_DATE_DELIMITER = "]";

    public static synchronized Date getCurrentDateWithUniqueSeconds() {
        long current = System.currentTimeMillis();
        while (current / 1000 <= lastTime / 1000) {
            current += 1000;
        }
        lastTime = current;
        return new Date(lastTime);
    }

    public static String formatDateWithPattern(Date value, String pattern) {
        if (value == null) {
            return "";
        }
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            result = sdf.format(value);
        } catch (Exception e) {
            result = "";
        }

        return result;
    }

    /**
     * Replaces a date format string in text with a current date. E.g. sales-#{re.id}-[yyyy_MM_dd].html changes to sales-#{re.id}-2018-05-18.html
     * 
     * @param input string that contains the date format
     * @return Text with date value replaced
     */
    public static String evaluteDateFormat(String input) {
        if (!(input.contains(START_DATE_DELIMITER) && input.contains(END_DATE_DELIMITER))) {
            return input;
        }
        String dateFormatStr = input.substring(input.indexOf(START_DATE_DELIMITER) + 1, input.lastIndexOf(END_DATE_DELIMITER));
        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        Calendar cal = Calendar.getInstance();
        return input.substring(0, input.indexOf(START_DATE_DELIMITER)) + dateFormat.format(cal.getTime()) + input.substring(input.lastIndexOf(END_DATE_DELIMITER) + 1);
    }

    public static Date setTimeToZero(Date date) {
        if (date == null) {
            return null;
        }
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    public static Date parseDateWithPattern(String dateValue, String pattern) {
        if (dateValue == null || dateValue.trim().length() == 0) {
            return null;
        }
        Date result = null;

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            result = sdf.parse(dateValue);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public static boolean isDateTimeWithinPeriod(Date date, Date periodStart, Date periodEnd) {
        if (date == null)
            return true;

        if (periodStart == null && periodEnd == null)
            return true;

        if (periodStart != null && periodEnd != null && periodStart.after(periodEnd))
            throw new IllegalArgumentException("To test if a date is within a period, the period must be valid");

        boolean result = false;

        if (periodStart != null && periodEnd != null)
            result = date.after(periodStart) && date.before(periodEnd);
        else if (periodStart != null)
            result = date.after(periodStart);
        else
            result = date.before(periodEnd);

        return result;

    }

    public static boolean isTodayWithinPeriod(Date periodStart, Date periodEnd) {
        return isDateWithinPeriod(new Date(), periodStart, periodEnd);
    }

    /**
     * Check if given date are in period [periodStart,periodEnd[
     * 
     * @param date date to check
     * @param periodStart periodStart
     * @param periodEnd periodEnd
     * @return true if date are in period
     */
    public static boolean isDateWithinPeriod(Date date, Date periodStart, Date periodEnd) {
        if (date == null)
            return true;

        if (periodStart == null && periodEnd == null)
            return true;

        Date start = (periodStart != null) ? setDateToStartOfDay(periodStart) : null;
        Date end = (periodEnd != null) ? setDateToStartOfDay(periodEnd) : null;

        if (start != null && end != null && start.after(end))
            throw new IllegalArgumentException("To test if a date is within a period, the period must be valid");

        Date dateToCheck = setDateToStartOfDay(date);
        boolean result = false;

        // case 1 end and start not nunll

        if (start != null && end != null) {
            result = (dateToCheck.after(start) || dateToCheck.equals(start)) && (dateToCheck.before(end));
        } else if (end == null) {
            result = (dateToCheck.after(start) || dateToCheck.equals(start));
        } else {
            result = (dateToCheck.before(end) );
        }
        return result;

    }

    public static Date setDateToStartOfDay(Date date) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            calendar.set(year, month, day, 0, 0, 0);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setDateToEndOfDay(Date date) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            calendar.set(year, month, day, 23, 59, 59);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date addDaysToDate(Date date, Integer days) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, days);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date addWeeksToDate(Date date, Integer weeks) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, weeks);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date addMonthsToDate(Date date, Integer months) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, months);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date addYearsToDate(Date date, Integer years) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, years);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setYearToDate(Date date, Integer year) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.YEAR, year);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setMonthToDate(Date date, Integer month) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MONTH, month);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setDayToDate(Date date, Integer day) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, day);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setHourToDate(Date date, Integer hour) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setMinuteToDate(Date date, Integer minute) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, minute);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setSecondsToDate(Date date, Integer seconds) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.SECOND, seconds);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date setMilliSecondsToDate(Date date, Integer milliSeconds) {
        Date result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MILLISECOND, milliSeconds);
            result = calendar.getTime();
        }

        return result;
    }

    public static Integer getSecondsFromDate(Date date) {
        Integer result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = calendar.get(Calendar.SECOND);
        }

        return result;
    }

    public static Integer getMinuteFromDate(Date date) {
        Integer result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = calendar.get(Calendar.MINUTE);
        }

        return result;
    }

    public static Integer getHourFromDate(Date date) {
        Integer result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = calendar.get(Calendar.HOUR);
        }

        return result;
    }

    public static Integer getDayFromDate(Date date) {
        Integer result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = calendar.get(Calendar.DATE);
        }

        return result;
    }

    public static Integer getMonthFromDate(Date date) {
        Integer result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = calendar.get(Calendar.MONTH);
        }

        return result;
    }

    public static Integer getYearFromDate(Date date) {
        Integer result = null;

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = calendar.get(Calendar.YEAR);
        }

        return result;
    }

    public static Date newDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Calculates number of month between two dates.
     * 
     * @param firstDate First date parameter.
     * @param secondDate Second date parameter.
     * @return number of month.
     */
    public static int monthsBetween(Date firstDate, Date secondDate) {
        Date before = null;
        Date after = null;
        if (firstDate.after(secondDate)) {
            after = firstDate;
            before = secondDate;
        } else {
            after = secondDate;
            before = firstDate;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(before);
        int beforeMonth = cal.get(Calendar.MONTH);
        int beforeYear = cal.get(Calendar.YEAR);
        cal.setTime(after);
        int afterMonth = cal.get(Calendar.MONTH);
        int afterYear = cal.get(Calendar.YEAR);

        return ((afterYear * 12) + afterMonth) - ((beforeYear * 12) + beforeMonth);
    }

    public static double daysBetween(Date start, Date end) {
        DateTime dateTimeStart = new DateTime(start.getTime());
        DateTime dateTimeEnd = new DateTime(end.getTime());
        return Days.daysBetween(dateTimeStart, dateTimeEnd).getDays();
    }

    public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar value) {
        Calendar cal = value.toGregorianCalendar();
        Date d = cal.getTime();
        return d;
    }

    public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);

    }
    
    public static XMLGregorianCalendar dateToXMLGregorianCalendarFieldUndefined(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
    }

    final static Pattern fourDigitsPattern = Pattern.compile("(?<!\\d)\\d{4}(?!\\d)");
    final static Pattern monthPattern = Pattern.compile("(?<!\\d)[0-1][0-9](?!\\d)");
    final static Pattern dayPattern = Pattern.compile("(?<!\\d)\\d{2}(?!\\d)");
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Guess a date
     * 
     * @param stringDate Date as a string or a timestamp number
     * @param hints Date formats to consider
     * @return A date object
     */
    public static Date guessDate(String stringDate, String... hints) {

        if (stringDate == null) {
            return null;
        }

        Date result = null;
        stringDate = stringDate.trim();

        // First check if it is not a timestamp number
        try {

            long timeStamp = Long.parseLong(stringDate);
            if (stringDate.equals(timeStamp + "")) {
                return new Date(timeStamp);
            }
        } catch (Exception e) {
            // ignore any exception
        }

        // Try different formats
        for (String hint : hints) {
            SimpleDateFormat sdf = new SimpleDateFormat(hint);
            try {
                result = sdf.parse(stringDate);
            } catch (ParseException e) {
            }
            if (result != null) {
                return result;
            }
        }
        // test if the string contains a sequence of 4 digits
        final Matcher fourDigitsMatcher = fourDigitsPattern.matcher(stringDate);
        if (fourDigitsMatcher.find()) {
            String year = fourDigitsMatcher.group();
            // test if we have something that match a month
            String dayMonth = stringDate.substring(4);
            if (stringDate.indexOf(year) > 0) {
                dayMonth = stringDate.substring(0, stringDate.length() - 4);
            }
            final Matcher monthMatcher = monthPattern.matcher(dayMonth);
            if (monthMatcher.find()) {
                String month = monthMatcher.group();
                // if some other 2 digit also match month we cannot guess for sure
                if (!monthMatcher.find()) {
                    String dayString = dayMonth.replaceFirst(month, "");
                    final Matcher dayMatcher = dayPattern.matcher(dayString);
                    if (dayMatcher.find()) {
                        // we are done
                        String day = dayMatcher.group();
                        try {
                            result = sdf.parse(year + "-" + month + "-" + day);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Check if periods overlap
     * 
     * @param periodStart One period start date
     * @param periodEnd One period end date
     * @param checkStart Second period start date
     * @param checkEnd Second period end date
     * @return True if period is within another period
     */
    public static boolean isPeriodsOverlap(Date periodStart, Date periodEnd, Date checkStart, Date checkEnd) {

        // Logger log = LoggerFactory.getLogger(DateUtils.class);
        if ((checkStart == null && checkEnd == null) || (periodStart == null && periodEnd == null)) {
            return true;
        }

        // Period is not after dates being checked
        if (checkStart == null && (periodStart == null || (checkEnd != null && periodStart.compareTo(checkEnd) < 0))) {
            return true;

            // Period is not before dates being checked
        } else if (checkEnd == null && (periodEnd == null || (checkStart != null && periodEnd.compareTo(checkStart) > 0))) {
            return true;

            // Dates are not after period
        } else if (periodStart == null && (checkStart == null || (periodEnd != null && checkStart.compareTo(periodEnd) < 0))) {
            return true;

            // Dates are not before period
        } else if (periodEnd == null && (checkEnd == null || (periodStart != null && checkEnd.compareTo(periodStart) > 0))) {
            return true;

        } else if (checkStart != null && checkEnd != null && periodStart != null && periodEnd != null) {

            // Dates end or start within the period
            if ((checkEnd.compareTo(periodEnd) <= 0 && checkEnd.compareTo(periodStart) > 0) || (checkStart.compareTo(periodEnd) < 0 && checkStart.compareTo(periodStart) >= 0)) {
                return true;
            }

            // Period end or start within the dates
            if ((periodEnd.compareTo(checkEnd) <= 0 && periodEnd.compareTo(checkStart) > 0) || (periodStart.compareTo(checkEnd) < 0 && periodStart.compareTo(checkStart) >= 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWithinDate(Date dateToCheck, Date startDate, Date endDate) {
        if (startDate == null && endDate == null) {
            return true;
        }
        if (startDate == null) {
            return !dateToCheck.after(endDate);
        }
        if (endDate == null) {
            return !dateToCheck.before(startDate);
        }

        return !dateToCheck.before(startDate) && !dateToCheck.after(endDate);
    }
    
    /**
     * Format DDMMY : 
     *   SimpleDateFormat way is not working for this format => e.g : with sfd, 2009 will return '9' but 2018 will return 18 , 
     *   BUT the need here is to return always the last digit of the year !
     *
     * @param date the date
     * @return the string
     */
    public static String formatDDMMY (Date date) {
        
        String ddMM = formatDateWithPattern(date, "ddMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String y = String.valueOf(calendar.get(Calendar.YEAR) % 10);
        
        return ddMM+y;
    }
    
    public static Date truncateTime(Date date) {
        try {
            if (date == null) {
                return null;
            }
            Calendar cal = Calendar.getInstance(); // locale-specific
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            LOG.error(" error on truncateTime : [{}] ", e.getMessage());
            return date;
        }
    }
}