/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
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
