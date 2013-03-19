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
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;

/**
 * AccountOperation service implementation.
 * 
 * @author Ignas
 * @created 2009.10.15
 */
@Stateless
public class AccountOperationService extends
		PersistenceService<AccountOperation> {

	@SuppressWarnings("unchecked")
	public List<AccountOperation> getAccountOperations(Date date,
			String operationCode, Provider provider) {
		Query query = em
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

		Query query = em
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

}
