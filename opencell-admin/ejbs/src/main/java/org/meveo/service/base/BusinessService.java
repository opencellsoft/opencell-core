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
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.lucene.search.Collector;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;

/**
 * @author phung
 *
 * @param <P> extension of Business Entity.
 */
public abstract class BusinessService<P extends BusinessEntity> extends PersistenceService<P> {

    /**
     * Find entity by code - strict match.
     * 
     * @param code Code to match
     * @return A single entity matching code
     */
    public P findByCode(String code) {

        if (code == null) {
            return null;
        }

        TypedQuery<P> query = getEntityManager().createQuery("select be from " + entityClass.getSimpleName() + " be where lower(code)=:code", entityClass)
            .setParameter("code", code.toLowerCase()).setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code);
            return null;
        }
    }

    /**
     * Find entities by codes - strict match.
     *
     * @param codes list of Codes to match
     * @return A list of entities matching code
     */
    public List<P> findByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return null;
        }
        codes = codes.stream().map(s -> s.toLowerCase()).collect(Collectors.toList());
        TypedQuery<P> query = getEntityManager().createQuery("select be from " + entityClass.getSimpleName() + " be where lower(be.code) IN :codes", entityClass)
                .setParameter("codes", codes);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), codes);
            return null;
        }
    }
    /**
     * Find entity by code - strict match.
     * 
     * @param code Code to match
     * @param fetchFields Fields to fetch
     * @return A single entity matching code
     */
    public P findByCode(String code, List<String> fetchFields) {
        return findByCode(code, fetchFields, null, null, null);
    }

    /**
     * Find entity by code - strict match.
     * 
     * @param code Code to match
     * @param fetchFields Fields to fetch
     * @param additionalSql Additional sql to append to the find clause
     * @param additionalParameters An array of Parameter names and values for additional sql
     * @return A single entity matching code
     */
    @SuppressWarnings("unchecked")
    protected P findByCode(String code, List<String> fetchFields, String additionalSql, Object... additionalParameters) {

        if (StringUtils.isBlank(code)) {
            return null;
        }

        QueryBuilder qb = new QueryBuilder(getEntityClass(), "be", fetchFields);
        qb.addCriterion("be.code", "=", code, true);
        if (additionalSql != null) {
            qb.addSqlCriterionMultiple(additionalSql, additionalParameters);
        }

        try {
            return (P) qb.getQuery(getEntityManager()).getSingleResult();

        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code);
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {} found. A first entry is returned.", entityClass, code);
            return (P) qb.getQuery(getEntityManager()).getResultList().get(0);
        }
    }

    /**
     * Find entity by code - match the beginning of code.
     * 
     * @param codePrefix Beginning of code
     * @return A list of entities which code starts with a given value
     */
    @SuppressWarnings("unchecked")
    public List<P> findStartsWithCode(String codePrefix) {
        try {
            QueryBuilder qb = new QueryBuilder(getEntityClass(), "be");
            qb.like("be.code", codePrefix, QueryLikeStyleEnum.MATCH_BEGINNING, false);

            return (List<P>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException ne) {
            return null;
        } catch (NonUniqueResultException nre) {
            return null;
        }
    }

    public String findDuplicateCode(BusinessEntity entity) {
        return findDuplicateCode(entity, "-Copy");
    }

    public String findDuplicateCode(BusinessEntity entity, String suffix) {
        String code = entity.getCode() + suffix;
        int id = 1;
        String criteria = code;
        BusinessEntity temp = null;
        while (true) {
            temp = findByCode(criteria);
            if (temp == null) {
                break;
            }
            id++;
            criteria = code + "-" + id;
        }
        return criteria;
    }

    @SuppressWarnings("rawtypes")
    public BusinessEntity findByEntityClassAndCode(Class clazz, String code) {
        QueryBuilder qb = new QueryBuilder(clazz, "be", null);
        qb.addCriterion("be.code", "=", code, true);

        try {
            return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code);
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {} found", entityClass, code);
            return null;
        }
    }

}
