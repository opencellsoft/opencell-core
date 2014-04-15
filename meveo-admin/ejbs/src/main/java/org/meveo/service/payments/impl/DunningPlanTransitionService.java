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
