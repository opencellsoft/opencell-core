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
package org.meveo.service.communication.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.PersistenceService;

/**
 * MeveoInstance service implementation.
 */
@Stateless
public class MeveoInstanceService extends PersistenceService<MeveoInstance> {

	public MeveoInstance findByCode(String meveoInstanceCode) {
		try {
			QueryBuilder qb = new QueryBuilder(MeveoInstance.class, "c");
			qb.addCriterion("code", "=", meveoInstanceCode, true);
			return (MeveoInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn("failed to find MeveoInstance",e);
			return null;
		}
	}
}
