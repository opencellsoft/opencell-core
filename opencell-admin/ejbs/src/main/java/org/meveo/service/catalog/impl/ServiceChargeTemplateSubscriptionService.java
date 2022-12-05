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
package org.meveo.service.catalog.impl;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ServiceChargeTemplateSubscriptionService extends PersistenceService<ServiceChargeTemplateSubscription> {

    @SuppressWarnings("unchecked")
    public List<ServiceChargeTemplateSubscription> findBySubscriptionChargeTemplate(OneShotChargeTemplate chargeTemplate) {

        QueryBuilder qb = new QueryBuilder(ServiceChargeTemplateSubscription.class, "a");
        qb.addCriterionEntity("chargeTemplate", chargeTemplate);
        

        return (List<ServiceChargeTemplateSubscription>) qb.getQuery(getEntityManager()).getResultList();
    }

    public void removeByServiceTemplate(ServiceTemplate serviceTemplate) {
        Query query = getEntityManager().createQuery("DELETE ServiceChargeTemplateSubscription t WHERE t.serviceTemplate=:serviceTemplate ");
        query.setParameter("serviceTemplate", serviceTemplate);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<ServiceChargeTemplateSubscription> findByWalletTemplate(WalletTemplate walletTemplate) {
        QueryBuilder qb = new QueryBuilder(ServiceChargeTemplateSubscription.class, "s");
        qb.addCriterionEntity("walletTemplate", walletTemplate);
        return qb.find(getEntityManager());
    }
}