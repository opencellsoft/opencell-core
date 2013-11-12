/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.crm.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Customer service implementation.
 */
@Stateless
@LocalBean
public class CustomerService extends PersistenceService<Customer> {

	public Customer findByCode(String code, Provider provider) {
		Query query = getEntityManager()
				.createQuery(
						"from " + Customer.class.getSimpleName()
								+ " where code=:code and provider=:provider")
				.setParameter("code", code).setParameter("provider", provider);
		if (query.getResultList().size() == 0) {
			return null;
		}
		return (Customer) query.getResultList().get(0);
	}

	public Customer findByCode(EntityManager em, String code, Provider provider) {
		QueryBuilder qb = new QueryBuilder(Customer.class, "c");

		try {
			qb.addCriterion("code", "=", code, true);
			qb.addCriterionEntity("provider", provider);

			return (Customer) qb.getQuery(em).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
