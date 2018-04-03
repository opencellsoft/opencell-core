/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.seam.international.status;

import java.io.Serializable;

import javax.faces.application.FacesMessage.Severity;

import org.jboss.seam.international.status.builder.BundleKey;

/**
 * A convenient way to add messages to be displayed to the user as Feedback, Toast, Alerts, etc.
 * 
 * It is the responsibility of the view-layer technology to consume and perform operations required to display any messages
 * added in this way.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Messages extends Serializable {
    /**
     * Clear all pending messages.
     */
    public void clear();

    /**
     * Return true if there are no pending {@link Message} or {@link MessageBuilder} objects in the queue.
     * @return true if it is empty
     */
    public boolean isEmpty();

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle message
     */
    public void info(final BundleKey message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params parameter values
     */
    public void info(final BundleKey message, final Object... params);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     */
    public void warn(final BundleKey message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params list of parameter.s
     */
    public void warn(final BundleKey message, final Object... params);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key.
     */
    public void error(final BundleKey message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params  list of values
     */
    public void error(final BundleKey message, final Object... params);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle message
     */
    public void fatal(final BundleKey message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params  list of values
     */
    public void fatal(final BundleKey message, final Object... params);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message message info.
     */
    public void info(final String message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params  list of values
     */
    public void info(final String message, final Object... params);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message message to be displayed
     */
    public void warn(final String message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params  list of values
     */
    public void warn(final String message, final Object... params);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message error message.
     */
    public void error(final String message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params  list of values
     */
    public void error(final String message, final Object... params);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     */
    public void fatal(final String message);

    /**
     * Create a {@link MessageBuilder} with the specified {@link Severity}, add it to the internal queue, and return it.
     * @param message bundle key
     * @param params  list of values
     */
    public void fatal(final String message, final Object... params);

}
