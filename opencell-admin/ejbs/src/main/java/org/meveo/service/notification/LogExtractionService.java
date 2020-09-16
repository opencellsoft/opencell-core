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

package org.meveo.service.notification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.joda.time.DateTimeComparator;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lauch a jobInstance and create a notificationHistory
 *
 * @author anasseh
 * @since 19.06.2015
 */

public class LogExtractionService {

    /**
     * logger
     */
    private static Logger log = LoggerFactory.getLogger(LogExtractionService.class);

    /**
     * @param fromDate from date we get logs
     * @param toDate   until which date we get logs
     * @return name of log.
     */
    public static String getLogs(Date fromDate, Date toDate) {
        String result = "";
        try (FileInputStream logginConfigurationInStream = new FileInputStream(System.getProperty("logging.configuration").substring(5))) {
            Properties props = new Properties();
            props.load(logginConfigurationInStream);
            String logFile = props.getProperty("handler.FILE.fileName");
            String dateFormat = props.getProperty("formatter.FILE.pattern").substring(props.getProperty("formatter.FILE.pattern").indexOf("{") + 1,
                    props.getProperty("formatter.FILE.pattern").indexOf("}"));
            int length = 0, maxLength = Integer.parseInt(ParamBeanFactory.getAppScopeInstance().getProperty("meveo.notifier.log.lengthInBytes", "100000"));
            DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
            boolean mustBeInToo = false;
            Date dateCurrentLine = null;
            String line = null;
            boolean isAfterToDate = false;
            try (BufferedReader logReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)))) {
                while ((line = logReader.readLine()) != null && length < maxLength && !isAfterToDate) {
                    dateCurrentLine = getDateTime(line, dateFormat);
                    if ((dateCurrentLine == null && mustBeInToo) || // include the line that not start by a date but it is in the period
                            (dateCurrentLine != null && comparator.compare(dateCurrentLine, fromDate) >= 0 && comparator.compare(dateCurrentLine, toDate) <= 0)) {
                        result += line + "\n";
                        length += line.length();
                        mustBeInToo = true;
                    } else {
                        mustBeInToo = false;
                    }
                    if (dateCurrentLine != null) {
                        if (dateCurrentLine.after(toDate)) {
                            isAfterToDate = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.warn("", e);
        }
        return result;
    }

    /**
     * @param line       line inside log
     * @param dateFormat date format
     * @return date
     */
    private static Date getDateTime(String line, String dateFormat) {
        Date result = null;
        if (line == null) {
            return null;
        }
        if (line.length() < dateFormat.length()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        try {
            result = sdf.parse(line.substring(0, dateFormat.length()));
        } catch (Exception e) {
        }
        return result;
    }
}
