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

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.ApplyMinimumModeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CreateMinAmountsResult;
import org.meveo.model.billing.ExtraMinAmount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.MinAmountData;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionMinAmountTypeEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Customer;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.order.OrderService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

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

    /**
     * Check if Billing account has any not yet billed Rated transactions
     *
     * @param billingAccount       billing account
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate  date of last transaction
     * @return true/false
     */
    public Boolean isBillingAccountBillable(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {
        long count = 0;
        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }
        TypedQuery<Long> q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedOpenByBA", Long.class);
        count = q.setParameter("billingAccount", billingAccount).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
                .getSingleResult();
        log.debug("isBillingAccountBillable code={},lastTransactionDate={}) : {}", billingAccount.getCode(), lastTransactionDate, count);
        return count > 0 ? true : false;
    }

    /**
     * @param invoice            invoice
     * @param invoiceSubCategory sub category invoice
     * @return list of rated transaction
     */
    public List<RatedTransaction> getListByInvoiceAndSubCategory(Invoice invoice, InvoiceSubCategory invoiceSubCategory) {
        if ((invoice == null) || (invoiceSubCategory == null)) {
            return null;
        }
        return getEntityManager().createNamedQuery("RatedTransaction.getListByInvoiceAndSubCategory", RatedTransaction.class).setParameter("invoice", invoice)
                .setParameter("invoiceSubCategory", invoiceSubCategory).getResultList();
    }

    /**
     * Convert Wallet operations to Rated transactions for a given billable entity up to a given date
     *
     * @param entityToInvoice Entity to invoice
     * @param invoicingDate   Invoicing date
     * @throws BusinessException General business exception.
     */
    public void createRatedTransaction(IBillableEntity entityToInvoice, Date invoicingDate) throws BusinessException {
        List<WalletOperation> walletOps = walletOperationService.listToRate(entityToInvoice, invoicingDate);

        for (WalletOperation walletOp : walletOps) {
            createRatedTransaction(walletOp, false);
        }
    }

    /**
     * Create Rated transaction from wallet operation.
     *
     * @param walletOperation Wallet operation
     * @param isVirtual       Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return Rated transaction
     * @throws BusinessException business exception
     */
    public RatedTransaction createRatedTransaction(WalletOperation walletOperation, boolean isVirtual) throws BusinessException {

        RatedTransaction ratedTransaction = new RatedTransaction(walletOperation);
        walletOperation.changeStatus(WalletOperationStatusEnum.TREATED);

        if (!isVirtual) {
            create(ratedTransaction);
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
    public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo, RatedTransactionsJobAggregationSetting aggregatedSettings, Date invoicingDate)
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
    public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo, RatedTransactionsJobAggregationSetting aggregationSettings, Date invoicingDate,
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
        if (aggregationSettings.isAggregateByDay()) {
            cal.set(Calendar.YEAR, aggregatedWo.getYear(), aggregatedWo.getMonth(), aggregatedWo.getDay(), 0, 0);
            ratedTransaction.setUsageDate(cal.getTime());
        } else {
            cal.set(Calendar.YEAR, aggregatedWo.getYear(), aggregatedWo.getMonth(), 1, 0, 0);
            ratedTransaction.setUsageDate(cal.getTime());

        }

        isc = invoiceSubCategoryService.refreshOrRetrieve(aggregatedWo.getInvoiceSubCategory());

        switch (aggregationSettings.getAggregationLevel()) {
        case BA:
            ba = billingAccountService.findById(aggregatedWo.getIdAsLong());
            seller = ba.getCustomerAccount().getCustomer().getSeller();
            code = isc.getCode();
            description = isc.getDescription();
            break;

        case UA:
            ua = userAccountService.findById(aggregatedWo.getIdAsLong());
            ba = ua.getBillingAccount();
            seller = ba.getCustomerAccount().getCustomer().getSeller();
            code = isc.getCode();
            description = isc.getDescription();
            break;

        case SUB:
            sub = subscriptionService.findById(aggregatedWo.getIdAsLong());
            ua = sub.getUserAccount();
            ba = ua.getBillingAccount();
            seller = sub.getSeller();
            code = isc.getCode();
            description = isc.getDescription();
            break;

        case SI:
            si = serviceInstanceService.findById(aggregatedWo.getIdAsLong());
            sub = si.getSubscription();
            ua = sub.getUserAccount();
            ba = ua.getBillingAccount();
            seller = sub.getSeller();
            code = si.getCode();
            description = si.getDescription();
            break;

        case CI:
            ci = (ChargeInstance) chargeInstanceService.findById(aggregatedWo.getIdAsLong());
            sub = ci.getSubscription();
            ua = sub.getUserAccount();
            ba = ua.getBillingAccount();
            seller = sub.getSeller();
            code = ci.getCode();
            description = ci.getDescription();
            break;

        case DESC:
            ci = (ChargeInstance) chargeInstanceService.findById(aggregatedWo.getIdAsLong());
            sub = ci.getSubscription();
            ua = sub.getUserAccount();
            ba = ua.getBillingAccount();
            seller = sub.getSeller();
            code = ci.getCode();
            description = aggregatedWo.getComputedDescription();
            break;

        default:
            ba = billingAccountService.findById(aggregatedWo.getIdAsLong());
            seller = ba.getCustomerAccount().getCustomer().getSeller();
        }

        if (aggregationSettings.isAggregateByOrder()) {
            ratedTransaction.setOrderNumber(aggregatedWo.getOrderNumber());
        }
        if (aggregationSettings.isAggregateByParam1()) {
            ratedTransaction.setParameter1(aggregatedWo.getParameter1());
        }
        if (aggregationSettings.isAggregateByParam2()) {
            ratedTransaction.setParameter2(aggregatedWo.getParameter2());
        }
        if (aggregationSettings.isAggregateByParam3()) {
            ratedTransaction.setParameter3(aggregatedWo.getParameter3());
        }
        if (aggregationSettings.isAggregateByExtraParam()) {
            ratedTransaction.setParameterExtra(aggregatedWo.getParameterExtra());
        }

        Tax tax = taxService.refreshOrRetrieve(aggregatedWo.getTax());

        ratedTransaction.setCode(code);
        ratedTransaction.setDescription(description);
        ratedTransaction.setTax(tax);
        ratedTransaction.setTaxPercent(tax.getPercent());
        ratedTransaction.setInvoiceSubCategory(isc);
        ratedTransaction.setSeller(seller);
        ratedTransaction.setBillingAccount(ba);
        ratedTransaction.setUserAccount(ua);
        ratedTransaction.setSubscription(sub);
        ratedTransaction.setChargeInstance(ci);
        ratedTransaction.setAmountWithTax(aggregatedWo.getAmountWithTax());
        ratedTransaction.setAmountTax(aggregatedWo.getAmountTax());
        ratedTransaction.setAmountWithoutTax(aggregatedWo.getAmountWithoutTax());
        ratedTransaction.setUnitAmountWithTax(aggregatedWo.getUnitAmountWithTax());
        ratedTransaction.setUnitAmountTax(aggregatedWo.getUnitAmountTax());
        ratedTransaction.setUnitAmountWithoutTax(aggregatedWo.getUnitAmountWithoutTax());
        ratedTransaction.setQuantity(aggregatedWo.getQuantity());

        if (!isVirtual) {
            create(ratedTransaction);

            WalletOperationAggregatorQueryBuilder woa = new WalletOperationAggregatorQueryBuilder(aggregationSettings);
            String strQuery = woa.listWoQuery(aggregatedWo.getIdAsLong());
            Query query = getEntityManager().createQuery(strQuery);
            query.setParameter("invoicingDate", invoicingDate);
            List<WalletOperation> walletOps = (List<WalletOperation>) query.getResultList();

            for (WalletOperation tempWo : walletOps) {
                tempWo.changeStatus(WalletOperationStatusEnum.TREATED);
            }
        }

        return ratedTransaction;
    }

    /**
     * List unprocessed Rated transactions from a given wallet instance (user account) and invoice subcategory
     *
     * @param walletInstance     Wallet instance
     * @param invoiceSubCategory Invoice sub category. Optional.
     * @param from               Date range - from. Optional.
     * @param to                 Date range - to. Optional.
     * @return A list of rated transactions
     */
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
            return (RatedTransaction) getEntityManager().createNamedQuery("RatedTransaction.findByWalletOperationId").setParameter("walletOperationId", walletOperationId)
                    .getSingleResult();

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
     * @param entity                           Entity to invoice
     * @param billingRun                       the billing run
     * @param instantiateMinRtsForService      Should rated transactions to reach minimum invoicing amount be checked and instantiated on service level.
     * @param instantiateMinRtsForSubscription Should rated transactions to reach minimum invoicing amount be checked and instantiated on subscription level.
     * @param instantiateMinRtsForBA           Should rated transactions to reach minimum invoicing amount be checked and instantiated on Billing account level.
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
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null ?
                        (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount() :
                        null;
            }
        }
        //MinAmountForAccounts minAmountForAccounts = new MinAmountForAccounts(instantiateMinRtsForBA, false, instantiateMinRtsForSubscription, instantiateMinRtsForService);
        calculateAmountsAndCreateMinAmountTransactions(entity, null, billingRun.getLastTransactionDate(), true, minAmountForAccounts);

        BigDecimal invoiceAmount = entity.getTotalInvoicingAmountWithoutTax();
        if (invoiceAmount != null) {
            BigDecimal invoicingThreshold = null;
            if (billingAccount != null) {
                invoicingThreshold = billingAccount.getInvoicingThreshold();
            }
            if ((invoicingThreshold == null) && (billingRun.getBillingCycle() != null)) {
                invoicingThreshold = billingRun.getBillingCycle().getInvoicingThreshold();
            }

            if (invoicingThreshold != null && invoicingThreshold.compareTo(invoiceAmount) > 0) {
                log.debug("Invoicing threshold {}/{} was not met for {}. Entity will not be invoiced", invoiceAmount, invoicingThreshold, entity.getClass().getSimpleName(),
                        entity.getCode());
                return null;
            }

            log.debug("{}/{} will be updated with BR amount {}. Invoice threshold applied {}", entity.getClass().getSimpleName(), entity.getId(), invoiceAmount,
                    invoicingThreshold);
        }

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
     * @param entityId     ID of an entity to invoice
     * @param billingRun   The billing run
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
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null ?
                        (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount() :
                        null;
            }
            break;
        }
        entity.setTotalInvoicingAmountWithoutTax(totalAmounts.getAmountWithoutTax());
        entity.setTotalInvoicingAmountWithTax(totalAmounts.getAmountWithTax());
        entity.setTotalInvoicingAmountTax(totalAmounts.getAmountTax());

        BigDecimal invoiceAmount = totalAmounts.getAmountWithoutTax();
        BigDecimal invoicingThreshold = null;
        if (billingAccount != null) {
            invoicingThreshold = billingAccount.getInvoicingThreshold();
        }
        if (invoicingThreshold == null) {
            invoicingThreshold = billingRun.getBillingCycle().getInvoicingThreshold();
        }

        if (invoicingThreshold != null && invoicingThreshold.compareTo(invoiceAmount) > 0) {
            log.debug("Invoicing threshold {}/{} was not met for {}. Entity will not be invoiced", invoiceAmount, invoicingThreshold, entity.getClass().getSimpleName(),
                    entity.getCode());
            return null;
        }

        log.debug("{}/{} will be updated with BR amount {}. Invoice threshold applied {}", entity.getClass().getSimpleName(), entity.getId(), invoiceAmount, invoicingThreshold);

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
     * @param billableEntity                 The billable entity
     * @param lastTransactionDate            Last transaction date
     * @param firstTransactionDate           first transaction Date
     * @param calculateAndUpdateTotalAmounts Should total amounts be calculated and entity updated with those amounts
     * @param minAmountForAccounts           Booleans to knows if an accounts has minimum amount activated
     * @throws BusinessException General business exception
     */
    public void calculateAmountsAndCreateMinAmountTransactions(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate,
            boolean calculateAndUpdateTotalAmounts, MinAmountForAccounts minAmountForAccounts) throws BusinessException {

        Amounts totalInvoiceableAmounts = null;

        List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();
        List<ExtraMinAmount> extraMinAmounts = new ArrayList<>();

        Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

        if (billableEntity instanceof Order && calculateAndUpdateTotalAmounts) {
            totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);

        } else {
            //Create Min Amount RTs for hierarchy

            BillingAccount billingAccount = (billableEntity instanceof Subscription) ?
                    ((Subscription) billableEntity).getUserAccount().getBillingAccount() :
                    (BillingAccount) billableEntity;

            Class[] accountClasses = new Class[] { ServiceInstance.class, Subscription.class, UserAccount.class, BillingAccount.class, CustomerAccount.class, Customer.class };
            for (Class accountClass : accountClasses) {
                if (minAmountForAccounts.isMinAmountForAccountsActivated(accountClass, billableEntity)) {
                    CreateMinAmountsResult createMinAmountsResults = createMinRTForAccount(billableEntity, billingAccount, lastTransactionDate, minRatingDate, extraMinAmounts,
                            accountClass);
                    extraMinAmounts = createMinAmountsResults.getExtraMinAmounts();
                    minAmountTransactions.addAll(createMinAmountsResults.getMinAmountTransactions());
                }
            }
            //get totalInvoicable for the billableEntity
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
     * Create Rated transactions to reach minimum invoiced amount per service level. Only those services that have minimum invoice amount rule are considered. Updates
     * minAmountTransactions parameter.
     *
     * @param billableEntity      Entity to bill - entity for which minimum rated transactions should be created
     * @param billingAccount      Billing account to associate new minimum amount Rated transactions with
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate       Date to assign to newly created minimum amount Rated transactions
     * @return CreateMinAmountsResult Contains createMinRTForServices result
     * @throws BusinessException General business exception
     */
    private CreateMinAmountsResult createMinRTForServices(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate)
            throws BusinessException {

        EntityManager em = getEntityManager();

        CreateMinAmountsResult createMinAmountsResult = new CreateMinAmountsResult();

        // Service id as a key and array of <min amount>, <min amount label>, <total amounts>, map of <Invoice subCategory id, amounts], serviceInstance>
        Map<Long, Object[]> serviceInstanceToMinAmount = new HashMap<>();

        Subscription billingSubscription = null;
        if (billableEntity instanceof Subscription) {
            billingSubscription = (Subscription) billableEntity;
        }

        // Only interested in services with minAmount condition
        // Calculate amounts on service level grouped by invoice category and service instance
        // Calculate a total sum of amounts on service level
        List<Object[]> amountsList = computeInvoiceableAmountForServicesWithMinAmountRule(billableEntity, new Date(0), lastTransactionDate);

        for (Object[] amounts : amountsList) {
            BigDecimal invSubcategoryAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal invSubcategoryAmountWithTax = (BigDecimal) amounts[1];

            Long invSubCategoryId = (Long) amounts[2];
            ServiceInstance serviceInstance = em.find(ServiceInstance.class, amounts[3]);

            Object[] minAmountInfo = serviceInstanceToMinAmount.get(serviceInstance.getId());

            if (minAmountInfo == null) {
                String minAmountEL = serviceInstance.getMinimumAmountEl();
                String minAmountLabelEL = serviceInstance.getMinimumLabelEl();
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, null, null, serviceInstance);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, null, serviceInstance);
                Amounts serviceAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                Map<Long, Amounts> amountMap = new HashMap<Long, Amounts>();
                amountMap.put(invSubCategoryId, serviceAmounts);

                serviceInstanceToMinAmount.put(serviceInstance.getId(), new Object[] { minAmount, minAmountLabel, serviceAmounts, amountMap, serviceInstance });
            } else {
                ((Amounts) minAmountInfo[2]).addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                ((Map<Long, Amounts>) minAmountInfo[3]).put(invSubCategoryId, new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null));
            }
        }

        List<BusinessEntity> servicesWithMinAmount = getServicesWithMinAmount(billableEntity);
        for (BusinessEntity entity : servicesWithMinAmount) {
            ServiceInstance serviceWithMinAmount = (ServiceInstance) entity;
            Object[] minAmountInfo = serviceInstanceToMinAmount.get(serviceWithMinAmount.getId());
            if (minAmountInfo == null) {
                BigDecimal invSubcategoryAmountWithoutTax = BigDecimal.ZERO;
                BigDecimal invSubcategoryAmountWithTax = BigDecimal.ZERO;
                String minAmountEL = serviceWithMinAmount.getMinimumAmountEl();
                String minAmountLabelEL = serviceWithMinAmount.getMinimumLabelEl();
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, null, null, serviceWithMinAmount);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, null, serviceWithMinAmount);

                Amounts serviceAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                Map<Long, Amounts> amountMap = new HashMap<Long, Amounts>();
                InvoiceSubCategory invoiceSubCategory = serviceWithMinAmount.getMinimumInvoiceSubCategory();
                if (invoiceSubCategory != null) {
                    amountMap.put(invoiceSubCategory.getId(), serviceAmounts);
                    serviceInstanceToMinAmount.put(serviceWithMinAmount.getId(), new Object[] { minAmount, minAmountLabel, serviceAmounts, amountMap, serviceWithMinAmount });
                } else {
                    throw new BusinessException("minAmountInvoiceSubCategory not defined for service code=" + serviceWithMinAmount.getCode());
                }
            } else {
                // Service amount exceed the minimum amount per service
                if (((BigDecimal) minAmountInfo[0])
                        .compareTo(appProvider.isEntreprise() ? ((Amounts) minAmountInfo[2]).getAmountWithoutTax() : ((Amounts) minAmountInfo[2]).getAmountWithTax()) <= 0) {
                    serviceInstanceToMinAmount.put(serviceWithMinAmount.getId(), null);
                    continue;
                }
            }
        }

        // Create Rated transactions to reach a minimum amount per service
        Map<Long, Map<String, Amounts>> minRTAmountMap = new HashMap<>();
        List<ExtraMinAmount> extraMinAmounts = new ArrayList<>();

        for (Entry<Long, Object[]> serviceAmounts : serviceInstanceToMinAmount.entrySet()) {

            if (serviceAmounts.getValue() == null) {
                continue;
            }
            BigDecimal minAmount = (BigDecimal) serviceAmounts.getValue()[0];
            String minAmountLabel = (String) serviceAmounts.getValue()[1];
            BigDecimal totalServiceAmount = appProvider.isEntreprise() ?
                    ((Amounts) serviceAmounts.getValue()[2]).getAmountWithoutTax() :
                    ((Amounts) serviceAmounts.getValue()[2]).getAmountWithTax();
            ServiceInstance serviceInstance = (ServiceInstance) serviceAmounts.getValue()[4];

            Subscription subscription = serviceInstance.getSubscription();
            Seller seller = subscription.getSeller();
            String mapKeyPrefix = seller.getId().toString() + "_";

            BigDecimal totalRatio = BigDecimal.ZERO;
            Iterator<Entry<Long, Amounts>> amountIterator = ((Map<Long, Amounts>) serviceAmounts.getValue()[3]).entrySet().iterator();

            Map<String, Amounts> minRTAmountSubscriptionMap = new HashMap<>();
            minRTAmountMap.put(subscription.getId(), minRTAmountSubscriptionMap);

            BigDecimal diff = minAmount.subtract(totalServiceAmount);

            while (amountIterator.hasNext()) {
                Entry<Long, Amounts> amountsEntry = amountIterator.next();

                Long invoiceSubCategoryId = amountsEntry.getKey();
                if (invoiceSubCategoryId == null) {
                    invoiceSubCategoryId = -1l;
                }

                String mapKey = mapKeyPrefix + invoiceSubCategoryId;
                InvoiceSubCategory invoiceSubCategory = em.getReference(InvoiceSubCategory.class, invoiceSubCategoryId);
                Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

                BigDecimal invSubcategoryAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

                BigDecimal ratio =
                        totalServiceAmount.compareTo(invSubcategoryAmount) == 0 ? BigDecimal.ONE : invSubcategoryAmount.divide(totalServiceAmount, 4, RoundingMode.HALF_UP);

                // Ensure that all ratios sum up to 1
                if (!amountIterator.hasNext()) {
                    ratio = BigDecimal.ONE.subtract(totalRatio);
                }

                BigDecimal rtMinAmount = diff.multiply(ratio);

                BigDecimal[] unitAmounts = NumberUtils
                        .computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                        appProvider.getRoundingMode().getRoundingMode());

                RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1],
                        amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, billingSubscription, null, null,
                        null, null, null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode() + "_" + serviceInstance.getCode(), minAmountLabel, null, null, seller, tax,
                        tax.getPercent(), serviceInstance);

                createMinAmountsResult.addMinAmountRT(ratedTransaction);

                // Remember newly "created" transaction amounts, as they are not persisted yet to DB
                if (minRTAmountSubscriptionMap.containsKey(mapKey)) {
                    minRTAmountSubscriptionMap.get(mapKey).addAmounts(new Amounts(amounts[0], amounts[1], amounts[2]));
                } else {
                    minRTAmountSubscriptionMap.put(mapKey, new Amounts(amounts[0], amounts[1], amounts[2]));
                }
                extraMinAmounts.add(new ExtraMinAmount(serviceInstance, minRTAmountSubscriptionMap));
                totalRatio = totalRatio.add(ratio);
            }
        }

        createMinAmountsResult.setCreatedAmountServices(minRTAmountMap);
        createMinAmountsResult.setExtraMinAmounts(extraMinAmounts);
        return createMinAmountsResult;
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per subscription level. Only those subscriptions that have minimum invoice amount rule are considered. Updates
     * minAmountTransactions parameter.
     *
     * @param billableEntity      Entity to bill - entity for which minimum rated transactions should be created
     * @param billingAccount      Billing account to associate new minimum amount Rated transactions with
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate       Date to assign to newly created minimum amount Rated transactions
     * @param extraMinAmounts     Additional Rated transaction amounts created to reach minimum invoicing amount per service. A map of amounts created with subscription id
     *                            as a main key and a secondary map of "&lt;seller.id&gt;_&lt;invoiceSubCategory.id&gt; as a key a and amounts as values" as a value
     * @param accountClass        the account class
     * @return CreateMinAmountsResult Contains createMinRTForAccount result
     * @throws BusinessException General Business exception
     */
    private CreateMinAmountsResult createMinRTForAccount(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate,
            List<ExtraMinAmount> extraMinAmounts, Class accountClass) throws BusinessException {

        CreateMinAmountsResult createMinAmountsResult = new CreateMinAmountsResult();

        EntityManager em = getEntityManager();

        // Subscription id as a key and array of <min amount>, <min amount label>, <total amounts>, map of <Invoice subCategory id, amounts], subscription>
        Map<Long, MinAmountData> accountToMinAmount = new HashMap<>();

        // Only interested in subscriptions with minAmount condition
        // Calculate amounts on subscription level grouped by invoice category and subscription
        // Calculate a total sum of amounts on subscription level
        List<Object[]> amountsList = computeInvoiceableAmountForAccount(billableEntity, new Date(0), lastTransactionDate, accountClass);

        for (Object[] amounts : amountsList) {
            BigDecimal invSubcategoryAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal invSubcategoryAmountWithTax = (BigDecimal) amounts[1];
            Long invSubCategoryId = (Long) amounts[2];
            BusinessEntity entity = (BusinessEntity) em.find(accountClass, amounts[3]);
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
                MinAmountData minAmountData = new MinAmountData(minAmount, minAmountLabel, new Amounts(), new HashMap<Long, Amounts>(), entity, seller);
                accountToMinAmount.put(entity.getId(), minAmountData);

                if (extraMinAmounts != null) {
                    accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                }
            }

            minAmountDataInfo = accountToMinAmount.get(entity.getId());
            minAmountDataInfo.getAmounts().addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
            Amounts subCatAmounts = minAmountDataInfo.getInvoiceSubCategoryAmounts().get(invSubCategoryId);
            if (subCatAmounts == null) {
                subCatAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                minAmountDataInfo.getInvoiceSubCategoryAmounts().put(invSubCategoryId, subCatAmounts);
            } else {
                subCatAmounts.addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
            }
        }

        List<BusinessEntity> accountsWithMinAmount = new ArrayList<>();

        accountsWithMinAmount = getAccountsWithMinAmountElNotNull(billableEntity, accountClass);

        for (BusinessEntity entity : accountsWithMinAmount) {
            MinAmountData minAmountInfo = accountToMinAmount.get(entity.getId());
            if (minAmountInfo == null) {
                BigDecimal invSubcategoryAmountWithoutTax = BigDecimal.ZERO;
                BigDecimal invSubcategoryAmountWithTax = BigDecimal.ZERO;
                String minAmountEL = getMinimumAmountElInfo(entity, "getMinimumAmountEl");
                String minAmountLabelEL = getMinimumAmountElInfo(entity, "getMinimumLabelEl");
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, entity);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, entity);

                Amounts accountAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                Map<Long, Amounts> amountMap = new HashMap<Long, Amounts>();
                InvoiceSubCategory invoiceSubCategory = getMinimumInvoiceSubCategory(billingAccount, entity);
                if (invoiceSubCategory != null) {
                    amountMap.put(invoiceSubCategory.getId(), accountAmounts);
                    accountToMinAmount
                            .put(entity.getId(), new MinAmountData(minAmount, minAmountLabel, accountAmounts.clone(), amountMap, entity, getSeller(billingAccount, entity)));

                    if (extraMinAmounts != null) {
                        accountToMinAmount = appendExtraAmount(extraMinAmounts, accountToMinAmount, entity);
                    }

                } else {
                    throw new BusinessException("minAmountInvoiceSubCategory not defined for " + accountClass.getSimpleName() + " code=" + entity.getCode());
                }
            } else {
                // Service amount exceed the minimum amount per service
                if ((minAmountInfo.getMinAmount())
                        .compareTo(appProvider.isEntreprise() ? minAmountInfo.getAmounts().getAmountWithoutTax() : minAmountInfo.getAmounts().getAmountWithTax()) <= 0) {
                    accountToMinAmount.put(entity.getId(), null);
                    continue;
                }
            }
        }

        // Create Rated transactions to reach a minimum amount per subscription

        for (Entry<Long, MinAmountData> accountAmounts : accountToMinAmount.entrySet()) {
            Map<String, Amounts> minRTAmountMap = new HashMap<>();

            if (accountAmounts.getValue() == null || accountAmounts.getValue().getMinAmount() == null) {
                continue;
            }

            BigDecimal minAmount = accountAmounts.getValue().getMinAmount();
            String minAmountLabel = accountAmounts.getValue().getMinAmountLabel();
            BigDecimal totalAccountAmount = appProvider.isEntreprise() ?
                    accountAmounts.getValue().getAmounts().getAmountWithoutTax() :
                    accountAmounts.getValue().getAmounts().getAmountWithTax();
            BusinessEntity entity = accountAmounts.getValue().getEntity();

            Seller seller = accountAmounts.getValue().getSeller();
            if (seller == null) {
                throw new BusinessException("Default Seller mandatory for invoice minimum (Customer.seller)");
            }
            String mapKeyPrefix = seller.getId().toString() + "_";

            BigDecimal totalRatio = BigDecimal.ZERO;
            Iterator<Entry<Long, Amounts>> amountIterator = accountAmounts.getValue().getInvoiceSubCategoryAmounts().entrySet().iterator();

            BigDecimal diff = minAmount.subtract(totalAccountAmount);
            if (diff.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            while (amountIterator.hasNext()) {
                Entry<Long, Amounts> amountsEntry = amountIterator.next();

                Long invoiceSubCategoryId = amountsEntry.getKey();
                String mapKey = mapKeyPrefix + invoiceSubCategoryId;

                InvoiceSubCategory invoiceSubCategory = em.getReference(InvoiceSubCategory.class, invoiceSubCategoryId);
                Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

                BigDecimal invSubcategoryAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

                BigDecimal ratio =
                        totalAccountAmount.compareTo(invSubcategoryAmount) == 0 ? BigDecimal.ONE : invSubcategoryAmount.divide(totalAccountAmount, 4, RoundingMode.HALF_UP);

                // Ensure that all ratios sum up to 1
                if (!amountIterator.hasNext()) {
                    ratio = BigDecimal.ONE.subtract(totalRatio);
                }

                BigDecimal rtMinAmount = diff.multiply(ratio);
                String code = getMinAmountRTCode(entity, accountClass);
                RatedTransaction ratedTransaction = getNewRatedTransaction(billableEntity, billingAccount, minRatingDate, minAmountLabel, entity, seller, invoiceSubCategory, tax,
                        rtMinAmount, code);

                createMinAmountsResult.addMinAmountRT(ratedTransaction);

                // Remember newly "created" transaction amounts, as they are not persisted yet to DB
                minRTAmountMap.put(mapKey, new Amounts(ratedTransaction.getUnitAmountWithoutTax(), ratedTransaction.getAmountWithTax(), ratedTransaction.getAmountTax()));
                extraMinAmounts.add(new ExtraMinAmount(entity, minRTAmountMap));

                totalRatio = totalRatio.add(ratio);
            }
        }

        //createMinAmountsResult.setCreatedAmountSubscription(minRTAmountMap);
        createMinAmountsResult.setExtraMinAmounts(extraMinAmounts);
        return createMinAmountsResult;
    }

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

    private List<Object[]> computeInvoiceableAmountForAccount(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, Class accountClass) {
        if (accountClass.equals(ServiceInstance.class)) {
            return computeInvoiceableAmountForServicesWithMinAmountRule(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(Subscription.class)) {
            return computeInvoiceableAmountForSubscriptions(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(UserAccount.class)) {
            return computeInvoiceableAmountForUserAccounts(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return computeInvoiceableAmountForBillingAccount(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return computeInvoiceableAmountForCustomerAccount(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        if (accountClass.equals(Customer.class)) {
            return computeInvoiceableAmountForCustomer(billableEntity, firstTransactionDate, lastTransactionDate);
        }
        return null;
    }

    private List<BusinessEntity> getAccountsWithMinAmountElNotNull(IBillableEntity billableEntity, Class<? extends BusinessEntity> accountClass) {

        if (accountClass.equals(ServiceInstance.class)) {
            return getServicesWithMinAmount(billableEntity);
        }
        if (accountClass.equals(Subscription.class)) {
            return getSubscriptionsWithMinAmount(billableEntity);
        }
        if (accountClass.equals(UserAccount.class)) {
            return getUserAccountsWithMinAmountELNotNullByBA(billableEntity);
        }
        if (accountClass.equals(BillingAccount.class)) {
            return getBillingAccountsWithMinAmountELNotNullByBA(billableEntity);
        }
        if (accountClass.equals(CustomerAccount.class)) {
            return getCustomerAccountsWithMinAmountELNotNullByBA(billableEntity);
        }
        if (accountClass.equals(Customer.class)) {
            return getCustomersWithMinAmountELNotNullByBA(billableEntity);
        }

        return new ArrayList<>();
    }

    private List<BusinessEntity> getBillingAccountsWithMinAmountELNotNullByBA(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }

        Query q = getEntityManager().createNamedQuery("BillingAccount.getBillingAccountsWithMinAmountELNotNullByBA").setParameter("billingAccount", billableEntity);
        return q.getResultList();
    }

    private List<BusinessEntity> getCustomerAccountsWithMinAmountELNotNullByBA(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        Query q = getEntityManager().createNamedQuery("CustomerAccount.getCustomerAccountsWithMinAmountELNotNullByBA")
                .setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount());
        return q.getResultList();
    }

    private List<BusinessEntity> getCustomersWithMinAmountELNotNullByBA(IBillableEntity billableEntity) {
        if (billableEntity instanceof Subscription) {
            billableEntity = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }
        Query q = getEntityManager().createNamedQuery("Customer.getCustomersWithMinAmountELNotNullByBA")
                .setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer());
        return q.getResultList();
    }

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

    private RatedTransaction getNewRatedTransaction(IBillableEntity billableEntity, BillingAccount billingAccount, Date minRatingDate, String minAmountLabel, BusinessEntity entity,
            Seller seller, InvoiceSubCategory invoiceSubCategory, Tax tax, BigDecimal rtMinAmount, String code) {
        BigDecimal[] unitAmounts = NumberUtils
                .computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());
        RatedTransaction rt = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1], amounts[2],
                RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, null, null, null, null, null, null, code,
                minAmountLabel, null, null, seller, tax, tax.getPercent(), null);
        if (entity instanceof ServiceInstance) {
            rt.setServiceInstance((ServiceInstance) entity);
        }
        if (entity instanceof Subscription) {
            rt.setSubscription((Subscription) entity);
        }
        if (billableEntity instanceof Subscription) {
            rt.setSubscription((Subscription) billableEntity);
        }
        if (entity instanceof UserAccount) {
            rt.setUserAccount((UserAccount) entity);
        }
        return rt;
    }

    private Seller getSeller(BillingAccount billingAccount, BusinessEntity entity) {
        if (entity instanceof ServiceInstance) {
            return ((ServiceInstance) entity).getSubscription().getSeller();
        }
        if (entity instanceof Subscription) {
            return ((Subscription) entity).getSeller();
        }
        return billingAccount.getCustomerAccount().getCustomer().getSeller();
    }

    private Map<Long, MinAmountData> appendExtraAmount(List<ExtraMinAmount> extraMinAmounts, Map<Long, MinAmountData> accountToMinAmount, BusinessEntity entity) {
        MinAmountData minAmountDataInfo = accountToMinAmount.get(entity.getId());

        extraMinAmounts.forEach(extraMinAmount -> {
            BusinessEntity extraMinAmountEntity = extraMinAmount.getEntity();

            if (isExtraMinAmountEntityChildOfEntity(extraMinAmountEntity, entity)) {
                Map<String, Amounts> extraAmounts = extraMinAmount.getCreatedAmount();
                for (Entry<String, Amounts> amountInfo : extraAmounts.entrySet()) {
                    minAmountDataInfo.getAmounts().addAmounts(amountInfo.getValue());
                    if (minAmountDataInfo.getInvoiceSubCategoryAmounts().containsKey(Long.parseLong(amountInfo.getKey().split("_")[1]))) {
                        minAmountDataInfo.getInvoiceSubCategoryAmounts().get(Long.parseLong(amountInfo.getKey().split("_")[1])).addAmounts(amountInfo.getValue());
                    } else {
                        minAmountDataInfo.getInvoiceSubCategoryAmounts().put(Long.parseLong(amountInfo.getKey().split("_")[1]), amountInfo.getValue().clone());

                    }
                }
            }

        });

        return accountToMinAmount;
    }

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

    private List<BusinessEntity> getUserAccountsWithMinAmountELNotNullByBA(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByUA")
                    .setParameter("userAccount", ((Subscription) billableEntity).getUserAccount());
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("UserAccount.getUserAccountsWithMinAmountELNotNullByBA").setParameter("billingAccount", (BillingAccount) billableEntity);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts for a given subscription
     *
     * @param subscription         Subscription
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Amounts with and without tax, tax amount
     */
    private Amounts computeTotalInvoiceableAmountForSubscription(Subscription subscription, Date firstTransactionDate, Date lastTransactionDate) {

        //        boolean ignorePrepaidWallets = false;  TODO AKK if (prePaidWalletsIds != null && !prePaidWalletsIds.isEmpty()) {
        String query = "RatedTransaction.sumTotalInvoiceableBySubscription";
        //        if (ignorePrepaidWallets) {
        //            query = "RatedTransaction.sumTotalInvoiceableBySubscriptionExcludePrepaidWO";
        //        }

        Query q = getEntityManager().createNamedQuery(query).setParameter("subscription", subscription).setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);

        //        if (ignorePrepaidWallets) {
        //            q = q.setParameter("walletsIds", prePaidWalletsIds);
        //        }

        return (Amounts) q.getSingleResult();
    }

    /**
     * Summed rated transaction amounts for a given billing account
     *
     * @param billingAccount       Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Amounts with and without tax, tax amount
     */
    private Amounts computeTotalInvoiceableAmountForBillingAccount(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {

        //      boolean ignorePrepaidWallets = false;  TODO AKK if (prePaidWalletsIds != null && !prePaidWalletsIds.isEmpty()) {
        String query = "RatedTransaction.sumTotalInvoiceableByBA";
        //      if (ignorePrepaidWallets) {
        //          query = "RatedTransaction.sumTotalInvoiceableByBAExcludePrepaidWO";
        //      }

        Query q = getEntityManager().createNamedQuery(query).setParameter("billingAccount", billingAccount).setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);

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
        return null;
    }

    private List<BusinessEntity> getSubscriptionsWithMinAmount(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountBySubscription").setParameter("subscription", (Subscription) billableEntity);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountByBA").setParameter("billingAccount", (BillingAccount) billableEntity);
            return q.getResultList();
        }
        return null;
    }

    private List<Seller> getSellersByBillingAccount(BillingAccount billingAccount) {
        Query q = getEntityManager().createNamedQuery("Subscription.getSellersByBA").setParameter("billingAccount", billingAccount);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts applied on services, that have minimum invoiceable amount rule, grouped by invoice subCategory for a given billable entity.
     *
     * @param billableEntity       Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, serviceInstance
     */
    private List<Object[]> computeInvoiceableAmountForServicesWithMinAmountRule(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByServiceWithMinAmountBySubscription")
                    .setParameter("subscription", (Subscription) billableEntity).setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByServiceWithMinAmountByBA")
                    .setParameter("billingAccount", (BillingAccount) billableEntity).setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }
        return null;
    }

    /**
     * Summed rated transaction amounts applied on subscriptions, that have minimum invoiceable amount rule, grouped by invoice subCategory for a given billable entity.
     *
     * @param billableEntity       Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, subscription
     */
    private List<Object[]> computeInvoiceableAmountForSubscriptions(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountBySubscription")
                    .setParameter("subscription", (Subscription) billableEntity).setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountByBA")
                    .setParameter("billingAccount", (BillingAccount) billableEntity).setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }
        return null;
    }

    /**
     * Summed rated transaction amounts applied on UserAccounts, that have minimum invoiceable amount rule, grouped by invoice subCategory for a given billable entity.
     *
     * @param billableEntity       Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, subscription
     */
    private List<Object[]> computeInvoiceableAmountForUserAccounts(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForUABySubscription").setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByUA").setParameter("billingAccount", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by invoice subcategory and seller for a given billing account
     *
     * @param billableEntity       Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, seller id
     */
    private List<Object[]> computeInvoiceableAmountForBillingAccount(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForUABySubscription").setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByBA").setParameter("billingAccount", billableEntity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by invoice subcategory and seller for a given customer account
     *
     * @param billableEntity       Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, seller id
     */
    private List<Object[]> computeInvoiceableAmountForCustomerAccount(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForCABySubscription").setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }

        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByCA").setParameter("customerAccount", ((BillingAccount) billableEntity).getCustomerAccount())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Summed rated transaction amounts grouped by invoice subcategory and seller for a given customer account
     *
     * @param billableEntity       BillingAccount or subscription
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate  Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, seller id
     */
    private List<Object[]> computeInvoiceableAmountForCustomer(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableForCustomerBySubscription").setParameter("subscription", billableEntity)
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
            return q.getResultList();
        }
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByCustomer")
                .setParameter("customer", ((BillingAccount) billableEntity).getCustomerAccount().getCustomer()).setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
        return q.getResultList();
    }

    /**
     * Evaluate double expression. Either ba, subscription or service instance must be specified.
     *
     * @param expression      EL expression
     * @param ba              Billing account
     * @param subscription    Subscription
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
     * @param entity     Business Entity
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
     * @param entity     Business Entity
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
     * @param ba         billing account
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
     * @param expression      EL expression
     * @param ba              Billing account
     * @param subscription    Subscription
     * @param serviceInstance Service instance
     * @return Context of variable
     */
    private Map<Object, Object> constructElContext(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance, UserAccount ua) {

        Map<Object, Object> contextMap = new HashMap<Object, Object>();
        if (expression.startsWith("#{")) {
            if (expression.indexOf("serviceInstance") >= 0) {
                contextMap.put("serviceInstance", serviceInstance);
            }

            if (expression.indexOf("sub") >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put("sub", subscription);
            }
            if (expression.indexOf("offer") >= 0) {
                if (subscription == null) {
                    subscription = serviceInstance.getSubscription();
                }
                contextMap.put("offer", subscription.getOffer());
            }

            if (expression.indexOf("ua") >= 0) {
                if (ua == null) {
                    ua = subscription != null ? subscription.getUserAccount() : serviceInstance.getSubscription().getUserAccount();
                }

                contextMap.put("ua", ua);
            }

            if (expression.indexOf("ba") >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }

                contextMap.put("ba", ba);
            }

            if (expression.indexOf("ca") >= 0) {

                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put("ca", ba.getCustomerAccount());
            }

            if (expression.indexOf("c") >= 0) {
                if (ba == null) {
                    ba = subscription != null ? subscription.getUserAccount().getBillingAccount() : serviceInstance.getSubscription().getUserAccount().getBillingAccount();
                }
                contextMap.put("c", ba.getCustomerAccount().getCustomer());
            }

            if (expression.indexOf("prov") >= 0) {
                contextMap.put("prov", appProvider);
            }
        }
        return contextMap;
    }

    /**
     * Compute the invoice amount for order.
     *
     * @param order                order
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate  last transaction date
     * @return computed order's invoice amount.
     */
    private Amounts computeTotalOrderInvoiceAmount(Order order, Date firstTransactionDate, Date lastTransactionDate) {

        //      boolean ignorePrepaidWallets = false;  TODO AKK if (prePaidWalletsIds != null && !prePaidWalletsIds.isEmpty()) {
        String query = "RatedTransaction.sumTotalInvoiceableByOrderNumber";
        //      if (ignorePrepaidWallets) {
        //          query = "RatedTransaction.sumTotalInvoiceableByOrderNumberExcludePrepaidWO";
        //      }

        Query q = getEntityManager().createNamedQuery(query).setParameter("orderNumber", order.getOrderNumber()).setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);

        //      if (ignorePrepaidWallets) {
        //          q = q.setParameter("walletsIds", prePaidWalletsIds);
        //      }

        return (Amounts) q.getSingleResult();
    }

    /**
     * Get a list of invoiceable Rated transactions for a given billable entity and date range or from a filter
     *
     * @param entityToInvoice        Entity to invoice (subscription, billing account or order)
     * @param firstTransactionDate   Usage date range - start date
     * @param lastTransactionDate    Usage date range - end date
     * @param ratedTransactionFilter Filter returning a list of rated transactions
     * @param rtPageSize             Number of records to return
     * @return A list of RT entities
     * @throws BusinessException General exception
     */
    public List<RatedTransaction> listRTsToInvoice(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate, Filter ratedTransactionFilter,
            int rtPageSize) throws BusinessException {

        if (ratedTransactionFilter != null) {
            return (List<RatedTransaction>) filterService.filteredListAsObjects(ratedTransactionFilter);

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceBySubscription", RatedTransaction.class)
                    .setParameter("subscriptionId", entityToInvoice.getId()).setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();

        } else if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccount", RatedTransaction.class)
                    .setParameter("billingAccountId", entityToInvoice.getId()).setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByOrderNumber", RatedTransaction.class)
                    .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber()).setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate).setHint("org.hibernate.readOnly", true).setMaxResults(rtPageSize).getResultList();
        }

        return new ArrayList<>();
    }

    /**
     * Determine if minimum RT transactions functionality is used at service level
     *
     * @return True if exists any serviceInstance with minimumAmountEl value
     */
    @Deprecated
    public boolean isServiceMinRTsUsed() {

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

        try {
            getEntityManager().createNamedQuery("BillingAccount.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Determine if minimum RT transactions functionality is used at all. A check is done on serviceInstance, subscription, userAccount or billing account entities for minimumAmountEl field
     * value presence.
     *
     * @return An array of booleans indicating if minimum invoicing amount rule exists on service, subscription, userAccount and billingAccount levels, in that particular order.
     */
    public boolean[] isMinRTsUsed() {

        EntityManager em = getEntityManager();

        boolean baMin = false;
        boolean subMin = false;
        boolean servMin = false;
        boolean uaMin = false;
        boolean caMin = false;
        boolean custMin = false;
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
            em.createNamedQuery("OfferTemplate.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            subMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("ServiceInstance.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            servMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("ServiceTemplate.getMimimumRTUsed").setMaxResults(1).getSingleResult();
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
     * @param lastTransactionDate  a last transaction date
     * @param lastId               a last id used for pagination
     * @param max                  a max result used for pagination
     * @return All open rated transaction between two date.
     */
    public List<RatedTransaction> getNotOpenedRatedTransactionBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, long lastId, int max) {
        return getEntityManager().createNamedQuery("RatedTransaction.listNotOpenedBetweenTwoDates", RatedTransaction.class)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("lastId", lastId)
                .setMaxResults(max).getResultList();

    }

    /**
     * Remove All not open rated transaction between two date.
     *
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate  last operation date
     * @return the number of deleted entities
     */
    public long purge(Date firstTransactionDate, Date lastTransactionDate) {
        return getEntityManager().createNamedQuery("RatedTransaction.deleteNotOpenBetweenTwoDates").setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
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
     * Delete min RT associated to an invoice
     *
     * @param invoice Invoice
     */
    public void deleteMinRTs(Invoice invoice) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteMinRTByInvoice").setParameter("invoice", invoice).executeUpdate();
    }

    /**
     * Delete min RT associated to a billing run
     *
     * @param billingRun Billing run
     */
    public void deleteMinRTs(BillingRun billingRun) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteMinRTByBR").setParameter("billingRun", billingRun).executeUpdate();
    }

    /**
     * Mark open RTs associated to an invoice
     *
     * @param invoice Invoice
     */
    public void uninvoiceRTs(Invoice invoice) {
        getEntityManager().createNamedQuery("RatedTransaction.unInvoiceByInvoice").setParameter("invoice", invoice).executeUpdate();
    }

    /**
     * Mark open RTs associated to a billing run
     *
     * @param billingRun Billing run
     */
    public void uninvoiceRTs(BillingRun billingRun) {
        getEntityManager().createNamedQuery("RatedTransaction.unInvoiceByBR").setParameter("billingRun", billingRun).executeUpdate();
    }

    /**
     * Retrieve rated transactions associated to an invoice
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
     * Retrieve rated transactions associated to an invoice aggregate
     *
     * @param subCategoryInvoiceAgregate Invoice
     * @return A list of rated transactions
     */
    public List<RatedTransaction> getRatedTransactionsByInvoiceAggr(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {

        if (subCategoryInvoiceAgregate.getId() == null) {
            return new ArrayList<>();
        }

        return getEntityManager().createNamedQuery("RatedTransaction.listByInvoiceSubCategoryAggr", RatedTransaction.class)
                .setParameter("invoice", subCategoryInvoiceAgregate.getInvoice()).setParameter("invoiceAgregateF", subCategoryInvoiceAgregate).getResultList();
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
		return getEntityManager().createNamedQuery("RatedTransaction.listBetweenTwoDatesByStatus", RatedTransaction.class)
                .setParameter("firstTransactionDate", firstDate)
                .setParameter("lastTransactionDate", lastDate)
                .setParameter("lastId", lastId)
                .setParameter("status", formattedStatus)
                .setMaxResults(maxResult)
                .getResultList();
	}

	public long purge(Date firstTransactionDate, Date lastTransactionDate, List<RatedTransactionStatusEnum> targetStatusList) {
		return getEntityManager().createNamedQuery("RatedTransaction.deleteBetweenTwoDatesByStatus")
				.setParameter("status", targetStatusList)
				.setParameter("firstTransactionDate", firstTransactionDate)
	            .setParameter("lastTransactionDate", lastTransactionDate)
	            .executeUpdate();
	}

}