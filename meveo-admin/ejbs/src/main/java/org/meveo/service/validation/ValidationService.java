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
package org.meveo.service.validation;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meveo.model.crm.Provider;

/**
 * @author Ignas Lelys
 * @created Jan 5, 2011
 * 
 */
@Stateless 
public class ValidationService {

	@PersistenceContext(unitName = "MeveoAdmin")
	private EntityManager em;

	/**
	 * @see org.meveo.service.validation.ValidationServiceLocal#validateUniqueField(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Object)
	 */
	public boolean validateUniqueField(String className, String fieldName, Object id, Object value,
			Provider provider) {

		// Proxy classes contain a name in "..._$$_javassist.. format" If a
		// proxy class object claname was passed, string the ending
		// "_$$_javassist.."to obtain real class name
		int pos = className.indexOf("_$$_");
		if (pos > 0) {
			className = className.substring(0, pos);
		}

        String queryString = null;
        if (id == null) {
            queryString = String.format("select count(*) from %s where lower(%s)='%s' and provider.id = %s", className, fieldName,
                (value != null && value instanceof String) ? ((String) value).toLowerCase() : value, provider.getId());
        } else {
            queryString = String.format("select count(*) from %s where lower(%s)='%s' and provider.id = %s and id != %s", className, fieldName,
                (value != null && value instanceof String) ? ((String) value).toLowerCase() : value, provider.getId(), id);
        }
        Query query = em.createQuery(queryString);
		long count = (Long) query.getSingleResult();
		return count == 0L;
	}

}
