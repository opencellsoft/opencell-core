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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;

/**
 * AccountOperation service implementation.
 */
@Stateless
public class AccountOperationService extends
		PersistenceService<AccountOperation> {

	@SuppressWarnings("unchecked")
	public List<AccountOperation> getAccountOperations(Date date,
			String operationCode, Provider provider) {
		Query query = getEntityManager()
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where a.occCode=:operationCode and  a.transactionDate=:date and a.provider=:providerId")
				.setParameter("date", date)
				.setParameter("operationCode", operationCode)
				.setParameter("providerId", provider);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public AccountOperation getAccountOperation(BigDecimal amount,
			CustomerAccount customerAccount, String transactionType,
			Provider provider) {

		Query query = getEntityManager()
				.createQuery(
						"from "
								+ getEntityClass().getSimpleName()
								+ " a where a.amount=:amount and  a.customerAccount=:customerAccount and  a.type=:transactionType and a.provider=:providerId")
				.setParameter("amount", amount)
				.setParameter("transactionType", transactionType)
				.setParameter("customerAccount", customerAccount)
				.setParameter("providerId", provider);
		List<AccountOperation> accountOperations = query.getResultList();

		return accountOperations.size() > 0 ? accountOperations.get(0) : null;
	}
	
	
	public AccountOperation findByReference(String reference,Provider provider) {
		try {
			QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a");
			qb.addCriterion("reference", "=", reference, false);
			qb.addCriterionEntity("provider", provider);
			return (AccountOperation) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException ne) {
			return null;
		}
	}

}
