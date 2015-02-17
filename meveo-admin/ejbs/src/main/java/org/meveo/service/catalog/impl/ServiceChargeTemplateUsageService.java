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
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ServiceChargeTemplateUsageService extends PersistenceService<ServiceChargeTemplateUsage> {

	public void removeByPrefix(EntityManager em, String prefix, Provider provider) {
		Query query = em.createQuery("DELETE ServiceChargeTemplateUsage t WHERE t.chargeTemplate.code LIKE '" + prefix
				+ "%' AND t.provider=:provider");
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	public void removeByServiceTemplate(ServiceTemplate serviceTemplate, Provider provider) {
		Query query = getEntityManager()
				.createQuery(
						"DELETE ServiceChargeTemplateUsage t WHERE t.serviceTemplate=:serviceTemplate AND t.provider=:provider");
		query.setParameter("serviceTemplate", serviceTemplate);
		query.setParameter("provider", provider);
		query.executeUpdate();
	}

	public List<ServiceChargeTemplateUsage> findByUsageChargeTemplate(UsageChargeTemplate usageChargeTemplate,
			Provider provider) {
		return findByUsageChargeTemplate(getEntityManager(), usageChargeTemplate, provider);
	}

	@SuppressWarnings("unchecked")
	public List<ServiceChargeTemplateUsage> findByUsageChargeTemplate(EntityManager em,
			UsageChargeTemplate usageChargeTemplate, Provider provider) {
		QueryBuilder qb = new QueryBuilder(ServiceChargeTemplateUsage.class, "a");
		qb.addCriterionEntity("chargeTemplate", usageChargeTemplate);
		qb.addCriterionEntity("provider", provider);

		return (List<ServiceChargeTemplateUsage>) qb.getQuery(em).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ServiceChargeTemplateUsage> findByServiceTemplate(ServiceTemplate serviceTemplate, Provider provider) {
		QueryBuilder qb = new QueryBuilder(ServiceChargeTemplateUsage.class, "s");
		qb.addCriterionEntity("s.serviceTemplate", serviceTemplate);
		qb.addCriterionEntity("s.provider", provider);

		try {
			return (List<ServiceChargeTemplateUsage>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn(e.getMessage());
			return null;
		}
	}

}
