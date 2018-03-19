/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.meveo.event.monitoring;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.security.Role;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.script.ScriptInstanceService;
import org.slf4j.Logger;

/**
 * A Message Driven Bean to handle data synchronization between cluster nodes. Messages are read from a topic "topic/CLUSTEREVENTTOPIC".
 * 
 * Currently two event types are supported - job instance and script instance refresh
 * 
 * @author Andrius Karpavicius
 */
@MessageDriven(name = "ClusterEventMonitor", activationConfig = { @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/CLUSTEREVENTTOPIC"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ClusterEventMonitor implements MessageListener {

    @Inject
    private Logger log;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message rcvMessage) {
        try {
            if (rcvMessage instanceof ObjectMessage) {
                ClusterEventDto eventDto = (ClusterEventDto) ((ObjectMessage) rcvMessage).getObject();
                if (EjbUtils.getCurrentClusterNode().equals(eventDto.getSourceNode())) {
                    return;
                }
                log.info("Received cluster synchronization event message {}", eventDto);

                processClusterEvent(eventDto);

            } else {
                log.warn("Unhandled cluster synchronization event message type: " + rcvMessage.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Failed to process JMS message", e);
        }
    }

    /**
     * Process incoming data synchronization between cluster nodes event
     * 
     * @param eventDto Data synchronization between cluster nodes event.
     */
    private void processClusterEvent(ClusterEventDto eventDto) {

        currentUserProvider.forceAuthentication(eventDto.getUserName(), eventDto.getProviderCode());

        if (eventDto.getClazz().equals(ScriptInstance.class.getSimpleName())) {
            scriptInstanceService.clearCompiledScripts(eventDto.getCode());

        } else if (eventDto.getClazz().equals(JobInstance.class.getSimpleName())) {
            jobInstanceService.scheduleUnscheduleJob(eventDto.getId());

        } else if (eventDto.getClazz().equals(Role.class.getSimpleName())) {
            currentUserProvider.invalidateRoleToPermissionMapping();

        }
    }
}