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
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.FileFormat;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

/**
 * File format service
 *
 * @author Abdellatif BARI
 * @since 7.3.0
 */

@Stateless
public class FileFormatService extends PersistenceService<FileFormat> {

    /**
     * Find the File format by code.
     *
     * @param code File format code.
     * @return found File format
     */
    public FileFormat findByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(FileFormat.class, "c");
        qb.addCriterion("code", "=", code, false);

        try {
            return (FileFormat) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @return list of faile format
     * @see PersistenceService#list()
     */
    @SuppressWarnings("unchecked")
    public List<FileFormat> list() {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "e", null);
        queryBuilder.addOrderCriterion("e.code", true);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

}