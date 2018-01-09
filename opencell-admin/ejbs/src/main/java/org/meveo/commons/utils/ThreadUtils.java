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
package org.meveo.commons.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utils for multithreading.
 * 
 * @author Ignas Lelys
 * 
 */
public class ThreadUtils {

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

}
