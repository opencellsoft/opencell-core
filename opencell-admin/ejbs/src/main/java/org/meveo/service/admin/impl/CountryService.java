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
package org.meveo.service.admin.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Country;
import org.meveo.service.base.PersistenceService;

/**
 * @author anasseh
 * @lastModifiedVersion 5.0
 */

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
        qb.startOrClause();
        qb.addCriterion("countryCode", "=", countryCode, false);
        qb.addCriterion("description", "=", countryCode, true);
        qb.addSql("lower(descriptionI18n) like '%:\"" + countryCode.toLowerCase() + "\"%'");
        qb.endOrClause();
        
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