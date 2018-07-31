package org.meveo.service.notification;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import org.meveo.model.notification.InboundRequest;
import org.meveo.service.base.BusinessService;

@Stateless
public class InboundRequestService extends BusinessService<InboundRequest> {

    /**
     * Count Inbound requests which date is older then a given date.
     * 
     * @param date Date to check
     * @return A number of Inbound requests which date is older then a given date
     */
    public long countRequestsToDelete(Date date) {
        long result = getEntityManager().createNamedQuery("InboundRequest.countRequestsToPurgeByDate", Long.class).setParameter("date", date).getSingleResult();
        return result;
    }

    /**
     * Remove Inbound requests which date is older than a given date.
     * 
     * @param date Date to check
     * @return A number of Inbound requests that were removed
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteRequests(Date date) {
        log.debug("Removing Inbound requests which date is older then a {} date", date);

        // Can not delete with a single bulk query as need to remove associated NotificationHistory records and header, cookies records
        long itemsDeleted = 0;
        EntityManager em = getEntityManager();
        List<InboundRequest> requests = em.createNamedQuery("InboundRequest.getRequestsToPurgeByDate", InboundRequest.class).getResultList();
        for (InboundRequest request : requests) {
            em.remove(request);
            itemsDeleted++;
        }

        log.info("Removed {} Inbound requests which date is older then a {} date", itemsDeleted, date);

        return itemsDeleted;
    }
}