package org.meveo.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.event.qualifier.LowBalance;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.billing.impl.WalletService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for wallet related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class WalletCacheContainerProvider implements Serializable { // CacheContainerProvider, Serializable {

    private static final long serialVersionUID = -4969288143287203121L;

    @Inject
    protected Logger log;

    @EJB
    private UsageChargeInstanceService usageChargeInstanceService;

    @EJB
    private WalletService walletService;

    @Inject
    @LowBalance
    protected Event<WalletInstance> lowBalanceEventProducer;

    /**
     * Contains association between prepaid wallet instance and balance value. Key format: <WalletInstance.id>, value: <prepaid wallet balance amount>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-balance")
    private Cache<Long, BigDecimal> balanceCache;

    /**
     * Contains association between prepaid wallet instance and reserved balance value. Key format: <WalletInstance.id>, value: <prepaid wallet reserved balance amount>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-reservedBalance")
    private Cache<Long, BigDecimal> reservedBalanceCache;

    /**
     * Contains association between usage chargeInstance and wallets ids (if it is not the only principal one). Key format: <UsageChargeInstance.id>, value: List of
     * <WalletInstance.id>
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-usageChargeInstanceWallet")
    private Cache<Long, List<Long>> usageChargeInstanceWalletCache;

    // @Resource(name = "java:jboss/infinispan/container/meveo")
    // private CacheContainer meveoContainer;

    @PostConstruct
    private void init() {
        try {
            log.debug("WalletCacheContainerProvider initializing...");

            // balanceCache = meveoContainer.getCache("meveo-balance");
            // reservedBalanceCache = meveoContainer.getCache("meveo-reservedBalance");
            // usageChargeInstanceWalletCache = meveoContainer.getCache("meveo-usageChargeInstanceWallet");

            refreshCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));

            log.info("WalletCacheContainerProvider initialized");
        } catch (Exception e) {
            log.error("WalletCacheContainerProvider init() error", e);
            throw e;
        }
    }

    /**
     * Populate wallet balance, reserved balanced and charge association caches from DB
     */
    private void populateWalletCache() {
        log.debug("Start to populate wallet cache");

        balanceCache.clear();
        reservedBalanceCache.clear();
        usageChargeInstanceWalletCache.clear();

        // for each prepaid Usage chargeInstance of active subscription we create association
        List<UsageChargeInstance> charges = usageChargeInstanceService.getPrepaidUsageChargeInstancesForCache();
        for (UsageChargeInstance charge : charges) {
            updateCache(charge);
        }

        // Populate cache with prepaid wallet balance and reserved balance
        List<Long> walletIds = walletService.getWalletsIdsForCache();
        for (Long walletId : walletIds) {
            if (!balanceCache.containsKey(walletId)) {
                fillBalanceCaches(walletId);
            }
        }

        log.info("Wallet cache populated with {} usagecharges and {} wallets", charges.size(), walletIds.size());
    }

    /**
     * Add association between usage charge instance and prepaid wallets
     * 
     * @param charge
     */
    // @Lock(LockType.WRITE)
    public void updateCache(UsageChargeInstance charge) {
        log.error("AKK WCCP line 142");
        List<WalletInstance> wallets = charge.getWalletInstances();
        List<Long> walletIds = new ArrayList<>();

        log.error("AKK WCCP line 145");
        for (WalletInstance wallet : wallets) {
            if (!walletIds.contains(wallet.getId()) && wallet.getWalletTemplate() != null && wallet.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
                walletIds.add(wallet.getId());
                if (!balanceCache.containsKey(wallet.getId())) {
                    fillBalanceCaches(wallet.getId());
                }
            }
        }

        log.debug("Update usageChargeInstanceWallet cache with charge {} wallets:{}", charge.getId(), walletIds.size());
        if (walletIds.size() > 0) {
            usageChargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(charge.getId(), walletIds);
        } else {
            usageChargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(charge.getId());
        }
        log.error("AKK WCCP line 162");
    }

    /**
     * Update cached balance and reserved balance for a given wallet instance
     * 
     * @param walletId Wallet ID
     * @return Balance amount
     */
    // @Lock(LockType.WRITE)
    private BigDecimal fillBalanceCaches(Long walletId) {
        BigDecimal balance = walletService.getWalletBalance(walletId);
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        balanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(walletId, balance);

        BigDecimal reservedBalance = walletService.getWalletReservedBalance(walletId);
        if (reservedBalance == null) {
            reservedBalance = BigDecimal.ZERO;
        }
        reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(walletId, reservedBalance);

        log.debug("Added to balance caches walletId:{} balance:{} reservedBalance:{}", walletId, balance, reservedBalance);
        return balance;
    }

    /**
     * Update cached balance and reserved balance for a given wallet instance
     * 
     * @param walletInstance Wallet instance
     */
    public void updateBalanceCache(WalletInstance walletInstance) {
        if (walletInstance.getWalletTemplate() != null && walletInstance.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
            fillBalanceCaches(walletInstance.getId());
        }
    }

    /**
     * Update cached balance and reserved balance for a given wallet operation
     * 
     * @param op Wallet operation
     */
    // @Lock(LockType.WRITE)
    public void updateBalanceCache(WalletOperation op) {
        // FIXME: handle reservation
        BigDecimal oldValue = null;
        BigDecimal newValue = null;

        if (reservedBalanceCache.containsKey(op.getWallet().getId())
                && (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.RESERVED) || (op.getStatus() == WalletOperationStatusEnum.CANCELED))) {

            oldValue = reservedBalanceCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(op.getWallet().getId());

            if (op.getStatus() == WalletOperationStatusEnum.CANCELED) {
                newValue = oldValue.add(op.getAmountWithTax());
            } else {
                newValue = oldValue.subtract(op.getAmountWithTax());
            }

            log.debug("Update reservedBalance Cache for wallet {} {}->{}", op.getWallet().getId(), oldValue, newValue);

            reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(op.getWallet().getId(), newValue);
        }

        if (balanceCache.containsKey(op.getWallet().getId()) && (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.OPEN))) {

            oldValue = balanceCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(op.getWallet().getId());
            newValue = oldValue.subtract(op.getAmountWithTax());

            log.debug("Update balance Cache for wallet {} {}->{} lowBalanceLevel:{}", op.getWallet().getId(), oldValue, newValue, op.getWallet().getLowBalanceLevel());

            balanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(op.getWallet().getId(), newValue);

            if (op.getWallet().getLowBalanceLevel() != null) {
                if (op.getWallet().getLowBalanceLevel().compareTo(newValue) >= 0 && op.getWallet().getLowBalanceLevel().compareTo(oldValue) < 0) {
                    lowBalanceEventProducer.fire(op.getWallet());
                }
            }

        } else if (op.getChargeInstance() instanceof UsageChargeInstance) {
            updateCache((UsageChargeInstance) op.getChargeInstance());
        }
    }

    /**
     * Get cached balance for a given wallet id
     * 
     * @param walletId Wallet id
     * @return Cached balance amount
     */
    public BigDecimal getBalance(Long walletId) {
        BigDecimal result = null;
        // if (balanceCache.containsKey(walletId)) {
        result = balanceCache.get(walletId);
        // }
        return result;
    }

    /**
     * Get cached reserved balance for a given wallet id
     * 
     * @param walletId Wallet id
     * @return Cached reserved balance amount
     */
    public BigDecimal getReservedBalance(Long walletId) {
        BigDecimal result = null;
        // if (reservedBalanceCache.containsKey(walletId)) {
        result = reservedBalanceCache.get(walletId);
        // }
        return result;
    }

    /**
     * Get a total cached reserved balance for a given list of wallet id
     * 
     * @param walletIds A list of Wallet ids
     * @return Total cached reserved balance amount
     */
    public BigDecimal getReservedBalance(List<Long> walletIds) {
        BigDecimal totalBalance = reservedBalanceCache.get(walletIds.get(0));
        if (walletIds.size() > 1) {
            for (int i = 1; i < walletIds.size(); i++) {
                totalBalance = totalBalance.add(reservedBalanceCache.get(walletIds.get(i)));
            }
        }
        return totalBalance;
    }

    /**
     * Is reserved balance cached for a given wallet id
     * 
     * @param walletId Wallet id
     * @return True if reserved balance is cached
     */
    public boolean isReservedBalanceCached(Long walletId) {
        return reservedBalanceCache.containsKey(walletId);
    }

    /**
     * Are wallet ids cached for a given usage charge instance
     * 
     * @param usageChargeInstanceId Usage charge instance id
     * @return True if wallet ids cached
     */
    public boolean isWalletIdsCached(Long usageChargeInstanceId) {
        return usageChargeInstanceWalletCache.containsKey(usageChargeInstanceId);
    }

    /**
     * Get a list of wallets associated to a given usage charge instance
     * 
     * @param usageChargeInstanceId Usage charge instance id
     * @return A list of wallets
     */
    public List<Long> getWallets(Long usageChargeInstanceId) {
        return usageChargeInstanceWalletCache.get(usageChargeInstanceId);
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
        summaryOfCaches.put(usageChargeInstanceWalletCache.getName(), usageChargeInstanceWalletCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(balanceCache.getName()) || cacheName.equals(reservedBalanceCache.getName())
                || cacheName.equals(usageChargeInstanceWalletCache.getName()) || cacheName.contains(balanceCache.getName()) || cacheName.contains(reservedBalanceCache.getName())
                || cacheName.contains(usageChargeInstanceWalletCache.getName())) {
            populateWalletCache();
        }
    }
}