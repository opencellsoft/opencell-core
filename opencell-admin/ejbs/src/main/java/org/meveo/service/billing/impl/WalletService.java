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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * Wallet service implementation.
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class WalletService extends PersistenceService<WalletInstance> {
	
    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;

    private static boolean usePrepaidBalanceCache = true;

    @PostConstruct
    private void initialize() {
        usePrepaidBalanceCache = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("cache.cachePrepaidBalance", "true"));
    }

    @Override
    public void create(WalletInstance walletInstance) throws BusinessException {
        super.create(walletInstance);
        walletCacheContainerProvider.addWalletInstance(walletInstance);
    }

    public WalletInstance findByUserAccount(UserAccount userAccount) {

        QueryBuilder qb = new QueryBuilder(WalletInstance.class, "w");
        try {
            qb.addCriterionEntity("userAccount", userAccount);

            return (WalletInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to find walletInstance by user account", e);
            return null;
        }
    }

    public WalletInstance findByUserAccountAndCode(UserAccount userAccount, String code) {
        QueryBuilder qb = new QueryBuilder(WalletInstance.class, "w");
        try {
            qb.addCriterionEntity("userAccount", userAccount);
            qb.addCriterion("w.code", "=", code, true);

            return (WalletInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to find walletInstance by user account And code", e);
            return null;
        }
    }

    public WalletInstance getWalletInstance(UserAccount userAccount, WalletTemplate walletTemplate, boolean isVirtual) throws BusinessException {
        String walletCode = walletTemplate.getCode();
        log.debug("get wallet instance for userAccount {} and wallet template {}", userAccount.getCode(), walletCode);
        if (!WalletTemplate.PRINCIPAL.equals(walletCode)) {
            if (!userAccount.getPrepaidWallets().containsKey(walletCode)) {
                WalletInstance wallet = new WalletInstance();
                wallet.setCode(walletCode);
                wallet.setWalletTemplate(walletTemplate);
                wallet.setUserAccount(userAccount);

                if (!isVirtual) {
                    create(wallet);
                }

                log.debug("add prepaid wallet {} to useraccount {}", walletCode, userAccount.getCode());
                userAccount.getPrepaidWallets().put(walletCode, wallet);

                if (!isVirtual) {
                    getEntityManager().merge(userAccount);
                }
            }

            return userAccount.getPrepaidWallets().get(walletCode);
        } else {
            log.debug("return the Principal wallet instance {}", userAccount.getWallet().getId());
            return userAccount.getWallet();
        }
    }

    public List<Long> getWalletsToMatch(Date date) {
        return getEntityManager().createNamedQuery("WalletInstance.listPrepaidWalletsToMatchIds", Long.class).setParameter("matchingDate", date).getResultList();
    }

    /**
     * Get a list of prepaid and active wallet ids (user account is active) to populate a cache
     * 
     * @return A list of prepaid and active wallet ids
     */
    public List<Long> getWalletsIdsForCache() {
        return getEntityManager().createNamedQuery("WalletInstance.listPrepaidActiveWalletIds", Long.class).getResultList();
    }

    /**
     * Get balance amount for a give wallet instance. Balance is either retrieved from cache or calculated from DB.
     * 
     * @param walletInstanceId Wallet instance id
     * @return Wallet balance amount
     */
    public BigDecimal getWalletBalance(Long walletInstanceId) {

        if (usePrepaidBalanceCache) {

            BigDecimal balance = walletCacheContainerProvider.getBalance(walletInstanceId);

            // Populate cache if record does not exist in cache
            if (balance == null) {
                balance = walletCacheContainerProvider.initializeBalanceCachesForWallet(walletInstanceId);
            }

            return balance != null ? balance : BigDecimal.ZERO;

        } else {
            try {
                Object[] balances = (Object[]) getEntityManager().createNamedQuery("WalletOperation.getBalancesForWalletInstance").setParameter("walletId", walletInstanceId)
                    .getSingleResult();
                return balances[0] != null ? (BigDecimal) balances[0] : BigDecimal.ZERO;

            } catch (NoResultException e) {
                return BigDecimal.ZERO;
            }
        }
    }

    /**
     * Get reserved balance amount for a give wallet instance. Balance is either retrieved from cache or calculated from DB.
     * 
     * @param walletInstanceId Wallet instance id
     * @return Wallet's reserved balance amount
     */
    public BigDecimal getWalletReservedBalance(Long walletInstanceId) {

        if (usePrepaidBalanceCache) {

            BigDecimal balance = walletCacheContainerProvider.getReservedBalance(walletInstanceId);

            // Populate cache if record does not exist in cache
            if (balance == null) {
                walletCacheContainerProvider.initializeBalanceCachesForWallet(walletInstanceId);
                balance = walletCacheContainerProvider.getReservedBalance(walletInstanceId);
            }

            return balance != null ? balance : BigDecimal.ZERO;

        } else {

            try {
                Object[] balances = (Object[]) getEntityManager().createNamedQuery("WalletOperation.getBalancesForWalletInstance").setParameter("walletId", walletInstanceId)
                    .getSingleResult();
                return balances[0] != null ? (BigDecimal) balances[0] : BigDecimal.ZERO;

            } catch (NoResultException e) {
                return BigDecimal.ZERO;
            }
        }
    }

    /**
     * Get reserved balances for a list of wallet ids.
     * 
     * @param walletIds A list of Wallet ids
     * @return A map of SORTED balances with wallet id as a key
     */
    public Map<Long, BigDecimal> getWalletReservedBalances(Collection<Long> walletIds) {

        Map<Long, BigDecimal> balances = new HashMap<>();
        for (Long walletId : walletIds) {
            balances.put(walletId, getWalletReservedBalance(walletId));
        }

        return ListUtils.sortMapByValue(balances);
    }

    /**
     * Calculate balance amount for a given wallet instance by summing wallet operations in DB
     * 
     * @param walletInstanceId Wallet instance id
     * @return Wallet balance amount
     */
    public BigDecimal[] calculateWalletBalances(Long walletInstanceId) {
        try {
            Object[] balances = (Object[]) getEntityManager().createNamedQuery("WalletOperation.getBalancesForWalletInstance").setParameter("walletId", walletInstanceId)
                .getSingleResult();
            return new BigDecimal[] { (BigDecimal) balances[0], (BigDecimal) balances[1] };

        } catch (NoResultException e) {
            return new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO };
        }
    }

    /**
     * Get Open and reserved balances for prepaid wallets
     * 
     * @return A list of Open and reserved balances in the following array format: walletId, open balance, reserved balance
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getOpenAndReservedBalancesForCache() {
        List<Object[]> resultList = getEntityManager().createNamedQuery("WalletOperation.getBalancesForCache").getResultList();

        return resultList;
    }

    @SuppressWarnings("unchecked")
    public List<WalletInstance> findByWalletTemplate(WalletTemplate walletTemplate) {
        QueryBuilder qb = new QueryBuilder(WalletInstance.class, "w");
        qb.addCriterionEntity("walletTemplate", walletTemplate);
        return qb.find(getEntityManager());
    }

    /**
     * Get a list of PREPAID wallets (ids) associated to a given charge instance. Wallets are looked up in cache or retrieved from DB.
     * 
     * @param chargeInstance Charge instance
     * @return A Map of prepaid wallets ids and their balance rejection limits
     */
    public Map<Long, BigDecimal> getWalletIds(ChargeInstance chargeInstance) {

        if (usePrepaidBalanceCache) {

            Map<Long, BigDecimal> walletLimits = walletCacheContainerProvider.getWalletIdAndLimits(chargeInstance.getId());

            // Populate cache if record does not exist in cache
            if (walletLimits == null) {
                walletLimits = walletCacheContainerProvider.addChargeInstance(chargeInstance);
            }

            return walletLimits;

        } else {

            Map<Long, BigDecimal> walletLimits = new HashMap<>();

            for (WalletInstance wallet : chargeInstance.getWalletInstances()) {
            	WalletTemplate walletTemplate = wallet.getWalletTemplate();
                if (walletTemplate != null && walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
    				walletLimits.put(wallet.getId(), evaluateRejectLevel(chargeInstance, wallet, walletTemplate));
                }
            }

            return walletLimits;
        }
    }

	private BigDecimal evaluateRejectLevel(ChargeInstance chargeInstance, WalletInstance wallet, WalletTemplate walletTemplate) {
		BigDecimal rejectLevel = wallet.getRejectLevel();
		if(walletTemplate!=null && walletTemplate.getRejectLevelEl()!=null) {
			rejectLevel = evaluateElExpressionValue(walletTemplate.getRejectLevelEl(), wallet, chargeInstance);
		}
		return rejectLevel;
	}
    
    public BigDecimal evaluateElExpressionValue(String expression, WalletInstance walletInstance, ChargeInstance chargeInstance) {
    	if(expression == null){
            return null;
        }
    	Map<Object, Object> initialContext = new HashMap<Object, Object>(); 
        if (expression.indexOf(ValueExpressionWrapper.VAR_WALLET_INSTANCE) >= 0) {
        	initialContext.put(ValueExpressionWrapper.VAR_WALLET_INSTANCE, walletInstance);
        }
        Map<Object, Object> context = ValueExpressionWrapper.completeContext(expression, initialContext, chargeInstance);
		return ValueExpressionWrapper.evaluateExpression(expression, context, BigDecimal.class);
    }
}
