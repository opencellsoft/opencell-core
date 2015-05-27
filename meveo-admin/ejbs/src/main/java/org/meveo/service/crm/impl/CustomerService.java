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
package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Customer service implementation.
 */
@Stateless
public class CustomerService extends PersistenceService<Customer> {

	public Customer findByCode(String code, Provider provider) {
		Query query = getEntityManager()
				.createQuery("from " + Customer.class.getSimpleName() + " where code=:code and provider=:provider")
				.setParameter("code", code).setParameter("provider", provider);
		if (query.getResultList().size() == 0) {
			return null;
		}
		return (Customer) query.getResultList().get(0);
	}

	public Customer findByCodeAndFetch(String code, List<String> fetchFields, Provider provider) {
		QueryBuilder qb = new QueryBuilder(Customer.class, "c", fetchFields, provider);
		qb.addCriterionEntity("c.provider", provider);
		qb.addCriterion("c.code", "=", code, true);

		try {
			return (Customer) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
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

	@SuppressWarnings("unchecked")
	public List<Customer> filter(String customerCode, CustomerCategory customerCategory, Seller seller,
			CustomerBrand brand, Provider provider) {
		QueryBuilder qb = new QueryBuilder(Customer.class, "c");
		qb.addCriterion("code", "=", customerCode, true);
		qb.addCriterionEntity("provider", provider);

		if (customerCategory != null) {
			qb.addCriterionEntity("customerCategory", customerCategory);
		}

		if (seller != null) {
			qb.addCriterionEntity("seller", seller);
		}

		if (brand != null) {
			qb.addCriterionEntity("customerBrand", brand);
		}

		try {
			return (List<Customer>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Customer> listBySellerCode(Provider provider, String code) {
		QueryBuilder qb = new QueryBuilder(Customer.class, "c");
		qb.addCriterion("seller.code", "=", code, true);
		qb.addCriterionEntity("provider", provider);
		try {
			return (List<Customer>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Seller> listSellersWithCustomers(Provider provider) {
		try {
			return (List<Seller>) getEntityManager()
					.createQuery("SELECT DISTINCT c.seller " + "FROM Customer c WHERE c.provider=:provider ")
					.setParameter("provider", provider).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
