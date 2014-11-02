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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.service.base.PersistenceService;

@Stateless
public class ActionPlanItemService extends PersistenceService<ActionPlanItem> {

	@SuppressWarnings("unchecked")
	public List<ActionPlanItem> getActionPlanItems(DunningPlan dunningPlan,
			DunningPlanTransition dunningPlanTransition) {
		List<ActionPlanItem> actionPlanItems = new ArrayList<ActionPlanItem>();
		try {
			actionPlanItems = (List<ActionPlanItem>) getEntityManager()
					.createQuery(
							"from "
									+ ActionPlanItem.class.getSimpleName()
									+ " where dunningPlan.id=:dunningPlanId and dunningLevel=:dunningLevel order by itemOrder")
					.setParameter("dunningPlanId", dunningPlan.getId())
					.setParameter("dunningLevel",
							dunningPlanTransition.getDunningLevelTo())
					.getResultList();
		} catch (Exception e) {
		}
		return actionPlanItems;
	}

}
