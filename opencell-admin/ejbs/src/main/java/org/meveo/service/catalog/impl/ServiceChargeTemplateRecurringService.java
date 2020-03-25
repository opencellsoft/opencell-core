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

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.PersistenceService;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
@Stateless
public class ServiceChargeTemplateRecurringService extends PersistenceService<ServiceChargeTemplateRecurring> {

    public void removeByServiceTemplate(ServiceTemplate serviceTemplate) {
        Query query = getEntityManager().createQuery("DELETE ServiceChargeTemplateRecurring t WHERE t.serviceTemplate=:serviceTemplate ");
        query.setParameter("serviceTemplate", serviceTemplate);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<ServiceChargeTemplateRecurring> findByWalletTemplate(WalletTemplate walletTemplate) {
        QueryBuilder qb = new QueryBuilder(ServiceChargeTemplateRecurring.class, "r");
        qb.addCriterionEntity("walletTemplate", walletTemplate);
        return qb.find(getEntityManager());
    }
    //
    // @Override
    // public void remove(ServiceChargeTemplateRecurring e) throws BusinessException {
    // refreshOrRetrieve(e);
    // super.remove(e);
    // }
    
    /**
     * Gets the list service charge template recurring of from counterTemplate.
     * 
     * @param counterTemplate the counter template
     * @return list service charge template recurring
     */
    @SuppressWarnings("unchecked")
    public List<ServiceChargeTemplateRecurring> findByCounterTemplate(CounterTemplate counterTemplate){
        QueryBuilder qb=new QueryBuilder(ServiceChargeTemplateRecurring.class,"r");
        qb.addCriterionEntity("counterTemplate", counterTemplate);
        return qb.find(getEntityManager());
    }
}