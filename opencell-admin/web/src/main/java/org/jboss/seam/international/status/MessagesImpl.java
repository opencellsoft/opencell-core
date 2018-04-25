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

import java.util.Iterator;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.seam.international.status.builder.BundleKey;

/**
 * An implementation of the {@link Messages} interface.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RequestScoped
public class MessagesImpl implements Messages {
    private static final long serialVersionUID = -2908193057765795662L;

    @Inject
    private MessageFactory factory;

    // private Logger log = LoggerFactory.getLogger(this.getClass());

    public void clear() {
        Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public boolean isEmpty() {
        return FacesContext.getCurrentInstance().getMessages().hasNext();
    }

    private void enqueueBuilder(final MessageBuilder builder) {
        Message message = builder.build();
        FacesContext.getCurrentInstance().addMessage(message.getTargets(), new FacesMessage(message.getSeverity(), message.getText(), message.getDetail()));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    /*
     * Bundle Factory Methods
     */
    public void info(final BundleKey message) {
        enqueueBuilder(factory.info(message));
    }

    public void info(final BundleKey message, final Object... params) {
        enqueueBuilder(factory.info(message, params));
    }

    public void warn(final BundleKey message) {
        enqueueBuilder(factory.warn(message));
    }

    public void warn(final BundleKey message, final Object... params) {
        enqueueBuilder(factory.warn(message, params));
    }

    public void error(final BundleKey message) {
        enqueueBuilder(factory.error(message));
    }

    public void error(final BundleKey message, final Object... params) {
        enqueueBuilder(factory.error(message, params));
    }

    public void fatal(final BundleKey message) {
        enqueueBuilder(factory.fatal(message));
    }

    public void fatal(final BundleKey message, final Object... params) {
        enqueueBuilder(factory.fatal(message, params));
    }

    /*
     * Template Factory Methods
     */
    public void info(final String message) {
        enqueueBuilder(factory.info(message));
    }

    public void info(final String message, final Object... params) {
        enqueueBuilder(factory.info(message, params));
    }

    public void warn(final String message) {
        enqueueBuilder(factory.warn(message));
    }

    public void warn(final String message, final Object... params) {
        enqueueBuilder(factory.warn(message, params));
    }

    public void error(final String message) {
        enqueueBuilder(factory.error(message));
    }

    public void error(final String message, final Object... params) {
        enqueueBuilder(factory.error(message, params));
    }

    public void fatal(final String message) {
        enqueueBuilder(factory.fatal(message));
    }

    public void fatal(final String message, final Object... params) {
        enqueueBuilder(factory.fatal(message, params));
    }

}
