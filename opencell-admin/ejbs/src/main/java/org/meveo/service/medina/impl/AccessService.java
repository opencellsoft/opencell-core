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
        List<Access> accesses = retreiveAccessByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription());

        if(accesses.isEmpty()){
            return false;
        }else if(access.getStartDate() == null && access.getEndDate() == null){
            return true;
        }

        for (Access element : accesses){
            if((access.getStartDate() != null && isDateBetween(access.getStartDate(), element.getStartDate(), element.getEndDate()))
                    || (access.getEndDate() != null && isDateBetween(access.getEndDate(), element.getStartDate(), element.getEndDate()))){
                return true;
            }
        }
        return false;
    }

    private List<Access> retreiveAccessByUserIdAndSubscription(String accessUserId, Subscription subscription) {
        String selectAccessByUserIdSubscriptionIdStartEndDateQuery ="SELECT a FROM " + Access.class.getName() +" a"
                + " WHERE a.accessUserId=:accessUserId AND a.subscription.id=:subscriptionId";
        Query query = getEntityManager().createQuery(selectAccessByUserIdSubscriptionIdStartEndDateQuery);
        query.setParameter("accessUserId", accessUserId);
        query.setParameter("subscriptionId", subscription.getId());
        return query.getResultList();
    }

    @Deprecated
    public Access findByUserIdAndSubscription(String accessUserId, Subscription subscription) {
        return findByUserIdAndSubscription(accessUserId, subscription, new Date(), new Date());
    }

    public Access findByUserIdAndSubscription(String accessUserId, Subscription subscription, Date date) {
        return findByUserIdAndSubscription(accessUserId, subscription, date, date);
    }

    public Access findByUserIdAndSubscription(String accessUserId, Subscription subscription, Date startDate, Date endDate) {
        try {
            List<Access> accesses = retreiveAccessByUserIdAndSubscription(accessUserId, subscription);
            for (Object resultElement : accesses){
                Access access = (Access) resultElement;
                if(isEqualsOrOverlaps(startDate, endDate, access)){
                    return access;
                }
            }
            return null;
        } catch (NoResultException e) {
            log.warn("no result found");
            return null;
        }
    }

    private boolean isEqualsOrOverlaps(Date startDate, Date endDate, Access access2){
        if(access2.getStartDate() == null && access2.getEndDate() == null){
            return true;
        }
        if(startDate != null){
            return isDateBetween(startDate, access2.getStartDate(), access2.getEndDate())
                    ? endDate == null || isDateBetween(endDate, access2.getStartDate(), access2.getEndDate())
                    : false;
        }else if(endDate != null){
            return access2.getStartDate() != null && endDate.after(access2.getStartDate());
        }
        return true;
    }

    private boolean isDateBetween(Date date, Date startDate, Date endDate) {
        return ((startDate != null && (startDate.getTime() == date.getTime() || startDate.before(date)))
                && (endDate != null && (endDate.getTime() == date.getTime() || endDate.after(date))))
                || (endDate == null && startDate == null)
                || (startDate != null && endDate == null && (startDate.getTime() == date.getTime() || startDate.before(date)))
                || (endDate != null && startDate == null && (endDate.getTime() == date.getTime() || endDate.after(date)));
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