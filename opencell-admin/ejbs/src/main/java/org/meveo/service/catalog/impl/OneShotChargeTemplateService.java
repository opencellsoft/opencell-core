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

	public int getNbrOneShotWithNotPricePlan() {
		return ((Long) getEntityManager()
				.createNamedQuery("oneShotChargeTemplate.getNbrOneShotWithNotPricePlan", Long.class)
				.getSingleResult()).intValue();
	}

	public List<OneShotChargeTemplate> getOneShotChrgWithNotPricePlan() {
		return (List<OneShotChargeTemplate>) getEntityManager()
				.createNamedQuery("oneShotChargeTemplate.getOneShotWithNotPricePlan", OneShotChargeTemplate.class)
				.getResultList();
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