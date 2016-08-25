package org.meveo.jpa.event;

import org.hibernate.HibernateException;
import org.hibernate.ejb.event.EJB3FlushEventListener;
import org.hibernate.engine.spi.SessionImplementor;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.service.index.ElasticClient;

/**
 * JPA flush event listener. Flushes pending changes to Elastic Search
 * 
 * @author Andrius Karpavicius
 */
public class FlushEventListener extends EJB3FlushEventListener {

    private static final long serialVersionUID = -9043373325952642047L;

//    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void postFlush(SessionImplementor session) throws HibernateException {

        super.postFlush(session);

        ElasticClient elasticClient = (ElasticClient) EjbUtils.getServiceInterface("ElasticClient");
        elasticClient.flushChanges();
    }
}