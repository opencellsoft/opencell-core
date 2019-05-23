package org.meveo.jpa.event;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jpa.event.internal.core.JpaFlushEventListener;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.service.index.ElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA flush event listener. Flushes pending changes to Elastic Search
 * 
 * @author Andrius Karpavicius
 */
public class FlushEventListener extends JpaFlushEventListener {

    private static final long serialVersionUID = -9043373325952642047L;

    @Override
    protected void postFlush(SessionImplementor session) throws HibernateException {

        super.postFlush(session);

        ElasticClient elasticClient = (ElasticClient) EjbUtils.getServiceInterface("ElasticClient");
        try {
            elasticClient.flushChanges();
        } catch (BusinessException e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Failed to flush ES changes", e);
        }
    }
}