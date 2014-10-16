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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingWalletDetailDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.AccountService;

@Stateless
@LocalBean
public class UserAccountService extends AccountService<UserAccount> {

	@EJB
	private BillingAccountService billingAccountService;

	@EJB
	private WalletService walletService;

	public void createUserAccount(BillingAccount billingAccount,
			UserAccount userAccount, User creator)
			throws AccountAlreadyExistsException {
		createUserAccount(getEntityManager(), billingAccount, userAccount,
				creator);
	}

	public void createUserAccount(EntityManager em,
			BillingAccount billingAccount, UserAccount userAccount, User creator)
			throws AccountAlreadyExistsException {

		UserAccount existingUserAccount = findByCode(em, userAccount.getCode(),
				userAccount.getProvider());
		if (existingUserAccount != null) {
			throw new AccountAlreadyExistsException(userAccount.getCode());
		}

		userAccount.setBillingAccount(billingAccount);
		create(em, userAccount, creator, billingAccount.getProvider());
		WalletInstance wallet = new WalletInstance();
		wallet.setCode(WalletTemplate.PRINCIPAL);
		wallet.setUserAccount(userAccount);
		walletService.create(em, wallet, creator, billingAccount.getProvider());
		userAccount.setWallet(wallet);

		List<WalletTemplate> prepaidWalletTemplates = billingAccount
				.getProvider().getPrepaidWalletTemplates();
		if (prepaidWalletTemplates != null && prepaidWalletTemplates.size() > 0) {
			HashMap<String, WalletInstance> prepaidWallets = new HashMap<String, WalletInstance>(
					prepaidWalletTemplates.size());
			for (WalletTemplate prepaidWalletTemplate : prepaidWalletTemplates) {
				WalletInstance prepaidWallet = new WalletInstance();
				wallet.setUserAccount(userAccount);
				wallet.setWalletTemplate(prepaidWalletTemplate);
				walletService.create(em, wallet, creator,
						billingAccount.getProvider());
				prepaidWallets.put(prepaidWalletTemplate.getCode(),
						prepaidWallet);
			}
			userAccount.setPrepaidWallets(prepaidWallets);
		}
	}

	public void updateUserAccount(UserAccount userAccount, User updater)
			throws BusinessException {
		update(userAccount, updater);
	}

	public void updateUserAccount(EntityManager em, UserAccount userAccount,
			User updater) throws BusinessException {
		update(em, userAccount, updater);
	}

	public void userAccountTermination(UserAccount userAccount,
			Date terminationDate,
			SubscriptionTerminationReason terminationReason, User updater)
			throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.terminateSubscription(subscription,
					terminationDate, terminationReason, updater);
		}
		userAccount.setTerminationReason(terminationReason);
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.TERMINATED);
		update(userAccount, updater);
	}

	public void userAccountCancellation(UserAccount userAccount,
			Date terminationDate, User updater) throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);

		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.subscriptionCancellation(subscription,
					terminationDate, updater);
		}
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.CANCELED);
		update(userAccount, updater);
	}

	public void userAccountReactivation(UserAccount userAccount,
			Date activationDate, User updater) throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		if (userAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& userAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("user account",
					userAccount.getCode());
		}

		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		userAccount.setStatusDate(activationDate);
		update(userAccount, updater);
	}

	public BillingWalletDetailDTO BillingWalletDetail(UserAccount userAccount)
			throws BusinessException {
		BillingWalletDetailDTO BillingWalletDetailDTO = new BillingWalletDetailDTO();

		BigDecimal amount = BigDecimal.valueOf(0);
		BigDecimal amountWithoutTax = BigDecimal.valueOf(0);
		BigDecimal amountTax = BigDecimal.valueOf(0);

		WalletInstance wallet = userAccount.getWallet();
		if (wallet == null) {
			return null;
		}
		for (RatedTransaction ratedTransaction : wallet.getRatedTransactions()) {
			if (ratedTransaction.getBillingRun() == null) {
				amount = amount.add(ratedTransaction.getAmountWithTax());
				amountWithoutTax = amountWithoutTax.add(ratedTransaction
						.getAmountWithoutTax());
				amountTax = amountTax.add(ratedTransaction.getAmountTax());
			}
		}
		BillingWalletDetailDTO.setAmount(amount);
		BillingWalletDetailDTO.setAmountTax(amountWithoutTax);
		BillingWalletDetailDTO.setAmountWithoutTax(amountTax);
		return BillingWalletDetailDTO;
	}

	public List<RatedTransaction> BillingRatedTransactionList(
			UserAccount userAccount) throws BusinessException {
		WalletInstance wallet = userAccount.getWallet();
		return wallet.getRatedTransactions();
	}

	public boolean isDuplicationExist(UserAccount userAccount) {
		if (userAccount == null || !userAccount.getDefaultLevel()) {
			return false;
		}
		BillingAccount ba = userAccount.getBillingAccount();
		List<UserAccount> userAccounts = ba.getUsersAccounts();
		for (UserAccount ua : userAccounts) {
			if (ua.getDefaultLevel() != null
					&& ua.getDefaultLevel()
					&& (userAccount.getId() == null || (userAccount.getId() != null && !userAccount
							.getId().equals(ua.getId())))) {
				return true;
			}
		}

		return false;

	}

}
