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
 * This {@link MessageBuilder} creates {@link Message} objects by interpolating templates with values supplied as parameters.
 * 
 * <b>For example:</b> Given the following {@link Message} m
 * 
 * <pre>
 * Message m = {@link MessageFactory}.info(&quot;There are {0} cars, and they are all {1}.&quot;, 5, &quot;green&quot;).build();
 * </pre>
 * 
 * A subsequent call to <code>m.getSummary()</code> will return:
 * <code>"There are 5 cars, and they are all green."</code>;
 * 
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ssachtleben@gmail.com">Sebastian Sachtleben</a>
 */
public interface TemplateMessage extends MessageBuilder {
    /**
     * Set the template for this message.
     * 
     * Any expressions of the form "{0}, {1} ... {N}" found in the template will be interpolated; numbers reference the index of
     * any given parameters, and can be used more than once per template.
     */
    public TemplateMessage text(final String summary);

    /**
     * Set the detail text for this message.
     * Any expressions of the form "{0}, {1} ... {N}" found in the template will be interpolated; numbers reference the index of
     * any given parameters, and can be used more than once per template.
     */
    public TemplateMessage detail(final String detail);

    /**
     * Set the parameters for this builder's template.
     * 
     * Parameters may be referenced by index in the template, using expressions of the form "{0}, {1} ... {N}"
     */
    public TemplateMessage textParams(final Object... summaryParams);

    /**
     * Set the parameters for detail text of this builder's template.
     * 
     * Parameters may be referenced by index in the template, using expressions of the form "{0}, {1} ... {N}"
     */
    public TemplateMessage detailParams(final Object... detailParams);

    /**
     * Set the targets for this message. If supported by the consuming view-layer, these targets may control where/how the
     * message is displayed to the user.
     */
    public TemplateMessage targets(final String targets);

    /**
     * Set the severity, level of importance of this message.
     */
    public TemplateMessage severity(final Severity severity);

}
