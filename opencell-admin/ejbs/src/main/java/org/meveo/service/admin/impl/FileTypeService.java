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
package org.meveo.service.admin.impl;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.FileType;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

/**
 * File format service
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */

@Stateless
public class FileTypeService extends BusinessService<FileType> {

    /**
     * @return list of file type
     * @see PersistenceService#list()
     */
    @SuppressWarnings("unchecked")
    public List<FileType> list() {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "e", null);
        queryBuilder.addOrderCriterion("e.code", true);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

}