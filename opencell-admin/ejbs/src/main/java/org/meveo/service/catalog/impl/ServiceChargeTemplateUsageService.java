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
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ServiceChargeTemplateUsageService extends PersistenceService<ServiceChargeTemplateUsage> {

	public void removeByServiceTemplate(ServiceTemplate serviceTemplate) {
		Query query = getEntityManager()
				.createQuery(
						"DELETE ServiceChargeTemplateUsage t WHERE t.serviceTemplate=:serviceTemplate ");
		query.setParameter("serviceTemplate", serviceTemplate);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
    public List<ServiceChargeTemplateUsage> findByUsageChargeTemplate(UsageChargeTemplate usageChargeTemplate) {

		QueryBuilder qb = new QueryBuilder(ServiceChargeTemplateUsage.class, "a");
		qb.addCriterionEntity("chargeTemplate", usageChargeTemplate);
		

		return (List<ServiceChargeTemplateUsage>) qb.getQuery(getEntityManager()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ServiceChargeTemplateUsage> findByServiceTemplate(ServiceTemplate serviceTemplate) {
		QueryBuilder qb = new QueryBuilder(ServiceChargeTemplateUsage.class, "s");
		qb.addCriterionEntity("s.serviceTemplate", serviceTemplate);

		try {
			return (List<ServiceChargeTemplateUsage>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("failed to find ServiceChargeTemplateUsage by ServiceTemplate ",e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceChargeTemplateUsage> findByWalletTemplate(WalletTemplate walletTemplate){
		QueryBuilder qb=new QueryBuilder(ServiceChargeTemplateUsage.class,"u");
		qb.addCriterionEntity("walletTemplate", walletTemplate);
		return qb.find(getEntityManager());
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceChargeTemplateUsage> findByCounterTemplate(CounterTemplate counterTemplate){
		QueryBuilder qb=new QueryBuilder(ServiceChargeTemplateUsage.class,"u");
		qb.addCriterionEntity("counterTemplate", counterTemplate);
		return qb.find(getEntityManager());
	}

}
