package org.meveo.cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;

import org.infinispan.api.BasicCache;
import org.infinispan.manager.CacheContainer;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.slf4j.Logger;

/**
 * MeveoCacheContainerProvider
 * 
 * @author R.AITYAAZZA
 * 
 */
@Startup
@Singleton
public class WalletCacheContainerProvider {

    @Inject
    protected Logger log;

    /**
     * Contains association between prepaid wallet instance and balance value. WalletInstance.id is a cache key.
     */
    private BasicCache<Long, BigDecimal> balanceCache;

    /**
     * Contains association between prepaid wallet instance and reserved balance value. WalletInstance.id is a cache key.
     */
    private BasicCache<Long, BigDecimal> reservedBalanceCache;

    /**
     * Contains association between usage chargeInstance and wallets ids (if it is not the only principal one). UsageChargeInstance.id is a cache key.
     */
    private BasicCache<Long, List<Long>> usageChargeInstanceWallet;

    @PostConstruct
    private void init() {
        try {
            log.info("WalletCacheContainerProvider initializing...");
            CacheContainer meveoContainer = (CacheContainer) new InitialContext().lookup("java:jboss/infinispan/container/meveo");

            balanceCache = meveoContainer.getCache("meveo-balance");
            reservedBalanceCache = meveoContainer.getCache("meveo-reservedBalance");
            usageChargeInstanceWallet = meveoContainer.getCache("meveo-usageChargeInstanceWallet");

            populateWalletCache();

        } catch (Exception e) {
            log.error("WalletCacheContainerProvider init() error", e);
        }
    }

    private void populateWalletCache() {
        log.info("start to populate wallet cache");
        // for each recurring usage charInstance of active subscription we create association
        List<UsageChargeInstance> charges = (List<UsageChargeInstance>) getEntityManager().createNamedQuery("UsageChargeInstance.listPrepaidActive", UsageChargeInstance.class)
            .getResultList();
        for (UsageChargeInstance charge : charges) {
            updateCache(charge);
        }
        List<Long> walletIds = (List<Long>) getEntityManager().createNamedQuery("WalletInstance.listPrepaidActiveWalletIds", Long.class).getResultList();
        for (Long walletId : walletIds) {
            if (!balanceCache.containsKey(walletId)) {
                fillBalanceCaches(walletId);
            }
        }
        log.debug("wallet cache populated with {} usagecharges and {} wallets", charges.size(), walletIds.size());
    }

    private void updateCache(UsageChargeInstance charge) {
        // TODO:: make sure ordering is correct
        List<WalletInstance> wallets = charge.getWalletInstances();
        List<Long> walletIds = new ArrayList<>();

        for (WalletInstance wallet : wallets) {
            log.debug("wallet {}", wallet);
            if (!walletIds.contains(wallet.getId()) && wallet.getWalletTemplate() != null && wallet.getWalletTemplate().getWalletType() == BillingWalletTypeEnum.PREPAID) {
                walletIds.add(wallet.getId());
                log.debug("updateCache walletId:{}", wallet.getId());
                if (!balanceCache.containsKey(wallet.getId())) {
                    fillBalanceCaches(wallet.getId());
                }
            }
        }

        log.info("updateCache usageChargeInstanceWallet charge {} wallets:{}", charge.getId(), walletIds.size());
        if (walletIds.size() > 0) {
            usageChargeInstanceWallet.put(charge.getId(), walletIds);
        } else {
            usageChargeInstanceWallet.remove(charge.getId());
        }
    }

    private BigDecimal fillBalanceCaches(Long walletId) {
        BigDecimal balance = (BigDecimal) getEntityManager().createNamedQuery("WalletOperation.getBalance", BigDecimal.class).setParameter("walletId", walletId).getSingleResult();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        balanceCache.put(walletId, balance);
        BigDecimal reservedBalance = (BigDecimal) getEntityManager().createNamedQuery("WalletOperation.getReservedBalance", BigDecimal.class).setParameter("walletId", walletId)
            .getSingleResult();
        if (reservedBalance == null) {
            reservedBalance = BigDecimal.ZERO;
        }
        reservedBalanceCache.put(walletId, reservedBalance);
        log.info("fillBalanceCaches walletId:{} balance:{} reservedBalance:{}", walletId, balance, reservedBalance);
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
            log.info("update reservedBalance Cache for wallet {} {}->{}", op.getWallet().getId(), oldValue, newValue);
            reservedBalanceCache.put(op.getWallet().getId(), newValue);
        }

        if (balanceCache.containsKey(op.getWallet().getId()) && (!(op instanceof WalletReservation) || (op.getStatus() == WalletOperationStatusEnum.OPEN))) {
            oldValue = balanceCache.get(op.getWallet().getId());
            newValue = oldValue.subtract(op.getAmountWithTax());
            log.info("update balance Cache for wallet {} {}->{}", op.getWallet().getId(), oldValue, newValue);
            balanceCache.put(op.getWallet().getId(), newValue);
            // FIXME: handle low balance notifications

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
    public BigDecimal getCacheBalance(Long walletId) {
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
    public BigDecimal getReservedCacheBalance(Long walletId) {
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
    public BigDecimal getReservedCacheBalance(List<Long> walletIds) {
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
        return usageChargeInstanceWallet.containsKey(usageChargeInstanceId);
    }

    /**
     * Get a list of wallets associated to a given usage charge instance
     * 
     * @param usageChargeInstanceId Usage charge instance id
     * @return A list of wallets
     */
    public List<Long> getWallets(Long usageChargeInstanceId) {
        return usageChargeInstanceWallet.get(usageChargeInstanceId);
    }

    private EntityManager getEntityManager() {
        return null;
    }
}