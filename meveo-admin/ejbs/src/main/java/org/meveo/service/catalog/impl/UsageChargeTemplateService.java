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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

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
@LocalBean
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
