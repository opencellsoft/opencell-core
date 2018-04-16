package org.meveo.event.monitoring;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Topic;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

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

    public void publishEvent(IEntity entity, CrudActionEnum action) {

        if (!EjbUtils.isRunningInClusterMode()) {
            return;
        }

        try {
            String code = entity instanceof BusinessEntity ? ((BusinessEntity) entity).getCode() : null;
            ClusterEventDto eventDto = new ClusterEventDto(ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()), (Long) entity.getId(), code, action,
                EjbUtils.getCurrentClusterNode(), currentUser.getProviderCode(), currentUser.getUserName());
            log.trace("Publishing data synchronization between cluster nodes event {}", eventDto);

            context.createProducer().send(topic, eventDto);

        } catch (Exception e) {
            log.error("Failed to publish data synchronization between cluster nodes event", e);
        }
    }
}