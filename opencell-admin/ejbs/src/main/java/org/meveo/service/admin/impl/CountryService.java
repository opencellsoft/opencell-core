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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Country;
import org.meveo.service.base.PersistenceService;

@Stateless
@Named
public class CountryService extends PersistenceService<Country> {

    /**
     * @param countryCode country code
     * @return found country
     */
    public Country findByCode(String countryCode) {

        if (countryCode == null || countryCode.trim().length() == 0) {
            return null;
        }

        QueryBuilder qb = new QueryBuilder(Country.class, "c");
        if (countryCode.length() <= 3) {
            qb.addCriterion("countryCode", "=", countryCode, false);
        } else {
            qb.startOrClause();
            qb.addCriterion("description", "=", countryCode, false);
            qb.addSql("lower(descriptionI18n) like'%" + countryCode.toLowerCase() + "%'");
            qb.endOrClause();
        }
        try {
            return (Country) qb.getQuery(getEntityManager()).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param countryName countryName
     * @return country
     */
    public Country findByName(String countryName) {
        QueryBuilder qb = new QueryBuilder(Country.class, "c");
        qb.startOrClause();
        qb.addCriterion("description", "=", countryName, false);
        qb.endOrClause();
        try {
            return (Country) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @return list of country
     * @see org.meveo.service.base.PersistenceService#list()
     */
    @SuppressWarnings("unchecked")
    public List<Country> list() {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        queryBuilder.addOrderCriterion("a.description", true);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

}