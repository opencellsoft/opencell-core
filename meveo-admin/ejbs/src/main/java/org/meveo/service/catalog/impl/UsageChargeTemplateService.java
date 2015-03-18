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
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class UsageChargeTemplateService extends
		ChargeTemplateService<UsageChargeTemplate> {

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

	public void create(UsageChargeTemplate e, User creator, Provider provider) {
		super.create(e, creator, provider);
		ratingCacheContainerProvider.updateUsageChargeTemplateInCache(e);
	}

	public UsageChargeTemplate update(UsageChargeTemplate e, User updater) {
		e = super.update(e, updater);
		ratingCacheContainerProvider.updateUsageChargeTemplateInCache(e);
		return e;
	}

	@SuppressWarnings("unchecked")
	public List<UsageChargeTemplate> findByPrefix(EntityManager em,
			String usageChargePrefix, Provider provider) {
		QueryBuilder qb = new QueryBuilder(UsageChargeTemplate.class, "a");
		qb.like("code", usageChargePrefix, QueryLikeStyleEnum.MATCH_BEGINNING, true);

		return (List<UsageChargeTemplate>) qb.getQuery(em).getResultList();
    }

    public List<UsageChargeTemplate> findAssociatedToEDRTemplate(TriggeredEDRTemplate triggeredEDRTemplate) {
        return getEntityManager().createNamedQuery("UsageChargeTemplate.getWithTemplateEDR", UsageChargeTemplate.class).setParameter("edrTemplate", triggeredEDRTemplate)
            .getResultList();
    }
}