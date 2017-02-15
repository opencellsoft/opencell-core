package org.meveo.jpa.event;

/**
 * JPA flush event listener. Flushes pending changes to Elastic Search
 * 
 * @author Andrius Karpavicius
 */
public class FlushEventListener{// extends EJB3FlushEventListener {

    private static final long serialVersionUID = -9043373325952642047L;
//
//    @Override
//    protected void postFlush(SessionImplementor session) throws HibernateException {
//
//        super.postFlush(session);
//
//        ElasticClient elasticClient = (ElasticClient) EjbUtils.getServiceInterface("ElasticClient");
//        elasticClient.flushChanges();
//    }
}