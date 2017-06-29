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
package org.meveo.service.base;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.model.BusinessEntity;

public abstract class BusinessService<P extends BusinessEntity> extends PersistenceService<P> {

    /**
     * Find entity by code - strict match
     * 
     * @param code Code to match
     * @return A single entity matching code
     */
    public P findByCode(String code) {
        return findByCode(code, null);
    }

    /**
     * Find entity by code - strict match
     * 
     * @param code Code to match
     * @param fetchFields Fields to fetch
     * @return A single entity matching code
     */
    public P findByCode(String code, List<String> fetchFields) {
        return findByCode(code, fetchFields, null, null, null);
    }

    /**
     * Find entity by code - strict match
     * 
     * @param code Code to match
     * @param fetchFields Fields to fetch
     * @param additionalSql Additional sql to append to the find clause
     * @param additionalParameters An array of Parameter names and values for additional sql
     * @param additionalParamValue Parameter value in additional sql
     * @return A single entity matching code
     */
    @SuppressWarnings("unchecked")
    protected P findByCode(String code, List<String> fetchFields, String additionalSql, Object... additionalParameters) {
        QueryBuilder qb = new QueryBuilder(getEntityClass(), "be", fetchFields);
        qb.addCriterion("be.code", "=", code, true);
        if (additionalSql!=null){
            qb.addSqlCriterionMultiple(additionalSql, additionalParameters);
        }

        try {
            return (P) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code);
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {} found", entityClass, code);
            return null;
        }
    }

    /**
     * Find entity by code - match the beginning of code
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
        return findDuplicateCode(entity, " - Copy");
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
            criteria = code + " " + id;
        }
        return criteria;
    }
    
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
