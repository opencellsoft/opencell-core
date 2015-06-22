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
package org.meveo.service.payments.impl;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class DunningPlanService extends PersistenceService<DunningPlan> {

	@SuppressWarnings("unchecked")
	public List<DunningPlan> getDunningPlans(Provider provider) {
		return (List<DunningPlan>) getEntityManager()
				.createQuery(
						"from " + DunningPlan.class.getSimpleName()
								+ " where status=:status and provider=:provider")
				.setParameter("status", DunningPlanStatusEnum.ACTIVE)
				.setParameter("provider", provider)
				.getResultList();
	}

}
