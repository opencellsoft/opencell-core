/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

import java.util.List;

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

	public List<OneShotChargeTemplate> getSubscriptionChargeTemplates(
			Provider provider) {
		return getSubscriptionChargeTemplates(getEntityManager(), provider);
	}

	@SuppressWarnings("unchecked")
	public List<OneShotChargeTemplate> getSubscriptionChargeTemplates(
			EntityManager em, Provider provider) {
		QueryBuilder qb = new QueryBuilder(OneShotChargeTemplate.class, "t");
		qb.addCriterionEntity("provider", provider);

		try {
			return (List<OneShotChargeTemplate>) qb.getQuery(em)
					.getResultList();
		} catch (NoResultException e) {
			log.warn("failed to get subscriptionChargeTemplates",e);
			return null;
		}
	}


	public int getNbrOneShotWithNotPricePlan(Provider provider) { 
		return ((Long)getEntityManager().
				createNamedQuery("oneShotChargeTemplate.getNbrOneShotWithNotPricePlan",Long.class)
				.setParameter("provider", provider).getSingleResult()).intValue();
		}
	public  List<OneShotChargeTemplate> getOneShotChrgWithNotPricePlan(Provider provider) { 
		return (List<OneShotChargeTemplate>)getEntityManager().createNamedQuery("oneShotChargeTemplate.getOneShotWithNotPricePlan",OneShotChargeTemplate.class)
				.setParameter("provider", provider).getResultList();
		}
	
	public int getNbrSubscriptionChrgNotAssociated(Provider provider) { 
		return ((Long)getEntityManager().
				createNamedQuery("oneShotChargeTemplate.getNbrSubscriptionChrgNotAssociated",Long.class)
				.setParameter("oneShotChargeTemplateType",OneShotChargeTemplateTypeEnum.SUBSCRIPTION)
				.setParameter("provider", provider).getSingleResult()).intValue();
		}
	
	public  List<OneShotChargeTemplate> getSubscriptionChrgNotAssociated(Provider provider) { 
		return (List<OneShotChargeTemplate>)getEntityManager().createNamedQuery("oneShotChargeTemplate.getSubscriptionChrgNotAssociated",OneShotChargeTemplate.class)
				.setParameter("oneShotChargeTemplateType",OneShotChargeTemplateTypeEnum.SUBSCRIPTION)
				.setParameter("provider", provider).getResultList();
		      }
	
	public int getNbrTerminationChrgNotAssociated(Provider provider) { 
		return ((Long)getEntityManager().createNamedQuery("oneShotChargeTemplate.getNbrTerminationChrgNotAssociated",Long.class)
				.setParameter("oneShotChargeTemplateType",OneShotChargeTemplateTypeEnum.TERMINATION)
				.setParameter("provider", provider).getSingleResult()).intValue();
		      }
	
	public  List<OneShotChargeTemplate> getTerminationChrgNotAssociated(Provider provider) { 
		return (List<OneShotChargeTemplate>)getEntityManager().createNamedQuery("oneShotChargeTemplate.getTerminationChrgNotAssociated",OneShotChargeTemplate.class)
				.setParameter("oneShotChargeTemplateType",OneShotChargeTemplateTypeEnum.TERMINATION)
				.setParameter("provider", provider).getResultList();
		      }
}