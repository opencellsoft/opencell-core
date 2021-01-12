/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.DatePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */
public class DateUtils {

    private static long lastTime = System.currentTimeMillis() / 1000;

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
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

    /**
     * Format date to a given pattern
     * 
     * @param value Date to format
     * @param pattern Pattern to format to
     * @return A date as a string
     */
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
     * Format date to yyyy-MM-dd pattern
     * 
     * @param value Date to format
     * @return Date as yyyy-MM-dd string
     */
    public static String formatAsDate(Date value) {
        return formatDateWithPattern(value, DATE_PATTERN);
    }

    /**
     * Format date to yyyy-MM-dd'T'hh:mm:ssXXX pattern
     * 
     * @param value Date to format
     * @return Date as yyyy-MM-dd'T'hh:mm:ssXXX string
     */
    public static String formatAsTime(Date value) {
        return formatDateWithPattern(value, DATE_TIME_PATTERN);
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

    /**
     * Parse date. Will consider formats in the following order: number of miliseconds, "yyyy-MM-dd'T'hh:mm:ssXXX", meveo.dateTimeFormat configuration value, "yyyy-MM-dd",
     * meveo.dateFormat configuration value
     *
     * @param dateValue The date/timestamp as a number of miliseconds or a date/tiestamp string to parse
     * @return Date object
     */
    public static Date parseDate(Object dateValue) {
        if (dateValue instanceof Number) {
            return new Date(((Number) dateValue).longValue());
        } else if (dateValue instanceof String) {
            Long time = NumberUtils.toLong((String) dateValue);
            if (time > 0L) {
                return new Date(time);
            }
            String[] datePatterns = new String[] { DateUtils.DATE_TIME_PATTERN, ParamBean.getInstance().getDateTimeFormat(), DateUtils.DATE_PATTERN, ParamBean.getInstance().getDateFormat() };
            return parseDate((String) dateValue, datePatterns);
        }
        return null;
    }

    /**
     * Parse date
     *
     * @param dateValue The date/timestamp string to parse
     * @param datePatterns Date patterns to consider for a date string
     * @return Date object
     */
    public static Date parseDate(String dateValue, String[] datePatterns) {
        if (datePatterns != null) {
            for (String datePattern : datePatterns) {
                Date date = parseDateWithPattern(dateValue, datePattern);
                if (date != null) {
                    return date;
                }
            }
        }
        return null;
    }

    /**
     * Parse date with a given pattern
     * 
     * @param dateValue The date/timestamp string to parse
     * @param pattern Date pattern to use for parsing
     * @return Date object
     */
    public static Date parseDateWithPattern(String dateValue, String pattern) {
        if (StringUtils.isBlank(dateValue)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        // sdf.setLenient(false);

        try {
            return sdf.parse(dateValue);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if given date is in the period [periodStart,periodEnd[. Start and end dates will NOT be truncated.
     * 
     * @param date date to check
     * @param periodStart Period start date.
     * @param periodEnd Period end date.
     * @return True if date are in the period
     */
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
     * Check if given date is in the period [periodStart,periodEnd[. Start and end dates will be truncated to day.
     * 
     * @param date date to check
     * @param periodStart Period start date. Will be truncated to day.
     * @param periodEnd Period end date. Will be truncated to day.
     * @return True if date are in the period
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
            result = (dateToCheck.before(end));
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

    /**
     * Instantiate a new date
     * 
     * @param year Year
     * @param month Month - a 0 based. Use 0 for January, 11 for December
     * @param day Day of month
     * @param hour Hour
     * @param minute Minute
     * @param second Second
     * @return A date instance
     */
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
        if (start == null || end == null) {
            return 0;
        }
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
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
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
     * Determine overlap between two date period. Null date values in both input and calculated date period mean open dates.
     * 
     * @param oneStart First period - start date
     * @param oneEnd First period - end date
     * @param twoStart Second period - start date
     * @param twoEnd Second period - end date
     * @return A date period with an overlaping dates between the two date periods OR NULL if no overlapping period found.
     */
    public static DatePeriod getPeriodOverlap(Date oneStart, Date oneEnd, Date twoStart, Date twoEnd) {

        Date maxStart = oneStart;
        if (twoStart != null && maxStart != null && twoStart.after(maxStart)) {
            maxStart = twoStart;
        }
        Date minEnd = oneEnd;
        if (twoEnd != null && twoEnd != null && twoEnd.before(minEnd)) {
            minEnd = twoEnd;
        }

        if (maxStart == null || minEnd == null || maxStart.before(minEnd)) {
            return new DatePeriod(maxStart, minEnd);
        } else {
            return null;
        }
    }

    /**
     * Normalize overlapping date periods. Split into smaller non-overlapping periods based on priority.
     * 
     * @param periods Date periods to normalize
     * @return A list of non-overlapping date periods
     */
    public static List<DatePeriodSplit> normalizeOverlapingDatePeriods(DatePeriodSplit... periods) {

        // Sort by priority and then period in ascending order
        Arrays.sort(periods);

        List<DatePeriodSplit> normalizedPeriods = new ArrayList<>();

        for (int i = 0; i < periods.length; i++) {
            DatePeriodSplit overlappingPeriod = periods[i];

            Date nextDate = overlappingPeriod.period.getFrom();
            overlapLoop: while (nextDate != null && (overlappingPeriod.period.getTo() == null || nextDate.before(overlappingPeriod.period.getTo()))) {

                Date periodStart = nextDate;
                Date periodEnd = overlappingPeriod.period.getTo();
                boolean foundUnoverlappedSpace = false;

                normalizedLoop: for (DatePeriodSplit normalizedPeriod : normalizedPeriods) {

                    // if (!isPeriodsOverlap(overlappingPeriod.period.getFrom(), overlappingPeriod.period.getTo(), normalizedPeriod.getPeriod().getFrom(),
                    // normalizedPeriod.getPeriod().getTo())) {
                    // continue;
                    // }

                    Date normFrom = normalizedPeriod.getPeriod().getFrom();
                    Date normTo = normalizedPeriod.getPeriod().getTo();

                    // periodStart < normFrom < periodEnd
                    // Handles case when period starts earlier than the normalized period. Will result in matching of the beginning of the period.
                    // E.g. matching period 1-10 to a normalized period 3-12. Will result in match of the beginning: 1-3
                    if (periodStart.before(normFrom) && (periodEnd == null || periodEnd.compareTo(normFrom) >= 0)) {
                        periodEnd = normFrom;
                        foundUnoverlappedSpace = true;

                        // periodEnd <= normTo
                        // Handles case when period ends within the normalized period.
                        // E.g. matching period xx-10 to a normalized period xx-12. Will result in termination of matching.
                        // In case, start of period was matched earlier (see IF above), it will be saved, otherwise will skip to a next period to match
                    } else if (normTo == null || (periodEnd == null && normTo == null) || (periodEnd != null && periodEnd.compareTo(normTo) <= 0)) {
                        if (foundUnoverlappedSpace) {
                            break normalizedLoop;
                        } else {
                            break overlapLoop;
                        }

                        // periodStart < normTo Still falls in the period
                        // Handles case when period starts within the normalized period. Will advance to the normalized period end.
                        // E.g. matching period 1-20 to a normalized period 1-12. Will advance start date to 12
                    } else if (normTo == null || periodStart.before(normTo)) {
                        periodStart = normTo;
                    }
                }

                if (overlappingPeriod.getPriority() != null) {
                    normalizedPeriods.add(new DatePeriodSplit(new DatePeriod(periodStart, periodEnd), overlappingPeriod.getPriority(), overlappingPeriod.getValue()));
                } else {
                    normalizedPeriods.add(new DatePeriodSplit(new DatePeriod(periodStart, periodEnd), overlappingPeriod.getPriorityInt(), overlappingPeriod.getValue()));
                }
                normalizedPeriods.sort(Comparator.comparing(DatePeriodSplit::getPeriod));

                nextDate = periodEnd;
            }
        }

        normalizedPeriods.sort(Comparator.comparing(DatePeriodSplit::getPeriod));

        return normalizedPeriods;
    }

    /**
     * Format DDMMY : <br>
     * SimpleDateFormat way is not working for this format. e.g : with sfd, 2009 will return '9' but 2018 will return 18<br>
     * BUT the need here is to return always the last digit of the year !
     *
     * @param date the date
     * @return the string
     */
    public static String formatDDMMY(Date date) {

        String ddMM = formatDateWithPattern(date, "ddMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String y = String.valueOf(calendar.get(Calendar.YEAR) % 10);

        return ddMM + y;
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
            Logger log = LoggerFactory.getLogger(DateUtils.class);
            log.error(" error on truncateTime : [{}] ", e.getMessage());
            return date;
        }
    }

    public static String changeFormat(String dateValue, String fromFormat, String toFormat) {
        try {
            if (StringUtils.isEmpty(dateValue) || StringUtils.isEmpty(fromFormat) || StringUtils.isEmpty(toFormat)) {
                return dateValue;
            }

            Date date = parseDateWithPattern(dateValue, fromFormat);
            return formatDateWithPattern(date, toFormat);

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(DateUtils.class);
            log.error(" error on changeFormat : [{}] ", e.getMessage());
            return dateValue;
        }
    }

    /**
     * Calculate prorata ration between two date periods
     * 
     * @param partFrom Partial period - start date
     * @param partTo Partial period - end date
     * @param overallFrom Full period - start date
     * @param overallTo Full period - end date
     * @param ignoreEmptyPeriod If TRUE, when full period is empty (start date = end date), a ZERO will be returned, If FALSE, a NULL value will be returned
     * @return A protation value equal to partial period length/full period length
     */
    public static BigDecimal calculateProrataRatio(Date partFrom, Date partTo, Date overallFrom, Date overallTo, boolean ignoreEmptyPeriod) {

        double part1 = DateUtils.daysBetween(partFrom, partTo);
        double part2 = DateUtils.daysBetween(overallFrom, overallTo);

        if (part2 == 0) {
            if (ignoreEmptyPeriod) {
                return BigDecimal.ZERO;
            } else {
                return null;
            }
        } else if (part1 != part2) {
            return BigDecimal.valueOf(part1 / part2);
        } else {
            return BigDecimal.ONE;
        }
    }

    /**
     * Compares two dates. Handles null dates without exception
     * 
     * @param one First date
     * @param two Second date
     * @return Matches Date.compare() return value as in one.compare(two)
     */
    public static int compare(Date one, Date two) {

        if (one == null && two != null) {
            return 1;
        } else if (one != null && two == null) {
            return -1;
        } else if (one == null && two == null) {
            return 0;
        } else if (one != null && two != null) {
            return one.compareTo(two);
        }

        return 0;
    }

    /**
     * Used to normalize overlapping date periods holding some value into smaller non-overlapping periods by priority. Priority can either be a string or an integer. <br/>
     * It must be of same data type across all the periods to normalize
     */
    public static class DatePeriodSplit implements Serializable, Comparable<DatePeriodSplit> {

        private static final long serialVersionUID = 8310815516122393994L;

        /**
         * Date period
         */
        DatePeriod period;

        /**
         * Priority as string, in ascending order. e.g 'a' wins over 'b'.
         */
        String priority;

        /**
         * Priority as integer, in ascending order. e.g 1 wins over 2.
         */
        int priorityInt;

        /**
         * Value
         */
        Object value;

        /**
         * Constructor
         * 
         * @param period Date period
         * @param priority Priority as string, in ascending order. e.g 'a' wins over 'b'.
         * @param value Value
         */
        public DatePeriodSplit(DatePeriod period, String priority, Object value) {
            super();
            this.period = period;
            this.priority = priority;
            this.value = value;
        }

        /**
         * Constructor
         * 
         * @param period Date period
         * @param priorityInt Priority as integer, in ascending order. e.g 1 wins over 2.
         * @param value Value
         */
        public DatePeriodSplit(DatePeriod period, int priorityInt, Object value) {
            super();
            this.period = period;
            this.priorityInt = priorityInt;
            this.value = value;
        }

        /**
         * @return Date period
         */
        public DatePeriod getPeriod() {
            return period;
        }

        /**
         * @return Priority as String
         */
        public String getPriority() {
            return priority;
        }

        /**
         * @return Priority as integer
         */
        public int getPriorityInt() {
            return priorityInt;
        }

        /**
         * @return Value
         */
        public Object getValue() {
            return value;
        }

        @Override
        public int compareTo(DatePeriodSplit other) {

            // Sort by priority and then period in ascending order
            int priorityCompare = priority != null ? priority.compareTo(other.getPriority()) : priorityInt - other.getPriorityInt();
            if (priorityCompare == 0) {
                return period.compareTo(other.getPeriod());
            }
            return priorityCompare;
        }

        @Override
        public String toString() {
            return period.toString(DateUtils.DATE_TIME_PATTERN) + " - " + (priority != null ? priority : priorityInt) + " - " + value;
        }
    }

    /**
     * Used to split a specific date period by prioritized Calendars
     * 
     * @param datePeriod Date period
     * @param initDate Init Date
     * @param calendars List of calendars
     * @return Split date periods
     */
    public static List<DatePeriodSplit> splitDatePeriodByCalendars(DatePeriod datePeriod, Date initDate, CalendarSplit... calendars) {
        if (datePeriod.getFrom() == null || datePeriod.getTo() == null || datePeriod.getTo().before(datePeriod.getFrom())) {
            throw new IllegalArgumentException("Please provide a valid period!");
        }
        if (initDate == null) {
            throw new IllegalArgumentException("Please provide a valid init Date!");
        }
        if (calendars == null || calendars.length == 0) {
            throw new IllegalArgumentException("Please provide at least one calendar!");
        }
        List<DatePeriodSplit> periods = new ArrayList<>();
        Date fromDate = null;
        Date toDate = null;
        for (CalendarSplit calendar : calendars) {
            calendar.getCalendar().setInitDate(initDate);
            fromDate = datePeriod.getFrom();
            toDate = fromDate;
            int i = 0;
            while (i < 100) {
                toDate = calendar.getCalendar().nextCalendarDate(fromDate);
                if (toDate != null) {
                    if (toDate.after(datePeriod.getTo())) {
                        toDate = datePeriod.getTo();
                    }
                    periods.add(new DatePeriodSplit(new DatePeriod(fromDate, toDate), calendar.getPriority(), calendar.getValue()));
                }
                fromDate = calendar.getCalendar().nextPeriodStartDate((toDate == null) ? fromDate : toDate);
                if (fromDate == null || fromDate.equals(datePeriod.getTo()) || fromDate.after(datePeriod.getTo())) {
                    break;
                }
                i++;
            }
            if (i == 100) {
                throw new IllegalArgumentException("Calendar " + calendar.getCalendar().getCode() + " is cyclic");
            }
        }
        return normalizeOverlapingDatePeriods(periods.toArray(new DatePeriodSplit[] {}));
    }

    /**
     * Calendar Split holder that is used for sending prioritized calendars to apply on a specific Date Period
     *
     */
    public static class CalendarSplit implements Serializable {
        private static final long serialVersionUID = -5526548624466778266L;
        private org.meveo.model.catalog.Calendar calendar;
        private int priority;
        private Object value;

        /**
         * Constructor of calendar split holder
         * 
         * @param calendar Calendar
         * @param priority Priority assigned to the calendar
         * @param value Value to apply
         */
        public CalendarSplit(org.meveo.model.catalog.Calendar calendar, int priority, Object value) {
            this.calendar = calendar;
            this.priority = priority;
            this.value = value;
        }

        public org.meveo.model.catalog.Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(org.meveo.model.catalog.Calendar calendar) {
            this.calendar = calendar;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return calendar.getCode() + " #" + priority + " value:" + value;
        }
    }
}