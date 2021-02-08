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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.order.OrderService;
import org.meveo.service.tax.TaxClassService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;

/**
 * RatedTransactionService : A class for Rated transaction persistence services.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * @author Abdellatif BARI
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
@Stateless
public class RatedTransactionService extends PersistenceService<RatedTransaction> {

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private TaxMappingService taxMappingService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private TaxService taxService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OrderService orderService;

    @Inject
    private SellerService sellerService;

    @Inject
    private FilterService filterService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private TaxClassService taxClassService;

    @Inject
    private WalletService walletService;

    /**
     * Check if Billing account has any not yet billed Rated transactions
     *
     * @param billingAccount       billing account
     * @param firstTransactionDate date of first transaction. Optional
     * @param lastTransactionDate  date of last transaction
     * @return true/false
     */
    public Boolean isBillingAccountBillable(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {
        long count = 0;
        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }
        TypedQuery<Long> q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedOpenByBA", Long.class);
        count = q.setParameter("billingAccount", billingAccount).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getSingleResult();
        log.debug("isBillingAccountBillable code={},lastTransactionDate={}) : {}", billingAccount.getCode(), lastTransactionDate, count);
        return count > 0 ? true : false;
    }

    /**
     * @param invoice invoice
     * @param invoiceSubCategory sub category invoice
     * @return list of rated transaction
     */
    public List<RatedTransaction> getListByInvoiceAndSubCategory(Invoice invoice, InvoiceSubCategory invoiceSubCategory) {
        if ((invoice == null) || (invoiceSubCategory == null)) {
            return null;
        }
        return getEntityManager().createNamedQuery("RatedTransaction.getListByInvoiceAndSubCategory", RatedTransaction.class).setParameter("invoice", invoice).setParameter("invoiceSubCategory", invoiceSubCategory)
            .getResultList();
    }

    /**
     * Convert Wallet operations to Rated transactions for a given entity up to a given date
     * 
     * @param entityToInvoice Entity for which to convert Wallet operations to Rated transactions
     * @param uptoInvoicingDate Up to invoicing date. Convert Wallet operations which invoicingDate is null or less than a specified date
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createRatedTransactions(IBillableEntity entityToInvoice, Date uptoInvoicingDate) {
        List<WalletOperation> walletOps = walletOperationService.listToRate(entityToInvoice, uptoInvoicingDate);

        EntityManager em = getEntityManager();

        Date now = new Date();
        for (WalletOperation walletOp : walletOps) {
            RatedTransaction ratedTransaction = new RatedTransaction(walletOp);
            create(ratedTransaction);
            em.createNamedQuery("WalletOperation.setStatusToTreatedWithRT").setParameter("rt", ratedTransaction).setParameter("now", now).setParameter("id", walletOp.getId()).executeUpdate();
        }
    }

    public List<WalletOperation> getWalletOperations(IBillableEntity entityToInvoice, Date invoicingDate) {
        return walletOperationService.listToRate(entityToInvoice, invoicingDate);
    }

    public List<WalletOperation> getWalletOperations(List<Long> ids){
        return walletOperationService.listByIds(ids);
    }

    /**
     * Create Rated transaction from wallet operation.
     * 
     * @param walletOperation Wallet operation
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return Rated transaction
     * @throws BusinessException business exception
     */
    public RatedTransaction createRatedTransaction(WalletOperation walletOperation, boolean isVirtual) throws BusinessException {

        RatedTransaction ratedTransaction = new RatedTransaction(walletOperation);
        walletOperation.changeStatus(WalletOperationStatusEnum.TREATED);

        if (!isVirtual) {
            create(ratedTransaction);
            walletOperation.setRatedTransaction(ratedTransaction);
        }
        return ratedTransaction;
    }

    /**
     * Create a {@link RatedTransaction} from a group of wallet operations.
     *
     * @param aggregatedWo       aggregated wallet operations
     * @param aggregatedSettings aggregation settings of wallet operations
     * @param invoicingDate      the invoicing date
     * @return created {@link RatedTransaction}
     * @throws BusinessException Exception when RT is not create successfully
     * @see WalletOperation
     */
    public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo, WalletOperationAggregationSettings aggregatedSettings, Date invoicingDate)
            throws BusinessException {
        return createRatedTransaction(aggregatedWo, aggregatedSettings, invoicingDate, false);
    }

    /**
     * @param aggregatedWo        aggregated wallet operations
     * @param aggregationSettings aggregation settings of wallet operations
     * @param isVirtual           is virtual
     * @param invoicingDate       the invoicing date
     * @return {@link RatedTransaction}
     * @throws BusinessException Exception when RT is not create successfully
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo, WalletOperationAggregationSettings aggregationSettings, Date invoicingDate,
            boolean isVirtual) throws BusinessException {
        RatedTransaction ratedTransaction = new RatedTransaction();

        Seller seller = null;
        BillingAccount ba = null;
        UserAccount ua = null;
        Subscription sub = null;
        ServiceInstance si = null;
        ChargeInstance ci = null;
        String code = null;
        String description = null;
        InvoiceSubCategory isc = null;

        Calendar cal = Calendar.getInstance();
        if (aggregatedWo.getYear() != null && aggregatedWo.getMonth() != null && aggregatedWo.getDay() != null) {
            cal.set(aggregatedWo.getYear(), aggregatedWo.getMonth(), aggregatedWo.getDay(), 0, 0, 0);
            ratedTransaction.setUsageDate(cal.getTime());
        } else {
            ratedTransaction.setUsageDate(aggregatedWo.getOperationDate());
        }

        isc = invoiceSubCategoryService.refreshOrRetrieve(aggregatedWo.getInvoiceSubCategory());
        ci = (ChargeInstance) chargeInstanceService.refreshOrRetrieve(aggregatedWo.getChargeInstance());
        si = (aggregatedWo.getServiceInstance() == null && ci != null) ? ci.getServiceInstance() : serviceInstanceService.refreshOrRetrieve(aggregatedWo.getServiceInstance());
        sub = (aggregatedWo.getSubscription() == null && ci != null) ? ci.getSubscription() : subscriptionService.refreshOrRetrieve(aggregatedWo.getSubscription());
        ua = (aggregatedWo.getUserAccount() == null && sub != null) ? sub.getUserAccount() : userAccountService.refreshOrRetrieve(aggregatedWo.getUserAccount());
        ba = (aggregatedWo.getBillingAccount() == null && ua != null) ? ua.getBillingAccount() : billingAccountService.refreshOrRetrieve(aggregatedWo.getBillingAccount());
        seller = (aggregatedWo.getSeller() == null && sub != null) ? sub.getSeller() : sellerService.refreshOrRetrieve(aggregatedWo.getSeller());
        if (ci != null) {
            code = ci.getCode();
        } else if (si != null) {
            code = si.getCode();
        } else {
            code = isc.getCode();
        }
        description = (aggregatedWo.getDescription() != null) ? aggregatedWo.getDescription() : aggregatedWo.getComputedDescription();

        ratedTransaction.setOrderNumber(aggregatedWo.getOrderNumber());
        ratedTransaction.setParameter1(aggregatedWo.getParameter1());
        ratedTransaction.setParameter2(aggregatedWo.getParameter2());
        ratedTransaction.setParameter3(aggregatedWo.getParameter3());
        ratedTransaction.setParameterExtra(aggregatedWo.getParameterExtra());
        Tax tax = taxService.refreshOrRetrieve(aggregatedWo.getTax());
        TaxClass taxClass = taxClassService.refreshOrRetrieve(aggregatedWo.getTaxClass());
        ratedTransaction.setCode(code);
        ratedTransaction.setType(RatedTransactionTypeEnum.AGGREGATED);
        ratedTransaction.setDescription(description);
        ratedTransaction.setTax(tax);
        ratedTransaction.setTaxPercent(tax.getPercent());
        ratedTransaction.setInvoiceSubCategory(isc);
        ratedTransaction.setSeller(seller);
        ratedTransaction.setBillingAccount(ba);
        ratedTransaction.setUserAccount(ua);
        ratedTransaction.setSubscription(sub);
        ratedTransaction.setChargeInstance(ci);
        BigDecimal amountWithoutTax = aggregatedWo.getAmountWithoutTax();
        BigDecimal amountWithTax = aggregatedWo.getAmountWithTax();
        BigDecimal amountTax = aggregatedWo.getAmountTax();
        if (aggregationSettings.getAggregationRoundingMode() != null) {
            amountWithoutTax = amountWithoutTax.setScale(aggregationSettings.getAggregationRounding(), aggregationSettings.getAggregationRoundingMode().getRoundingMode());
            amountWithTax = amountWithTax.setScale(aggregationSettings.getAggregationRounding(), aggregationSettings.getAggregationRoundingMode().getRoundingMode());
            amountTax = amountTax.setScale(aggregationSettings.getAggregationRounding(), aggregationSettings.getAggregationRoundingMode().getRoundingMode());
        }
        ratedTransaction.setAmountWithTax(amountWithTax);
        ratedTransaction.setAmountTax(amountTax);
        ratedTransaction.setAmountWithoutTax(amountWithoutTax);
        ratedTransaction.setQuantity(aggregatedWo.getQuantity());
        ratedTransaction.setTaxClass(taxClass);
        ratedTransaction.setUnitAmountWithTax(aggregatedWo.getUnitAmountWithTax());
        ratedTransaction.setUnitAmountTax(aggregatedWo.getUnitAmountTax());
        ratedTransaction.setUnitAmountWithoutTax(aggregatedWo.getUnitAmountWithoutTax());
        ratedTransaction.setSortIndex(aggregatedWo.getSortIndex());
        ratedTransaction.setStartDate(aggregatedWo.getStartDate());
        ratedTransaction.setEndDate(aggregatedWo.getEndDate());
        //ratedTransaction.setEdr(aggregatedWo.getEdr());
        WalletInstance wallet = walletService.refreshOrRetrieve(aggregatedWo.getWallet());
        ratedTransaction.setWallet(wallet);
        populateCustomfield(ratedTransaction, aggregatedWo);
        if (!isVirtual) {
            create(ratedTransaction);
            updateAggregatedWalletOperations(aggregatedWo.getWalletOperationsIds(), ratedTransaction);
        }

        return ratedTransaction;
    }

    private void populateCustomfield(RatedTransaction ratedTransaction, AggregatedWalletOperation aggregatedWo) {
        if (aggregatedWo.getCfValues() != null && !aggregatedWo.getCfValues().isEmpty()) {
            for (String cfField : aggregatedWo.getCfValues().keySet()) {
                if (isCfAppliedTo(cfField, ratedTransaction)) {
                    customFieldInstanceService.setCFValue(ratedTransaction, cfField, aggregatedWo.getCfValues().get(cfField));
                }
            }
        }
    }

    private boolean isCfAppliedTo(String cfField, RatedTransaction ratedTransaction) {
        CustomFieldTemplate customFieldTemplate = customFieldTemplateService.findByCodeAndAppliesTo(cfField, ratedTransaction);
        return customFieldTemplate != null;
    }

    public void updateAggregatedWalletOperations(List<Long> woIds, RatedTransaction ratedTransaction) {
        // batch update
        String strQuery =
                "UPDATE WalletOperation o SET o.status=org.meveo.model.billing.WalletOperationStatusEnum.TREATED," + " o.ratedTransaction=:ratedTransaction , o.updated=:updated"
                        + " WHERE o.id in (:woIds) ";
        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("woIds", woIds);
        query.setParameter("ratedTransaction", ratedTransaction);
        query.setParameter("updated", new Date());
        int affectedRecords = query.executeUpdate();
        log.debug("updated record wo count={}", affectedRecords);
    }

    /**
     * List unprocessed Rated transactions from a given wallet instance (user account) and invoice subcategory
     * 
     * @param walletInstance Wallet instance
     * @param invoiceSubCategory Invoice sub category. Optional.
     * @param from Date range - from. Optional.
     * @param to Date range - to. Optional.
     * @return A list of rated transactions
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> openRTbySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder("select rt from RatedTransaction rt ", "rt");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("rt.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("rt.wallet", walletInstance);
        qb.addSql("rt.status='OPEN'");
        if (from != null) {
            qb.addCriterion("usageDate", ">=", from, false);
        }
        if (to != null) {
            qb.addCriterion("usageDate", "<=", to, false);
        }

        try {
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long countNotInvoicedRTByBA(BillingAccount billingAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedByBA").setParameter("billingAccount", billingAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotInvoiced RT by BA", e);
            return null;
        }
    }

    public Long countNotInvoicedRTByUA(UserAccount userAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedByUA").setParameter("userAccount", userAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotInvoiced RT by UA", e);
            return null;
        }
    }

    public Long countNotInvoicedRTByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotInvoiced RT by CA", e);
            return null;
        }
    }

    /**
     * Find the rated transaction by wallet operation id.
     *
     * @param walletOperationId the wallet operation id
     * @return the rated transaction
     */
    public RatedTransaction findByWalletOperationId(Long walletOperationId) {
        try {
            return (RatedTransaction) getEntityManager().createNamedQuery("RatedTransaction.findByWalletOperationId").setParameter("walletOperationId", walletOperationId).getSingleResult();

        } catch (NoResultException e) {
            log.warn("No ratedTransaction found with the given walletOperation.id. {}", e.getMessage());
            return null;
        }
    }

    /**
     * Call RatedTransaction.setStatusToCanceledByRsCodes Named query to cancel just opened RatedTransaction of all passed RatedTransaction ids.
     * 
     * @param rsToCancelIds rated transactions to cancel
     */
    public void cancelRatedTransactions(List<Long> rsToCancelIds) {
        if ((rsToCancelIds.size() > 0) && !rsToCancelIds.isEmpty()) {
            getEntityManager().createNamedQuery("RatedTransaction.cancelByRTIds").setParameter("now", new Date()).setParameter("rtIds", rsToCancelIds).executeUpdate();
        }
    }

    /**
     * Calculate billable amount per entity, create additional rated transactions to reach a minimum invoiceable amount and link billable entity with a Billing run
     *
     * @param entity Entity to invoice
     * @param billingRun the billing run
     * @param instantiateMinRtsForService Should rated transactions to reach minimum invoicing amount be checked and instantiated on service level.
     * @param instantiateMinRtsForSubscription Should rated transactions to reach minimum invoicing amount be checked and instantiated on subscription level.
     * @param instantiateMinRtsForBA Should rated transactions to reach minimum invoicing amount be checked and instantiated on Billing account level.
     * @return Updated entity
     * @throws BusinessException the business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBillableEntity updateEntityTotalAmountsAndLinkToBR(IBillableEntity entity, BillingRun billingRun, MinAmountForAccounts minAmountForAccounts) throws BusinessException {

        log.debug("Calculating total amounts and creating min RTs for {}/{}", entity.getClass().getSimpleName(), entity.getId());

        BillingAccount billingAccount = null;
        if (entity instanceof BillingAccount) {
            entity = billingAccountService.findById((Long) entity.getId());
            billingAccount = (BillingAccount) entity;
        }

        if (entity instanceof Subscription) {
            entity = subscriptionService.findById((Long) entity.getId());
            billingAccount = ((Subscription) entity).getUserAccount() != null ? ((Subscription) entity).getUserAccount().getBillingAccount() : null;
        }

        if (entity instanceof Order) {
            entity = orderService.findById((Long) entity.getId());
            if ((((Order) entity).getUserAccounts() != null) && !((Order) entity).getUserAccounts().isEmpty()) {
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null ? (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount() : null;
            }
        }
        // MinAmountForAccounts minAmountForAccounts = new MinAmountForAccounts(instantiateMinRtsForBA, false, instantiateMinRtsForSubscription, instantiateMinRtsForService);
        calculateAmountsAndCreateMinAmountTransactions(entity, null, billingRun.getLastTransactionDate(), true, minAmountForAccounts);

        BigDecimal invoiceAmount = entity.getTotalInvoicingAmountWithoutTax();

        entity.setBillingRun(getEntityManager().getReference(BillingRun.class, billingRun.getId()));

        if (entity instanceof BillingAccount) {
            ((BillingAccount) entity).setBrAmountWithoutTax(invoiceAmount);
            billingAccountService.updateNoCheck((BillingAccount) entity);
        }
        if (entity instanceof Order) {
            orderService.updateNoCheck((Order) entity);
        }
        if (entity instanceof Subscription) {
            subscriptionService.updateNoCheck((Subscription) entity);
        }

        return entity;
    }

    /**
     * Calculate billable amount per entity, create additional rated transactions to reach a minimum invoiceable amount and link billable entity with a Billing run
     *
     * @param entityId ID of an entity to invoice
     * @param billingRun The billing run
     * @param totalAmounts Amounts to invoice
     * @return Updated entity
     * @throws BusinessException The business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBillableEntity updateEntityTotalAmountsAndLinkToBR(Long entityId, BillingRun billingRun, Amounts totalAmounts) throws BusinessException {

        IBillableEntity entity = null;
        BillingAccount billingAccount = null;

        switch (billingRun.getBillingCycle().getType()) {
        case BILLINGACCOUNT:
            entity = billingAccountService.findById(entityId);
            billingAccount = (BillingAccount) entity;
            break;

        case SUBSCRIPTION:
            entity = subscriptionService.findById(entityId);
            billingAccount = ((Subscription) entity).getUserAccount() != null ? ((Subscription) entity).getUserAccount().getBillingAccount() : null;
            break;

        case ORDER:
            entity = orderService.findById(entityId);
            if ((((Order) entity).getUserAccounts() != null) && !((Order) entity).getUserAccounts().isEmpty()) {
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null ? (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount() : null;
            }
            break;
        }
        entity.setTotalInvoicingAmountWithoutTax(totalAmounts.getAmountWithoutTax());
        entity.setTotalInvoicingAmountWithTax(totalAmounts.getAmountWithTax());
        entity.setTotalInvoicingAmountTax(totalAmounts.getAmountTax());

        BigDecimal invoiceAmount = totalAmounts.getAmountWithoutTax();

        entity.setBillingRun(getEntityManager().getReference(BillingRun.class, billingRun.getId()));

        if (entity instanceof BillingAccount) {
            ((BillingAccount) entity).setBrAmountWithoutTax(invoiceAmount);
            billingAccountService.updateNoCheck((BillingAccount) entity);
        } else if (entity instanceof Order) {
            orderService.updateNoCheck((Order) entity);
        } else if (entity instanceof Subscription) {
            subscriptionService.updateNoCheck((Subscription) entity);
        }

        return entity;
    }

    /**
     * Create min amounts rated transactions and set invoiceable amounts to the billable entity
     *
     * @param billableEntity The billable entity
     * @param lastTransactionDate Last transaction date
     * @param calculateAndUpdateTotalAmounts Should total amounts be calculated and entity updated with those amounts
     * @param minAmountForAccounts Booleans to knows if an accounts has minimum amount activated
     * @throws BusinessException General business exception
     */
    public void calculateAmountsAndCreateMinAmountTransactions(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, boolean calculateAndUpdateTotalAmounts,
            MinAmountForAccounts minAmountForAccounts) throws BusinessException {

        Amounts totalInvoiceableAmounts = null;

        List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();
        List<ExtraMinAmount> extraMinAmounts = new ArrayList<>();

        Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

        if (billableEntity instanceof Order) {
            if (calculateAndUpdateTotalAmounts) {
                totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);
            }
        } else {
            // Create Min Amount RTs for hierarchy

            BillingAccount billingAccount = (billableEntity instanceof Subscription) ? ((Subscription) billableEntity).getUserAccount().getBillingAccount() : (BillingAccount) billableEntity;

            Class[] accountClasses = new Class[] { ServiceInstance.class, Subscription.class, UserAccount.class, BillingAccount.class, CustomerAccount.class, Customer.class };
            for (Class accountClass : accountClasses) {
                if (minAmountForAccounts.isMinAmountForAccountsActivated(accountClass, billableEntity)) {
                    MinAmountsResult minAmountsResults = createMinRTForAccount(billableEntity, billingAccount, lastTransactionDate, minRatingDate, extraMinAmounts, accountClass);
                    extraMinAmounts = minAmountsResults.getExtraMinAmounts();
                    minAmountTransactions.addAll(minAmountsResults.getMinAmountTransactions());
                }
            }
            // get totalInvoicable for the billableEntity
            totalInvoiceableAmounts = computeTotalInvoiceableAmount(billableEntity, new Date(0), lastTransactionDate);

            // Sum up
            final Amounts totalAmounts = new Amounts();
            extraMinAmounts.forEach(extraMinAmount -> {
                extraMinAmount.getCreatedAmount().values().forEach(amounts -> {
                    totalAmounts.addAmounts(amounts);
                });
            });
            totalInvoiceableAmounts.addAmounts(totalAmounts);

        }

        billableEntity.setMinRatedTransactions(minAmountTransactions);

        if (calculateAndUpdateTotalAmounts) {
            totalInvoiceableAmounts.calculateDerivedAmounts(appProvider.isEntreprise());

            billableEntity.setTotalInvoicingAmountWithoutTax(totalInvoiceableAmounts.getAmountWithoutTax());
            billableEntity.setTotalInvoicingAmountWithTax(totalInvoiceableAmounts.getAmountWithTax());
            billableEntity.setTotalInvoicingAmountTax(totalInvoiceableAmounts.getAmountTax());
        }
    }

    private Amounts computeTotalInvoiceableAmount(IBillableEntity billableEntity, Date date, Date lastTransactionDate) {
        if (billableEntity instanceof Subscription) {
            return computeTotalInvoiceableAmountForSubscription((Subscription) billableEntity, date, lastTransactionDate);
        }
        return computeTotalInvoiceableAmountForBillingAccount((BillingAccount) billableEntity, date, lastTransactionDate);
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per subscription level. Only those subscriptions that have minimum invoice amount rule are considered. Updates
     * minAmountTransactions parameter.
     *
     * @param billableEntity Entity to bill - entity for which minimum rated transactions should be created
     * @param billingAccount Billing account to associate new minimum amount Rated transactions with
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param extraMinAmounts Additional Rated transaction amounts created to reach minimum invoicing amount per account level
     * @param accountClass the account class which can be : ServiceInstance, Subscription or any class for the accounts hierarchy
     * @return MinAmountsResult Contains new rated transaction created to reach the minimum for an account class and the extra amount.
     * @throws BusinessException General Business exception
     */
    private MinAmountsResult createMinRTForAccount(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate, List<ExtraMinAmount> extraMinAmounts, Class accountClass)
            throws BusinessException {

        MinAmountsResult minAmountsResult = new MinAmountsResult();

        Map<Long, MinAmountData> accountToMinAmount = getInvoiceableAmountDataPerAccount(billableEntity, billingAccount, lastTransactionDate, extraMinAmounts, accountClass);

        accountToMinAmount = prepareAccountsWithMinAmount(billableEntity, billingAccount, extraMinAmounts, accountClass, accountToMinAmount);

        // Create Rated transactions to reach a minimum amount per account level

        for (Entry<Long, MinAmountData> accountAmounts : accountToMinAmount.entrySet()) {
            Map<String, Amounts> minRTAmountMap = new HashMap<>();

            if (accountAmounts.getValue() == null || accountAmounts.getValue().getMinAmount() == null) {
                continue;
            }

            BigDecimal minAmount = accountAmounts.getValue().getMinAmount();
            String minAmountLabel = accountAmounts.getValue().getMinAmountLabel();
            BigDecimal totalInvoiceableAmount = appProvider.isEntreprise() ? accountAmounts.getValue().getAmounts().getAmountWithoutTax() : accountAmounts.getValue().getAmounts().getAmountWithTax();
            BusinessEntity entity = accountAmounts.getValue().getEntity();

            Seller seller = accountAmounts.getValue().getSeller();
            if (seller == null) {
                throw new BusinessException("Default Seller is mandatory for invoice minimum (Customer.seller)");
            }
            String mapKeyPrefix = seller.getId().toString() + "_";

            BigDecimal diff = minAmount.subtract(totalInvoiceableAmount);
            if (diff.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            OneShotChargeTemplate oneShotChargeTemplate = getMinimumChargeTemplate(entity);
            if (oneShotChargeTemplate == null) {
                log.error("The charge template target is not defined for the entity: {}", entity);
                continue;
            }

            InvoiceSubCategory invoiceSubCategory = oneShotChargeTemplate.getInvoiceSubCategory();
            String mapKey = mapKeyPrefix + invoiceSubCategory.getId();

            TaxInfo taxInfo = taxMappingService.determineTax(oneShotChargeTemplate.getTaxClass(), seller, billingAccount, null, minRatingDate, true, false);

            String code = getMinAmountRTCode(entity, accountClass);
            RatedTransaction ratedTransaction = getNewRatedTransaction(billableEntity, billingAccount, minRatingDate, minAmountLabel, entity, seller, invoiceSubCategory, taxInfo, diff, code, RatedTransactionTypeEnum.MINIMUM);

            minAmountsResult.addMinAmountRT(ratedTransaction);

            // Remember newly "created" transaction amounts, as they are not persisted yet to DB
            minRTAmountMap.put(mapKey, new Amounts(ratedTransaction.getUnitAmountWithoutTax(), ratedTransaction.getAmountWithTax(), ratedTransaction.getAmountTax()));
            extraMinAmounts.add(new ExtraMinAmount(entity, minRTAmountMap));

        }

        minAmountsResult.setExtraMinAmounts(extraMinAmounts);
        return minAmountsResult;
    }

    /**
     * Prepare each account level with minimum amount activated to generate the minimum RT.
     *
     * @param billableEntity the billable entity can be a subscription or a billing account
     * @param billingAccount The billing account
     * @param extraMinAmounts The extra minimum amount generated in children levels
     * @param accountClass The account class
     * @param accountToMinAmount where to store the entity and new generated amounts to reach the minimum
     * @return A map where to store amounts for each entity
     */
    private Map<Long, MinAmountData> prepareAccountsWithMinAmount(IBillableEntity billableEntity, BillingAccount billingAccount, List<ExtraMinAmount> extraMinAmounts, Class accountClass,
            Map<Long, MinAmountData> accountToMinAmount) {
        List<BusinessEntity> accountsWithMinAmount = new ArrayList<>();

        accountsWithMinAmount = getAccountsWithMinAmountElNotNull(billableEntity, accountClass);

        for (BusinessEntity entity : accountsWithMinAmount) {
            MinAmountData minAmountInfo = accountToMinAmount.get(entity.getId());
            if (minAmountInfo == null) {
                String minAmountEL = getMinimumAmountElInfo(entity, "getMinimumAmountEl");
                String minAmountLabelEL = getMinimumAmountElInfo(entity, "getMinimumLabelEl");
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, entity);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, entity);

                Amounts accountAmounts = new Amounts(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                accountToMinAmount.put(entity.getId(), new MinAmountData(minAmount, minAmountLabel, accountAmounts.clone(), null, entity, getSeller(billingAccount, entity)));

                if (extraMinAmounts != null) {
                    accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                }

            } else {
                // The amount exceed the minimum amount per account level
                if ((minAmountInfo.getMinAmount()).compareTo(appProvider.isEntreprise() ? minAmountInfo.getAmounts().getAmountWithoutTax() : minAmountInfo.getAmounts().getAmountWithTax()) <= 0) {
                    accountToMinAmount.put(entity.getId(), null);
                }
            }
        }
        return accountToMinAmount;
    }

    /**
     * Gets the invoiceable amount for each level account.
     *
     * @param billableEntity The billable entity
     * @param billingAccount The billing account
     * @param lastTransactionDate last transaction date
     * @param extraMinAmounts The extra minimum amounts generated in children levels
     * @param accountClass The account level's class
     * @return return invoiceable amount grouped by entity
     */
    private Map<Long, MinAmountData> getInvoiceableAmountDataPerAccount(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, List<ExtraMinAmount> extraMinAmounts, Class accountClass) {
        EntityManager em = getEntityManager();

        Map<Long, MinAmountData> accountToMinAmount = new HashMap<>();

        // Get the invoiceable amount per account level
        List<Object[]> amountsList = computeInvoiceableAmountForAccount(billableEntity, new Date(0), lastTransactionDate, accountClass);

        for (Object[] amounts : amountsList) {
            BigDecimal amountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal amountWithTax = (BigDecimal) amounts[1];
            BusinessEntity entity = (BusinessEntity) em.find(accountClass, amounts[2]);
            Seller seller = getSeller(billingAccount, entity);

            MinAmountData minAmountDataInfo = accountToMinAmount.get(entity.getId());

            // Resolve if minimal invoice amount rule applies
            if (minAmountDataInfo == null) {
                String minAmountEL = getMinimumAmountElInfo(entity, "getMinimumAmountEl");
                String minAmountLabelEL = getMinimumAmountElInfo(entity, "getMinimumLabelEl");
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, entity);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, entity);
                if (minAmount == null) {
                    continue;
                }
                MinAmountData minAmountData = new MinAmountData(minAmount, minAmountLabel, new Amounts(), null, entity, seller);
                accountToMinAmount.put(entity.getId(), minAmountData);

                if (extraMinAmounts != null) {
                    accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                }
            }

            minAmountDataInfo = accountToMinAmount.get(entity.getId());
            minAmountDataInfo.getAmounts().addAmounts(amountWithoutTax, amountWithTax, null);

        }
        return accountToMinAmount;
    }

    /**
     * Gets the minimum amount RT code used in Rated transaction.
     *
     * @param entity the entity
     * @param accountClass the account class
     * @return the minimum amount RT code
     */
    private String getMinAmountRTCode(BusinessEntity entity, Class accountClass) {
        String prefix = "";
        if (accountClass.equals(ServiceInstance.class)) {
            prefix = RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode();
        }
        if (accountClass.equals(Subscription.class)) {
            prefix = RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SU.getCode();
        }
        if (accountClass.equals(UserAccount.class)) {
            prefix = RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_UA.getCode();
        }
        if (accountClass.equals(BillingAccount.class)) {
            prefix = RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_BA.getCode();
        }
        if (accountClass.equals(CustomerAccount.class)) {
            prefix = RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_CA.getCode();
        }
        if (accountClass.equals(Customer.class)) {
            prefix = RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_CUST.getCode();
        }

        return prefix + "_" + entity.getCode();
    }

    /**
     * Gets the invoiceable amount for an entity in each hierarchy level.
     *
     * @param billableEntity the billable entity
     * @param firstTransactionDate first Transaction Date
     * @param lastTransactionDate last Transaction Date
     * @param accountClass account class
     * @return invoiceable amount
     */
    private List<Object[]> computeInvoiceableAmountForAccount(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, Class accountClass) {
        if (accountClass.equals(ServiceInstance.class)) {
            return computeInvoiceableAmountForServicesWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(Subscription.class)) {
            return computeInvoiceableAmountForSubscriptionsWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(UserAccount.class)) {
            return computeInvoiceableAmountForUserAccountsWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return computeInvoiceableAmountForBillingAccountWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return computeInvoiceableAmountForCustomerAccountWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(Customer.class)) {
            return computeInvoiceableAmountForCustomerWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        return null;
    }

    /**
     * Gets Accounts, subscriptions or services where the minimum amounts is activated.
     *
     * @param billableEntity the billable entity
     * @param accountClass the account class
     * @return a list of entities where the minimum amounts is activated.
     */
    private List<BusinessEntity> getAccountsWithMinAmountElNotNull(IBillableEntity billableEntity, Class<? extends BusinessEntity> accountClass) {

        if (accountClass.equals(ServiceInstance.class)) {
            return getServicesWithMinAmount(billableEntity);
        }
        if (accountClass.equals(Subscription.class)) {
            return getSubscriptionsWithMinAmount(billableEntity);
        }
        if (accountClass.equals(UserAccount.class)) {
            return getUserAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return getBillingAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return getCustomerAccountsWithMinAmountELNotNull(billableEntity);
        }
        if (accountClass.equals(Customer.class)) {
            return getCustomersWithMinAmountELNotNull(billableEntity);
        }

        return new ArrayList<>();
    }

    /**
     * Gets billing account where the minimum amount is activated.
     *
     * @param billableEntity the billable entity;
     * @return a list of billing account
     */
    private List<BusinessEntity> getBillingAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }

        Query q = getEntityManager().createNamedQuery("BillingAccount.getBillingAccountsWithMinAmountELNotNullByBA").setParameter("billingAccount", billableEntity);
        return q.getResultList();
    }

    /**
     * Gets Customer accounts where the minimum amount is activated.
     *
     * @param billableEntity the billable entity;
     * @return a list of customer accounts
     */
    private List<BusinessEntity> getCustomerAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        Query q = getEntityManager().createNamedQuery("CustomerAccount.getCustomerAccountsWithMinAmountELNotNullByBA").setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount());
        return q.getResultList();
    }

    /**
     * Gets Customers where the minimum amount is activated.
     *
     * @param billableEntity the billable entity;
     * @return a list of customers
     */
    private List<BusinessEntity> getCustomersWithMinAmountELNotNull(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        Query q = getEntityManager().createNamedQuery("Customer.getCustomersWithMinAmountELNotNullByBA").setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer());
        return q.getResultList();
    }

    @Deprecated
    private InvoiceSubCategory getMinimumInvoiceSubCategory(BillingAccount billingAccount, BusinessEntity entity) {
        if (entity instanceof ServiceInstance) {
            InvoiceSubCategory invoiceSubCategory = ((ServiceInstance) entity).getMinimumInvoiceSubCategory();
            if (invoiceSubCategory == null && ((ServiceInstance) entity).getServiceTemplate().getMinimumInvoiceSubCategory() != null) {
                invoiceSubCategory = ((ServiceInstance) entity).getServiceTemplate().getMinimumInvoiceSubCategory();
            }
            return invoiceSubCategory;
        }
        if (entity instanceof Subscription) {
            InvoiceSubCategory invoiceSubCategory = ((Subscription) entity).getMinimumInvoiceSubCategory();
            if (invoiceSubCategory == null && ((Subscription) entity).getOffer().getMinimumInvoiceSubCategory() != null) {
                invoiceSubCategory = ((Subscription) entity).getOffer().getMinimumInvoiceSubCategory();
            }
            return invoiceSubCategory;
        } else {
            return billingAccount.getMinimumInvoiceSubCategory();
        }

    }

    private OneShotChargeTemplate getMinimumChargeTemplate(BusinessEntity entity) {
        try {
            Method method = entity.getClass().getMethod("getMinimumChargeTemplate", null);
            return (OneShotChargeTemplate) method.invoke(entity, null);
        } catch (NoSuchMethodException e) {
            throw new BusinessException("The method getMinimumChargeTemplate is not defined for the entity: " + entity.getClass().getName(), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException("Error when calling the getMinimumChargeTemplate on : " + entity.getClass().getName(), e);
        }
    }

    /**
     * Extract minimum amount EL.
     *
     * @param entity the entity
     * @param method the method used to get the minimum amount EL
     * @return the minimum amount as String
     */
    private String getMinimumAmountElInfo(BusinessEntity entity, String method) {
        try {
            Method getMinimumAmountElMethod = entity.getClass().getMethod(method);
            if (getMinimumAmountElMethod != null) {
                String value = (String) getMinimumAmountElMethod.invoke(entity);
                if (value == null && entity instanceof ServiceInstance) {
                    getMinimumAmountElMethod = ((ServiceInstance) entity).getServiceTemplate().getClass().getMethod(method);
                    value = (String) getMinimumAmountElMethod.invoke(((ServiceInstance) entity).getServiceTemplate());
                }
                if (value == null && entity instanceof Subscription) {
                    getMinimumAmountElMethod = ((Subscription) entity).getOffer().getClass().getMethod(method);
                    value = (String) getMinimumAmountElMethod.invoke(((Subscription) entity).getOffer());
                }
                return value;
            } else {
                throw new BusinessException("The method getMinimumAmountEl () is not defined for the entity " + entity.getClass().getSimpleName());
            }

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BusinessException("The method getMinimumAmountEl () is not defined for the entity " + entity.getClass().getSimpleName());
        }
    }

    /**
     * Generate the minimum amount rated transaction
     *
     * @param billableEntity the billable entity
     * @param billingAccount the billing account
     * @param minRatingDate the rated transaction date
     * @param minAmountLabel the rated transaction label
     * @param entity the entity
     * @param seller the seller
     * @param invoiceSubCategory the invoice subcategory
     * @param taxInfo the tax info
     * @param rtMinAmount the rated transaction amount
     * @param code the rated transaction code.
     * @param minimum
     * @return a rated transaction
     */
    private RatedTransaction getNewRatedTransaction(IBillableEntity billableEntity, BillingAccount billingAccount, Date minRatingDate, String minAmountLabel, BusinessEntity entity, Seller seller,
                                                    InvoiceSubCategory invoiceSubCategory, TaxInfo taxInfo, BigDecimal rtMinAmount, String code, RatedTransactionTypeEnum type) {
        Tax tax = taxInfo.tax;
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        RatedTransaction rt = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1], amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount,
            null, invoiceSubCategory, null, null, null, null, null, null, null, null, null, null, null, code, minAmountLabel, null, null, seller, tax, tax.getPercent(), null, taxInfo.taxClass, null, type);
        if (entity instanceof ServiceInstance) {
            rt.setServiceInstance((ServiceInstance) entity);
        }
        if (entity instanceof Subscription) {
            rt.setSubscription((Subscription) entity);
        }
        if (billableEntity instanceof Subscription) {
            rt.setSubscription((Subscription) billableEntity);
        }
        return rt;
    }

    /**
     * Gets the seller.
     *
     * @param billingAccount the billing account
     * @param entity the entity, can be ServiceInstance, subscription or billinAccount
     * @return a seller
     */
    private Seller getSeller(BillingAccount billingAccount, BusinessEntity entity) {
        if (entity instanceof ServiceInstance) {
            return ((ServiceInstance) entity).getSubscription().getSeller();
        }
        if (entity instanceof Subscription) {
            return ((Subscription) entity).getSeller();
        }
        return billingAccount.getCustomerAccount().getCustomer().getSeller();
    }

    /**
     * For each level in the account's hierarchy, append the extra minimum generated in a level to its parent level.
     *
     * @param extraMinAmounts the extra amounts generated
     * @param accountToMinAmount a map amounts grouped by an entity.
     * @param entity the entity
     * @return a map amounts grouped by an entity.
     */
    private Map<Long, MinAmountData> appendExtraAmount(List<ExtraMinAmount> extraMinAmounts, Map<Long, MinAmountData> accountToMinAmount, BusinessEntity entity) {
        MinAmountData minAmountDataInfo = accountToMinAmount.get(entity.getId());

        extraMinAmounts.forEach(extraMinAmount -> {
            BusinessEntity extraMinAmountEntity = extraMinAmount.getEntity();

            if (isExtraMinAmountEntityChildOfEntity(extraMinAmountEntity, entity)) {
                Map<String, Amounts> extraAmounts = extraMinAmount.getCreatedAmount();
                for (Entry<String, Amounts> amountInfo : extraAmounts.entrySet()) {
                    minAmountDataInfo.getAmounts().addAmounts(amountInfo.getValue());
                }
            }

        });

        return accountToMinAmount;
    }

    /**
     * Check if an entity is a child of an other entity
     *
     * @param child the child entity
     * @param parent the parent entity
     * @return true if is a child
     */
    private boolean isExtraMinAmountEntityChildOfEntity(BusinessEntity child, BusinessEntity parent) {
        if (parent instanceof Subscription && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().equals(parent);
        }
        if (parent instanceof UserAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().equals(parent);
        }
        if (parent instanceof UserAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().equals(parent);
        }
        if (parent instanceof BillingAccount && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().getCustomerAccount().equals(parent);
        }
        if (parent instanceof CustomerAccount && child instanceof BillingAccount) {
            return ((BillingAccount) child).getCustomerAccount().equals(parent);
        }
        if (parent instanceof Customer && child instanceof ServiceInstance) {
            return ((ServiceInstance) child).getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof Subscription) {
            return ((Subscription) child).getUserAccount().getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof UserAccount) {
            return ((UserAccount) child).getBillingAccount().getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof BillingAccount) {
            return ((BillingAccount) child).getCustomerAccount().getCustomer().equals(parent);
        }
        if (parent instanceof Customer && child instanceof CustomerAccount) {
            return ((CustomerAccount) child).getCustomer().equals(parent);
        }
        return false;
    }

    /**
     * Gets user accounts for a billable entity where the minimum amount is activated.
     *
     * @param billableEntity the billable entity
     * @return a list of user account
     */
    private List<BusinessEntity> getUserAccountsWithMinAmountELNotNull(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByUA").setParameter("userAccount", ((Subscription) billableEntity).getUserAccount());
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByBA").setParameter("billingAccount", (BillingAccount) billableEntity);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts for a given subscription
     * 
     * @param subscription Subscription
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Amounts with and without tax, tax amount
     */
    private Amounts computeTotalInvoiceableAmountForSubscription(Subscription subscription, Date firstTransactionDate, Date lastTransactionDate) {

//        boolean ignorePrepaidWallets = false;  TODO AKK if (prePaidWalletsIds != null && !prePaidWalletsIds.isEmpty()) {
        String query = "RatedTransaction.sumTotalInvoiceableBySubscription";
//        if (ignorePrepaidWallets) {
//            query = "RatedTransaction.sumTotalInvoiceableBySubscriptionExcludePrepaidWO";
//        }        

        Query q = getEntityManager().createNamedQuery(query).setParameter("subscription", subscription).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);

//        if (ignorePrepaidWallets) {
//            q = q.setParameter("walletsIds", prePaidWalletsIds);
//        }

        return (Amounts) q.getSingleResult();
    }

    /**
     * Summed rated transaction amounts for a given billing account
     * 
     * @param billingAccount Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Amounts with and without tax, tax amount
     */
    private Amounts computeTotalInvoiceableAmountForBillingAccount(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {

//      boolean ignorePrepaidWallets = false;  TODO AKK if (prePaidWalletsIds != null && !prePaidWalletsIds.isEmpty()) {
        String query = "RatedTransaction.sumTotalInvoiceableByBA";
//      if (ignorePrepaidWallets) {
//          query = "RatedTransaction.sumTotalInvoiceableByBAExcludePrepaidWO";
//      }        

        Query q = getEntityManager().createNamedQuery(query).setParameter("billingAccount", billingAccount).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate",
            lastTransactionDate);

//      if (ignorePrepaidWallets) {
//          q = q.setParameter("walletsIds", prePaidWalletsIds);
//      }        

        return (Amounts) q.getSingleResult();
    }

    private List<BusinessEntity> getServicesWithMinAmount(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountBySubscription").setParameter("subscription", (Subscription) billableEntity);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountByBA").setParameter("billingAccount", (BillingAccount) billableEntity);
            return q.getResultList();
        }
        return Collections.emptyList();
    }

    /**
     * Gets subscriptions where the minimum amount is activated
     *
     * @param billableEntity the billable entity.
     * @return a list of subscription
     */
    private List<BusinessEntity> getSubscriptionsWithMinAmount(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountBySubscription").setParameter("subscription", (Subscription) billableEntity);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountByBA").setParameter("billingAccount", (BillingAccount) billableEntity);

            return q.getResultList();
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private List<Seller> getSellersByBillingAccount(BillingAccount billingAccount) {
        Query q = getEntityManager().createNamedQuery("Subscription.getSellersByBA").setParameter("billingAccount", billingAccount);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts applied on services, that have minimum invoiceable amount rule, grouped by invoice subCategory for a given billable entity.
     * 
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, serviceInstance
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForServicesWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByServiceWithMinAmountBySubscription").setParameter("subscription", (Subscription) billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByServiceWithMinAmountByBA").setParameter("billingAccount", (BillingAccount) billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }
        return null;
    }

    /**
     * Summed rated transaction amounts applied on subscriptions, that have minimum invoiceable amount rule for a given billable entity.
     *
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, subscription
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForSubscriptionsWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountBySubscription").setParameter("subscription", (Subscription) billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountByBA").setParameter("billingAccount", (BillingAccount) billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }
        return null;
    }

    /**
     * Summed rated transaction amounts applied on UserAccounts, that have minimum invoiceable amount rule for a given billable entity.
     *
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, subscription
     */
    private List<Object[]> computeInvoiceableAmountForUserAccountsWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForUAWithMinAmountBySubscription").setParameter("subscription", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableWithMinAmountByUA").setParameter("billingAccount", billableEntity).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by and seller for a given billing account
     *
     * @param billableEntity Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, seller id
     */
    private List<Object[]> computeInvoiceableAmountForBillingAccountWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForBAWithMinAmountBySubscription").setParameter("subscription", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableWithMinAmountByBA").setParameter("billingAccount", billableEntity).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by seller for a given customer account
     *
     * @param billableEntity Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, seller id
     */
    private List<Object[]> computeInvoiceableAmountForCustomerAccountWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForCAWithMinAmountBySubscription").setParameter("subscription", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableWithMinAmountByCA").setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount())
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by invoice subcategory and seller for a given customer account
     *
     * @param billableEntity BillingAccount or subscription
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, seller id
     */
    private List<Object[]> computeInvoiceableAmountForCustomerWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForCustomerWithMinAmountBySubscription").setParameter("subscription", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableWithMinAmountByCustomer").setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer())
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Evaluate double expression. Either ba, subscription or service instance must be specified.
     *
     * @param expression EL expression
     * @param ba Billing account
     * @param subscription Subscription
     * @param serviceInstance serviceInstance
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private BigDecimal evaluateMinAmountExpression(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, ba, subscription, serviceInstance, null);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
    }

    /**
     * Evaluate double expression. Either ba, ua, subscription or service instance must be specified.
     *
     * @param expression EL expression
     * @param entity Business Entity
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private BigDecimal evaluateMinAmountExpression(String expression, BusinessEntity entity) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (entity instanceof BillingAccount) {
            userMap = constructElContext(expression, (BillingAccount) entity, null, null, null);
        }
        if (entity instanceof UserAccount) {
            userMap = constructElContext(expression, null, null, null, (UserAccount) entity);
        }
        if (entity instanceof Subscription) {
            userMap = constructElContext(expression, null, (Subscription) entity, null, null);
        }
        if (entity instanceof ServiceInstance) {
            userMap = constructElContext(expression, null, null, (ServiceInstance) entity, null);
        }

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
    }

    /**
     * Evaluate string expression.
     *
     * @param expression EL expression
     * @param entity Business Entity
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private String evaluateMinAmountLabelExpression(String expression, BusinessEntity entity) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (entity instanceof BillingAccount) {
            userMap = constructElContext(expression, (BillingAccount) entity, null, null, null);
        }
        if (entity instanceof UserAccount) {
            userMap = constructElContext(expression, null, null, null, (UserAccount) entity);
        }
        if (entity instanceof Subscription) {
            userMap = constructElContext(expression, null, (Subscription) entity, null, null);
        }
        if (entity instanceof ServiceInstance) {
            userMap = constructElContext(expression, null, null, (ServiceInstance) entity, null);
        }

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
    }

    /**
     * Evaluate string expression.
     *
     * @param expression EL expression
     * @param ba billing account
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private String evaluateMinAmountLabelExpression(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> userMap = constructElContext(expression, ba, subscription, serviceInstance, null);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
    }

    /**
     * Construct EL context of variables
     *
     * @param expression EL expression
     * @param ba Billing account
     * @param subscription Subscription
     * @param serviceInstance Service instance
     * @return Context of variable
     */
    private Map<Object, Object> constructElContext(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance, UserAccount ua) {

        Map<Object, Object> contextMap = new HashMap<Object, Object>();
        if (expression.startsWith("#{")) {
            if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_INSTANCE) >= 0) {
                contextMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);
            }

            if (expression.indexOf(ValueExpressionWrapper.VAR_SUBSCRIPTION) >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put(ValueExpressionWrapper.VAR_SUBSCRIPTION, subscription);
            }
            if (expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put(ValueExpressionWrapper.VAR_OFFER, subscription.getOffer());
            }

            if (expression.indexOf(ValueExpressionWrapper.VAR_USER_ACCOUNT) >= 0) {
                if (ua == null) {
                    ua = subscription != null ? subscription.getUserAccount() : serviceInstance.getSubscription().getUserAccount();
                }

                contextMap.put(ValueExpressionWrapper.VAR_USER_ACCOUNT, ua);
            }

            if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }

                contextMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, ba);
            }

            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {

                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, ba.getCustomerAccount());
            }

            if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_SHORT) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER) >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER_SHORT, ba.getCustomerAccount().getCustomer());
                contextMap.put(ValueExpressionWrapper.VAR_CUSTOMER, ba.getCustomerAccount().getCustomer());
            }

            if (expression.indexOf(ValueExpressionWrapper.VAR_PROVIDER) >= 0) {
                contextMap.put(ValueExpressionWrapper.VAR_PROVIDER, appProvider);
            }
        }
        return contextMap;
    }

    /**
     * Compute the invoice amount for order.
     * 
     * @param order order
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate last transaction date
     * @return computed order's invoice amount.
     */
    private Amounts computeTotalOrderInvoiceAmount(Order order, Date firstTransactionDate, Date lastTransactionDate) {

//      boolean ignorePrepaidWallets = false;  TODO AKK if (prePaidWalletsIds != null && !prePaidWalletsIds.isEmpty()) {
        String query = "RatedTransaction.sumTotalInvoiceableByOrderNumber";
//      if (ignorePrepaidWallets) {
//          query = "RatedTransaction.sumTotalInvoiceableByOrderNumberExcludePrepaidWO";
//      }        

        Query q = getEntityManager().createNamedQuery(query).setParameter("orderNumber", order.getOrderNumber()).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate",
            lastTransactionDate);

//      if (ignorePrepaidWallets) {
//          q = q.setParameter("walletsIds", prePaidWalletsIds);
//      }        

        return (Amounts) q.getSingleResult();
    }

    /**
     * Get a list of invoiceable Rated transactions for a given billable entity and date range or from a filter
     * 
     * @param entityToInvoice Entity to invoice (subscription, billing account or order)
     * @param firstTransactionDate Usage date range - start date
     * @param lastTransactionDate Usage date range - end date
     * @param ratedTransactionFilter Filter returning a list of rated transactions
     * @param rtPageSize Number of records to return
     * @return A list of RT entities
     * @throws BusinessException General exception
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> listRTsToInvoice(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate, Filter ratedTransactionFilter, int rtPageSize) throws BusinessException {

        if (ratedTransactionFilter != null) {
            return (List<RatedTransaction>) filterService.filteredListAsObjects(ratedTransactionFilter, null);

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceBySubscription", RatedTransaction.class).setParameter("subscriptionId", entityToInvoice.getId())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();

        } else if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccount", RatedTransaction.class).setParameter("billingAccountId", entityToInvoice.getId())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByOrderNumber", RatedTransaction.class).setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();
        }

        return new ArrayList<>();
    }

    /**
     * Get a list of invoiceable Rated transactions for a given BllingAccount and a list of ids
     *
     * @param billingAccountId
     * @param ids
     *
     * @return A list of RT entities
     * @throws BusinessException General exception
     */
	public List<RatedTransaction> listByBillingAccountAndIDs(Long billingAccountId, Set<Long> ids) throws BusinessException {
		return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccountAndIDs", RatedTransaction.class)
				.setParameter("billingAccountId", billingAccountId).setParameter("listOfIds", ids).getResultList();
	}

    /**
     * Determine if minimum RT transactions functionality is used at service level
     * 
     * @return True if exists any serviceInstance with minimumAmountEl value
     */
    @Deprecated
    public boolean isServiceMinRTsUsed() {

        Boolean booleanValue = ParamBean.getInstance().getBooleanValue("billing.minimumRating.global.enabled");
        if(booleanValue!=null) {
            return booleanValue;
        }
         
        try {
            getEntityManager().createNamedQuery("ServiceInstance.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Determine if minimum RT transactions functionality is used at subscription level
     * 
     * @return True if exists any subscription with minimumAmountEl value
     */
    @Deprecated
    public boolean isSubscriptionMinRTsUsed() {
        
        Boolean booleanValue = ParamBean.getInstance().getBooleanValue("billing.minimumRating.global.enabled");
        if(booleanValue!=null) {
            return booleanValue;
        }

        try {
            getEntityManager().createNamedQuery("Subscription.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Determine if minimum RT transactions functionality is used at billing account level
     * 
     * @return True if exists any billing account with minimumAmountEl value
     */
    @Deprecated
    public boolean isBAMinRTsUsed() {
        
        Boolean booleanValue = ParamBean.getInstance().getBooleanValue("billing.minimumRating.global.enabled");
        if(booleanValue!=null) {
            return booleanValue;
        }

        try {
            getEntityManager().createNamedQuery("BillingAccount.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Determine if minimum RT transactions functionality is used at all. A check is done on serviceInstance, subscription or billing account entities for minimumAmountEl field
     * value presence.
     * 
     * @return An array of booleans indicating if minimum invoicing amount rule exists on service, subscription and billingAccount levels, in that particular order.
     */
    public boolean[] isMinRTsUsed() {
        
        Boolean booleanValue = ParamBean.getInstance().getBooleanValue("billing.minimumRating.global.enabled");
        if(booleanValue!=null) {
            return new boolean[] { booleanValue, booleanValue, booleanValue, booleanValue, booleanValue, booleanValue};
        }
        
        boolean baMin = false;
        boolean subMin = false;
        boolean servMin = false;
        boolean uaMin = false;
        boolean caMin = false;
        boolean custMin = false;

        EntityManager em = getEntityManager();

        try {
            em.createNamedQuery("BillingAccount.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            baMin = true;
        } catch (NoResultException e) {
        }
        try {
            em.createNamedQuery("UserAccount.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            uaMin = true;
        } catch (NoResultException e) {
        }
        try {
            em.createNamedQuery("Subscription.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            subMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("ServiceInstance.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            servMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("CustomerAccount.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            caMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("Customer.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            custMin = true;
        } catch (NoResultException e) {
        }
        return new boolean[] { servMin, subMin, uaMin, baMin, caMin, custMin };
    }

    /**
     * Determine if minimum RT transactions functionality is used at all accounts hierarchy. A check is done on serviceInstance, subscription, userAccount, billing account,
     * customer account and customer entities for minimumAmountEl field.
     *
     * @return the MinAmountForAccounts
     */
    public MinAmountForAccounts isMinAmountForAccountsActivated(IBillableEntity entity, ApplyMinimumModeEnum applyMinimumModeEnum) {
        return new MinAmountForAccounts(isMinRTsUsed(), entity, applyMinimumModeEnum);
    }

    /**
     * Determine if minimum RT transactions functionality is used at all accounts hierarchy. A check is done on serviceInstance, subscription, userAccount, billing account,
     * customer account and customer entities for minimumAmountEl field.
     *
     * @return the MinAmountForAccounts
     */
    public MinAmountForAccounts isMinAmountForAccountsActivated() {
        return new MinAmountForAccounts(isMinRTsUsed());
    }

    /**
     * Gets All open rated transaction between two date.
     *
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate a last transaction date
     * @param lastId a last id used for pagination
     * @param max a max result used for pagination
     * @return All open rated transaction between two date.
     */
    public List<RatedTransaction> getNotOpenedRatedTransactionBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, long lastId, int max) {
        return getEntityManager().createNamedQuery("RatedTransaction.listNotOpenedBetweenTwoDates", RatedTransaction.class).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).setParameter("lastId", lastId).setMaxResults(max).getResultList();

    }

    /**
     * Remove All not open rated transaction between two date.
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @return the number of deleted entities
     */
    public long purge(Date firstTransactionDate, Date lastTransactionDate) {
        return getEntityManager().createNamedQuery("RatedTransaction.deleteNotOpenBetweenTwoDates").setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
            .executeUpdate();
    }

    public void importRatedTransaction(List<RatedTransactionDto> ratedTransactions) throws BusinessException {
        for (RatedTransactionDto dto : ratedTransactions) {
            RatedTransaction ratedTransaction = new RatedTransaction();
            if (dto.getPriceplanCode() != null) {
                PricePlanMatrix pricePlan = pricePlanMatrixService.findByCode(dto.getPriceplanCode());
                ratedTransaction.setPriceplan(pricePlan);
            }
            if (dto.getTaxCode() != null) {
                Tax tax = taxService.findByCode(dto.getTaxCode());
                ratedTransaction.setTax(tax);
            }
            if (dto.getBillingAccountCode() != null) {
                BillingAccount billingAccount = billingAccountService.findByCode(dto.getBillingAccountCode());
                ratedTransaction.setBillingAccount(billingAccount);
            }
            if (dto.getSellerCode() != null) {
                Seller seller = sellerService.findByCode(dto.getSellerCode());
                ratedTransaction.setSeller(seller);
            }

            ratedTransaction.setType(RatedTransactionTypeEnum.MANUAL);
            ratedTransaction.setUsageDate(dto.getUsageDate());
            ratedTransaction.setUnitAmountWithoutTax(dto.getUnitAmountWithoutTax());
            ratedTransaction.setUnitAmountWithTax(dto.getUnitAmountWithTax());
            ratedTransaction.setUnitAmountTax(dto.getUnitAmountTax());
            ratedTransaction.setQuantity(dto.getQuantity());
            ratedTransaction.setAmountWithoutTax(dto.getAmountWithoutTax());
            ratedTransaction.setAmountWithTax(dto.getAmountWithTax());
            ratedTransaction.setAmountTax(dto.getAmountTax());
            ratedTransaction.setCode(dto.getCode());
            ratedTransaction.setDescription(dto.getDescription());
            ratedTransaction.setUnityDescription(dto.getUnityDescription());
            ratedTransaction.setDoNotTriggerInvoicing(dto.isDoNotTriggerInvoicing());
            ratedTransaction.setStartDate(dto.getStartDate());
            ratedTransaction.setEndDate(dto.getEndDate());
            ratedTransaction.setTaxPercent(dto.getTaxPercent());
            create(ratedTransaction);
        }
    }

    /**
     * Delete supplemental Rated transactions associated to an invoice. Includes Rated transactions created to reach a minimum invoicing amount or any other Rated transaction
     * created just before invoicing and relied on an overall data to bill.
     *
     * @param invoice Invoice
     */
    public void deleteSupplementalRTs(Invoice invoice) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteSupplementalRTByInvoice").setParameter("invoice", invoice).executeUpdate();
    }

    /**
     * Delete supplemental Rated transactions associated to a billing run. Includes Rated transactions created to reach a minimum invoicing amount or any other Rated transaction
     * created just before invoicing and relied on an overall data to bill.
     *
     * @param billingRun Billing run
     */
    public void deleteSupplementalRTs(BillingRun billingRun) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteSupplementalRTByBR").setParameter("billingRun", billingRun).executeUpdate();
    }

    /**
     * Mark open RTs associated to an invoice
     *
     * @param invoice Invoice
     */
    public void uninvoiceRTs(Invoice invoice) {
        getEntityManager().createNamedQuery("RatedTransaction.unInvoiceByInvoice").setParameter("invoice", invoice).setParameter("now", new Date()).executeUpdate();
    }

    /**
     * Mark open RTs associated to a billing run
     *
     * @param billingRun Billing run
     */
    public void uninvoiceRTs(BillingRun billingRun) {
        getEntityManager().createNamedQuery("RatedTransaction.unInvoiceByBR").setParameter("billingRun", billingRun).setParameter("now", new Date()).executeUpdate();
    }

    /**
     * Retrieve billed rated transactions associated to an invoice
     * 
     * @param invoice Invoice
     * @return A list of rated transactions
     */
    public List<RatedTransaction> getRatedTransactionsByInvoice(Invoice invoice, boolean includeFree) {
        if (invoice.getId() == null) {
            return new ArrayList<>();
        }

        if (includeFree) {
            return getEntityManager().createNamedQuery("RatedTransaction.listByInvoice", RatedTransaction.class).setParameter("invoice", invoice).getResultList();
        } else {
            return getEntityManager().createNamedQuery("RatedTransaction.listByInvoiceNotFree", RatedTransaction.class).setParameter("invoice", invoice).getResultList();
        }
    }
    
    /**
     * Retrieve all rated transactions associated to an invoice
     * 
     * @param invoice Invoice
     * @return A list of rated transactions
     */
    public List<RatedTransaction> listRatedTransactionsByInvoice(Invoice invoice) {
        if (invoice.getId() == null) {
            return new ArrayList<>();
        }
        return getEntityManager().createNamedQuery("RatedTransaction.listAllByInvoice", RatedTransaction.class).setParameter("invoice", invoice).getResultList();
    }

    /**
     * Retrieve rated transactions associated to an invoice aggregate
     * 
     * @param subCategoryInvoiceAgregate Invoice
     * @return A list of rated transactions
     */
    public List<RatedTransaction> getRatedTransactionsByInvoiceAggr(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {

        if (subCategoryInvoiceAgregate.getId() == null) {
            return new ArrayList<>();
        }

        return getEntityManager().createNamedQuery("RatedTransaction.listByInvoiceSubCategoryAggr", RatedTransaction.class).setParameter("invoice", subCategoryInvoiceAgregate.getInvoice())
            .setParameter("invoiceAgregateF", subCategoryInvoiceAgregate).getResultList();
    }

    /**
     * @param firstDate
     * @param lastDate
     * @param lastId
     * @param maxResult
     * @param formattedStatus
     * @return
     */
    public List<RatedTransaction> getRatedTransactionBetweenTwoDatesByStatus(Date firstDate, Date lastDate, long lastId, int maxResult, List<RatedTransactionStatusEnum> formattedStatus) {
        return getEntityManager().createNamedQuery("RatedTransaction.listBetweenTwoDatesByStatus", RatedTransaction.class).setParameter("firstTransactionDate", firstDate).setParameter("lastTransactionDate", lastDate)
            .setParameter("lastId", lastId).setParameter("status", formattedStatus).setMaxResults(maxResult).getResultList();
    }
    
    public long purge(Date lastTransactionDate, List<RatedTransactionStatusEnum> targetStatusList) {
        return getEntityManager().createNamedQuery("RatedTransaction.deleteByLastTransactionDateAndStatus")
                .setParameter("status", targetStatusList)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .executeUpdate();
    }

    public long purge(Date firstTransactionDate, Date lastTransactionDate, List<RatedTransactionStatusEnum> targetStatusList) {
        return getEntityManager().createNamedQuery("RatedTransaction.deleteBetweenTwoDatesByStatus").setParameter("status", targetStatusList).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
    }
    
    /**
     * Detach RTs From subscription.
     *
     * @param subscription subscription
     */
    public void detachRTsFromSubscription(Subscription subscription) {
        getEntityManager().createNamedQuery("RatedTransaction.detachRTsFromSubscription").setParameter("subscription", subscription).executeUpdate();
    }

    /**
     * Retrun the total of positive rated transaction grouped by billing account for a billing run.
     *
     * @param billingRun the billing run
     * @return a map of positive rated transaction grouped by billing account.
     */
    public List<Object[]> getTotalPositiveRTAmountsByBR(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("RatedTransaction.sumPositiveRTByBillingRun").setParameter("billingRunId", billingRun.getId()).getResultList();
    }

    /**
     * Uninvoice RT by a list of invoices Ids.
     *
     * @param invoicesIds invoices Ids
     */
    public void uninvoiceRTs(Collection<Long> invoicesIds) {
        getEntityManager().createNamedQuery("RatedTransaction.unInvoiceByInvoiceIds").setParameter("now", new Date()).setParameter("invoiceIds", invoicesIds).executeUpdate();

    }

    public void deleteSupplementalRTs(Collection<Long> invoicesIds) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteSupplementalRTByInvoiceIds").setParameter("invoicesIds", invoicesIds).executeUpdate();

    }

	/**
	 * invalidate RTs related to an invoice
	 * @param invoice
	 */
	public void invalidateRTs(Invoice invoice) {
		getEntityManager().createNamedQuery("RatedTransaction.invalidateRTByInvoice").setParameter("invoice", invoice).executeUpdate();
	}
}