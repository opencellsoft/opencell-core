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
package org.meveo.service.validation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;

/**
 * @author Ignas Lelys
 * @since Jan 5, 2011
 * 
 */
@Stateless
public class ValidationService {

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    /**
     * @param className class name
     * @param fieldName field name
     * @param id id of checking object
     * @param value value of checking object
     * @return true if object has unique field
     */
    public boolean validateUniqueField(String className, String fieldName, Object id, Object value) {

        className = ReflectionUtils.getCleanClassName(className);

        String queryString = null;
        if (id == null) {
            queryString = String.format("select count(*) from %s where lower(%s)='%s'", className, fieldName,
                (value != null && value instanceof String) ? ((String) value).toLowerCase().replaceAll("'", "''") : value);
        } else {
            queryString = String.format("select count(*) from %s where lower(%s)='%s' and id != %s", className, fieldName,
                (value != null && value instanceof String) ? ((String) value).toLowerCase().replaceAll("'", "''") : value, id);
        }
        Query query = emWrapper.getEntityManager().createQuery(queryString);
        long count = (Long) query.getSingleResult();
        return count == 0L;
    }

}
