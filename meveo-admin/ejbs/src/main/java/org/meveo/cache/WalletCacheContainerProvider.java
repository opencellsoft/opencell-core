package org.meveo.cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
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
public class WalletCacheContainerProvider {

    @Inject
    protected Logger log;

    @EJB
    private UsageChargeInstanceService usageChargeInstanceService;

    @EJB
    private WalletService walletService;
    
    @Inject
    @LowBalance
    protected Event<BigDecimal> lowBalanceEventProducer;

    /**
     * Contains association between prepaid wallet instance and balance value. Key format:  WalletInstance.id.
     */
    // @Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-balance")
    private BasicCache<Long, BigDecimal> balanceCache;

    /**
     * Contains association between prepaid wallet instance and reserved balance value. Key format: WalletInstance.id.
     */
    // @Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-reservedBalance")
    private BasicCache<Long, BigDecimal> reservedBalanceCache;

    /**
     * Contains association between usage chargeInstance and wallets ids (if it is not the only principal one). Key format: UsageChargeInstance.id.
     */
    // @Resource(lookup = "java:jboss/infinispan/cache/meveo/meveo-usageChargeInstanceWallet")
    private BasicCache<Long, List<Long>> usageChargeInstanceWalletCache;

    @Resource(name = "java:jboss/infinispan/container/meveo")
    private CacheContainer meveoContainer;

    @PostConstruct
    private void init() {
        try {
            log.debug("WalletCacheContainerProvider initializing...");

            balanceCache = meveoContainer.getCache("meveo-balance");
            reservedBalanceCache = meveoContainer.getCache("meveo-reservedBalance");
            usageChargeInstanceWalletCache = meveoContainer.getCache("meveo-usageChargeInstanceWallet");

            populateWalletCache();

            log.debug("WalletCacheContainerProvider initialized");
        } catch (Exception e) {
            log.error("WalletCacheContainerProvider init() error", e);
        }
    }

    private void populateWalletCache() {
        log.info("Start to populate wallet cache");

        balanceCache.clear();
        reservedBalanceCache.clear();
        usageChargeInstanceWalletCache.clear();

        // for each recurring usage charInstance of active subscription we create association
        List<UsageChargeInstance> charges = usageChargeInstanceService.getPrepaidUsageChargeInstancesForCache();
        for (UsageChargeInstance charge : charges) {
            updateCache(charge);
        }
        List<Long> walletIds = walletService.getWalletsIdsForCache();
        for (Long walletId : walletIds) {
            if (!balanceCache.containsKey(walletId)) {
                fillBalanceCaches(walletId);
            }
        }
        log.debug("Wallet cache populated with {} usagecharges and {} wallets", charges.size(), walletIds.size());
    }

    public void updateCache(UsageChargeInstance charge) {
        // TODO:: make sure ordering is correct
        List<WalletInstance> wallets = charge.getWalletInstances();
        List<Long> walletIds = new ArrayList<>();

        for (WalletInstance wallet : wallets) {
            if (!walletIds.contains(wallet.getId()) && wallet.getWalletTemplate() != null && wallet.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
                walletIds.add(wallet.getId());
                if (!balanceCache.containsKey(wallet.getId())) {
                    fillBalanceCaches(wallet.getId());
                }
            }
        }

        log.info("UpdateCache usageChargeInstanceWallet charge {} wallets:{}", charge.getId(), walletIds.size());
        if (walletIds.size() > 0) {
            usageChargeInstanceWalletCache.put(charge.getId(), walletIds);
        } else {
            usageChargeInstanceWalletCache.remove(charge.getId());
        }
    }

    private BigDecimal fillBalanceCaches(Long walletId) {
        BigDecimal balance = walletService.getWalletBalance(walletId);
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        balanceCache.put(walletId, balance);
        BigDecimal reservedBalance = walletService.getWalletReservedBalance(walletId);
        if (reservedBalance == null) {
            reservedBalance = BigDecimal.ZERO;
        }
        reservedBalanceCache.put(walletId, reservedBalance);
        log.info("Added to balance caches walletId:{} balance:{} reservedBalance:{}", walletId, balance, reservedBalance);
        return balance;
    }

    public void updateBalanceCache(WalletInstance walletInstance) {
        if (walletInstance.getWalletTemplate() != null && walletInstance.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
            fillBalanceCaches(walletInstance.getId());
        }
    }

    public void updateBalanceCache(WalletOperation op) {
        // FIXME: handle reservation
        BigDecimal oldValue = null;
        BigDecimal newValue = null;

        if (reservedBalanceCache.containsKey(op.getWallet().getId())
                && (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.RESERVED) || (op.getStatus() == WalletOperationStatusEnum.CANCELED))) {
            oldValue = reservedBalanceCache.get(op.getWallet().getId());
            if (op.getStatus() == WalletOperationStatusEnum.CANCELED) {
                newValue = oldValue.add(op.getAmountWithTax());
            } else {
                newValue = oldValue.subtract(op.getAmountWithTax());
            }
            log.info("Update reservedBalance Cache for wallet {} {}->{}", op.getWallet().getId(), oldValue, newValue);
            reservedBalanceCache.put(op.getWallet().getId(), newValue);
        }

        if (balanceCache.containsKey(op.getWallet().getId()) && (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.OPEN))) {
            oldValue = balanceCache.get(op.getWallet().getId());
            newValue = oldValue.subtract(op.getAmountWithTax());
            log.info("Update balance Cache for wallet {} {}->{} lowBalanceLevel:{}", op.getWallet().getId(), oldValue, newValue,op.getWallet().getLowBalanceLevel());
            balanceCache.put(op.getWallet().getId(), newValue);
            if(op.getWallet().getLowBalanceLevel()!=null){
                if(op.getWallet().getLowBalanceLevel().compareTo(newValue)>=0 
                        && op.getWallet().getLowBalanceLevel().compareTo(oldValue)<0){
                    lowBalanceEventProducer.equals(newValue);
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
        if (balanceCache.containsKey(walletId)) {
            result = balanceCache.get(walletId);
        }
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
        if (reservedBalanceCache.containsKey(walletId)) {
            result = reservedBalanceCache.get(walletId);
        }
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
    @SuppressWarnings("rawtypes")
    public Map<String, BasicCache> getCaches() {
        Map<String, BasicCache> summaryOfCaches = new HashMap<String, BasicCache>();
        summaryOfCaches.put(balanceCache.getName(), balanceCache);
        summaryOfCaches.put(reservedBalanceCache.getName(), reservedBalanceCache);
        summaryOfCaches.put(usageChargeInstanceWalletCache.getName(), usageChargeInstanceWalletCache);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name
     * 
     * @param cacheName Name of cache to refresh
     */
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName.equals(balanceCache.getName()) || cacheName.equals(reservedBalanceCache.getName()) || cacheName.equals(usageChargeInstanceWalletCache.getName())) {
            populateWalletCache();
        }
    }
}