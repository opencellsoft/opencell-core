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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CreateMinAmountsResult;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
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

/**
 * RatedTransactionService : A class for Rated transaction persistence services.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
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
     * @param billingAccount billing account
     * @param firstTransactionDate date of first transaction
     * @param lastTransactionDate date of last transaction
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
     * @param invoice invoice
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
     * @param invoicingDate Invoicing date
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
     * @param aggregatedWo aggregated wallet operations
     * @param aggregatedSettings aggregation settings of wallet operations
     * @param invoicingDate the invoicing date
     * @return created {@link RatedTransaction}
     * @throws BusinessException Exception when RT is not create successfully
     * @see WalletOperation
     */
    public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo, RatedTransactionsJobAggregationSetting aggregatedSettings, Date invoicingDate)
            throws BusinessException {
        return createRatedTransaction(aggregatedWo, aggregatedSettings, invoicingDate, false);
    }

    /**
     * 
     * @param aggregatedWo aggregated wallet operations
     * @param aggregationSettings aggregation settings of wallet operations
     * @param isVirtual is virtual
     * @param invoicingDate the invoicing date
     * @return {@link RatedTransaction}
     * @throws BusinessException Exception when RT is not create successfully
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
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
    public IBillableEntity updateEntityTotalAmountsAndLinkToBR(IBillableEntity entity, BillingRun billingRun, boolean instantiateMinRtsForService,
            boolean instantiateMinRtsForSubscription, boolean instantiateMinRtsForBA) throws BusinessException {

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
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null
                        ? (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount()
                        : null;
            }
        }

        calculateAmountsAndCreateMinAmountTransactions(entity, null, billingRun.getLastTransactionDate(), true, instantiateMinRtsForService, instantiateMinRtsForSubscription,
            instantiateMinRtsForBA);

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
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null
                        ? (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount()
                        : null;
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
     * @param billableEntity The billable entity
     * @param lastTransactionDate Last transaction date
     * @param calculateAndUpdateTotalAmounts Should total amounts be calculated and entity updated with those amounts
     * @param instantiateMinRtsForService Should rated transactions to reach minimum invoicing amount be checked and instantiated on service level.
     * @param instantiateMinRtsForSubscription Should rated transactions to reach minimum invoicing amount be checked and instantiated on subscription level.
     * @param instantiateMinRtsForBA Should rated transactions to reach minimum invoicing amount be checked and instantiated on Billing account level.
     * @throws BusinessException General business exception
     */
    public void calculateAmountsAndCreateMinAmountTransactions(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate,
            boolean calculateAndUpdateTotalAmounts, boolean instantiateMinRtsForService, boolean instantiateMinRtsForSubscription, boolean instantiateMinRtsForBA)
            throws BusinessException {

        Amounts totalInvoiceableAmounts = null;

        List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();

        Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

        if (billableEntity instanceof Order && calculateAndUpdateTotalAmounts) {
            totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);

        } else if (billableEntity instanceof Subscription) {

            BillingAccount billingAccount = ((Subscription) billableEntity).getUserAccount().getBillingAccount();

            Map<Long, Map<String, Amounts>> createdAmountServices = null;
            if (instantiateMinRtsForService) {            
                CreateMinAmountsResult createMinAmountsResultServices = createMinRTForServices(billableEntity, billingAccount, lastTransactionDate, minRatingDate);
                createdAmountServices = createMinAmountsResultServices.getCreatedAmountServices();
                minAmountTransactions.addAll(createMinAmountsResultServices.getMinAmountTransactions());
            }    
            
            Map<String, Amounts> createdAmountSubscription = null;
            if (instantiateMinRtsForSubscription && !StringUtils.isBlank(((Subscription) billableEntity).getMinimumAmountEl())) {
                CreateMinAmountsResult createMinAmountsResultSubscription = createMinRTForSubscriptions(billableEntity, billingAccount, lastTransactionDate, minRatingDate, createdAmountServices);
                createdAmountSubscription = createMinAmountsResultSubscription.getCreatedAmountSubscription();
                minAmountTransactions.addAll(createMinAmountsResultSubscription.getMinAmountTransactions());
            }
            
            if (calculateAndUpdateTotalAmounts) {
                // Get total invoiceable amount for subscription and add created amounts during min RT creation
                totalInvoiceableAmounts = computeTotalInvoiceableAmountForSubscription((Subscription) billableEntity, new Date(0), lastTransactionDate);

                // Sum up
                if (createdAmountServices != null) {
                    for (Map<String, Amounts> amountInfo : createdAmountServices.values()) {
                        for (Amounts amounts : amountInfo.values()) {
                            totalInvoiceableAmounts.addAmounts(amounts);
                        }
                    }
                }
                if (createdAmountSubscription != null) {
                    for (Amounts amounts : createdAmountSubscription.values()) {
                        totalInvoiceableAmounts.addAmounts(amounts);
                    }
                }
            }

        } else if (billableEntity instanceof BillingAccount) {

            BillingAccount billingAccount = (BillingAccount) billableEntity;

            Map<Long, Map<String, Amounts>> createdAmountServices = null;
            if (instantiateMinRtsForService) {
                CreateMinAmountsResult createMinAmountsResultServices = createMinRTForServices(billableEntity, billingAccount, lastTransactionDate, minRatingDate);
                createdAmountServices = createMinAmountsResultServices.getCreatedAmountServices();
                minAmountTransactions.addAll(createMinAmountsResultServices.getMinAmountTransactions());
            }
            
            Map<String, Amounts> createdAmountSubscription = null;
            if (instantiateMinRtsForSubscription) {
                CreateMinAmountsResult createMinAmountsResultSubscription = createMinRTForSubscriptions(billableEntity, billingAccount, lastTransactionDate, minRatingDate, createdAmountServices);
                createdAmountSubscription = createMinAmountsResultSubscription.getCreatedAmountSubscription();
                minAmountTransactions.addAll(createMinAmountsResultSubscription.getMinAmountTransactions());
            }

            if (calculateAndUpdateTotalAmounts || (instantiateMinRtsForBA && isAppliesMinRTForBA(billingAccount, null))) {
                // Get total invoiceable amount for billing account and add created amounts during min RT creation for service and subscription
                totalInvoiceableAmounts = computeTotalInvoiceableAmountForBillingAccount(billingAccount, new Date(0), lastTransactionDate);

                // Sum up
                if (createdAmountServices != null) {
                    for (Map<String, Amounts> serviceAmountInfo : createdAmountServices.values()) {
                        for (Amounts amounts : serviceAmountInfo.values()) {
                            totalInvoiceableAmounts.addAmounts(amounts);
                        }
                    }
                }

                if (createdAmountSubscription != null) {
                    for (Amounts amounts : createdAmountSubscription.values()) {
                        totalInvoiceableAmounts.addAmounts(amounts);
                    }
                }

                if (instantiateMinRtsForBA && isAppliesMinRTForBA(billingAccount, totalInvoiceableAmounts)) {

                    Map<String, Amounts> extraAmounts = new HashMap<>();
                    if (createdAmountSubscription != null) {
                        extraAmounts.putAll(createdAmountSubscription);
                    }
                    if (createdAmountServices != null) {
                        for (Map<String, Amounts> serviceAmountInfo : createdAmountServices.values()) {

                            for (Entry<String, Amounts> amountInfo : serviceAmountInfo.entrySet()) {
                                if (extraAmounts.containsKey(amountInfo.getKey())) {
                                    extraAmounts.get(amountInfo.getKey()).addAmounts(amountInfo.getValue());
                                } else {
                                    extraAmounts.put(amountInfo.getKey(), amountInfo.getValue());
                                }
                            }
                        }
                    }

                    // Create min RTs for billing account and add to the total amount
                    Map<String, Amounts> createdAmountBillingAccount = createMinRTForBillingAccount(billingAccount, lastTransactionDate, minRatingDate, minAmountTransactions,
                        totalInvoiceableAmounts, extraAmounts);
                    for (Amounts amounts : createdAmountBillingAccount.values()) {
                        totalInvoiceableAmounts.addAmounts(amounts);
                    }
                }
            }
        }

        billableEntity.setMinRatedTransactions(minAmountTransactions);

        if (calculateAndUpdateTotalAmounts) {
            totalInvoiceableAmounts.calculateDerivedAmounts(appProvider.isEntreprise());

            billableEntity.setTotalInvoicingAmountWithoutTax(totalInvoiceableAmounts.getAmountWithoutTax());
            billableEntity.setTotalInvoicingAmountWithTax(totalInvoiceableAmounts.getAmountWithTax());
            billableEntity.setTotalInvoicingAmountTax(totalInvoiceableAmounts.getAmountTax());
        }
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per service level. Only those services that have minimum invoice amount rule are considered. Updates
     * minAmountTransactions parameter.
     * 
     * @param billableEntity Entity to bill - entity for which minimum rated transactions should be created
     * @param billingAccount Billing account to associate new minimum amount Rated transactions with
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @return CreateMinAmountsResult Contains createMinRTForServices result
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("unchecked")
    private CreateMinAmountsResult createMinRTForServices(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate) throws BusinessException {

        EntityManager em = getEntityManager();

        CreateMinAmountsResult createMinAmountsResult = new CreateMinAmountsResult();
        
        // Service id as a key and array of <min amount>, <min amount label>, <total amounts>, map of <Invoice subCategory id, amounts], serviceInstance>
        Map<Long, Object[]> serviceInstanceToMinAmount = new HashMap<>();
        
        Subscription billingSubscription = null;
        if(billableEntity instanceof Subscription) {
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
        
        List<ServiceInstance> servicesWithMinAmount = getServicesWithMinAmount(billableEntity);
        for (ServiceInstance serviceWithMinAmount : servicesWithMinAmount) {
            Object[] minAmountInfo = serviceInstanceToMinAmount.get(serviceWithMinAmount.getId());
            if(minAmountInfo == null) {
                BigDecimal invSubcategoryAmountWithoutTax = BigDecimal.ZERO;
                BigDecimal invSubcategoryAmountWithTax = BigDecimal.ZERO;
                String minAmountEL = serviceWithMinAmount.getMinimumAmountEl();
                String minAmountLabelEL = serviceWithMinAmount.getMinimumLabelEl();
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, null, null, serviceWithMinAmount);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, null, serviceWithMinAmount);
                
                Amounts serviceAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                Map<Long, Amounts> amountMap = new HashMap<Long, Amounts>();
                InvoiceSubCategory invoiceSubCategory = serviceWithMinAmount.getMinimumInvoiceSubCategory();
                if(invoiceSubCategory != null) {
                    amountMap.put(invoiceSubCategory.getId(), serviceAmounts);
                    serviceInstanceToMinAmount.put(serviceWithMinAmount.getId(), new Object[] { minAmount, minAmountLabel, serviceAmounts, amountMap, serviceWithMinAmount });
                } else {
                    throw new BusinessException("minAmountInvoiceSubCategory not defined for service code="+ serviceWithMinAmount.getCode());
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

        for (Entry<Long, Object[]> serviceAmounts : serviceInstanceToMinAmount.entrySet()) {

            if (serviceAmounts.getValue() == null) {
                continue;
            }
            BigDecimal minAmount = (BigDecimal) serviceAmounts.getValue()[0];
            String minAmountLabel = (String) serviceAmounts.getValue()[1];
            BigDecimal totalServiceAmount = appProvider.isEntreprise() ? ((Amounts) serviceAmounts.getValue()[2]).getAmountWithoutTax()
                    : ((Amounts) serviceAmounts.getValue()[2]).getAmountWithTax();
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
                if(invoiceSubCategoryId == null) {
                    invoiceSubCategoryId = -1l;
                }
                
                String mapKey = mapKeyPrefix + invoiceSubCategoryId;
                InvoiceSubCategory invoiceSubCategory = em.getReference(InvoiceSubCategory.class, invoiceSubCategoryId);
                Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

                BigDecimal invSubcategoryAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

                BigDecimal ratio = totalServiceAmount.compareTo(invSubcategoryAmount) == 0 ? BigDecimal.ONE
                        : invSubcategoryAmount.divide(totalServiceAmount, 4, RoundingMode.HALF_UP);

                // Ensure that all ratios sum up to 1
                if (!amountIterator.hasNext()) {
                    ratio = BigDecimal.ONE.subtract(totalRatio);
                }

                BigDecimal rtMinAmount = diff.multiply(ratio);

                BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS,
                    RoundingMode.HALF_UP);
                BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                    appProvider.getRoundingMode().getRoundingMode());

                RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1],
                    amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, billingSubscription, null, null, null, null, null,
                    RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode() + "_" + serviceInstance.getCode(), minAmountLabel, null, null, seller, tax, tax.getPercent(),
                    serviceInstance);
                
                createMinAmountsResult.addMinAmountRT(ratedTransaction);

                // Remember newly "created" transaction amounts, as they are not persisted yet to DB
                minRTAmountSubscriptionMap.put(mapKey, new Amounts(amounts[0], amounts[1], amounts[2]));

                totalRatio = totalRatio.add(ratio);
            }
        }
        
        createMinAmountsResult.setCreatedAmountServices(minRTAmountMap);
        return createMinAmountsResult;
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per subscription level. Only those subscriptions that have minimum invoice amount rule are considered. Updates
     * minAmountTransactions parameter.
     * 
     * @param billableEntity Entity to bill - entity for which minimum rated transactions should be created
     * @param billingAccount Billing account to associate new minimum amount Rated transactions with
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param extraAmountsPerSubscription Additional Rated transaction amounts created to reach minimum invoicing amount per service. A map of amounts created with subscription id
     *        as a main key and a secondary map of "&lt;seller.id&gt;_&lt;invoiceSubCategory.id&gt; as a key a and amounts as values" as a value
     * @return CreateMinAmountsResult Contains createMinRTForSubscriptions result
     * @throws BusinessException General Business exception
     */
    @SuppressWarnings("unchecked")
    private CreateMinAmountsResult createMinRTForSubscriptions(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate,
             Map<Long, Map<String, Amounts>> extraAmountsPerSubscription) throws BusinessException {
        
        CreateMinAmountsResult createMinAmountsResult = new CreateMinAmountsResult();

        EntityManager em = getEntityManager();

        // Subscription id as a key and array of <min amount>, <min amount label>, <total amounts>, map of <Invoice subCategory id, amounts], subscription>
        Map<Long, Object[]> subscriptionToMinAmount = new HashMap<>();
        
        Subscription billingSubscription = null;
        if(billableEntity instanceof Subscription) {
            billingSubscription = (Subscription) billableEntity;
        }
        
        
        // Only interested in subscriptions with minAmount condition
        // Calculate amounts on subscription level grouped by invoice category and subscription
        // Calculate a total sum of amounts on subscription level
        List<Object[]> amountsList = computeInvoiceableAmountForSubscriptions(billableEntity, new Date(0), lastTransactionDate);

        for (Object[] amounts : amountsList) {
            BigDecimal invSubcategoryAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal invSubcategoryAmountWithTax = (BigDecimal) amounts[1];
            Long invSubCategoryId = (Long) amounts[2];
            Subscription subscription = em.find(Subscription.class, amounts[3]);

            Object[] minAmountInfo = subscriptionToMinAmount.get(subscription.getId());

            // Resolve if minimal invoice amount rule applies
            if (minAmountInfo == null) {
                String minAmountEL = subscription.getMinimumAmountEl();
                String minAmountLabelEL = subscription.getMinimumLabelEl();
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, null, subscription, null);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, subscription, null);
                subscriptionToMinAmount.put(subscription.getId(), new Object[] { minAmount, minAmountLabel, new Amounts(), new HashMap<Long, Amounts>(), subscription });
                
                // Append extra amounts from service level
                if (extraAmountsPerSubscription != null && extraAmountsPerSubscription.containsKey(subscription.getId())) {

                    Object[] subscriptionToMinAmountInfo = subscriptionToMinAmount.get(subscription.getId());
                    Map<String, Amounts> extraAmounts = extraAmountsPerSubscription.get(subscription.getId());

                    for (Entry<String, Amounts> amountInfo : extraAmounts.entrySet()) {
                        ((Amounts) subscriptionToMinAmountInfo[2]).addAmounts(amountInfo.getValue());
                        // Key consist of sellerId_invoiceSubCategoryId. Interested in invoiceSubCategoryId only
                        ((Map<Long, Amounts>) subscriptionToMinAmountInfo[3]).put(Long.parseLong(amountInfo.getKey().split("_")[1]), amountInfo.getValue().clone());
                    }
                }
            } 
            
            minAmountInfo = subscriptionToMinAmount.get(subscription.getId());
            ((Amounts) minAmountInfo[2]).addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
            Amounts subCatAmounts = ((Map<Long, Amounts>) minAmountInfo[3]).get(invSubCategoryId);
            if (subCatAmounts == null) {
                subCatAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                ((Map<Long, Amounts>) minAmountInfo[3]).put(invSubCategoryId, subCatAmounts);
            } else {
                subCatAmounts.addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
            }
        }
        
        List<Subscription> subscriptionsWithMinAmount = new ArrayList<Subscription>();
        if(billableEntity instanceof Subscription) {
            if(StringUtils.isNotBlank(((Subscription)billableEntity).getMinimumAmountEl())) { 
                subscriptionsWithMinAmount.add((Subscription)billableEntity);
            }
        } else {
            subscriptionsWithMinAmount = getSubscriptionsWithMinAmount(billableEntity);
        }
        
        for (Subscription subscriptionWithMinAmount : subscriptionsWithMinAmount) {
            Object[] minAmountInfo = subscriptionToMinAmount.get(subscriptionWithMinAmount.getId());
            if(minAmountInfo == null) {
                BigDecimal invSubcategoryAmountWithoutTax = BigDecimal.ZERO;
                BigDecimal invSubcategoryAmountWithTax = BigDecimal.ZERO;
                String minAmountEL = subscriptionWithMinAmount.getMinimumAmountEl();
                String minAmountLabelEL = subscriptionWithMinAmount.getMinimumLabelEl();
                BigDecimal minAmount = evaluateMinAmountExpression(minAmountEL, null, subscriptionWithMinAmount, null);
                String minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, subscriptionWithMinAmount, null);
    
                Amounts subscriptionAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                Map<Long, Amounts> amountMap = new HashMap<Long, Amounts>();
                InvoiceSubCategory invoiceSubCategory = subscriptionWithMinAmount.getMinimumInvoiceSubCategory();
                if(invoiceSubCategory != null) {
                    amountMap.put(invoiceSubCategory.getId(), subscriptionAmounts);
                    subscriptionToMinAmount.put(subscriptionWithMinAmount.getId(), new Object[] { minAmount, minAmountLabel, subscriptionAmounts, amountMap, subscriptionWithMinAmount });
                
                    // Append extra amounts from service level
                    if (extraAmountsPerSubscription != null && extraAmountsPerSubscription.containsKey(subscriptionWithMinAmount.getId())) {

                        Object[] subscriptionToMinAmountInfo = subscriptionToMinAmount.get(subscriptionWithMinAmount.getId());
                        Map<String, Amounts> extraAmounts = extraAmountsPerSubscription.get(subscriptionWithMinAmount.getId());

                        for (Entry<String, Amounts> amountInfo : extraAmounts.entrySet()) {
                            ((Amounts) subscriptionToMinAmountInfo[2]).addAmounts(amountInfo.getValue());
                            // Key consist of sellerId_invoiceSubCategoryId. Interested in invoiceSubCategoryId only
                            ((Map<Long, Amounts>) subscriptionToMinAmountInfo[3]).put(Long.parseLong(amountInfo.getKey().split("_")[1]), amountInfo.getValue().clone());
                        }
                    }
                
                } else {
                    throw new BusinessException("minAmountInvoiceSubCategory not defined for subscription code="+subscriptionWithMinAmount.getCode());
                }
            } else {
                // Service amount exceed the minimum amount per service
                if (((BigDecimal) minAmountInfo[0])
                    .compareTo(appProvider.isEntreprise() ? ((Amounts) minAmountInfo[2]).getAmountWithoutTax() : ((Amounts) minAmountInfo[2]).getAmountWithTax()) <= 0) {
                    subscriptionToMinAmount.put(subscriptionWithMinAmount.getId(), null);
                    continue;
                }
            }
        }

        // Create Rated transactions to reach a minimum amount per subscription
        Map<String, Amounts> minRTAmountMap = new HashMap<>();

        for (Entry<Long, Object[]> subscriptionAmounts : subscriptionToMinAmount.entrySet()) {

            if (subscriptionAmounts.getValue() == null) {
                continue;
            }
            BigDecimal minAmount = (BigDecimal) subscriptionAmounts.getValue()[0];
            String minAmountLabel = (String) subscriptionAmounts.getValue()[1];
            BigDecimal totalSubscriptionAmount = appProvider.isEntreprise() ? ((Amounts) subscriptionAmounts.getValue()[2]).getAmountWithoutTax()
                    : ((Amounts) subscriptionAmounts.getValue()[2]).getAmountWithTax();
            Subscription subscription = (Subscription) subscriptionAmounts.getValue()[4];

            Seller seller = subscription.getSeller();
            String mapKeyPrefix = seller.getId().toString() + "_";

            BigDecimal totalRatio = BigDecimal.ZERO;
            Iterator<Entry<Long, Amounts>> amountIterator = ((Map<Long, Amounts>) subscriptionAmounts.getValue()[3]).entrySet().iterator();

            BigDecimal diff = minAmount.subtract(totalSubscriptionAmount);

            while (amountIterator.hasNext()) {
                Entry<Long, Amounts> amountsEntry = amountIterator.next();

                Long invoiceSubCategoryId = amountsEntry.getKey();
                String mapKey = mapKeyPrefix + invoiceSubCategoryId;

                InvoiceSubCategory invoiceSubCategory = em.getReference(InvoiceSubCategory.class, invoiceSubCategoryId);
                Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

                BigDecimal invSubcategoryAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

                BigDecimal ratio = totalSubscriptionAmount.compareTo(invSubcategoryAmount) == 0 ? BigDecimal.ONE
                        : invSubcategoryAmount.divide(totalSubscriptionAmount, 4, RoundingMode.HALF_UP);

                // Ensure that all ratios sum up to 1
                if (!amountIterator.hasNext()) {
                    ratio = BigDecimal.ONE.subtract(totalRatio);
                }

                BigDecimal rtMinAmount = diff.multiply(ratio);

                BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS,
                    RoundingMode.HALF_UP);
                BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                    appProvider.getRoundingMode().getRoundingMode());

                RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1],
                    amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, billingSubscription, null, null, null, null,
                    null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SU.getCode() + "_" + subscription.getCode(), minAmountLabel, null, null, seller, tax, tax.getPercent(),
                    null);
                
                createMinAmountsResult.addMinAmountRT(ratedTransaction);

                // Remember newly "created" transaction amounts, as they are not persisted yet to DB
                minRTAmountMap.put(mapKey, new Amounts(amounts[0], amounts[1], amounts[2]));

                totalRatio = totalRatio.add(ratio);
            }
        }

        createMinAmountsResult.setCreatedAmountSubscription(minRTAmountMap);
        return createMinAmountsResult;
    }

    /**
     * Determine if any extra Rated transactions must be created to reach minimal invoiceable amount per Billing account
     * 
     * @param billingAccount Billing account
     * @param invoiceableAmounts Invoiceable amounts calculated per Billing account or null if just want to check if Billing account has any minimum invoiceable amount required
     * @return True in extra Rated transactions should be created
     * @throws BusinessException General business exception
     */
    private boolean isAppliesMinRTForBA(BillingAccount billingAccount, Amounts invoiceableAmounts) throws BusinessException {

        // Interested in Billing accounts with minimum amount criteria
        BigDecimal minAmount = null;

        String minAmountEL = billingAccount.getMinimumAmountEl();

        if (!StringUtils.isBlank(minAmountEL)) {
            minAmount = evaluateMinAmountExpression(minAmountEL, billingAccount, null, null);
        }

        if (minAmount == null) {
            return false;
        }

        if (invoiceableAmounts == null) {
            return true;
        }

        BigDecimal totalBaAmount = appProvider.isEntreprise() ? invoiceableAmounts.getAmountWithoutTax() : invoiceableAmounts.getAmountWithTax();

        // Billing account level amount is less than the minimum amount required per Billing account
        return totalBaAmount.compareTo(minAmount) < 0;
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per Billing account and update total amount sum. Updates minAmountTransactions, baLeveltotalAmounts and
     * baLevelAmounts parameters.
     * 
     * @param billingAccount Billing account
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param minAmountTransactions Newly created minimum amount Rated transactions. ARE UPDATED by this method. Rated trancastions created in this method are appended.
     * @param totalInvoiceableAmounts Invoiceable amounts calculated per Billing account. Already includes amounts created in service and subscription levels.
     * @param extraAmounts Amounts of extra rated transactions that were created on service and subscription levels. A map with &lt;Seller.id&gt;_&lt;InvoiceSubCategory.id&gt; as a
     *        key and amounts as values
     * @throws BusinessException General business exception
     */
    private Map<String, Amounts> createMinRTForBillingAccount(BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate,
            List<RatedTransaction> minAmountTransactions, Amounts totalInvoiceableAmounts, Map<String, Amounts> extraAmounts) throws BusinessException {

        EntityManager em = getEntityManager();

        // <Seller.id>_<InvoiceSubCategory.id> as a key and amounts as values
        Map<String, Amounts> baToMinAmounts = new HashMap<>();
        
        // Interested in Billing accounts with minimum amount criteria
        BigDecimal minAmount = null;
        String minAmountLabel = null;

        String minAmountEL = billingAccount.getMinimumAmountEl();
        String minAmountLabelEL = billingAccount.getMinimumLabelEl();

        // Calculate amounts on billing account level grouped by invoice category and seller
        // Calculate a total sum of amounts for billing account
        List<Object[]> amountsList = computeInvoiceableAmountForBillingAccount(billingAccount, new Date(0), lastTransactionDate);
        for (Object[] amounts : amountsList) {
            String amountsKey = amounts[3] + "_" + amounts[2];
            baToMinAmounts.put(amountsKey, new Amounts((BigDecimal) amounts[0], (BigDecimal) amounts[1], null));
        }

        // Add previously created amounts
        for (Entry<String, Amounts> extraAmount : extraAmounts.entrySet()) {

            if (baToMinAmounts.containsKey(extraAmount.getKey())) {
                baToMinAmounts.get(extraAmount.getKey()).addAmounts(extraAmount.getValue());
            } else {
                baToMinAmounts.put(extraAmount.getKey(), extraAmount.getValue().clone());
            }
        }
        
        if (!StringUtils.isBlank(minAmountEL)) {
            minAmount = evaluateMinAmountExpression(minAmountEL, billingAccount, null, null);
            minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, billingAccount, null, null);
            Map<Long, Amounts> amountMap = new HashMap<Long, Amounts>();
            if(baToMinAmounts.size() == 0) {
                InvoiceSubCategory invoiceSubCategory = billingAccount.getMinimumInvoiceSubCategory();
                if(invoiceSubCategory != null) {
                    Amounts baAmounts = new Amounts(BigDecimal.ZERO, BigDecimal.ZERO, null);
                    amountMap.put(invoiceSubCategory.getId(), baAmounts);
                    String key = billingAccount.getCustomerAccount().getCustomer().getSeller().getId() + "_" + invoiceSubCategory.getId();
                    baToMinAmounts.put(key, baAmounts);
                } else {
                    throw new BusinessException("minAmountInvoiceSubCategory not defined for billingAccount code="+ billingAccount.getCode());
                }
            }
        }

        BigDecimal totalBaAmount = appProvider.isEntreprise() ? totalInvoiceableAmounts.getAmountWithoutTax() : totalInvoiceableAmounts.getAmountWithTax();

        // Billing account level amount exceeds the minimum amount required per Billing account
        if (totalBaAmount.compareTo(minAmount) >= 0) {
            return new HashMap<>();
        }

        BigDecimal diff = minAmount.subtract(totalBaAmount);

        // Create Rated transactions to reach minimum amount per Billing account
        BigDecimal totalRatio = BigDecimal.ZERO;
        Map<String, Amounts> minRTAmountMap = new HashMap<>();
        Iterator<Entry<String, Amounts>> amountIterator = baToMinAmounts.entrySet().iterator();

        while (amountIterator.hasNext()) {
            Entry<String, Amounts> amountsEntry = amountIterator.next();

            String mapKey = amountsEntry.getKey();

            BigDecimal baAmount = appProvider.isEntreprise() ? amountsEntry.getValue().getAmountWithoutTax() : amountsEntry.getValue().getAmountWithTax();

            BigDecimal ratio = totalBaAmount.compareTo(baAmount) == 0 ? BigDecimal.ONE : baAmount.divide(totalBaAmount, 4, RoundingMode.HALF_UP);

            // Ensure that all ratios sum up to 1
            if (!amountIterator.hasNext()) {
                ratio = BigDecimal.ONE.subtract(totalRatio);
            }

            String[] ids = mapKey.split("_");
            Long sellerId = Long.parseLong(amountsEntry.getKey().substring(0, amountsEntry.getKey().indexOf("_")));
            Seller seller = sellerService.findById(sellerId);
            InvoiceSubCategory invoiceSubCategory = em.getReference(InvoiceSubCategory.class, Long.parseLong(ids[1]));

            Tax tax = invoiceSubCategoryCountryService.determineTax(invoiceSubCategory, seller, billingAccount, minRatingDate, false);

            BigDecimal rtMinAmount = diff.multiply(ratio);

            BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS,
                RoundingMode.HALF_UP);
            BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());

            RatedTransaction ratedTransaction = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1],
                amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, null, null, null, null, null, null,
                RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_BA.getCode() + "_" + billingAccount.getCode(), minAmountLabel, null, null, seller, tax, tax.getPercent(), null);

            minAmountTransactions.add(ratedTransaction);

            // Remember newly "created" transaction amounts, as they are not persisted yet to DB
            minRTAmountMap.put(mapKey, new Amounts(amounts[0], amounts[1], amounts[2]));

            totalRatio = totalRatio.add(ratio);
        }

        return minRTAmountMap;
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

        Query q = getEntityManager().createNamedQuery(query).setParameter("billingAccount", billingAccount).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate);

//      if (ignorePrepaidWallets) {
//          q = q.setParameter("walletsIds", prePaidWalletsIds);
//      }        

        return (Amounts) q.getSingleResult();
    }
    
    @SuppressWarnings("unchecked")
    private List<ServiceInstance> getServicesWithMinAmount(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountBySubscription")
                    .setParameter("subscription", (Subscription) billableEntity);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("ServiceInstance.getServicesWithMinAmountByBA") 
                    .setParameter("billingAccount", (BillingAccount) billableEntity);;
            return q.getResultList();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private List<Subscription> getSubscriptionsWithMinAmount(IBillableEntity billableEntity) {

        if (billableEntity instanceof Subscription) {
            Query q = getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountBySubscription")
                    .setParameter("subscription", (Subscription) billableEntity);
            return q.getResultList();

        } else if (billableEntity instanceof BillingAccount) {
            Query q = getEntityManager().createNamedQuery("Subscription.getSubscriptionsWithMinAmountByBA") 
                    .setParameter("billingAccount", (BillingAccount) billableEntity);;
            return q.getResultList();
        }
        return null;
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
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, subscription
     */
    @SuppressWarnings("unchecked")
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
     * Summed rated transaction amounts grouped by invoice subcategory and seller for a given billing account
     * 
     * @param billingAccount Billing account
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, seller id
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForBillingAccount(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumInvoiceableByBA").setParameter("billingAccount", billingAccount)
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

        Map<Object, Object> userMap = constructElContext(expression, ba, subscription, serviceInstance);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
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

        Map<Object, Object> userMap = constructElContext(expression, ba, subscription, serviceInstance);

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
    private Map<Object, Object> constructElContext(String expression, BillingAccount ba, Subscription subscription, ServiceInstance serviceInstance) {

        Map<Object, Object> contextMap = new HashMap<Object, Object>();

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
     * @param entityToInvoice Entity to invoice (subscription, billing account or order)
     * @param firstTransactionDate Usage date range - start date
     * @param lastTransactionDate Usage date range - end date
     * @param ratedTransactionFilter Filter returning a list of rated transactions
     * @param rtPageSize Number of records to return
     * @return A list of RT entities
     * @throws BusinessException General exception
     */
    @SuppressWarnings("unchecked")
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
    public boolean isBAMinRTsUsed() {

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

        EntityManager em = getEntityManager();

        boolean baMin = false;
        boolean subMin = false;
        boolean servMin = false;
        try {
            em.createNamedQuery("BillingAccount.getMimimumRTUsed").setMaxResults(1).getSingleResult();
            baMin = true;
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
        return new boolean[] { servMin, subMin, baMin };
    }

    /**
     * Gets All open rated transaction between two date.
     *
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate  a last transaction date
     * @param lastId a last id used for pagination
     * @param max a max result used for pagination
     * @return All open rated transaction between two date.
     */
    public List<RatedTransaction> getNotOpenedRatedTransactionBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, long lastId, int max) {
        return getEntityManager().createNamedQuery("RatedTransaction.listNotOpenedBetweenTwoDates", RatedTransaction.class)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .setParameter("lastId", lastId)
                .setMaxResults(max)
                .getResultList();

    }

    /**
     * Remove All not open rated transaction between two date.
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @return the number of deleted entities
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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

	public long purge(Date lastTransactionDate, List<RatedTransactionStatusEnum> targetStatusList) {
        return getEntityManager().createNamedQuery("RatedTransaction.deleteByLastTransactionDateAndStatus")
                .setParameter("status", targetStatusList)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .executeUpdate();
    }

	public long purge(Date firstTransactionDate, Date lastTransactionDate, List<RatedTransactionStatusEnum> targetStatusList) {
		return getEntityManager().createNamedQuery("RatedTransaction.deleteBetweenTwoDatesByStatus")
				.setParameter("status", targetStatusList)
				.setParameter("firstTransactionDate", firstTransactionDate)
	            .setParameter("lastTransactionDate", lastTransactionDate)
	            .executeUpdate();
	}

}