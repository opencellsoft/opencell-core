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
package org.meveo.commons.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils for multithreading.
 * 
 * @author Ignas Lelys
 * 
 */
public class ThreadUtils {

    private final static Logger log = LoggerFactory.getLogger(ThreadUtils.class);
    private static final ScheduledThreadPoolExecutor taskExec = new ScheduledThreadPoolExecutor(5);

    /**
     * Simple helper method to have timed run method (that timeouts if runs too
     * long). Good for long calculations wich has time limit.
     * 
     * @param <V> timed run method.
     * @param c Callable with calculation logic.
     * @param timeout Time after calculation times out. 
     * @param timeUnit Time metric (seconds, minutes etc).
     * 
     * @return Calculation result if calculation finished.
     * 
     * @throws Throwable Exception that might be thrown in calculation.
     */
    public static <V> V timedRun(Callable<V> c, long timeout, TimeUnit timeUnit) throws Throwable {
        Future<V> task = taskExec.submit(c);
        try {
            return task.get(timeout, timeUnit);
        } catch (ExecutionException e) {
            throw e.getCause();
        } catch (TimeoutException e) {
            // throw exception if need to know that timeout occured
            // or leave empty if null can be returned on timeout
        } finally {
            task.cancel(true);
        }
        return null;
    }

    /**
     * Try to sleep safely the Thread , using timeUnit and time params
     * @param timeUnit
     * @param time
     */
    public static void sleepSafe(TimeUnit timeUnit, long time) {
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException e) {
            log.error(" Error on sleepSafe : timeUnit = {} , time = {} ", timeUnit, time, e);
        }
    }

}
