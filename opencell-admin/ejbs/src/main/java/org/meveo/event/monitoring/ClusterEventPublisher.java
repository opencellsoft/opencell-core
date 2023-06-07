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

package org.meveo.event.monitoring;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSProducer;
import javax.jms.Topic;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * Handle data synchronization between cluster nodes. Inform about CRUD actions to certain entities. Messages are written to topic "topic/CLUSTEREVENTTOPIC".
 * 
 * Used in cases when each cluster node caches locally some information that needs to be updated when data changes.
 * 
 * @author Andrius Karpavicius
 */
@JMSDestinationDefinitions(value = { @JMSDestinationDefinition(name = "java:/topic/CLUSTEREVENTTOPIC", interfaceName = "javax.jms.Topic", destinationName = "ClusterEventTopic") })
@Stateless
public class ClusterEventPublisher implements Serializable {

    private static final long serialVersionUID = 4434372450314613654L;

    @Inject
    private Logger log;

    @Inject
    private JMSContext context;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Resource(lookup = "java:/topic/CLUSTEREVENTTOPIC")
    private Topic topic;

    /**
     * Publish event about some action on a given entity
     * 
     * @param entity Entity that triggered event
     * @param action Action performed
     */
    public void publishEvent(IEntity entity, CrudActionEnum action) {
        publishEvent(entity, action, null);
    }

    /**
     * Publish event about some action on a given entity
     * 
     * @param entity Entity that triggered event
     * @param action Action performed
     * @param additionalInformation Additional information about the action
     */
    public void publishEvent(IEntity entity, CrudActionEnum action, Map<String, Object> additionalInformation) {

        if (!EjbUtils.isRunningInClusterMode()) {
            return;
        }

        try {
            String code = entity instanceof BusinessEntity ? ((BusinessEntity) entity).getCode() : null;
            ClusterEventDto eventDto = new ClusterEventDto(ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), (Long) entity.getId(), code, action, EjbUtils.getCurrentClusterNode(),
                currentUser.getProviderCode(), currentUser.getUserName(), additionalInformation);
            log.debug("Publishing data synchronization between cluster nodes event {}", eventDto);

            // For create and update CRUD actions, send message with a delivery delay of two seconds, so data is saved to DB already before another node process the message
            if (action == CrudActionEnum.create || action == CrudActionEnum.update) {
                context.createProducer().setDeliveryDelay(2000L).send(topic, eventDto);
            } else {
                context.createProducer().send(topic, eventDto);
            }

        } catch (Exception e) {
            log.error("Failed to publish data synchronization between cluster nodes event", e);
        }
    }

    /**
     * Publish event about some action on a given entity
     * 
     * @param entity Entity that triggered event
     * @param action Action performed
     * @param additionalInformation Additional information about the action
     */
    @Asynchronous
    public void publishEventAsync(IEntity entity, CrudActionEnum action, Map<String, Object> additionalInformation, String providerCode, String username) {

        if (!EjbUtils.isRunningInClusterMode()) {
            return;
        }

        try {
            String code = entity instanceof BusinessEntity ? ((BusinessEntity) entity).getCode() : null;
            ClusterEventDto eventDto = new ClusterEventDto(ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), (Long) entity.getId(), code, action, EjbUtils.getCurrentClusterNode(), providerCode,
                username, additionalInformation);
            log.debug("Publishing data synchronization between cluster nodes event {}", eventDto);

            context.createProducer().send(topic, eventDto);

        } catch (Exception e) {
            log.error("Failed to publish data synchronization between cluster nodes event", e);
        }
    }

    /**
     * Publish multiple items to a queue or a topic
     * 
     * @param <T>
     * @param destination Queue or a topic to publish to
     * @param iterator Iterator of a data to publish
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> void publish(Destination destination, Iterator<T> iterator) {

        JMSProducer jmsProducer = context.createProducer();
        T itemToProcess = iterator.next();
        while (itemToProcess != null) {
            jmsProducer.send(destination, (Serializable) itemToProcess);
            itemToProcess = iterator.next();
        }
    }
}