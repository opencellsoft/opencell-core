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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectUserAccountException;
import org.meveo.admin.exception.UnknownAccountException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingWalletDetailDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletTemplate;
import org.meveo.service.base.AccountService;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless
@LocalBean
public class UserAccountService extends AccountService<UserAccount> {

	@EJB
	private BillingAccountService billingAccountService;

	@EJB
	private WalletService walletService;

	public List<Subscription> subscriptionList(String code) throws BusinessException {
		UserAccount userAccount = findByCode(code);
		return userAccount.getSubscriptions();
	}

	public void createUserAccount(String billingAccountCode, UserAccount userAccount, User creator)
			throws AccountAlreadyExistsException {
		BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);
		UserAccount existingUserAccount = findByCode(userAccount.getCode());
		if (existingUserAccount != null) {
			throw new AccountAlreadyExistsException(userAccount.getCode());
		}
		userAccount.setBillingAccount(billingAccount);
		create(userAccount, creator, billingAccount.getProvider());
		WalletInstance wallet = new WalletInstance();
		wallet.setCode("PRINCIPAL");
		wallet.setUserAccount(userAccount);
		walletService.create(wallet, creator, billingAccount.getProvider());
		userAccount.setWallet(wallet);

		List<WalletTemplate> prepaidWalletTemplates = billingAccount.getProvider()
				.getPrepaidWalletTemplates();
		if (prepaidWalletTemplates != null && prepaidWalletTemplates.size() > 0) {
			HashMap<String, WalletInstance> prepaidWallets = new HashMap<String, WalletInstance>(
					prepaidWalletTemplates.size());
			for (WalletTemplate prepaidWalletTemplate : prepaidWalletTemplates) {
				WalletInstance prepaidWallet = new WalletInstance();
				wallet.setUserAccount(userAccount);
				wallet.setWalletTemplate(prepaidWalletTemplate);
				walletService.create(wallet, creator, billingAccount.getProvider());
				prepaidWallets.put(prepaidWalletTemplate.getCode(), prepaidWallet);
			}
			userAccount.setPrepaidWallets(prepaidWallets);
		}
	}

	public void updateUserAccount(UserAccount userAccount, User updater) throws BusinessException {
		update(userAccount, updater);
	}

	public UserAccount userAccountDetails(String code) throws BusinessException {
		UserAccount userAccount = findByCode(code);
		return userAccount;
	}

	public void userAccountTermination(String code, Date terminationDate,
			SubscriptionTerminationReason terminationReason, User updater) throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		UserAccount userAccount = findByCode(code);
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.terminateSubscription(subscription, terminationDate,
					terminationReason, updater);
		}
		userAccount.setTerminationReason(terminationReason);
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.TERMINATED);
		update(userAccount, updater);
	}

	public void userAccountCancellation(String code, Date terminationDate, User updater)
			throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);

		if (terminationDate == null) {
			terminationDate = new Date();
		}
		UserAccount userAccount = findByCode(code);
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.subscriptionCancellation(subscription.getCode(), terminationDate,
					updater);
		}
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.CANCELED);
		update(userAccount, updater);
	}

	public void userAccountReactivation(String code, Date activationDate, User updater)
			throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		UserAccount userAccount = findByCode(code);
		if (userAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& userAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("user account", code);
		}

		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		userAccount.setStatusDate(activationDate);
		update(userAccount, updater);
	}

	public BillingWalletDetailDTO BillingWalletDetail(String code) throws BusinessException {
		BillingWalletDetailDTO BillingWalletDetailDTO = new BillingWalletDetailDTO();

		BigDecimal amount = BigDecimal.valueOf(0);
		BigDecimal amountWithoutTax = BigDecimal.valueOf(0);
		BigDecimal amountTax = BigDecimal.valueOf(0);

		UserAccount userAccount = findByCode(code);
		if (userAccount == null) {
			throw new IncorrectUserAccountException("user account does not exist. code=" + code);
		}
		WalletInstance wallet = userAccount.getWallet();
		if (wallet == null) {
			return null;
		}
		for (RatedTransaction ratedTransaction : wallet.getRatedTransactions()) {
			if (ratedTransaction.getInvoiceAgregateF() == null
					&& ratedTransaction.getInvoiceAgregateR() == null
					&& ratedTransaction.getInvoiceAgregateT() == null) {
				amount = amount.add(ratedTransaction.getAmountWithTax());
				amountWithoutTax = amountWithoutTax.add(ratedTransaction.getAmountWithoutTax());
				amountTax = amountTax.add(ratedTransaction.getAmountTax());
			}
		}
		BillingWalletDetailDTO.setAmount(amount);
		BillingWalletDetailDTO.setAmountTax(amountWithoutTax);
		BillingWalletDetailDTO.setAmountWithoutTax(amountTax);
		return BillingWalletDetailDTO;
	}

	public List<RatedTransaction> BillingRatedTransactionList(String code) throws BusinessException {
		UserAccount userAccount = findByCode(code);
		if (userAccount == null) {
			throw new UnknownAccountException(code);
		}
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
