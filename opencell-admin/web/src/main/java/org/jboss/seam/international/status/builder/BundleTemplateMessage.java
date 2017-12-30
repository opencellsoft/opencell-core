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
package org.jboss.seam.international.status.builder;

import javax.faces.application.FacesMessage.Severity;

import org.jboss.seam.international.status.Message;
import org.jboss.seam.international.status.MessageBuilder;
import org.jboss.seam.international.status.MessageFactory;

/**
 * This {@link MessageBuilder} implementation creates {@link Message} objects by loading resource bundle keys as templates with
 * values supplied as parameters.
 * 
 * <b>For example:</b>
 * 
 * Given the following {@link Message} m
 * 
 * <pre>
 * Message m = {@link MessageFactory}.info(new {@link BundleKey}(&quot;messageBundle&quot;, &quot;keyName&quot;), 5, &quot;green&quot;)
 * &nbsp;&nbsp;&nbsp;.defaultText("This is default text.").build();
 * </pre>
 * 
 * And the corresponding messageBundle.properties file:<br>
 * 
 * <pre>
 * keyName=There are {0} cars, and they are all {1}.
 * </pre>
 * 
 * A subsequent call to <code>m.getText()</code> will return:
 * 
 * <pre>
 * &quot;There are 5 cars, and they are all green.&quot;
 * </pre>
 * 
 * <b>Note:</b> If a bundle/key pair cannot be resolved, the default template will be used instead. If there is no default
 * template, a String representation of the {@link BundleKey} will be displayed instead.
 * 
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ssachtleben@gmail.com">Sebastian Sachtleben</a>
 */
public interface BundleTemplateMessage extends MessageBuilder {
    /**
     * Use the given {@link BundleKey} to perform a resource lookup, resolving the template to render for this message.
     * 
     * Any expressions of the form "{0}, {1} ... {N}" found in the template will be interpolated; numbers reference the index of
     * any given parameters, and can be used more than once per template.
     * @return bundle template message
     */
    public BundleTemplateMessage key(final BundleKey text);

    /**
     * Use the given {@link BundleKey} to perform a resource lookup, resolving the template to render detail text for this message.
     * 
     * Any expressions of the form "{0}, {1} ... {N}" found in the template will be interpolated; numbers reference the index of
     * any given parameters, and can be used more than once per template.
     * @param detail bundle key
     * @return bundle template message
     */
    public BundleTemplateMessage detail(final BundleKey detail);

    /**
     * Set the default template text.
     * 
     * If the bundle cannot be loaded for any reason, the builder will fall back to using provided default template text; if
     * there is no default template, a string representation of the {@link BundleKey} will be used instead.
     * 
     * Any expressions of the form "{0}, {1} ... {N}" found in the template will be interpolated; numbers reference the index of
     * any given parameters, and can be used more than once per template.
     * @param text template's text
     * @return bundle template message
     */
    public BundleTemplateMessage defaults(final String text);

    /**
     * Set the parameters for this builder's template.
     * 
     * Parameters may be referenced by index in the template , using expressions of the form "
     * {0}, {1} ... {N}". The same parameters will be used when interpolating default text, in the case when a bundle key
     * cannot be resolved.
     * @param textParams text params
     */
    public BundleTemplateMessage params(final Object... textParams);

    /**
     * Set the parameters for detail text of this builder's template.
     * 
     * Parameters may be referenced by index in the template or text default , using expressions of the form "
     * {0}, {1} ... {N}". The same parameters will be used when interpolating default text, in the case when a bundle key
     * cannot be resolved.
     */
    /**
     * @param detailParams detail
     * @return bundle template message.
     */
    public BundleTemplateMessage detailParams(final Object... detailParams);

    /**
     * Set the targets for this message. If supported by the consuming view-layer, these targets may control where/how the
     * message is displayed to the user.
     * @param targets target of messages
     * @return bundle template message.
     */
    public BundleTemplateMessage targets(final String targets);

    /**
     * Set the severity, level of importance of this message.
     * @param severity severity
     * @return bundle template message.
     */
    public BundleTemplateMessage severity(final Severity severity);

}
