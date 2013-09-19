/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
 * @created Apr 12, 2011
 * 
 */
public class ThreadUtils {

    private static final ScheduledThreadPoolExecutor taskExec = new ScheduledThreadPoolExecutor(5);

    /**
     * Simple helper method to have timed run method (that timeouts if runs too
     * long). Good for long calculations wich has time limit.
     * 
     * @param <V>
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
