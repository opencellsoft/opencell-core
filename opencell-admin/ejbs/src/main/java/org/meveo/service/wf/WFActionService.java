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
package org.meveo.service.wf;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WFActionService extends PersistenceService<WFAction> {


	public WFAction findWFActionByUUID(String uuid) {
		WFAction wfAction = null;
		try {
			QueryBuilder qb = new QueryBuilder(WFAction.class, "a", null);
			qb.addCriterion("a.uuid", "=", uuid, true);
			wfAction = (WFAction) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (Exception e) {
		}
		return wfAction;
	}

	public List<WFAction> listByTransition(WFTransition wfTransition) {		
		List<WFAction> wfTransitions =  (List<WFAction>) getEntityManager()
				.createNamedQuery("WFAction.listByTransition", WFAction.class)
				.setParameter("wfTransition", wfTransition)
				.getResultList();
		return wfTransitions;
	}

}
