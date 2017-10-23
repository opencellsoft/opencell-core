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
package org.meveo.service.crm.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.crm.Customer;
import org.meveo.service.base.AccountService;
/**
 * Customer service implementation.
 */
@Stateless
public class CustomerService extends AccountService<Customer> {
    public Customer findByCode(String code) {
        Query query = getEntityManager().createQuery("from " + Customer.class.getSimpleName() + " where code=:code").setParameter("code", code);
        if (query.getResultList().size() == 0) {
            return null;
        }
        return (Customer) query.getResultList().get(0);
    }
    public Customer findByCodeAndFetch(String code, List<String> fetchFields) {
        QueryBuilder qb = new QueryBuilder(Customer.class, "c", fetchFields);
        qb.addCriterion("c.code", "=", code, true);
        try {
            return (Customer) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    public List<Customer> listBySellerCode(String code) {
        QueryBuilder qb = new QueryBuilder(Customer.class, "c");
        qb.addCriterion("seller.code", "=", code, true);
        try {
            return (List<Customer>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    public List<Seller> listSellersWithCustomers() {
        try {
            return (List<Seller>) getEntityManager().createQuery("SELECT DISTINCT c.seller " + "FROM Customer c ").getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}