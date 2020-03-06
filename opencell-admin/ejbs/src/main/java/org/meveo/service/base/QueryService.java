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

package org.meveo.service.base;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;

/**
 * Service that allows HQL queries to be called directly.
 *
 * @author Tony Alejandro
 * @version %I%, %G%
 * @since 5.1
 * @lastModifiedVersion 5.1
 */
@Stateless
public class QueryService {

    private static final Logger log = LoggerFactory.getLogger(QueryService.class);

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    public EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    /**
     * Execute a native select query
     *
     * @param query HQL query to execute
     * @param alias alias name for the main entity that was used in the query.<br>
     *         e.g. if the query is "FROM Customer cust", then the alias should be "cust"
     * @param fields comma delimited fields. allows nested field names.
     * @param params Parameters to pass into the query
     * @return A map of values retrieved
     */
    public int count(String query, String alias, String fields, Map<String, Object> params) {

        StringBuilder queryString = new StringBuilder("select count(distinct ");
        queryString.append(alias).append(") ");
        queryString.append(query.substring(query.toLowerCase().indexOf("from")));

        Session session = getEntityManager().unwrap(Session.class);
        Query hqlQuery = session.createQuery(queryString.toString());

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            hqlQuery.setParameter(entry.getKey(), entry.getValue());
        }

        List list = hqlQuery.list();
        int count = 0;
        if (list != null && !list.isEmpty()) {
            String value = list.get(0) != null ? list.get(0).toString() : null;
            if (StringUtils.isNumeric(value)) {
                count = Integer.parseInt(value);
            }
        }

        return count;
    }

    /**
     * Execute a native select query
     *
     * @param query HQL query to execute
     * @param alias alias name for the main entity that was used in the query.<br>
     *        e.g. if the query is "FROM Customer cust", then the alias should be "cust"
     * @param fields comma delimited fields. allows nested field names.
     * @param params Parameters to pass into the query
     * @param offset - starting record number
     * @param limit - number of records to retrieve
     * @param sortBy - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder - sort order whether asc or desc.
     * @param groupBy - field to group by
     * @return A map of values retrieved
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> executeQuery(String query, String alias, String fields, Map<String, Object> params, int offset, int limit, String sortBy, String sortOrder, String groupBy) {

        Session session = getEntityManager().unwrap(Session.class);
        StringBuilder queryString = new StringBuilder("select distinct ");
        queryString.append(fields).append(" ");
        queryString.append(query);

        if (groupBy != null) {
            queryString.append(" group by ").append(groupBy);
        }

        if (sortBy != null) {
            queryString.append(" order by ").append(sortBy);
        }

        if (sortBy != null && sortOrder != null) {
            queryString.append(" ").append(sortOrder);
        }
        
        log.debug("Executing QUERY={}",queryString);
        Query hqlQuery = session.createQuery(queryString.toString());

        if (offset > 0) {
            hqlQuery.setFirstResult(offset);
        }

        if (limit > 0) {
            hqlQuery.setMaxResults(limit);
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                String valueString = (String) value;
                if (valueString.contains(",")) {
                    hqlQuery.setParameterList(entry.getKey(), valueString.split(","));
                } else {
                    hqlQuery.setParameter(entry.getKey(), valueString);
                }
            }
        }

        return hqlQuery.list();
    }
}
