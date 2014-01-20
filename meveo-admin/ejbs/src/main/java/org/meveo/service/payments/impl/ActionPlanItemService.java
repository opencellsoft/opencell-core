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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.service.base.PersistenceService;



@Stateless @LocalBean
public class ActionPlanItemService extends PersistenceService<ActionPlanItem> {
	
	
	@SuppressWarnings("unchecked")
	public List<ActionPlanItem> getActionPlanItems(DunningPlan dunningPlan, DunningPlanTransition dunningPlanTransition) {
		List<ActionPlanItem> actionPlanItems = new ArrayList<ActionPlanItem>();
		try {
			actionPlanItems = (List<ActionPlanItem>) getEntityManager()
					.createQuery(
							"from " + ActionPlanItem.class.getSimpleName()
									+ " where dunningPlan.id=:dunningPlanId and dunningLevel=:dunningLevel order by itemOrder")
					.setParameter("dunningPlanId", dunningPlan.getId()).setParameter("dunningLevel", dunningPlanTransition.getDunningLevelTo()).getResultList();
		} catch (Exception e) {
		}
		return actionPlanItems;
	}

}
