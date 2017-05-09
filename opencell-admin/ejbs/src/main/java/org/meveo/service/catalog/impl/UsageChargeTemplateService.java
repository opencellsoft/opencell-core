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
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class UsageChargeTemplateService extends ChargeTemplateService<UsageChargeTemplate> {

	@Inject
	private RatingCacheContainerProvider ratingCacheContainerProvider;

	@Override
	public void create(UsageChargeTemplate e) throws BusinessException {
		super.create(e);
		ratingCacheContainerProvider.createOrUpdateUsageChargeTemplateInCache(e, null);
	}

	@Override
	public UsageChargeTemplate update(UsageChargeTemplate e) throws BusinessException {
		e = super.update(e);
		ratingCacheContainerProvider.createOrUpdateUsageChargeTemplateInCache(e, null);
		return e;
	}

	@SuppressWarnings("unchecked")
	public List<UsageChargeTemplate> findByPrefix(EntityManager em, String usageChargePrefix) {
		QueryBuilder qb = new QueryBuilder(UsageChargeTemplate.class, "a");
		qb.like("code", usageChargePrefix, QueryLikeStyleEnum.MATCH_BEGINNING, true);

		return (List<UsageChargeTemplate>) qb.getQuery(em).getResultList();
	}

	public List<UsageChargeTemplate> findAssociatedToEDRTemplate(TriggeredEDRTemplate triggeredEDRTemplate) {
		return getEntityManager().createNamedQuery("UsageChargeTemplate.getWithTemplateEDR", UsageChargeTemplate.class)
				.setParameter("edrTemplate", triggeredEDRTemplate).getResultList();
	}

	public int getNbrUsagesChrgWithNotPricePlan() {
		return ((Long) getEntityManager()
				.createNamedQuery("usageChargeTemplate.getNbrUsagesChrgWithNotPricePlan", Long.class)
				.getSingleResult()).intValue();
	}

	public List<UsageChargeTemplate> getUsagesChrgWithNotPricePlan() {
		return (List<UsageChargeTemplate>) getEntityManager()
				.createNamedQuery("usageChargeTemplate.getUsagesChrgWithNotPricePlan", UsageChargeTemplate.class)
				.getResultList();
	}

	public int getNbrUsagesChrgNotAssociated() {
		return ((Long) getEntityManager()
				.createNamedQuery("usageChargeTemplate.getNbrUsagesChrgNotAssociated", Long.class)
				.getSingleResult()).intValue();
	}

	public List<UsageChargeTemplate> getUsagesChrgNotAssociated() {
		return (List<UsageChargeTemplate>) getEntityManager()
				.createNamedQuery("usageChargeTemplate.getUsagesChrgNotAssociated", UsageChargeTemplate.class)
				.getResultList();
	}

}