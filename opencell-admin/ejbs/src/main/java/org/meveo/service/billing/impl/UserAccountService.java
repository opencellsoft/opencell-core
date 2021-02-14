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
package org.meveo.service.billing.impl;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.service.base.AccountService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class UserAccountService extends AccountService<UserAccount> {

	@Inject
	private WalletService walletService;
	
	public void createUserAccount(BillingAccount billingAccount, UserAccount userAccount)
			throws BusinessException {

		log.debug("creating userAccount with details {}", new Object[] { userAccount});

		UserAccount existingUserAccount = findByCode(userAccount.getCode());
		if (existingUserAccount != null) {
			throw new AccountAlreadyExistsException(userAccount.getCode());
		}

		userAccount.setBillingAccount(billingAccount);
		
		WalletInstance wallet = new WalletInstance();
		wallet.setCode(WalletTemplate.PRINCIPAL);
        wallet.setUserAccount(userAccount);
        wallet.updateAudit(currentUser);
		
		userAccount.setWallet(wallet);
		create(userAccount);
		
	}

	@MeveoAudit
	public UserAccount userAccountTermination(UserAccount userAccount, Date terminationDate,
			SubscriptionTerminationReason terminationReason) throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {		
			subscriptionService.terminateSubscription(subscription, terminationDate, terminationReason, subscription.getOrderNumber());
		}
		userAccount.setTerminationReason(terminationReason);
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.TERMINATED);
		return update(userAccount);
	}

	@MeveoAudit
	public UserAccount userAccountCancellation(UserAccount userAccount, Date cancelationDate)
			throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);

		if (cancelationDate == null) {
			cancelationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.subscriptionCancellation(subscription, cancelationDate);
		}
		userAccount.setTerminationDate(cancelationDate);
		userAccount.setStatus(AccountStatusEnum.CANCELED);
		return update(userAccount);
	}

	@MeveoAudit
	public UserAccount userAccountReactivation(UserAccount userAccount, Date activationDate)
			throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		if (userAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& userAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("user account", userAccount.getCode());
		}

		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		return update(userAccount);
	}

	public List<UserAccount> listByBillingAccount(BillingAccount billingAccount) {
		return billingAccount.getUsersAccounts();
	}

    /**
     * Get a count of user accounts by a parent billing account
     * 
     * @param parent Parent billing account
     * @return A number of child user accounts
     */
    public long getCountByParent(BillingAccount parent) {

        return getEntityManager().createNamedQuery("UserAccount.getCountByParent", Long.class).setParameter("parent", parent).getSingleResult();
    }
}