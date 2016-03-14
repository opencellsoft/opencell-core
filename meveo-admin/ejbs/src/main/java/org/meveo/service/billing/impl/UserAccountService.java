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
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
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
import org.meveo.model.crm.Provider;
import org.meveo.service.base.AccountService;
import org.meveo.service.base.ValueExpressionWrapper;


@Stateless
public class UserAccountService extends AccountService<UserAccount> {

	@Inject
	private WalletService walletService;

	public void createUserAccount(BillingAccount billingAccount, UserAccount userAccount, User creator)
			throws AccountAlreadyExistsException {

		log.debug("creating userAccount with details {}, creator={}, provider={}", new Object[] { userAccount, creator,
				billingAccount.getProvider() });

		UserAccount existingUserAccount = findByCode(userAccount.getCode(), userAccount.getProvider());
		if (existingUserAccount != null) {
			throw new AccountAlreadyExistsException(userAccount.getCode());
		}

		userAccount.setBillingAccount(billingAccount);
		create(userAccount, creator, billingAccount.getProvider());
		WalletInstance wallet = new WalletInstance();
		wallet.setCode(WalletTemplate.PRINCIPAL);
		wallet.setUserAccount(userAccount);
		walletService.create(wallet, creator, billingAccount.getProvider());

		userAccount.setWallet(wallet);
	}

	public UserAccount userAccountTermination(UserAccount userAccount, Date terminationDate,
			SubscriptionTerminationReason terminationReason, User updater) throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.terminateSubscription(subscription, terminationDate, terminationReason, updater);
		}
		userAccount.setTerminationReason(terminationReason);
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.TERMINATED);
		return update(userAccount, updater);
	}

	public UserAccount userAccountCancellation(UserAccount userAccount, Date terminationDate, User updater)
			throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);

		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.subscriptionCancellation(subscription, terminationDate, updater);
		}
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.CANCELED);
		return update(userAccount, updater);
	}

	public UserAccount userAccountReactivation(UserAccount userAccount, Date activationDate, User updater)
			throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		if (userAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& userAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("user account", userAccount.getCode());
		}

		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		userAccount.setStatusDate(activationDate);
		return update(userAccount, updater);
	}

	public BillingWalletDetailDTO BillingWalletDetail(UserAccount userAccount) throws BusinessException {
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
				amountWithoutTax = amountWithoutTax.add(ratedTransaction.getAmountWithoutTax());
				amountTax = amountTax.add(ratedTransaction.getAmountTax());
			}
		}
		BillingWalletDetailDTO.setAmount(amount);
		BillingWalletDetailDTO.setAmountTax(amountWithoutTax);
		BillingWalletDetailDTO.setAmountWithoutTax(amountTax);
		return BillingWalletDetailDTO;
	}

	public List<RatedTransaction> BillingRatedTransactionList(UserAccount userAccount) throws BusinessException {
		WalletInstance wallet = userAccount.getWallet();
		return wallet.getRatedTransactions();
	}


	@SuppressWarnings("unchecked")
	public List<UserAccount> listByBillingAccount(BillingAccount billingAccount) {
		QueryBuilder qb = new QueryBuilder(UserAccount.class, "c");
		qb.addCriterionEntity("billingAccount", billingAccount);

		try {
			return (List<UserAccount>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("error while getting user account list by billing account",e);
			return null;
		}
	}

	/**
	 * Evatuate the exoneration Taxes EL
	 * 
	 * @param ua The UserAccount
	 * @param provider The Provider
	 * @return
	 */
	public boolean isExonerated(UserAccount ua,Provider provider){
		boolean isExonerated = false;
		if(ua != null && ua.getBillingAccount().getCustomerAccount().getCustomer().getCustomerCategory().getExoneratedFromTaxes()){
			return true;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		if(provider != null &&  !StringUtils.isBlank(provider.getExonerationTaxEl())){
			if(provider.getExonerationTaxEl().indexOf("ua")>-1){
				userMap.put("ua", ua);
			}
			Boolean isExon = Boolean.FALSE;
			try {
				isExon = (Boolean) ValueExpressionWrapper.evaluateExpression(provider.getExonerationTaxEl(), userMap, Boolean.class);
			} catch (BusinessException e) {
				log.error("Error evaluateExpression Exoneration taxes:",e);
				e.printStackTrace();
			}
			isExonerated = (isExon == null ? false : isExon);
		}		
		return isExonerated;
	}
}
