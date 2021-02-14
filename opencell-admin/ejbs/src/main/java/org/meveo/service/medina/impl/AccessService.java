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
package org.meveo.service.medina.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.mediation.Access;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import static  org.meveo.model.shared.DateUtils.isPeriodsOverlap;

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
        List<Access> accesses = retrieveAccessByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription());
        if(accesses.isEmpty()){
            return false;
        }else if(access.getStartDate() == null && access.getEndDate() == null){
            return true;
        }
        for (Access element : accesses){
            if(isPeriodsOverlap(access.getStartDate(), access.getEndDate(), element.getStartDate(), element.getEndDate())){
                return true;
            }
        }
        return false;
    }

    private List<Access> retrieveAccessByUserIdAndSubscription(String accessUserId, Subscription subscription) {
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
        try {
            List<Access> accesses = retrieveAccessByUserIdAndSubscription(accessUserId, subscription);
            for (Object resultElement : accesses){
                Access access = (Access) resultElement;
                if(DateUtils.isDateWithinPeriod(date, access.getStartDate(), access.getEndDate())){
                    return access;
                }
            }
            return null;
        } catch (NoResultException e) {
            log.warn("no result found");
            return null;
        }
    }

    public Access findByUserIdAndSubscription(String accessUserId, Subscription subscription, Date startDate, Date endDate) {
        try {
            List<Access> accesses = retrieveAccessByUserIdAndSubscription(accessUserId, subscription);
            for (Object resultElement : accesses){
                Access access = (Access) resultElement;
                if(isPeriodsOverlap(startDate, endDate, access.getStartDate(), access.getEndDate())){
                    return access;
                }
            }
            return null;
        } catch (NoResultException e) {
            log.warn("no result found");
            return null;
        }
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

    /**
     * Get a count of access points by a parent subscription
     * 
     * @param parent Parent subscription
     * @return A number of child access points
     */
    public long getCountByParent(Subscription parent) {

        return getEntityManager().createNamedQuery("Access.getCountByParent", Long.class).setParameter("parent", parent).getSingleResult();
    }
}