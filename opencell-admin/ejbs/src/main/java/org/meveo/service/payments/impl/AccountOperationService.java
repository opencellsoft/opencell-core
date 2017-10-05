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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.DiscriminatorValue;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.primefaces.model.SortOrder;

/**
 * AccountOperation service implementation.
 */
@Stateless
public class AccountOperationService extends PersistenceService<AccountOperation> {

	@SuppressWarnings("unchecked")
	public List<AccountOperation> getAccountOperations(Date date, String operationCode) {
		Query query = getEntityManager()
				.createQuery("from " + getEntityClass().getSimpleName() + " a where a.occCode=:operationCode and  a.transactionDate=:date")
				.setParameter("date", date).setParameter("operationCode", operationCode);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public AccountOperation getAccountOperation(BigDecimal amount, CustomerAccount customerAccount, String transactionType) {

		Query query = getEntityManager()
				.createQuery(
						"from " + getEntityClass().getSimpleName()
								+ " a where a.amount=:amount and  a.customerAccount=:customerAccount and  a.type=:transactionType")
				.setParameter("amount", amount).setParameter("transactionType", transactionType).setParameter("customerAccount", customerAccount);
		List<AccountOperation> accountOperations = query.getResultList();

		return accountOperations.size() > 0 ? accountOperations.get(0) : null;
	}

	public AccountOperation findByReference(String reference) {
		try {
			QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a");
			qb.addCriterion("reference", "=", reference, false);
			return (AccountOperation) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException ne) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AccountOperation> listAccountOperationByCustomerAccount(CustomerAccount ca, String sortBy, SortOrder sortOrder) {
		QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a", null);
		qb.addCriterionEntity("customerAccount", ca);
		boolean ascending = true;
		if (sortOrder != null) {
			ascending = sortOrder.equals(SortOrder.ASCENDING);
		}
		qb.addOrderCriterion(sortBy, ascending);
		
		try {
			return (List<AccountOperation>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

    /**
     * Set the discriminatorValue value, so it would be available in the list of entities right away
     * @throws BusinessException 
     */	
    public Long createAndReturnId(AccountOperation aop) throws BusinessException {

        if (aop.getClass().isAnnotationPresent(DiscriminatorValue.class)) {
            aop.setType(aop.getClass().getAnnotation(DiscriminatorValue.class).value());
        }

        super.create(aop);
        return aop.getId();
        
    }
}