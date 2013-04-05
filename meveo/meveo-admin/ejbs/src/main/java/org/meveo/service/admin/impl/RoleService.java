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
package org.meveo.service.admin.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.security.Role;
import org.meveo.service.base.PersistenceService;

/**
 * User Role service implementation.
 * 
 * @author Gediminas Ubartas
 * @created 2010.05.31
 */

@Stateless @LocalBean
public class RoleService extends PersistenceService<Role> {
    /**
     * @see org.meveo.service.base.local.IPersistenceService#list()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Role> list() {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null, null);
        Query query = queryBuilder.getQuery(em);
        return query.getResultList();
    }

    public List<Role> getAllRoles() {
        return list();
    }
}
