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

package org.meveo.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.qualifier.LowBalance;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.WalletService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for wallet related operations
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class WalletCacheContainerProvider implements Serializable { 

    private static final long serialVersionUID = -4969288143287203121L;

    @Inject
    protected Logger log;
    
    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @EJB
    private WalletService walletService;

    @Inject
    @LowBalance
    protected Event<WalletInstance> lowBalanceEventProducer;

    /**
     * Contains association between prepaid wallet instance and balance value. Key format: &lt;WalletInstance.id&gt;, value: &lt;prepaid wallet balance amount&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-balance")
    private Cache<CacheKeyLong, BigDecimal> balanceCache;

    /**
     * Contains association between prepaid wallet instance and reserved balance value. Key format: &lt;WalletInstance.id&gt;, value: &lt;prepaid wallet reserved balance amount&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-reservedBalance")
    private Cache<CacheKeyLong, BigDecimal> reservedBalanceCache;

    /**
     * Contains association between usage chargeInstance and wallets ids (if it is not the only principal one). Key format: &lt;UsageChargeInstance.id&gt;, value: List of
     * &lt;WalletInstance.id&gt;
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-usageChargeInstanceWallet")
    private Cache<CacheKeyLong, Map<Long, BigDecimal>> chargeInstanceWalletCache;

    private ParamBean paramBean = ParamBeanFactory.getAppScopeInstance();

    private static boolean usePrepaidBalanceCache = true;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    static {
        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();
        usePrepaidBalanceCache = Boolean.parseBoolean(tmpParamBean.getProperty("cache.cachePrepaidBalance", "true"));
    }

    /**
     * Populate wallet to charge association caches from DB
     */
    private void populateWalletIdCache() {

        if (!usePrepaidBalanceCache) {
            log.info("Prepaid balance cache population will be skipped as cache will not be used");
            return;
        }

        boolean prepopulatePrepaidBalanceCache = Boolean.parseBoolean(paramBean.getProperty("cache.cachePrepaidBalance.prepopulate", "true"));

        if (!prepopulatePrepaidBalanceCache) {
            log.info("Prepaid balance cache pre-population will be skipped");
            return;
        }

        // for each prepaid Usage chargeInstance of active subscription we create association
        String currentProvider = currentUser.getProviderCode();
        log.debug("Start to pre-populate Prepaid wallet id cache for provider {}", currentProvider);

        List<ChargeInstance> charges = chargeInstanceService.getPrepaidChargeInstancesForCache();
        for (ChargeInstance charge : charges) {
            addChargeInstanceNoBalances(charge);
        }

        log.info("Wallet cache populated for provider {} with {} usagecharges", currentProvider, charges.size());
    }

    /**
     * Populate wallet balance and reserved balanced caches from DB
     */
    private void populateBalanceCache() {

        if (!usePrepaidBalanceCache) {
            log.info("Prepaid balance cache population will be skipped as cache will not be used");
            return;
        }

        boolean prepopulatePrepaidBalanceCache = Boolean.parseBoolean(paramBean.getProperty("cache.cachePrepaidBalance.prepopulate", "true"));

        if (!prepopulatePrepaidBalanceCache) {
            log.info("Prepaid balance cache pre-population will be skipped");
            return;
        }

        // for each prepaid Usage chargeInstance of active subscription we create association
        String currentProvider = currentUser.getProviderCode();
        log.debug("Start to pre-populate Prepaid balance cache for provider {}", currentProvider);

        // Populate cache with prepaid wallet balance and reserved balance
        List<Object[]> balanceInfos = walletService.getOpenAndReservedBalancesForCache();
        for (Object[] balanceInfo : balanceInfos) {

            CacheKeyLong cacheKey = new CacheKeyLong(currentUser.getProviderCode(), (Long) balanceInfo[0]);

            balanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, (BigDecimal) balanceInfo[1]);
            reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, (BigDecimal) balanceInfo[2]);
        }

        log.info("Wallet cache populated for provider {} with  {} wallets", currentProvider, balanceInfos.size());
    }

    /**
     * Add association between usage charge instance and prepaid wallets. Do not populate wallet balance.
     * 
     * @param chargeInstance usage charge instance.
     */
    // @Lock(LockType.WRITE)
    private void addChargeInstanceNoBalances(ChargeInstance chargeInstance) {

        String currentProvider = currentUser.getProviderCode();

        Map<Long, BigDecimal> walletLimits = new HashMap<>();

        for (WalletInstance wallet : chargeInstance.getWalletInstances()) {
            WalletTemplate walletTemplate = wallet.getWalletTemplate();
			if (walletTemplate != null && walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
                BigDecimal rejectLevel = wallet.getRejectLevel();
                if(walletTemplate!=null && walletTemplate.getRejectLevelEl()!=null) {
					rejectLevel = walletService.evaluateElExpressionValue(walletTemplate.getRejectLevelEl(), wallet, chargeInstance);
    			}
				walletLimits.put(wallet.getId(), rejectLevel);
            }
        }

        log.debug("Update chargeInstanceWallet cache with charge {} wallets:{}", chargeInstance.getId(), walletLimits.size());

        // If no value are left in the map - LEAVE, as cache can be populated at runtime instead of at application start and need to distinguish
        // between not cached key and key with no records
        chargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyLong(currentProvider, chargeInstance.getId()), walletLimits);

    }

    /**
     * Add association between usage charge instance and prepaid wallets.
     * 
     * @param usageChargeInstance usage charge instance.
     * @return A list of wallets ids
     */
    // @Lock(LockType.WRITE)
    public Map<Long, BigDecimal> addChargeInstance(ChargeInstance usageChargeInstance) {

        if (!usePrepaidBalanceCache || !usageChargeInstance.getPrepaid()) {
            return null;
        }

        Map<Long, BigDecimal> walletLimits = new HashMap<>();

        String currentProvider = currentUser.getProviderCode();

        for (WalletInstance wallet : usageChargeInstance.getWalletInstances()) {
        	WalletTemplate walletTemplate = wallet.getWalletTemplate();
			if (walletTemplate != null && walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
                BigDecimal rejectLevel = wallet.getRejectLevel();
                if(walletTemplate!=null && walletTemplate.getRejectLevelEl()!=null) {
					rejectLevel = walletService.evaluateElExpressionValue(walletTemplate.getRejectLevelEl(), wallet, usageChargeInstance);
					
                }
				walletLimits.put(wallet.getId(), rejectLevel);
                if (!balanceCache.containsKey(new CacheKeyLong(currentProvider, wallet.getId()))) {
                    initializeBalanceCachesForWallet(wallet.getId());
                }
            }
        }

        log.debug("Update chargeInstanceWallet cache with charge {} wallets:{}", usageChargeInstance.getId(), walletLimits.size());

        // If no value are left in the map - LEAVE, as cache can be populated at runtime instead of at application start and need to distinguish
        // between not cached key and key with no records
        chargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyLong(currentProvider, usageChargeInstance.getId()), walletLimits);

        return walletLimits;
    }

    /**
     * Initialize or update cached balance and reserved balance for a given wallet instance.
     * 
     * @param walletId Wallet ID
     * @return Balance amount
     */
    // @Lock(LockType.WRITE)
    public BigDecimal initializeBalanceCachesForWallet(Long walletId) {
        BigDecimal[] balances = walletService.calculateWalletBalances(walletId);

        CacheKeyLong cacheKey = new CacheKeyLong(currentUser.getProviderCode(), walletId);

        balanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, balances[0] == null ? BigDecimal.ZERO : balances[0]);
        reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, balances[1] == null ? BigDecimal.ZERO : balances[1]);

        log.debug("Added to balance caches walletId:{} balance:{} reservedBalance:{}", walletId, balances[0], balances[1]);
        return balances[0];
    }

    /**
     * Cache balance and reserved balance for a given new wallet instance
     * 
     * @param walletInstance Wallet instance
     */
    public void addWalletInstance(WalletInstance walletInstance) {
        if (usePrepaidBalanceCache && walletInstance.getWalletTemplate() != null && walletInstance.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
            initializeBalanceCachesForWallet(walletInstance.getId());
        }
    }

    /**
     * Update cached balance and reserved balance for a given wallet operation on prepaid usage type charge.
     * 
     * @param op Wallet operation
     */
    // @Lock(LockType.WRITE)
    public void updateBalance(WalletOperation op) {

        if (!usePrepaidBalanceCache) {
            return;
        }

        // Cache deals with prepaid charges only.
        if (!op.getChargeInstance().getPrepaid()) {
            return;
        }

        // FIXME: handle reservation
        BigDecimal oldValue = null;
        BigDecimal newValue = null;

        WalletInstance wallet = op.getWallet();
		CacheKeyLong cacheKey = new CacheKeyLong(currentUser.getProviderCode(), wallet.getId());

        // Either of caches is not initialized. By doing so, last operation will be included, no need to update it separatelly
        if (!reservedBalanceCache.containsKey(cacheKey) || !balanceCache.containsKey(cacheKey)) {
            initializeBalanceCachesForWallet(wallet.getId());
            return;
        }

        if (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.RESERVED) || (op.getStatus() == WalletOperationStatusEnum.CANCELED) || (op.getStatus() == WalletOperationStatusEnum.REJECTED) ) {

            oldValue = reservedBalanceCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);

            if (op.getStatus() == WalletOperationStatusEnum.CANCELED || op.getStatus() == WalletOperationStatusEnum.REJECTED) {
                newValue = oldValue.subtract(op.getAmountWithTax());
            } else {
                newValue = oldValue.add(op.getAmountWithTax());
            }

            log.debug("Update reservedBalance Cache for wallet {} {}->{}", wallet.getId(), oldValue, newValue);

            reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, newValue);
        }

        if (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.OPEN)) {

            oldValue = balanceCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(cacheKey);
            newValue = oldValue.add(op.getAmountWithTax());

            BigDecimal lowBalanceLevel = wallet.getLowBalanceLevel();
            WalletTemplate walletTemplate = wallet.getWalletTemplate();
			if(walletTemplate!=null && walletTemplate.getLowBalanceLevelEl()!=null) {
				lowBalanceLevel = walletService.evaluateElExpressionValue(walletTemplate.getLowBalanceLevelEl(), wallet, op.getChargeInstance());
			}
			log.debug("Update balance Cache for wallet {} {}->{} lowBalanceLevel:{} rejectLevel: {}", wallet.getId(), oldValue, newValue, lowBalanceLevel, wallet.getRejectLevel());

            balanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(cacheKey, newValue);

            if ( lowBalanceLevel != null) {
                if (lowBalanceLevel.compareTo(newValue) <= 0 && lowBalanceLevel.compareTo(oldValue) > 0) {
                    lowBalanceEventProducer.fire(wallet);
                }
            }

        }
    }
    
    /**
     * Get cached balance for a given wallet id
     * 
     * @param walletId Wallet id
     * @return Cached balance amount or NULL if no cache by that key exists
     */
    public BigDecimal getBalance(Long walletId) {
        BigDecimal result = balanceCache.get(new CacheKeyLong(currentUser.getProviderCode(), walletId));
        return result;
    }

    /**
     * Get cached reserved balance for a given wallet id
     * 
     * @param walletId Wallet id
     * @return Cached reserved balance amount or NULL if no cache by that key exists
     */
    public BigDecimal getReservedBalance(Long walletId) {
        BigDecimal result = reservedBalanceCache.get(new CacheKeyLong(currentUser.getProviderCode(), walletId));
        return result;
    }

    /**
     * Get a list of prepaid wallets (ids) associated to a given usage charge instance
     * 
     * @param usageChargeInstanceId Usage charge instance id
     * @return A list of wallets ids
     */
    public Map<Long, BigDecimal> getWalletIdAndLimits(Long usageChargeInstanceId) {
        return chargeInstanceWalletCache.get(new CacheKeyLong(currentUser.getProviderCode(), usageChargeInstanceId));
    }

    /**
     * Get a summary of cached information
     * 
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(balanceCache.getName(), balanceCache);
        summaryOfCaches.put(reservedBalanceCache.getName(), reservedBalanceCache);
        summaryOfCaches.put(chargeInstanceWalletCache.getName(), chargeInstanceWalletCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name. Removes current provider's data from cache and populates it again
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(chargeInstanceWalletCache.getName()) || cacheName.contains(chargeInstanceWalletCache.getName())) {
            clearWalletIdCache();
            populateWalletIdCache();
        }
        if (cacheName == null || cacheName.equals(balanceCache.getName()) || cacheName.equals(reservedBalanceCache.getName()) || cacheName.contains(balanceCache.getName())
                || cacheName.contains(reservedBalanceCache.getName())) {

            clearBalanceCache();
            populateBalanceCache();
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(chargeInstanceWalletCache.getName()) || cacheName.contains(chargeInstanceWalletCache.getName())) {

            populateWalletIdCache();
        }
        if (cacheName == null || cacheName.equals(balanceCache.getName()) || cacheName.equals(reservedBalanceCache.getName()) || cacheName.contains(balanceCache.getName())
                || cacheName.contains(reservedBalanceCache.getName())) {

            populateBalanceCache();
        }
    }

    /**
     * Clear the current provider data from wallet id cache
     * 
     */
    private void clearWalletIdCache() {
        String currentProvider = currentUser.getProviderCode();

        // chargeInstanceWalletCache.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));

        for (CacheKeyLong elem : cleanCache(chargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).keySet().iterator(), currentProvider)) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            chargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }
    }

    /**
     * Clear the current provider data from balance and reserved balance cache
     * 
     */
    private void clearBalanceCache() {
        String currentProvider = currentUser.getProviderCode();
        // balanceCache.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));
        // reservedBalanceCache.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));

        for (CacheKeyLong elem : cleanCache(reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).keySet().iterator(), currentProvider)) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }

        for (CacheKeyLong elem : cleanCache(chargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).keySet().iterator(), currentProvider)) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            chargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }
    }

    /**
     * return data keys of specific provider in cache
     * 
     * @param iter keys of provider in cache
     * @param currentProvider
     * @return List of keys of the specified provider
     */
    private ArrayList<CacheKeyLong> cleanCache(CloseableIterator<CacheKeyLong> iter, String currentProvider) {
        ArrayList<CacheKeyLong> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            CacheKeyLong entry = iter.next();
            boolean comparison = (entry.getProvider() == null) ? currentProvider == null : entry.getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry);
            }
        }
        return itemsToBeRemoved;
    }
}