/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.medina.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;

@Stateless
public class AccessService extends PersistenceService<Access> {

    /**
     * Get a list of Accesses matching a given accessUserId value
     * 
     * @param accessUserId accessUserId
     * @return List of Access
     */
    public List<Access> getActiveAccessByUserId(String accessUserId) {
        return getEntityManager().createNamedQuery("Access.getAccessesByUserId", Access.class).setParameter("accessUserId", accessUserId).getResultList();
    }

    public boolean isDuplicateAndOverlaps(Access access) {
        return findByUserIdAndSubscription(access.getAccessUserId(),access.getSubscription(),access.getStartDate(),access.getEndDate()) != null;
    }

    public Access findByUserIdAndSubscription(String accessUserId, Subscription subscription, Date startDate, Date endDate) {
        try {
            String selectAccessByUserIdSubscriptionIdStartEndDateQuery ="SELECT a FROM " + Access.class.getName() +" a"
                    + " WHERE a.accessUserId=:accessUserId AND a.subscription.id=:subscriptionId";
            Query query = getEntityManager().createQuery(selectAccessByUserIdSubscriptionIdStartEndDateQuery);
            query.setParameter("accessUserId", accessUserId);
            query.setParameter("subscriptionId", subscription.getId());

            for (Object resultElement : query.getResultList()){
                Access access = (Access) resultElement;
                if(isDuplicateAndOverlaps(startDate, endDate, access)){
                    return access;
                }
            }
            return null;
        } catch (NoResultException e) {
            log.warn("no result found");
            return null;
        }
    }

    private boolean isDuplicateAndOverlaps(Date startDate, Date endDate, Access access2){
        if((access2.getStartDate() == null && access2.getEndDate() == null)
                || (startDate == null && endDate == null )
                || (access2.getStartDate() == null && startDate == null)
                || (access2.getEndDate() == null && endDate == null)){
            return true;
        }

        if(startDate != null){
            return isDateBetween(startDate, access2.getStartDate(), access2.getEndDate()) || endDate != null
                    ? endDate != null && isDateBetween(endDate, access2.getStartDate(), access2.getEndDate())
                    : access2.getStartDate() != null && startDate.before(access2.getStartDate());
        }else if(endDate != null){
            return access2.getStartDate() != null && endDate.after(access2.getStartDate());
        }
        return true;
    }

    private boolean isDateBetween(Date date, Date startDate, Date endDate) {
        return ((startDate != null && (startDate.equals(date) || startDate.before(date)))
                && (endDate != null && (endDate.equals(date) || endDate.after(date))));
    }

    @SuppressWarnings("unchecked")
    public List<Access> listBySubscription(Subscription subscription) {
        QueryBuilder qb = new QueryBuilder(Access.class, "c");
        qb.addCriterionEntity("subscription", subscription);

        try {
            return (List<Access>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to get list Access by subscription", e);
            return null;
        }
    }
}