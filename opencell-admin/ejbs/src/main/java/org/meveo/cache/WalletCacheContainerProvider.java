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
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.event.qualifier.LowBalance;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.billing.impl.WalletService;
import org.slf4j.Logger;

/**
 * Provides cache related services (loading, update) for wallet related operations
 * 
 * @author Andrius Karpavicius
 * 
 */
//@Startup
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
    private Cache<CacheKeyLong, List<Long>> usageChargeInstanceWalletCache;

    private ParamBean paramBean = ParamBean.getInstance();

    static boolean usePrepaidBalanceCache = true;
    
    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    // @Resource(name = "java:jboss/infinispan/container/meveo")
    // private CacheContainer meveoContainer;

//    @PostConstruct
//    private void init() {
//        try {
//
//            usePrepaidBalanceCache = Boolean.parseBoolean(paramBean.getProperty("cache.cachePrepaidBalance", "true"));
//
//            log.debug("WalletCacheContainerProvider initializing for provider {}...", currentUser.getProviderCode());
//
//            // balanceCache = meveoContainer.getCache("meveo-balance");
//            // reservedBalanceCache = meveoContainer.getCache("meveo-reservedBalance");
//            // usageChargeInstanceWalletCache = meveoContainer.getCache("meveo-usageChargeInstanceWallet");
//
//            refreshCache(System.getProperty(CacheContainerProvider.SYSTEM_PROPERTY_CACHES_TO_LOAD));
//
//            log.info("WalletCacheContainerProvider initialized for provider {}", currentUser.getProviderCode());
//        } catch (Exception e) {
//            log.error("WalletCacheContainerProvider init() error", e);
//            throw e;
//        }
//    }

    /**
     * Populate wallet balance, reserved balanced and charge association caches from DB
     */
    private void populateWalletCache() {

        if (!usePrepaidBalanceCache) {
            log.info("Prepaid balance cache population will be skipped as cache will not be used");
            return;
        }
        
//        balanceCache.clear();
//        reservedBalanceCache.clear();
//        usageChargeInstanceWalletCache.clear();
        clear();

        boolean prepopulatePrepaidBalanceCache = Boolean.parseBoolean(paramBean.getProperty("cache.cachePrepaidBalance.prepopulate", "true"));

        if (!prepopulatePrepaidBalanceCache) {
            log.info("Prepaid balance cache pre-population will be skipped");
            return;
        }

        log.debug("Start to pre-populate Prepaid balance cache for Provider {}", currentUser.getProviderCode());

        // for each prepaid Usage chargeInstance of active subscription we create association
    	String lProvider = currentUser.getProviderCode();
        //jobInstanceService.registerJob(job);
        List<UsageChargeInstance> charges = usageChargeInstanceService.getPrepaidUsageChargeInstancesForCache();
        for (UsageChargeInstance charge : charges) {
            addUsageChargeInstance(charge);
        }
        
        // Populate cache with prepaid wallet balance and reserved balance
        List<Long> walletIds = walletService.getWalletsIdsForCache();
        for (Long walletId : walletIds) {
            if (!balanceCache.containsKey(new CacheKeyLong(lProvider, walletId))) {
                initializeBalanceCachesForWallet(walletId);
            }
        }
        
        log.info("Wallet cache populated for provider {} with {} usagecharges and {} wallets", lProvider, charges.size(), walletIds.size());
    }

    /**
     * Add association between usage charge instance and prepaid wallets.
     * 
     * @param usageChargeInstance usage charge instance.
     * @return A list of wallets ids
     */
    // @Lock(LockType.WRITE)
    public List<Long> addUsageChargeInstance(UsageChargeInstance usageChargeInstance) {

        if (!usePrepaidBalanceCache || !usageChargeInstance.getPrepaid()) {
            return null;
        }

        List<WalletInstance> wallets = usageChargeInstance.getWalletInstances();
        List<Long> walletIds = new ArrayList<>();

        for (WalletInstance wallet : wallets) {
            if (!walletIds.contains(wallet.getId()) && wallet.getWalletTemplate() != null && wallet.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
                walletIds.add(wallet.getId());
                if (!balanceCache.containsKey(new CacheKeyLong(currentUser.getProviderCode(),wallet.getId()))) {
                    initializeBalanceCachesForWallet(wallet.getId());
                }
            }
        }

        log.debug("Update usageChargeInstanceWallet cache with charge {} wallets:{}", usageChargeInstance.getId(), walletIds.size());

        // If no value are left in the map - LEAVE, as cache can be populated at runtime instead of at application start and need to distinguish
        // between not cached key and key with no records
        usageChargeInstanceWalletCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyLong(currentUser.getProviderCode(), usageChargeInstance.getId()), walletIds);

        return walletIds;
    }

    /**
     * Initialize or update cached balance and reserved balance for a given wallet instance.
     * 
     * @param walletId Wallet ID
     * @return Balance amount
     */
    // @Lock(LockType.WRITE)
    public BigDecimal initializeBalanceCachesForWallet(Long walletId) {
        BigDecimal balance = walletService.calculateWalletBalance(walletId);
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        balanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyLong(currentUser.getProviderCode(), walletId), balance);

        BigDecimal reservedBalance = walletService.calculateWalletReservedBalance(walletId);
        if (reservedBalance == null) {
            reservedBalance = BigDecimal.ZERO;
        }
        reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyLong(currentUser.getProviderCode(), walletId), reservedBalance);

        log.debug("Added to balance caches walletId:{} balance:{} reservedBalance:{}", walletId, balance, reservedBalance);
        return balance;
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

        // Cache deals with usage and prepaid charges only.
        if (!(op.getChargeInstance() instanceof UsageChargeInstance && op.getChargeInstance().getPrepaid())) {
            return;
        }

        // FIXME: handle reservation
        BigDecimal oldValue = null;
        BigDecimal newValue = null;

        Long walletId = op.getWallet().getId();

        // Either of caches is not initialized. By doing so, last operation will be included, no need to update it separatelly
        if (!reservedBalanceCache.containsKey(new CacheKeyLong(currentUser.getProviderCode(), walletId)) 
        		|| !balanceCache.containsKey(new CacheKeyLong(currentUser.getProviderCode(), walletId))) {
            initializeBalanceCachesForWallet(walletId);
            return;
        }

        if (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.RESERVED) || (op.getStatus() == WalletOperationStatusEnum.CANCELED)) {

            oldValue = reservedBalanceCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(new CacheKeyLong(currentUser.getProviderCode(), op.getWallet().getId()));

            if (op.getStatus() == WalletOperationStatusEnum.CANCELED) {
                newValue = oldValue.add(op.getAmountWithTax());
            } else {
                newValue = oldValue.subtract(op.getAmountWithTax());
            }

            log.debug("Update reservedBalance Cache for wallet {} {}->{}", op.getWallet().getId(), oldValue, newValue);

            reservedBalanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(
            		new CacheKeyLong(currentUser.getProviderCode(), op.getWallet().getId()), 
            		newValue
            		);
        }

        if (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.OPEN)) {

            oldValue = balanceCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(new CacheKeyLong(currentUser.getProviderCode(), op.getWallet().getId()));
            newValue = oldValue.subtract(op.getAmountWithTax());

            log.debug("Update balance Cache for wallet {} {}->{} lowBalanceLevel:{}", op.getWallet().getId(), oldValue, newValue, op.getWallet().getLowBalanceLevel());

            balanceCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(new CacheKeyLong(currentUser.getProviderCode(), op.getWallet().getId()), newValue);

            if (op.getWallet().getLowBalanceLevel() != null) {
                if (op.getWallet().getLowBalanceLevel().compareTo(newValue) >= 0 && op.getWallet().getLowBalanceLevel().compareTo(oldValue) < 0) {
                    lowBalanceEventProducer.fire(op.getWallet());
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
    public List<Long> getWalletIds(Long usageChargeInstanceId) {
        return usageChargeInstanceWalletCache.get(new CacheKeyLong(currentUser.getProviderCode(), usageChargeInstanceId));
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
    
    /**
     * Clear the current provider data from cache 
     */
    private void clear() {
    	String currentProvider = currentUser.getProviderCode();
		balanceCache.keySet().removeIf(key -> (key.getProvider() == null)? currentProvider == null: key.getProvider().equals(currentProvider));
		reservedBalanceCache.keySet().removeIf(key -> (key.getProvider() == null)? currentProvider == null: key.getProvider().equals(currentProvider));
		usageChargeInstanceWalletCache.keySet().removeIf(key -> (key.getProvider() == null)? currentProvider == null: key.getProvider().equals(currentProvider));
    }
    
    /**
     * Clear all the data from cache 
     */
    private void clearAll() {
    	balanceCache.clear();
        reservedBalanceCache.clear();
        usageChargeInstanceWalletCache.clear();
    }
}