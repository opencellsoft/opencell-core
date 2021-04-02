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
     * @author Mohamed Ali Hammal
     * @param date Date to check
     * @return A number of Inbound requests that were removed
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteRequests(Date date) {
        log.debug("Removing Inbound requests which date is older then a {} date", date);

        long itemsDeleted = 0;
        EntityManager em = getEntityManager();
        String query = "delete from InboundRequest ir WHERE ir.auditable.created<=:date";
        itemsDeleted = em.createQuery(query).setParameter("date", date).executeUpdate();
        log.info("Removed {} Inbound requests which date is older then a {} date", itemsDeleted, date);

        return itemsDeleted;
    }
}