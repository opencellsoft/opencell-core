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
