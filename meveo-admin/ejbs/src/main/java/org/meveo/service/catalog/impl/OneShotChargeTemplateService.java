/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.crm.Provider;

/**
 * Charge Template service implementation.
 */
@Stateless
@LocalBean
public class OneShotChargeTemplateService extends
		ChargeTemplateService<OneShotChargeTemplate> {

	/**
	 * @see org.meveo.service.catalog.local.OneShotChargeTemplateServiceLocal#getTerminationChargeTemplates()
	 */
	@SuppressWarnings("unchecked")
	public List<OneShotChargeTemplate> getTerminationChargeTemplates() {

		Query query = new QueryBuilder(OneShotChargeTemplate.class, "c", null,
				getCurrentProvider()).addCriterionEnum(
				"oneShotChargeTemplateType",
				OneShotChargeTemplateTypeEnum.TERMINATION).getQuery(
				getEntityManager());
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.catalog.local.OneShotChargeTemplateServiceLocal#getSubscriptionChargeTemplates()
	 */
	@SuppressWarnings("unchecked")
	public List<OneShotChargeTemplate> getSubscriptionChargeTemplates() {

		Query query = new QueryBuilder(OneShotChargeTemplate.class, "c", null,
				getCurrentProvider()).addCriterionEnum(
				"oneShotChargeTemplateType",
				OneShotChargeTemplateTypeEnum.SUBSCRIPTION).getQuery(
				getEntityManager());
		return query.getResultList();
	}

	public void removeByCode(EntityManager em, String code, Provider provider) {
		Query query = em
				.createQuery("DELETE OneShotChargeTemplate t WHERE t.code=:code AND t.provider=:provider");
		query.setParameter("code", code);
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<OneShotChargeTemplate> getSubscriptionChargeTemplates(
			Provider provider) {
		QueryBuilder qb = new QueryBuilder(OneShotChargeTemplate.class, "t");
		qb.addCriterionEntity("provider", provider);

		try {
			return (List<OneShotChargeTemplate>) qb
					.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

}