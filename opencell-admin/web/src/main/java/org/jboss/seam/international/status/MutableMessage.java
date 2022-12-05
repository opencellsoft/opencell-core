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

import jakarta.faces.application.FacesMessage.Severity;

/**
 * A stateful {@link Message} object.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ssachtleben@gmail.com">Sebastian Sachtleben</a>
 */
public interface MutableMessage extends Message {
    /**
     * Set the {@link Severity} representing the severity of this message.
     * @param severity serverity level
     */
    void setSeverity(Severity severity);

    /**
     * Set the message text.
     * @param summary text to set
     */
    void setText(String summary);

    /**
     * Set the message detail.
     * @param detail detail of message
     */
    void setDetail(String detail);

    /**
     * Set the targets for which a given view-layer or consumer should display this message, or to which this message should be
     * attached.
     * @param targets targets
     */
    void setTargets(String targets);
}
