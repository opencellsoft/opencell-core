/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.service.base.PersistenceService;


/**
 * @author AITYAAZZA
 *
 */
@Stateless @LocalBean
public class DunningPlanTransitionService extends PersistenceService<DunningPlanTransition> {
	



	public DunningPlanTransition getDunningPlanTransition(DunningLevelEnum dunningLevelFrom, DunningLevelEnum dunningLevelTo, DunningPlan dunningPlan) {
		DunningPlanTransition dunningPlanTransition = null;
		try {
			dunningPlanTransition = (DunningPlanTransition) getEntityManager()
					.createQuery(
							"from " + DunningPlanTransition.class.getSimpleName()
									+ " where dunningLevelFrom=:dunningLevelFrom and dunningLevelTo=:dunningLevelTo and dunningPlan.id=:dunningPlanId")
					.setParameter("dunningLevelFrom", dunningLevelFrom).setParameter("dunningLevelTo", dunningLevelTo)
					.setParameter("dunningPlanId", dunningPlan.getId()).getSingleResult();
		} catch (Exception e) {
		}
		return dunningPlanTransition;
	}



}
