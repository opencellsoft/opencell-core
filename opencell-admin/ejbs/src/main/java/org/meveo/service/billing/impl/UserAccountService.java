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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidEntityStatusException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingWalletDetailDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.AccountService;

/**
 * A service class to manage CRUD operations on UserAccount entity
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class UserAccountService extends AccountService<UserAccount> {

    @Inject
    private WalletService walletService;

    @Inject
    private SubscriptionService subscriptionService;

    /**
     * Create User account and associate it with a Principal wallet
     * 
     * @param userAccount User account to create
     * @throws BusinessException Business exception
     */
    @Override
    public void create(UserAccount userAccount) throws BusinessException {
        super.create(userAccount);

        WalletInstance wallet = new WalletInstance();
        wallet.setCode(WalletTemplate.PRINCIPAL);
        wallet.setUserAccount(userAccount);
        walletService.create(wallet);

        userAccount.setWallet(wallet);
    }

    /**
     * Terminate User account. Status will be changed to Terminated. Action will also terminate related Subscriptions.
     * 
     * @param userAccount User account
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @return Updated User account entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public UserAccount terminateUserAccount(UserAccount userAccount, Date terminationDate, TerminationReason terminationReason) throws BusinessException {

        if (userAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            // throw new InvalidEntityStatusException(UserAccount.class, userAccount.getCode(), "terminate", userAccount.getStatus(), AccountStatusEnum.ACTIVE);
            return userAccount;
        }

        log.debug("Will terminate User account " + userAccount.getCode());

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

        userAccount = update(userAccount);

        log.info("User account " + userAccount.getCode() + " was terminated");

        return userAccount;
    }

    /**
     * Cancel User account. Status will be changed to Canceled. Action will also cancel related Subscriptions.
     * 
     * @param userAccount User account
     * @param cancelationDate Cancellation date
     * @return Updated User account entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public UserAccount cancelUserAccount(UserAccount userAccount, Date cancelationDate) throws BusinessException {

        if (userAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            // throw new InvalidEntityStatusException(UserAccount.class, userAccount.getCode(), "cancel", userAccount.getStatus(), AccountStatusEnum.ACTIVE);
            return userAccount;
        }

        log.debug("Will cancel User account " + userAccount.getCode());

        if (cancelationDate == null) {
            cancelationDate = new Date();
        }
        List<Subscription> subscriptions = userAccount.getSubscriptions();
        for (Subscription subscription : subscriptions) {
            subscriptionService.cancelSubscription(subscription, cancelationDate);
        }
        userAccount.setTerminationDate(cancelationDate);
        userAccount.setStatus(AccountStatusEnum.CANCELED);

        userAccount = update(userAccount);

        log.info("User account " + userAccount.getCode() + " was canceled");

        return userAccount;
    }

    /**
     * Activate previously canceled or terminated User account. Status will be changed to Active.
     * 
     * @param userAccount User account
     * @param activationDate Activation date
     * @return Updated User account entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public UserAccount reactivateUserAccount(UserAccount userAccount, Date activationDate) throws BusinessException {

        if (userAccount.getStatus() != AccountStatusEnum.TERMINATED && userAccount.getStatus() != AccountStatusEnum.CANCELED) {
            throw new InvalidEntityStatusException(UserAccount.class, userAccount.getCode(), "reactivate", userAccount.getStatus(), AccountStatusEnum.TERMINATED,
                AccountStatusEnum.CANCELED);
        }

        log.debug("Will reactivate User account " + userAccount.getCode());

        if (activationDate == null) {
            activationDate = new Date();
        }

        userAccount.setStatus(AccountStatusEnum.ACTIVE);
        userAccount.setTerminationDate(null);
        userAccount.setTerminationReason(null);

        userAccount = update(userAccount);

        log.info("User account " + userAccount.getCode() + " was reactivated");

        return userAccount;
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

    public List<UserAccount> listByBillingAccount(BillingAccount billingAccount) {
        return billingAccount.getUsersAccounts();
        /**
		 * Check N + 1 query problem
		QueryBuilder qb = new QueryBuilder(UserAccount.class, "c");
		qb.addCriterionEntity("billingAccount", billingAccount);

		try {
			return (List<UserAccount>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			log.warn("error while getting user account list by billing account",e);
			return null;
		}*/
    }

}
