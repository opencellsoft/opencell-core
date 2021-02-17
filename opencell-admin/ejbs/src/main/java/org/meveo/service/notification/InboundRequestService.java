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
        List<InboundRequest> requests = em.createNamedQuery("InboundRequest.getRequestsToPurgeByDate", InboundRequest.class).setParameter("date", date).getResultList();
        for (InboundRequest request : requests) {
            em.remove(request);
            itemsDeleted++;
        }

        log.info("Removed {} Inbound requests which date is older then a {} date", itemsDeleted, date);

        return itemsDeleted;
    }
}