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
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;

/**
 * Charge Template service implementation.
 */
@Stateless
public class OneShotChargeTemplateService extends ChargeTemplateService<OneShotChargeTemplate> {
	

	/**
	 * @return list of one shot charge template.
	 */
	@SuppressWarnings("unchecked")
	public List<OneShotChargeTemplate> getTerminationChargeTemplates() {

		Query query = new QueryBuilder(OneShotChargeTemplate.class, "c", null)
				.addCriterionEnum("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.TERMINATION)
				.getQuery(getEntityManager());
		return query.getResultList();
	}

	
	/**
	 * @return list of one shot charge template.
	 */
	@SuppressWarnings("unchecked")
	public List<OneShotChargeTemplate> getSubscriptionChargeTemplates() {

		Query query = new QueryBuilder(OneShotChargeTemplate.class, "c", null)
				.addCriterionEnum("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.SUBSCRIPTION)
				.getQuery(getEntityManager());
		return query.getResultList();
	}

	public int getNbrSubscriptionChrgNotAssociated() {
		return ((Long) getEntityManager()
				.createNamedQuery("oneShotChargeTemplate.getNbrSubscriptionChrgNotAssociated", Long.class)
				.setParameter("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.SUBSCRIPTION)
				.getSingleResult()).intValue();
	}

	public List<OneShotChargeTemplate> getSubscriptionChrgNotAssociated() {
		return (List<OneShotChargeTemplate>) getEntityManager()
				.createNamedQuery("oneShotChargeTemplate.getSubscriptionChrgNotAssociated", OneShotChargeTemplate.class)
				.setParameter("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.SUBSCRIPTION)
				.getResultList();
	}

	public int getNbrTerminationChrgNotAssociated() {
		return ((Long) getEntityManager()
				.createNamedQuery("oneShotChargeTemplate.getNbrTerminationChrgNotAssociated", Long.class)
				.setParameter("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.TERMINATION)
				.getSingleResult()).intValue();
	}

	public List<OneShotChargeTemplate> getTerminationChrgNotAssociated() {
		return (List<OneShotChargeTemplate>) getEntityManager()
				.createNamedQuery("oneShotChargeTemplate.getTerminationChrgNotAssociated", OneShotChargeTemplate.class)
				.setParameter("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.TERMINATION)
				.getResultList();
	}
}