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

import static java.util.Collections.emptyList;
import static org.meveo.commons.utils.NumberUtils.round;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.job.ReRatingJob;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ReratingTargetEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.transformer.AliasToAggregatedWalletOperationResultTransformer;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.filter.FilterService;

/**
 * Service class for WalletOperation entity
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Phung tien lan
 * @author anasseh
 * @author Abdellatif BARI
 * @author Mbarek-Ay
 * @lastModifiedVersion 7.0
 */
@Stateless
public class WalletOperationService extends PersistenceService<WalletOperation> {

    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;

    @Inject
    private WalletService walletService;

    @Inject
    private SellerService sellerService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private TaxService taxService;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private FilterService filterService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;
    
    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;
    
    /**
     *
     * @param ids
     * @return list of walletOperations by ids
     */
    public List<WalletOperation> listByIds(List<Long> ids) {
        if (ids.isEmpty())
            return emptyList();
        return getEntityManager().createNamedQuery("WalletOperation.listByIds", WalletOperation.class).setParameter("idList", ids).getResultList();
    }

    /**
     * Get a list of wallet operations to rate up to a given date. WalletOperation.invoiceDate< date
     *
     * @param entityToInvoice Entity to invoice
     * @param invoicingDate Invoicing date
     * @return A list of wallet operations
     */
    public List<WalletOperation> listToRate(IBillableEntity entityToInvoice, Date invoicingDate) {

        if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByBA", WalletOperation.class).setParameter("invoicingDate", invoicingDate).setParameter("billingAccount", entityToInvoice)
                .getResultList();

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateBySubscription", WalletOperation.class).setParameter("invoicingDate", invoicingDate).setParameter("subscription", entityToInvoice)
                .getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByOrderNumber", WalletOperation.class).setParameter("invoicingDate", invoicingDate)
                .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber()).getResultList();
        }

        return new ArrayList<>();
    }

    public WalletOperation findByUserAccountAndCode(String code, UserAccount userAccount) {

        try {
            return getEntityManager().createNamedQuery("WalletOperation.findByUAAndCode", WalletOperation.class).setParameter("userAccount", userAccount).setParameter("code", code).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Charge wallet operation on prepaid wallets
     * 
     * @param chargeInstance Charge instance
     * @param op Wallet operation
     * @return A list of wallet operations containing a single original wallet operation or multiple wallet operations if had to be split among various wallets
     * @throws InsufficientBalanceException Balance is insufficient in the wallet
     */
    private List<WalletOperation> chargeOnPrepaidWallets(ChargeInstance chargeInstance, WalletOperation op) throws InsufficientBalanceException {

        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        List<WalletOperation> result = new ArrayList<>();
        Map<Long, BigDecimal> walletLimits = walletService.getWalletIds(chargeInstance);

        // Handles negative amounts (recharge) - apply recharge to the first wallet
        if (op.getAmountWithTax().compareTo(BigDecimal.ZERO) <= 0) {

            Long walletId = walletLimits.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);
            return result;
        }

        log.debug("chargeWalletOperation chargeInstanceId found with {} wallet ids", walletLimits.size());

        Map<Long, BigDecimal> balances = walletService.getWalletReservedBalances(walletLimits.keySet());

        Map<Long, BigDecimal> woAmounts = new HashMap<>();

        BigDecimal remainingAmountToCharge = op.getAmountWithTax();

        // First iterate over balances that have credit
        for (Long walletId : balances.keySet()) {

            BigDecimal balance = balances.get(walletId);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal negatedBalance = balance.negate();
                // Case when amount to deduct (5) is less than or equal to a negated balance amount -(-10)
                if (remainingAmountToCharge.compareTo(negatedBalance) <= 0) {
                    woAmounts.put(walletId, remainingAmountToCharge);
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Case when amount to deduct (10) is more tan a negated balance amount -(-5)
                } else {
                    woAmounts.put(walletId, negatedBalance);
                    balances.put(walletId, BigDecimal.ZERO);
                    remainingAmountToCharge = remainingAmountToCharge.add(balance);
                }
            }
        }

        // If not all the amount was deducted, then iterate again checking if any of the balances can be reduced pass the Zero up to a rejection limit as defined in a wallet.
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {

            for (Long walletId : balances.keySet()) {

                BigDecimal balance = balances.get(walletId);
                BigDecimal rejectLimit = walletLimits.get(walletId);

                // There is no limit upon which further consumption should be rejected
                if (rejectLimit == null) {
                    if (woAmounts.containsKey(walletId)) {
                        woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                    } else {
                        woAmounts.put(walletId, remainingAmountToCharge);
                    }
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Limit is not exceeded yet
                } else if (rejectLimit.compareTo(balance) > 0) {

                    BigDecimal remainingLimit = rejectLimit.subtract(balance);

                    // Case when amount to deduct (5) is less than or equal to a remaining limit (10)
                    if (remainingAmountToCharge.compareTo(remainingLimit) <= 0) {
                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                        } else {
                            woAmounts.put(walletId, remainingAmountToCharge);
                        }

                        balances.put(walletId, balance.add(remainingAmountToCharge));
                        remainingAmountToCharge = BigDecimal.ZERO;
                        break;

                        // Case when amount to deduct (10) is more tan a remaining limit (5)
                    } else {

                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingLimit));
                        } else {
                            woAmounts.put(walletId, remainingLimit);
                        }

                        balances.put(walletId, rejectLimit);
                        remainingAmountToCharge = remainingAmountToCharge.subtract(remainingLimit);
                    }
                }
            }
        }

        // Not possible to deduct all WO amount, so throw an Insufficient balance error
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {
            throw new InsufficientBalanceException("Insuficient balance when charging " + op.getAmountWithTax() + " for wallet operation " + op.getId());
        }

        // All charge was over one wallet
        if (woAmounts.size() == 1) {
            Long walletId = woAmounts.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);

            // Charge was over multiple wallets
        } else {

            for (Entry<Long, BigDecimal> amountInfo : woAmounts.entrySet()) {
                Long walletId = amountInfo.getKey();
                BigDecimal walletAmount = amountInfo.getValue();

                BigDecimal newOverOldCoeff = walletAmount.divide(op.getAmountWithTax(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                BigDecimal newOpAmountWithTax = walletAmount;
                BigDecimal newOpAmountWithoutTax = op.getAmountWithoutTax().multiply(newOverOldCoeff);

                newOpAmountWithoutTax = round(newOpAmountWithoutTax, rounding, roundingMode);
                newOpAmountWithTax = round(newOpAmountWithTax, rounding, roundingMode);
                BigDecimal newOpAmountTax = newOpAmountWithTax.subtract(newOpAmountWithoutTax);
                BigDecimal newOpQuantity = op.getQuantity().multiply(newOverOldCoeff);

                WalletOperation newOp = op.getUnratedClone();
                newOp.setWallet(getEntityManager().find(WalletInstance.class, walletId));
                newOp.setAmountWithTax(newOpAmountWithTax);
                newOp.setAmountTax(newOpAmountTax);
                newOp.setAmountWithoutTax(newOpAmountWithoutTax);
                newOp.setQuantity(newOpQuantity);
                log.debug("prepaid walletoperation partially fit in walletInstance {}, we charge {} of {} ", newOp.getWallet(), newOpAmountTax, op.getAmountWithTax());
                create(newOp);
                result.add(newOp);
                walletCacheContainerProvider.updateBalance(newOp);
            }
        }
        return result;
    }

    /**
     * Persist a wallet operation. For prepaid wallets, additional wallet operations might be created to span amount over multiple wallets.
     * 
     * @param op Wallet operation
     * @return A list of wallet operations. For postpaid wallets, it will contain the same wallet operation as passed in. For postpaid wallets, additional wallet operations might be returned.
     * @throws InsufficientBalanceException Insufficient balance to charge for prepaid wallets
     */
    public List<WalletOperation> chargeWalletOperation(WalletOperation op) throws InsufficientBalanceException {

        List<WalletOperation> result = new ArrayList<>();
        ChargeInstance chargeInstance = op.getChargeInstance();
        Long chargeInstanceId = chargeInstance.getId();
        // case of scheduled operation (for revenue recognition)
        UserAccount userAccount = chargeInstance.getUserAccount();

        if (chargeInstanceId == null) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            
            // Balance and reserved balance deals with prepaid wallets.
            // With wallet cache at all
        } else if (!chargeInstance.getPrepaid()) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            create(op);
            
            // Prepaid charges only
        } else {
            result = chargeOnPrepaidWallets(chargeInstance, op);
        }
        return result;
    }

    /**
     * Rerate existing wallet operation. Executed in new transaction. <br/>
     * <br/>
     *
     * <b>When rerateInvoiced = false:</b><br/>
     *
     * Update Wallet operations to status TO_RERATE and cancel related RTs. Only unbilled wallet operations will be considered. In case of Wallet operation aggregation to a single Rated transaction, all related wallet
     * operations through the same Rated transaction, will be marked for re-rating as well. Note, that a number of Wallet operation ids passed and a number of Wallet operations marked for re-rating might not match if
     * aggregation was used, or Wallet operation status were invalid.
     * <p/>
     * <b>When rerateInvoiced = true:</b> <br/>
     *
     * Billed wallet operations will be refunded and new wallet operations with status TO_RERATE will be created. For the unbilled wallet operations the logic of includeinvoiced=false applies.
     *
     * @param walletOperationIds A list of Wallet operation ids to mark for re-rating
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     * @return Number of wallet operations marked for re-rating.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int markToRerateInNewTx(List<Long> walletOperationIds, boolean rerateInvoiced) {
        return markToRerate(walletOperationIds, rerateInvoiced);
    }

    /**
     * Rerate existing wallet operation. <br/>
     * <br/>
     *
     * <b>When rerateInvoiced = false:</b><br/>
     *
     * Only unbilled wallet operations will be considered. <br/>
     * Update Wallet operations to status TO_RERATE and cancel related RTs. In case of Wallet operation aggregation to a single Rated transaction, all related wallet operations through the same Rated transaction, will be
     * marked for re-rating as well. <br/>
     * Note, that a number of Wallet operation ids passed and a number of Wallet operations marked for re-rating might not match if aggregation was used, or Wallet operation status were invalid.
     * <p/>
     * <b>When includeInvoiced = true:</b> <br/>
     *
     * Billed wallet operations will be refunded and wallet operation will be changed to status TO_RERATE. <br/>
     * For the un-billed wallet operations the logic of rerateInvoiced=false applies.
     *
     * @param walletOperationIds A list of Wallet operation ids to mark for re-rating
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded
     * @return Number of wallet operations marked for re-rating.
     */
    public int markToRerate(List<Long> walletOperationIds, boolean rerateInvoiced) {

        if (walletOperationIds.isEmpty()) {
            return 0;
        }

        int nrOfWosToRerate = 0;

        List<Long> unbilledWalletOperationIds = new ArrayList<Long>(walletOperationIds);

        // Ignore Rated transactions that were billed already
        List<Long> walletOperationsBilled = getEntityManager().createNamedQuery("WalletOperation.getWalletOperationsBilled", Long.class).setParameter("walletIdList", walletOperationIds).getResultList();
        unbilledWalletOperationIds.removeAll(walletOperationsBilled);

        // Handle invoiced wallet operations if requested: Refund invoiced operations (create an identical and negated WO with status OPEN) and create identical one with status TO_RERATE
        if (rerateInvoiced && !walletOperationsBilled.isEmpty()) {

            List<WalletOperation> invoicedWos = findByIds(walletOperationsBilled);

            for (WalletOperation invoicedWo : invoicedWos) {

                // A refund WO
                WalletOperation refundWo = refundWalletOperation(invoicedWo);
                walletOperationIds.add(invoicedWo.getId());
                invoicedWo.setStatus(WalletOperationStatusEnum.TO_RERATE);
                update(invoicedWo);
                nrOfWosToRerate++;
            }
        }

        // Cancelled related RTS and change WO status to re-rate. Note: in case of aggregation, it will re-rate all WOs that are linked through the related RTs
        if (!unbilledWalletOperationIds.isEmpty()) {
            getEntityManager().createNamedQuery("RatedTransaction.cancelByWOIds").setParameter("woIds", unbilledWalletOperationIds).setParameter("now", new Date()).executeUpdate();

            int nrStatusUpdated = getEntityManager().createNamedQuery("WalletOperation.setStatusToToRerate").setParameter("now", new Date()).setParameter("woIds", unbilledWalletOperationIds).executeUpdate();
            nrOfWosToRerate = nrOfWosToRerate + nrStatusUpdated;
        }

        log.info("{} out of {} requested Wallet operations are marked for rerating", nrOfWosToRerate, walletOperationIds.size());

        return nrOfWosToRerate;
    }

    public List<Long> listToRerate(String reratingTarget, List<Long> targetBatches, int nbToRetrieve) {
        // null | ALL
        TypedQuery<Long> query = getEntityManager().createNamedQuery("WalletOperation.listToRerate", Long.class);
        if (ReratingTargetEnum.NO_BATCH.name().equals(reratingTarget)) {
            query = getEntityManager().createNamedQuery("WalletOperation.listToRerateNoBatch", Long.class);
        } else if (ReratingTargetEnum.WITH_BATCH.name().equals(reratingTarget)) {
            if (CollectionUtils.isNotEmpty(targetBatches)) {
                targetBatches = getEntityManager().createNamedQuery("BatchEntity.listBatchEntities", Long.class)
                        .setParameter("ids", targetBatches).setParameter("targetJob", ReRatingJob.class.getSimpleName())
                        .getResultList();
                if (CollectionUtils.isNotEmpty(targetBatches)) {
                    query = getEntityManager().createNamedQuery("WalletOperation.listToRerateWithBatches", Long.class)
                            .setParameter("targetBatches", targetBatches);
                }
            }
            query = getEntityManager().createNamedQuery("WalletOperation.listToRerateAllBatches", Long.class);
        }
        if (nbToRetrieve > 0) {
            query = query.setMaxResults(nbToRetrieve);
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder("Select op from WalletOperation op", "op");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("op.chargeInstance.chargeTemplate.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("op.wallet", walletInstance);
        qb.addSql("op.status = 'OPEN'");
        if (from != null) {
            qb.addCriterion("operationDate", ">=", from, false);
        }
        if (to != null) {
            qb.addCriterion("operationDate", "<=", to, false);
        }

        try {
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> openWalletOperationsByCharge(WalletInstance walletInstance) {

        try {
            return getEntityManager().createNamedQuery("WalletInstance.openWalletOperationsByCharge").setParameter("walletInsanceId", walletInstance.getId()).getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    public Long countNonTreatedWOByBA(BillingAccount billingAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByBA").setParameter("billingAccount", billingAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by BA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByUA(UserAccount userAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByUA").setParameter("userAccount", userAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by UA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by CA", e);
            return null;
        }
    }

    public Long countNotBilledWOBySubscription(Subscription subscription) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotBilledWOBySubscription").setParameter("subscription", subscription).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotBilledWOBySubscription", e);
            return 0L;
        }
    }

    public int moveNotBilledWOToUA(WalletInstance newWallet, Subscription subscription) {
        return getEntityManager().createNamedQuery("WalletOperation.moveNotBilledWOToUA")
        							.setParameter("newWallet", newWallet)
        							.setParameter("newUserAccount", newWallet.getUserAccount())
                                    .setParameter("billingAccount", newWallet.getUserAccount().getBillingAccount())
        							.setParameter("subscription", subscription).executeUpdate();
    }

    public int moveAndRerateNotBilledWOToUA(WalletInstance wallet, Subscription subscription) {
        return getEntityManager().createNamedQuery("WalletOperation.moveAndRerateNotBilledWOToUA")
        							.setParameter("newWallet", wallet)
        							.setParameter("newUserAccount", wallet.getUserAccount())
        							.setParameter("billingAccount", wallet.getUserAccount().getBillingAccount())
        							.setParameter("subscription", subscription).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrWalletsOperationByStatus() {
        try {
            return getEntityManager().createNamedQuery("WalletOperation.countNbrWalletsOperationByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrWalletsOperationByStatus", e);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrEdrByStatus() {
        try {
            return getEntityManager().createNamedQuery("EDR.countNbrEdrByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrEdrByStatus", e);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<AggregatedWalletOperation> listToInvoiceIdsWithGrouping(Date invoicingDate, WalletOperationAggregationSettings aggregationSettings) {

        updateWalletOperationPeriodView(aggregationSettings);

        WalletOperationAggregatorQueryBuilder woa = new WalletOperationAggregatorQueryBuilder(aggregationSettings, customFieldTemplateService, filterService);

        String strQuery = woa.getGroupQuery();
        log.debug("aggregated query={}", strQuery);

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("invoicingDate", invoicingDate);
        // get the aggregated data
        @SuppressWarnings("rawtypes")
        List result = query.unwrap(org.hibernate.query.Query.class).setResultTransformer(new AliasToAggregatedWalletOperationResultTransformer(AggregatedWalletOperation.class)).getResultList();

        return result;
    }

    private void updateWalletOperationPeriodView(WalletOperationAggregationSettings aggregationSettings) {
       
        String truncateDateFunction = EntityManagerProvider.isDBOracle()?"TRUNC":"DATE";
        
        String queryTemplate = "DROP VIEW IF EXISTS billing_wallet_operation_period; CREATE OR REPLACE VIEW billing_wallet_operation_period AS select o.*, SUM(o.flag) over (partition by o.seller_id order by o.charge_instance_id {{ADDITIONAL_ORDER_BY}}) as period "
                + " from (select o.*, (case when (" + truncateDateFunction + "(lag(o.end_Date) over (partition by o.seller_id order by o.charge_instance_id {{ADDITIONAL_ORDER_BY}})) {{PERIOD_END_DATE_INCLUDED}}= " + truncateDateFunction + "(o.start_date)) then 0 else 1 end) as flag "
                + " FROM billing_wallet_operation o WHERE o.status='OPEN' ) o ";
        
        Map<String, String> parameters = new HashMap<>();
        if (aggregationSettings.isPeriodEndDateIncluded()) {
            parameters.put("{{PERIOD_END_DATE_INCLUDED}}", "+ interval '1' day");
        } else {
            parameters.put("{{PERIOD_END_DATE_INCLUDED}}", "");
        }
        if (!StringUtils.isBlank(aggregationSettings.getAdditionalOrderBy())) {
            String orderByClause = ", "+aggregationSettings.getAdditionalOrderBy();
            parameters.put("{{ADDITIONAL_ORDER_BY}}", orderByClause);
        } else {
            parameters.put("{{ADDITIONAL_ORDER_BY}}", "");
        }

        for (String key : parameters.keySet()) {
            queryTemplate = queryTemplate.replace(key, parameters.get(key));
        }
        Query q = getEntityManager().createNativeQuery(queryTemplate);
        q.executeUpdate();

    }

    public List<WalletOperation> listByRatedTransactionId(Long ratedTransactionId) {
        return getEntityManager().createNamedQuery("WalletOperation.listByRatedTransactionId", WalletOperation.class).setParameter("ratedTransactionId", ratedTransactionId).getResultList();
    }

    /**
     * Return a list of open Wallet operation between two date.
     *
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @param lastId a last id for pagination
     * @param max a max rows
     * @return a list of Wallet Operation
     */
    public List<WalletOperation> getNotOpenedWalletOperationBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, Long lastId, int max) {
        return getEntityManager().createNamedQuery("WalletOperation.listNotOpenedWObetweenTwoDates", WalletOperation.class).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).setParameter("lastId", lastId).setMaxResults(max).getResultList();
    }

    /**
     * Remove all not open Wallet operation between two date
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @return the number of deleted entities
     */
    public long purge(Date firstTransactionDate, Date lastTransactionDate) {

        return getEntityManager().createNamedQuery("WalletOperation.deleteNotOpenWObetweenTwoDates").setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
            .executeUpdate();
    }

    /**
     * Import wallet operations.
     * 
     * @param walletOperations Wallet Operations DTO list
     * @throws BusinessException
     */
    public void importWalletOperation(List<WalletOperationDto> walletOperations) throws BusinessException {

        for (WalletOperationDto dto : walletOperations) {
            Tax tax = null;
            ChargeInstance chargeInstance = null;

            if (dto.getTaxCode() != null) {
                tax = taxService.findByCode(dto.getTaxCode());
            } else if (dto.getTaxPercent() != null) {
                tax = taxService.findTaxByPercent(dto.getTaxPercent());
            }
            if (tax == null) {
                log.warn("No tax matched for wallet operation by code {} nor tax percent {}", dto.getTaxCode(), dto.getTaxPercent());
                continue;
            }

            if (dto.getChargeInstance() != null) {
                chargeInstance = chargeInstanceService.findByCode(dto.getChargeInstance());
            }

            WalletOperation wo = null;
            if (chargeInstance != null) {
                BigDecimal ratingQuantity = chargeTemplateService.evaluateRatingQuantity(chargeInstance.getChargeTemplate(), dto.getQuantity());

                Date invoicingDate = null;
                if (chargeInstance.getInvoicingCalendar() != null) {

                    Date defaultInitDate = null;
                    if (chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.RECURRING && ((RecurringChargeInstance) chargeInstance).getSubscriptionDate() != null) {
                        defaultInitDate = ((RecurringChargeInstance) chargeInstance).getSubscriptionDate();
                    } else if (chargeInstance.getServiceInstance() != null) {
                        defaultInitDate = chargeInstance.getServiceInstance().getSubscriptionDate();
                    } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                        defaultInitDate = chargeInstance.getSubscription().getSubscriptionDate();
                    }

                    Calendar invoicingCalendar = CalendarService.initializeCalendar(chargeInstance.getInvoicingCalendar(), defaultInitDate, chargeInstance);
                    invoicingDate = invoicingCalendar.nextCalendarDate(dto.getOperationDate());
                }

                wo = new WalletOperation(chargeInstance, dto.getQuantity(), ratingQuantity, dto.getOperationDate(), dto.getOrderNumber(), dto.getParameter1(), dto.getParameter2(), dto.getParameter3(),
                    dto.getParameterExtra(), tax, dto.getStartDate(), dto.getEndDate(), null, invoicingDate);

            } else {
                Seller seller = null;
                WalletInstance wallet = null;
                Currency currency = null;
                OfferTemplate offer = null;

                if (dto.getOfferCode() != null) {
                    offer = offerTemplateService.findByCode(dto.getOfferCode());
                }

                if (dto.getSeller() != null) {
                    seller = sellerService.findByCode(dto.getSeller());
                }
                if (dto.getWalletId() != null) {
                    wallet = walletService.findById(dto.getWalletId());
                }
                if (dto.getCurrency() != null) {
                    currency = currencyService.findByCode(dto.getCurrency());
                }
                wo = new WalletOperation(dto.getCode(), "description", wallet, dto.getOperationDate(), null, dto.getType(), currency, tax, dto.getUnitAmountWithoutTax(), dto.getUnitAmountWithTax(),
                    dto.getUnitAmountTax(), dto.getQuantity(), dto.getAmountWithoutTax(), dto.getAmountWithTax(), dto.getAmountTax(), dto.getParameter1(), dto.getParameter2(), dto.getParameter3(),
                    dto.getParameterExtra(), dto.getStartDate(), dto.getEndDate(), dto.getSubscriptionDate(), offer, seller, null, dto.getRatingUnitDescription(), null, null, null, null, dto.getStatus(),
                    wallet != null ? wallet.getUserAccount() : null, wallet != null ? wallet.getUserAccount().getBillingAccount() : null);
            }
            Integer sortIndex = RatingService.getSortIndex(wo);
            wo.setSortIndex(sortIndex);        
        	wo.setBusinessKey(dto.getBusinessKey());

        	create(wo);
        }
    }

    /**
     * Mark wallet operations, that were invoiced by a given billing run, to be rerated
     * 
     * @param billingRun Billing run that invoiced wallet operations
     */
    public void markToRerateByBR(BillingRun billingRun) {

        List<WalletOperation> walletOperations = getEntityManager().createNamedQuery("WalletOperation.listByBRId", WalletOperation.class).setParameter("brId", billingRun.getId()).getResultList();

        for (WalletOperation walletOperation : walletOperations) {
            walletOperation.changeStatus(WalletOperationStatusEnum.TO_RERATE);
        }
    }

    /**
     * @param firstDate
     * @param lastDate
     * @param lastId
     * @param maxResult
     * @param formattedStatus
     * @return
     */
    public List<WalletOperation> getWalletOperationBetweenTwoDatesByStatus(Date firstDate, Date lastDate, Long lastId, int maxResult, List<WalletOperationStatusEnum> formattedStatus) {
        return getEntityManager().createNamedQuery("WalletOperation.listWObetweenTwoDatesByStatus", WalletOperation.class).setParameter("firstTransactionDate", firstDate).setParameter("lastTransactionDate", lastDate)
            .setParameter("lastId", lastId).setParameter("status", formattedStatus).setMaxResults(maxResult).getResultList();
    }

    public long purge(Date lastTransactionDate, List<WalletOperationStatusEnum> targetStatusList) {
        return getEntityManager().createNamedQuery("WalletOperation.deleteWOByLastTransactionDateAndStatus").setParameter("status", targetStatusList).setParameter("lastTransactionDate", lastTransactionDate)
            .executeUpdate();
    }

    public long purge(Date firstTransactionDate, Date lastTransactionDate, List<WalletOperationStatusEnum> targetStatusList) {
        return getEntityManager().createNamedQuery("WalletOperation.deleteWObetweenTwoDatesByStatus").setParameter("status", targetStatusList).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
    }

    /**
     * Remove wallet operation rated 0 and chargeTemplate.dropZeroWo=true.
     */
    public void removeZeroWalletOperation() {
        getEntityManager().createNamedQuery("WalletOperation.deleteZeroWO").executeUpdate();
    }

    /**
     * Mark Wallet operation as failed to re-rate
     *
     * @param id Wallet operation identifier
     * @param e Exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void markAsFailedToRerateInNewTx(Long id, Exception e) {

        String message = e instanceof NullPointerException ? "NPE" : e.getMessage();
        getEntityManager().createNamedQuery("WalletOperation.setStatusFailedToRerate").setParameter("now", new Date()).setParameter("rejectReason", message).setParameter("id", id).executeUpdate();
    }

    /**
     * Update Wallet operations to status Canceled and cancel related RTs. Only unbilled wallet operations will be considered. In case of Wallet operation aggregation to a single Rated transaction, all OTHER related
     * wallet operations to the same Rated transaction, will be marked as Open (only the related ones).
     *
     * Note, that a number of Wallet operation ids passed and a number of Wallet operations marked as Canceled and Opened might not match if aggregation was used, or Wallet operation status were invalid.
     *
     * @param walletOperationIds A list of Wallet operation ids to mark as Canceled
     * @return Number of wallet operations marked for Canceled
     */
    public int cancelWalletOperations(List<Long> walletOperationIds) {

        if (walletOperationIds.isEmpty()) {
            return 0;
        }

        int nrOfWosUpdated = 0;

        // Ignore Rated transactions that were billed already
        List<Long> walletOperationsBilled = getEntityManager().createNamedQuery("WalletOperation.getWalletOperationsBilled", Long.class).setParameter("walletIdList", walletOperationIds).getResultList();
        walletOperationIds.removeAll(walletOperationsBilled);

        // Cancel related RTS and change WO status to Canceled. Note: in case of aggregation, WOs that were aggregated under same RT will be marked as Open
        if (!walletOperationIds.isEmpty()) {
            // Cancel related RTS
            int nrRtsCanceled = getEntityManager().createNamedQuery("RatedTransaction.cancelByWOIds").setParameter("woIds", walletOperationIds).setParameter("now", new Date()).executeUpdate();

            // Change WO status to Canceled
            nrOfWosUpdated = getEntityManager().createNamedQuery("WalletOperation.setStatusToCanceledById").setParameter("now", new Date()).setParameter("woIds", walletOperationIds).executeUpdate();

            // In case of aggregation, WOs that were aggregated under same RT will be marked as Open
            if (nrRtsCanceled > 0) {
                nrOfWosUpdated = nrOfWosUpdated
                        + getEntityManager().createNamedQuery("WalletOperation.setStatusToOpenForWosThatAreRelatedByRTsById").setParameter("now", new Date()).setParameter("woIds", walletOperationIds).executeUpdate();
            }

            log.info("{} out of {} requested Wallet operations are canceled/marked for rerating", nrOfWosUpdated, walletOperationIds.size());
        }
        return nrOfWosUpdated;
    }

    /**
     * Refund already billed wallet operations by creating an identical wallet operation with a negated amount and status OPEN
     *
     * @param ids A list of wallet operation identifiers to refund
     * @return A list of newly created wallet operations with a negated amount and status OPEN
     */
    public List<WalletOperation> refundWalletOperations(List<Long> ids) {

        List<WalletOperation> refundedWOs = new ArrayList<>();
        List<WalletOperation> invoicedWos = findByIds(ids);

        for (WalletOperation invoicedWo : invoicedWos) {

            // A refund WO
            WalletOperation wo = refundWalletOperation(invoicedWo);

            refundedWOs.add(wo);
        }
        return refundedWOs;
    }

    /**
     * Refund already billed wallet operation by creating an identical wallet operation with a negated amount and status OPEN
     *
     * @param woToRefund A wallet operation to refund
     * @return A newly created wallet operation with a negated amount and status OPEN
     */
    private WalletOperation refundWalletOperation(WalletOperation woToRefund) {

        Date today = new Date();

        // A refund WO
        WalletOperation refundWo = woToRefund.getClone();
        refundWo.setAmountTax(refundWo.getAmountTax().negate());
        refundWo.setAmountWithoutTax(refundWo.getAmountWithoutTax().negate());
        refundWo.setAmountWithTax(refundWo.getAmountWithTax().negate());
        refundWo.setStatus(WalletOperationStatusEnum.OPEN);
        refundWo.setCreated(today);
        refundWo.setUpdated(null);
        refundWo.setRefundsWalletOperation(woToRefund);
        create(refundWo);

        return refundWo;
    }
    
    public boolean isChargeMatch(ChargeInstance chargeInstance, String filterExpression) throws BusinessException {
    	if(chargeInstance.getServiceInstance()!=null) {
  		  boolean anyFalseAttribute = chargeInstance.getServiceInstance().getAttributeInstances().stream().filter(attributeInstance -> attributeInstance.getAttribute().getAttributeType() == AttributeTypeEnum.BOOLEAN)
      	 .filter(attributeInstance -> attributeInstance.getAttribute().getChargeTemplates().contains(chargeInstance.getChargeTemplate()))
              .anyMatch(attributeInstance ->  attributeInstance.getStringValue()==null  || "false".equals(attributeInstance.getStringValue()));
  	        if(anyFalseAttribute) return false;
  	}
     

      if (StringUtils.isBlank(filterExpression)) {
          return true;
      }

      return ValueExpressionWrapper.evaluateToBooleanOneVariable(filterExpression, "ci", chargeInstance);
    }
    
    
    public boolean ignoreChargeTemplate(ChargeInstance chargeInstance){
        ServiceInstance serviceInstance = chargeInstance.getServiceInstance();
        if(serviceInstance != null && serviceInstance.getProductVersion() != null){
            boolean dontApplyCharge = serviceInstance.getAttributeInstances()
                    .stream()
                    .filter(attributeInstance -> attributeInstance.getAttribute().getAttributeType() == AttributeTypeEnum.BOOLEAN)
                    .filter(attributeInstance -> attributeInstance.getAttribute().getChargeTemplates().contains(chargeInstance.getChargeTemplate()))
                    .anyMatch(attributeInstance -> !Boolean.valueOf(attributeInstance.getStringValue()));
            if(dontApplyCharge){
                log.debug(String.format("charge %s will be ignored, cause it was ignored by an attribute", chargeInstance.getChargeTemplate().getCode()));
                return true;
            }
        }
        return false;
    }

    /**
     * Detach WOs From subscription.
     *
     * @param subscription subscription
     */
    public void detachWOsFromSubscription(Subscription subscription) {
        getEntityManager().createNamedQuery("WalletOperation.detachWOsFromSubscription").setParameter("subscription", subscription).executeUpdate();
    }


    /**
     * Get a list of wallet operations to be invoiced/converted to rated transactions up to a given date. WalletOperation.invoiceDate< date
     *
     * @param woIds WO ids
     * @return A list of WalletOperation
     */
    public List<WalletOperation> getDiscountWalletOperation(List<Long> woIds) {
        if (CollectionUtils.isEmpty(woIds)) {
            return Collections.emptyList();
        }
        List<WalletOperation> wosToProcess = new ArrayList<>(woIds.size());

        // Due to jpa (or maybe db limitation), we cannot pass huge ids in one query, partition it if it is more than 5000 entries
        List<List<Long>> subWoIds = Lists.partition(woIds, 5000);

        log.info("GetDiscountWalletOperation : process {} wo in {} partitions with 5000 each", woIds.size(), subWoIds.size());

        subWoIds.forEach(partition -> {
                    log.info("\t Process partition with {}", partition.size());
                    wosToProcess.addAll(getEntityManager().createNamedQuery("WalletOperation.discountWalletOperation", WalletOperation.class)
                            .setParameter("woIds", partition)
                            .getResultList());
                }
        );

        return wosToProcess;
    }
    
    /**
     * Determine recurring period end date
     *
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period end date
     */
    private Calendar resolveCalendar(RecurringChargeInstance chargeInstance) {
        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        Calendar cal = chargeInstance.getCalendar();
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), null, recurringChargeTemplate, chargeInstance);
        }
        return cal;
    }
    
    

    public Date getRecurringPeriodEndDate(RecurringChargeInstance chargeInstance, Date date) {

        Calendar cal = resolveCalendar(chargeInstance);
        if (cal == null) {
            throw new BusinessException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }

        cal = CalendarService.initializeCalendar(cal, chargeInstance.getSubscriptionDate(), chargeInstance);

        Date nextChargeDate = cal.nextCalendarDate(cal.truncateDateTime(date));
        return nextChargeDate;
    }
    
    public WalletOperation findWoByRatedTransactionId(Long rtId) {
        try {
            return (WalletOperation) getEntityManager().createQuery("SELECT wo FROM WalletOperation wo WHERE wo.ratedTransaction.id = :rtId")
                    .setParameter("rtId", rtId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    public WalletOperation findByEdr(Long edrId) {
        try {
            return (WalletOperation) getEntityManager().createQuery("SELECT wo FROM WalletOperation wo WHERE wo.edr.id = :edrId")
                    .setParameter("edrId", edrId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }
    
    @Override
    public void create(WalletOperation walletOperation) throws BusinessException {
    	if(walletOperation.getDiscountedWO()!=null) {
    		walletOperation.setDiscountedWalletOperation(walletOperation.getDiscountedWO().getId());
    	}
    	super.create(walletOperation);
    }
    
    @SuppressWarnings("unchecked")
    public List<WalletOperation> findByDiscountedWo(Long discountedWalletOperation) {
    	List<WalletOperation> result = new ArrayList<>();
        try {
        	result=getEntityManager().createQuery("SELECT wo FROM WalletOperation wo WHERE wo.discountedWalletOperation = :discountedWalletOperation")
                    .setParameter("discountedWalletOperation", discountedWalletOperation)
                    .getResultList();
        } catch (NoResultException exception) {
        	return Collections.emptyList();
        }
        return result;
    }

    public void cancelDiscountedWalletOperation(List<Long> ids) {
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(ids)) {
            getEntityManager().createNamedQuery("WalletOperation.cancelDisountedWallet").setParameter("walletOperationIds", ids).executeUpdate();
        }else{
            log.warn("can not cancel discounted wallet operation, cause the list is empty");
        }
    }
}