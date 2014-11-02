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
package org.meveo.service.admin.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.security.Role;
import org.meveo.service.base.PersistenceService;

/**
 * User Role service implementation.
 */
@Stateless
public class RoleService extends PersistenceService<Role> {
	
	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> list() {
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null,
				null);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	public List<Role> getAllRoles() {
		return list();
	}
}
