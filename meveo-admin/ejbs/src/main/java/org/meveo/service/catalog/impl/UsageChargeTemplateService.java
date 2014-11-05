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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.UsageRatingService;

/**
 * Charge Template service implementation.
 * 
 */
@Stateless
public class UsageChargeTemplateService extends
		ChargeTemplateService<UsageChargeTemplate> {

	@EJB
	UsageRatingService usageRatingService;

	public void create(UsageChargeTemplate e) {
		super.create(e);
		usageRatingService.updateTemplateCache(e);
	}

	public void create(UsageChargeTemplate e, User creator) {
		super.create(e, creator);
		usageRatingService.updateTemplateCache(e);
	}

	public void create(UsageChargeTemplate e, User creator, Provider provider) {
		super.create(e, creator, provider);
		usageRatingService.updateTemplateCache(e);
	}

	public void update(UsageChargeTemplate e) {
		super.update(e);
		usageRatingService.updateTemplateCache(e);
	}

	public void update(UsageChargeTemplate e, User updater) {
		super.update(e, updater);
		usageRatingService.updateTemplateCache(e);
	}

	@SuppressWarnings("unchecked")
	public List<UsageChargeTemplate> findByPrefix(EntityManager em,
			String usageChargePrefix, Provider provider) {
		QueryBuilder qb = new QueryBuilder(UsageChargeTemplate.class, "a");
		qb.like("code", usageChargePrefix, 1, true);

		return (List<UsageChargeTemplate>) qb.getQuery(em).getResultList();
	}

}
