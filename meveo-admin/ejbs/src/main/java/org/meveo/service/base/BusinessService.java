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

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.Provider;

public abstract class BusinessService<P extends BusinessEntity> extends
		PersistenceService<P> {

    /**
     * Find entity by code - strict match
     * 
     * @param code Code to match
     * @param provider Provider
     * @return A single entity matching code for a given provider
     */
	public P findByCode(String code, Provider provider) {
		return findByCode(code, provider, null);
	}

    /**
     * Find entity by code - strict match
     * 
     * @param code Code to match
     * @param provider Provider
     * @param fetchFields Fields to fetch
     * @return A single entity matching code for a given provider
     */
    @SuppressWarnings("unchecked")
    public P findByCode(String code, Provider provider, List<String> fetchFields) {
        QueryBuilder qb = new QueryBuilder(getEntityClass(), "be", fetchFields, provider);
        qb.addCriterion("be.code", "=", code, true);
        qb.addCriterionEntity("be.provider", provider);

		try {
			return (P) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} for provider {} found", getEntityClass().getSimpleName(), code, provider.getId());
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code {} and provider {} found", entityClass, code, provider);
            return null;
        }
	}

	@SuppressWarnings("unchecked")
	public P findByCode(EntityManager em, String code, Provider provider) {
		log.debug("start of find {} by code (code={}) ..", getEntityClass()
				.getSimpleName(), code);
		final Class<? extends P> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from "
				+ productClass.getName() + " a");
		queryString.append(" where a.code = :code and a.provider=:provider");
		Query query = em.createQuery(queryString.toString());
		query.setParameter("code", code);
		query.setParameter("provider", provider);
		if (query.getResultList().size() == 0) {
			return null;
		}
		P e = (P) query.getResultList().get(0);
		log.debug("end of find {} by code (code={}). Result found={}.",
				new Object[] { getEntityClass().getSimpleName(), code,
						e != null });

		return e;
	}

}
