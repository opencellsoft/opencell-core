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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.job.AggregationConfiguration;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.ApplyMinimumModeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ExtraMinAmount;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.MinAmountData;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.MinAmountsResult;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionMinAmountTypeEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RatedTransactionTypeEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.IInvoicingMinimumApplicable;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.cpq.BillingRulesService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.order.OrderService;
import org.meveo.service.tax.TaxClassService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;

import com.google.common.collect.ImmutableMap;

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

    private static final String APPLY_MINIMA_EVEN_ON_ZERO_TRANSACTION = "apply.minima.even.on.zero.transaction";


    private static final String INVOICING_PROCESS_TYPE = "RatedTransaction";

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

    @Inject
    private MinAmountService minAmountService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private InvoiceLineService invoiceLineService;

    @Inject
    private AccountingArticleService accountingArticleService;

    @Inject
    private BillingRulesService billingRulesService;
    
    /**
     * Check if Billing account has any not yet billed Rated transactions
     *
     * @param billingAccount billing account
     * @param firstTransactionDate date of first transaction. Optional
     * @param lastTransactionDate date of last transaction
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return true/false
     */
    public boolean isBillingAccountBillable(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate) {
        if (firstTransactionDate == null) {
            firstTransactionDate = new Date(0);
        }
        TypedQuery<Long> q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedOpenByBA", Long.class);
        long count = q.setParameter("billingAccount", billingAccount).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("invoiceUpToDate", invoiceUpToDate).getSingleResult();
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
            walletOp.setStatus(WalletOperationStatusEnum.TREATED);
            walletOp.setUpdated(now);
            walletOp.setRatedTransaction(ratedTransaction);
            walletOperationService.updateNoCheck(walletOp);
            //em.createNamedQuery("WalletOperation.setStatusToTreatedWithRT").setParameter("rt", ratedTransaction).setParameter("now", now).setParameter("id", walletOp.getId()).executeUpdate();
        }
    }


    public List<WalletOperation> getWalletOperations(IBillableEntity entityToInvoice, Date invoicingDate) {
        return walletOperationService.listToRate(entityToInvoice, invoicingDate);
    }

    public List<WalletOperation> getWalletOperations(List<Long> ids) {
        return walletOperationService.listByIds(ids);
    }
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RatedTransaction createRatedTransactionNewTx(WalletOperation walletOperation, boolean isVirtual) throws BusinessException {
        return createRatedTransaction(walletOperation, isVirtual);
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
        updateBAForRT(List.of(ratedTransaction));
        return ratedTransaction;
    }

    /**
     * Create Rated transaction from wallet operation.
     *
     * @param walletOperations Wallet operations
     * @return Rated transaction
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<RatedTransaction> createRatedTransactionsInBatch(List<WalletOperation> walletOperations) throws BusinessException {
        EntityManager em = getEntityManager();
        boolean eventsEnabled = areEventsEnabled(NotificationEventTypeEnum.CREATED);
        List<RatedTransaction> lstRatedTransaction = new ArrayList<RatedTransaction>();

        boolean cftEndPeriodEnabled = customFieldTemplateService.areCFTEndPeriodEventsEnabled(new RatedTransaction());

        String providerCode = currentUser.getProviderCode();
        final String schemaPrefix = providerCode != null ? EntityManagerProvider.convertToSchemaName(providerCode) + "." : "";

        // Convert WO to RT and persist RT
        Long[][] woRtIds = new Long[walletOperations.size()][2];
        int i = 0;
        for (WalletOperation walletOperation : walletOperations) {
            if (i > 0 && i % 2000 == 0) {
                em.flush();
                em.clear();
            }
            RatedTransaction ratedTransaction = new RatedTransaction(walletOperation);                      

            if (cftEndPeriodEnabled) {
                customFieldInstanceService.scheduleEndPeriodEvents(ratedTransaction);
            }
            em.persist(ratedTransaction);
            
            lstRatedTransaction.add(ratedTransaction);
            
            // Fire notifications
            if (eventsEnabled) {
                entityCreatedEventProducer.fire((BaseEntity) ratedTransaction);
            }

            woRtIds[i][0] = walletOperation.getId();
            woRtIds[i][1] = ratedTransaction.getId();
            i++;
        }

        // Update WOs with Rated transaction information

        // Mass update WO status

        Session hibernateSession = em.unwrap(Session.class);
        hibernateSession.doWork(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into " + schemaPrefix + "billing_wallet_operation_pending (id, rated_transaction_id) values (?,?)")) {

//                int i = 0;
                for (Long[] woRtId : woRtIds) {
                    preparedStatement.setLong(1, woRtId[0]);
                    preparedStatement.setLong(2, woRtId[1]);

                    preparedStatement.addBatch();

//                        if (i > 0 && i % 500 == 0) {
//                            preparedStatement.executeBatch();
//                        }
//                        i++;
                }

                preparedStatement.executeBatch();

            } catch (SQLException e) {
                log.error("Failed to insert into billing_rated_transaction_pending", e);
                throw e;
            }
        });

        // Need to flush, so WOs can be updated in mass
        em.flush();

        // Mass update WOs with status and RT info
        em.createNamedQuery("WalletOperation.massUpdateWithRTInfoFromPendingTable" + (EntityManagerProvider.isDBOracle() ? "Oracle" : "")).executeUpdate();
        em.createNamedQuery("WalletOperation.deletePendingTable").executeUpdate();
        return lstRatedTransaction;
    }

    private Optional<AccountingArticle> getAccountingArticle(WalletOperation walletOperation) {
        ServiceInstance serviceInstance = walletOperation.getServiceInstance() != null
                ? serviceInstanceService.findById(walletOperation.getServiceInstance().getId()) : null;
        ChargeInstance chargeInstance = walletOperation.getChargeInstance() != null
                ? chargeInstanceService.findById(walletOperation.getChargeInstance().getId()) : null;
        Product product = serviceInstance != null
                ? serviceInstance.getProductVersion() != null
                ? serviceInstance.getProductVersion().getProduct() : null : null;
        ChargeTemplate charge = chargeInstance != null ? chargeInstance.getChargeTemplate() : null;
        List<AttributeValue> attributeValues = fromAttributeInstances(serviceInstance);
        Map<String, Object> attributes = fromAttributeValue(attributeValues);
        return accountingArticleService.getAccountingArticle(product, charge, attributes, walletOperation);
    }

    private List<AttributeValue> fromAttributeInstances(ServiceInstance serviceInstance) {
        if (serviceInstance == null) {
            return Collections.emptyList();
        }
        return serviceInstance.getAttributeInstances().stream().map(attributeInstance -> (AttributeValue) attributeInstance).collect(toList());
    }

    private Map<String, Object> fromAttributeValue(List<AttributeValue> attributeValues) {
        return attributeValues
                .stream()
                .filter(attributeValue -> attributeValue.getAttribute().getAttributeType().getValue(attributeValue) != null)
                .collect(toMap(key -> key.getAttribute().getCode(),
                        value -> value.getAttribute().getAttributeType().getValue(value)));
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
    public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo, WalletOperationAggregationSettings aggregatedSettings, Date invoicingDate) throws BusinessException {
        return createRatedTransaction(aggregatedWo, aggregatedSettings, invoicingDate, false);
    }


    /**
     * @param aggregatedWo aggregated wallet operations
     * @param aggregationSettings aggregation settings of wallet operations
     * @param isVirtual is virtual
     * @param invoicingDate the invoicing date
     * @return {@link RatedTransaction}
     * @throws BusinessException Exception when RT is not create successfully
     */
    public RatedTransaction createRatedTransaction(AggregatedWalletOperation aggregatedWo, WalletOperationAggregationSettings aggregationSettings, Date invoicingDate, boolean isVirtual) throws BusinessException {
        RatedTransaction ratedTransaction = new RatedTransaction();

        Seller seller;
        BillingAccount ba;
        UserAccount ua;
        Subscription sub;
        ServiceInstance si;
        ChargeInstance ci;
        String code;
        String description;
        InvoiceSubCategory isc;

        Calendar cal = Calendar.getInstance();
        if (aggregatedWo.getYear() != null && aggregatedWo.getMonth() != null && aggregatedWo.getDay() != null) {
            cal.set(aggregatedWo.getYear(), aggregatedWo.getMonth() - 1, aggregatedWo.getDay(), 0, 0, 0);
            ratedTransaction.setUsageDate(cal.getTime());
        } else {
            ratedTransaction.setUsageDate(aggregatedWo.getOperationDate());
        }

        isc = invoiceSubCategoryService.refreshOrRetrieve(aggregatedWo.getInvoiceSubCategory());
        ci = aggregatedWo.getChargeInstance() != null ? chargeInstanceService.findById(aggregatedWo.getChargeInstance()) : null;
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
        // ratedTransaction.setEdr(aggregatedWo.getEdr());
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
        SubListCreator subList = new SubListCreator(30000, woIds);
        while (subList.isHasNext()) {
            String strQuery = "UPDATE WalletOperation o SET o.status=org.meveo.model.billing.WalletOperationStatusEnum.TREATED," + " o.ratedTransaction=:ratedTransaction , o.updated=:updated" + " WHERE o.id in (:woIds) ";
            Query query = getEntityManager().createQuery(strQuery);
            query.setParameter("woIds", subList.getNextWorkSet());
            query.setParameter("ratedTransaction", ratedTransaction);
            query.setParameter("updated", new Date());
            int affectedRecords = query.executeUpdate();
            log.debug("updated record wo count={}", affectedRecords);
        }
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

    public Long countNotBilledRTBySubscription(Subscription subscription) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countNotBilledRTBySubscription").setParameter("subscription", subscription).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNotBilledRTBySubscription", e);
            return 0L;
        }
    }

    public int moveNotBilledRTToUA(WalletInstance newWallet, Subscription subscription) {
        return getEntityManager().createNamedQuery("RatedTransaction.moveNotBilledRTToUA")
                .setParameter("newWallet", newWallet)
                .setParameter("newBillingAccount", newWallet.getUserAccount().getBillingAccount())
                .setParameter("newUserAccount", newWallet.getUserAccount())
                .setParameter("subscription", subscription).executeUpdate();
    }

    public int moveAndRerateNotBilledRTToUA(WalletInstance newWallet, Subscription subscription) {
        return getEntityManager().createNamedQuery("RatedTransaction.moveAndRerateNotBilledRTToUA")
                .setParameter("newWallet", newWallet)
                .setParameter("newBillingAccount", newWallet.getUserAccount().getBillingAccount())
                .setParameter("newUserAccount", newWallet.getUserAccount())
                .setParameter("subscription", subscription).executeUpdate();
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
        getEntityManager().createNamedQuery("RatedTransaction.cancelByRTIds").setParameter("now", new Date()).setParameter("rtIds", rsToCancelIds).executeUpdate();
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

        if (entity instanceof BillingAccount) {
            entity = billingAccountService.findById((Long) entity.getId());
        }

        if (entity instanceof Subscription) {
            entity = subscriptionService.findById((Long) entity.getId());
        }

        if (entity instanceof Order) {
            entity = orderService.findById((Long) entity.getId());
        }

        calculateAmountsAndCreateMinAmountTransactions(entity, null, billingRun.getLastTransactionDate(), billingRun.getInvoiceDate(), true, minAmountForAccounts);

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

        if (billingRun.isExceptionalBR()) {
            entity = billingAccountService.findById(entityId);
        } else {
            switch (billingRun.getBillingCycle().getType()) {
            case BILLINGACCOUNT:
                entity = billingAccountService.findById(entityId);
                // billingAccount = (BillingAccount) entity;
                break;

            case SUBSCRIPTION:
                entity = subscriptionService.findById(entityId);
                // billingAccount = ((Subscription) entity).getUserAccount() != null ? ((Subscription) entity).getUserAccount().getBillingAccount() : null;
                break;

            case ORDER:
                entity = orderService.findById(entityId);
                // if ((((Order) entity).getUserAccounts() != null) && !((Order) entity).getUserAccounts().isEmpty()) {
                // billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null ? (((Order)
                // entity).getUserAccounts().stream().findFirst().get()).getBillingAccount() : null;
                // }
                break;
            }
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
     * @param invoiceDate Invoice date
     * @param calculateAndUpdateTotalAmounts Should total amounts be calculated and entity updated with those amounts
     * @param minAmountForAccounts Booleans to knows if an accounts has minimum amount activated
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("rawtypes")
    public void calculateAmountsAndCreateMinAmountTransactions(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, Date invoiceDate, boolean calculateAndUpdateTotalAmounts,
            MinAmountForAccounts minAmountForAccounts) throws BusinessException {

        Amounts totalInvoiceableAmounts = null;

        List<RatedTransaction> minAmountTransactions = new ArrayList<>();
        List<ExtraMinAmount> extraMinAmounts = new ArrayList<>();

        Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

        if (billableEntity instanceof Order) {
            if (calculateAndUpdateTotalAmounts) {
                totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate, invoiceDate);
            }
        } else {
            // Create Min Amount RTs for hierarchy

            BillingAccount billingAccount = (billableEntity instanceof Subscription) ? ((Subscription) billableEntity).getUserAccount().getBillingAccount() : (BillingAccount) billableEntity;

            Class[] accountClasses = new Class[] { ServiceInstance.class, Subscription.class, UserAccount.class, BillingAccount.class, CustomerAccount.class, Customer.class };
            for (Class accountClass : accountClasses) {
                if (minAmountForAccounts.isMinAmountForAccountsActivated(accountClass, billableEntity)) {
                    MinAmountsResult minAmountsResults = createMinRTForAccount(billableEntity, billingAccount, lastTransactionDate, invoiceDate, minRatingDate, extraMinAmounts, accountClass);
                    extraMinAmounts = minAmountsResults.getExtraMinAmounts();
                    minAmountTransactions.addAll(minAmountsResults.getMinAmountTransactions());
                }
            }
            // get totalInvoicable for the billableEntity
            totalInvoiceableAmounts =
                    minAmountService.computeTotalInvoiceableAmount(billableEntity, new Date(0), lastTransactionDate, invoiceDate, INVOICING_PROCESS_TYPE);

            // Sum up
            final Amounts totalAmounts = new Amounts();
            extraMinAmounts.forEach(extraMinAmount -> extraMinAmount.getCreatedAmount().values().forEach(totalAmounts::addAmounts));
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

    /**
     * Create Rated transactions to reach minimum invoiced amount per subscription level. Only those subscriptions that have minimum invoice amount rule are considered. Updates minAmountTransactions parameter.
     *
     * @param billableEntity Entity to bill - entity for which minimum rated transactions should be created
     * @param billingAccount Billing account to associate new minimum amount Rated transactions with
     * @param lastTransactionDate Last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param extraMinAmounts Additional Rated transaction amounts created to reach minimum invoicing amount per account level
     * @param accountClass the account class which can be : ServiceInstance, Subscription or any class for the accounts hierarchy
     * @return MinAmountsResult Contains new rated transaction created to reach the minimum for an account class and the extra amount.
     * @throws BusinessException General Business exception
     */
    private MinAmountsResult createMinRTForAccount(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date invoiceUpToDate, Date minRatingDate, List<ExtraMinAmount> extraMinAmounts,
            @SuppressWarnings("rawtypes") Class accountClass) throws BusinessException {

        MinAmountsResult minAmountsResult = new MinAmountsResult();

        Map<Long, MinAmountData> accountToMinAmount =
                minAmountService.getInvoiceableAmountDataPerAccount(billableEntity, billingAccount, lastTransactionDate, invoiceUpToDate, extraMinAmounts, accountClass, INVOICING_PROCESS_TYPE);

        accountToMinAmount = minAmountService.prepareAccountsWithMinAmount(billableEntity, billingAccount, extraMinAmounts, accountClass, accountToMinAmount);

        // Create Rated transactions to reach a minimum amount per account level

        for (Entry<Long, MinAmountData> accountAmounts : accountToMinAmount.entrySet()) {
            Map<String, Amounts> minRTAmountMap = new HashMap<>();

            if (accountAmounts.getValue() == null || accountAmounts.getValue().getMinAmount() == null) {
                continue;
            }

            BigDecimal minAmount = accountAmounts.getValue().getMinAmount();
            String minAmountLabel = accountAmounts.getValue().getMinAmountLabel();
            BigDecimal totalInvoiceableAmount = appProvider.isEntreprise() ? accountAmounts.getValue().getAmounts().getAmountWithoutTax() : accountAmounts.getValue().getAmounts().getAmountWithTax();
            IInvoicingMinimumApplicable entity = accountAmounts.getValue().getEntity();

            Seller seller = accountAmounts.getValue().getSeller();
            if (seller == null) {
                throw new BusinessException("Default Seller is mandatory for invoice minimum (Customer.seller)");
            }
            String mapKeyPrefix = seller.getId().toString() + "_";

            BigDecimal diff = minAmount.subtract(totalInvoiceableAmount);
            if (diff.compareTo(BigDecimal.ZERO) <= 0 || (BigDecimal.ZERO.equals(totalInvoiceableAmount) && !paramBeanFactory.getInstance().getPropertyAsBoolean(APPLY_MINIMA_EVEN_ON_ZERO_TRANSACTION, true))) {
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
            RatedTransaction ratedTransaction = getNewMinRatedTransaction(billableEntity, billingAccount, minRatingDate, minAmountLabel, entity, seller, invoiceSubCategory, taxInfo, diff, code);

            minAmountsResult.addMinAmountRT(ratedTransaction);

            // Remember newly "created" transaction amounts, as they are not persisted yet to DB
            minRTAmountMap.put(mapKey, new Amounts(ratedTransaction.getUnitAmountWithoutTax(), ratedTransaction.getAmountWithTax(), ratedTransaction.getAmountTax()));
            extraMinAmounts.add(new ExtraMinAmount(entity, minRTAmountMap));

        }

        minAmountsResult.setExtraMinAmounts(extraMinAmounts);
        return minAmountsResult;
    }

    /**
     * Gets the minimum amount RT code used in Rated transaction.
     *
     * @param entity the entity
     * @param accountClass the account class
     * @return the minimum amount RT code
     */
    private String getMinAmountRTCode(IInvoicingMinimumApplicable entity, @SuppressWarnings("rawtypes") Class accountClass) {
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

    private OneShotChargeTemplate getMinimumChargeTemplate(IInvoicingMinimumApplicable entity) {
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
    private RatedTransaction getNewMinRatedTransaction(IBillableEntity billableEntity, BillingAccount billingAccount, Date minRatingDate, String minAmountLabel, IInvoicingMinimumApplicable entity, Seller seller,
            InvoiceSubCategory invoiceSubCategory, TaxInfo taxInfo, BigDecimal rtMinAmount, String code) {
        Tax tax = taxInfo.tax;
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        RatedTransaction rt = new RatedTransaction(minRatingDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], BigDecimal.ONE, amounts[0], amounts[1], amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount,
            null, invoiceSubCategory, null, null, null, null, null, null, null, null, null, null, null, code, minAmountLabel, null, null, seller, tax, tax.getPercent(), null, taxInfo.taxClass, null, RatedTransactionTypeEnum.MINIMUM, null, null);

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
     * Compute the invoice amount for order.
     * 
     * @param order order
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate last transaction date
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @return computed order's invoice amount.
     */
    private Amounts computeTotalOrderInvoiceAmount(Order order, Date firstTransactionDate, Date lastTransactionDate, Date invoiceUpToDate) {

//      boolean ignorePrepaidWallets = false;  TODO AKK if (prePaidWalletsIds != null && !prePaidWalletsIds.isEmpty()) {
        String query = "RatedTransaction.sumTotalInvoiceableByOrderNumber";
//      if (ignorePrepaidWallets) {
//          query = "RatedTransaction.sumTotalInvoiceableByOrderNumberExcludePrepaidWO";
//      }

        Query q = getEntityManager().createNamedQuery(query).setParameter("orderNumber", order.getOrderNumber()).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate",
            lastTransactionDate).setParameter("invoiceUpToDate", invoiceUpToDate);

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
     * @param invoiceUpToDate Date up to which a transaction will be included in the invoice based on its invoicing date value
     * @param ratedTransactionFilter Filter returning a list of rated transactions
     * @param rtPageSize Number of records to return
     * @return A list of RT entities
     * @throws BusinessException General exception
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> listRTsToInvoice(IBillableEntity entityToInvoice, Date firstTransactionDate, Date lastTransactionDate,
                                                   Date invoiceUpToDate, Filter ratedTransactionFilter, Integer rtPageSize) throws BusinessException {
        TypedQuery<RatedTransaction> query = null;
        if (ratedTransactionFilter != null) {
            final List<RatedTransaction> filteredListAsObjects = (List<RatedTransaction>) filterService.filteredListAsObjects(ratedTransactionFilter, null);
            return filteredListAsObjects.stream()
                    .filter(rt -> entityToInvoice instanceof BillingAccount && rt.getBillingAccount().getId().equals(entityToInvoice.getId()))
                    .collect(toList());
        } else if (entityToInvoice instanceof Subscription) {
            query = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceBySubscription", RatedTransaction.class).setParameter("subscriptionId", entityToInvoice.getId());

        } else if (entityToInvoice instanceof BillingAccount) {
            query = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccount", RatedTransaction.class).setParameter("billingAccountId", entityToInvoice.getId());

        } else if (entityToInvoice instanceof Order) {
            query = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByOrderNumber", RatedTransaction.class).setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber());
        }
        if (query != null) {
            if (rtPageSize != null) {
                query.setMaxResults(rtPageSize);
            }
            return query.setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("invoiceUpToDate", invoiceUpToDate).setHint("org.hibernate.readOnly", true).getResultList();
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
        return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccountAndIDs", RatedTransaction.class).setParameter("billingAccountId", billingAccountId).setParameter("listOfIds", ids)
            .getResultList();
    }

    /**
     * Determine if minimum RT transactions functionality is used at service level
     * 
     * @return True if exists any serviceInstance with minimumAmountEl value
     */
    @Deprecated
    public boolean isServiceMinRTsUsed() {

        Boolean booleanValue = ParamBean.getInstance().getBooleanValue("billing.minimumRating.global.enabled");
        if (booleanValue != null) {
            return booleanValue;
        }

        try {
            getEntityManager().createNamedQuery("ServiceInstance.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
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
        if (booleanValue != null) {
            return booleanValue;
        }

        try {
            getEntityManager().createNamedQuery("Subscription.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
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
        if (booleanValue != null) {
            return booleanValue;
        }

        try {
            getEntityManager().createNamedQuery("BillingAccount.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
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
        if (booleanValue != null) {
            return new boolean[] { booleanValue, booleanValue, booleanValue, booleanValue, booleanValue, booleanValue };
        }

        boolean baMin = false;
        boolean subMin = false;
        boolean servMin = false;
        boolean uaMin = false;
        boolean caMin = false;
        boolean custMin = false;

        EntityManager em = getEntityManager();

        try {
            em.createNamedQuery("BillingAccount.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
            baMin = true;
        } catch (NoResultException e) {
        }
        try {
            em.createNamedQuery("UserAccount.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
            uaMin = true;
        } catch (NoResultException e) {
        }
        try {
            em.createNamedQuery("Subscription.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
            subMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("ServiceInstance.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
            servMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("CustomerAccount.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
            caMin = true;
        } catch (NoResultException e) {
        }
        try {
            getEntityManager().createNamedQuery("Customer.getMinimumAmountUsed").setMaxResults(1).getSingleResult();
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
        return getEntityManager().createNamedQuery("RatedTransaction.deleteByLastTransactionDateAndStatus").setParameter("status", targetStatusList).setParameter("lastTransactionDate", lastTransactionDate)
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
     * Detach RTs From invoice.
     *
     * @param invoice invoice
     */
    public void detachRTsFromInvoice(Invoice invoice) {
        getEntityManager().createNamedQuery("RatedTransaction.detachRTsFromInvoice").setParameter("invoice", invoice).executeUpdate();
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

    /**
     * @param billingAccountCode
     * @param userAccountCode
     * @param subscriptionCode
     * @param serviceInstanceCode
     * @param chargeInstanceCode
     * @param unitAmountWithoutTax
     * @param quantity
     * @return
     */
    public RatedTransaction createRatedTransaction(String billingAccountCode, String userAccountCode,
            String subscriptionCode, String serviceInstanceCode, String chargeInstanceCode, Date usageDate,
            BigDecimal unitAmountWithoutTax, BigDecimal quantity, String param1, String param2, String param3, String paramExtra) {

        String errors = "";
        if (billingAccountCode == null) {
            errors = errors + " billingAccountCode,";
        }
        if (subscriptionCode == null) {
            errors = errors + " subscriptionCode,";
        }
        if (serviceInstanceCode == null) {
            errors = errors + " sericeInstanceCode,";
        }
        if (chargeInstanceCode == null) {
            errors = errors + " chargeInstanceCode,";
        }
        if (!errors.isBlank()) {
            throw new ValidationException("Missing fields to create RatedTransaction : " + errors);
        }
        usageDate = usageDate == null ? new Date() : usageDate;

        BillingAccount billingAccount = (BillingAccount) tryToFindByEntityClassAndCode(BillingAccount.class,
                billingAccountCode);

        UserAccount userAccount = userAccountCode != null ? (UserAccount) tryToFindByEntityClassAndCode(UserAccount.class, userAccountCode) : billingAccount.getUsersAccounts().get(0);

        Map<String, Object> subscriptionCriterions = ImmutableMap.of("code", subscriptionCode, "userAccount", userAccount, "status", SubscriptionStatusEnum.ACTIVE);
        Subscription subscription = (Subscription) tryToFindByEntityClassAndMap(Subscription.class, subscriptionCriterions);

        Map<String, Object> serviceInstanceCriterions = ImmutableMap.of("code", serviceInstanceCode, "subscription", subscription, "status", InstanceStatusEnum.ACTIVE);
        ServiceInstance serviceInstance = (ServiceInstance) tryToFindByEntityClassAndMap(ServiceInstance.class, serviceInstanceCriterions);
        Map<String, Object> chargeInstanceCriterions = ImmutableMap.of("code", chargeInstanceCode, "serviceInstance", serviceInstance, "subscription", subscription, "status", InstanceStatusEnum.ACTIVE);
        ChargeInstance chargeInstance = (ChargeInstance) tryToFindByEntityClassAndMap(ChargeInstance.class, chargeInstanceCriterions);

        AccountingArticle accountingArticle = accountingArticleService.getAccountingArticleByChargeInstance(chargeInstance);
        TaxInfo taxInfo = taxMappingService.determineTax(chargeInstance, new Date(), accountingArticle);
        TaxClass taxClass = taxInfo.taxClass;

        final BigDecimal taxPercent = taxInfo.tax.getPercent();
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(unitAmountWithoutTax, unitAmountWithoutTax,
                taxPercent, appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal AmountWithoutTax = unitAmountWithoutTax.multiply(quantity);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(AmountWithoutTax, AmountWithoutTax, taxPercent,
                appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        RatedTransaction rt = new RatedTransaction(usageDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], quantity,
                amounts[0], amounts[1], amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, userAccount,
                null, param1, param2, param3, paramExtra, null, subscription, null, null, null, subscription.getOffer(), null,
                serviceInstance.getCode(), serviceInstance.getCode(), null, null, subscription.getSeller(), taxInfo.tax,
                taxPercent, serviceInstance, taxClass, null, RatedTransactionTypeEnum.MANUAL, chargeInstance, null);
        rt.setAccountingArticle(accountingArticle);
        create(rt);
        updateBAForRT(List.of(rt));
        return rt;
    }

    /**
     * @param ratedTransaction
     * @param unitAmountWithoutTax
     * @param quantity
     * @return
     */
    public void updateRatedTransaction(RatedTransaction ratedTransaction, BigDecimal unitAmountWithoutTax,
            BigDecimal quantity, String param1, String param2, String param3, String paramExtra) {
        BigDecimal[] unitAmounts = NumberUtils.computeDerivedAmounts(unitAmountWithoutTax, unitAmountWithoutTax,
                ratedTransaction.getTaxPercent(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal AmountWithoutTax = unitAmountWithoutTax.multiply(quantity);
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(AmountWithoutTax, AmountWithoutTax, ratedTransaction.getTaxPercent(),
                appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        ratedTransaction.setUnitAmountWithoutTax(unitAmounts[0]);
        ratedTransaction.setUnitAmountWithTax(unitAmounts[1]);
        ratedTransaction.setUnitAmountTax(unitAmounts[2]);
        ratedTransaction.setQuantity(quantity);
        ratedTransaction.setAmountWithoutTax(amounts[0]);
        ratedTransaction.setAmountWithTax(amounts[1]);
        ratedTransaction.setAmountTax(amounts[2]);
        

        if(param1 != null) {
            ratedTransaction.setParameter1(param1);
        }
        if(param2 != null) {
            ratedTransaction.setParameter2(param2);
        }
        if(param3 != null) {
            ratedTransaction.setParameter3(param3);
        }
        if(paramExtra != null) {
            ratedTransaction.setParameterExtra(paramExtra);
        }

        update(ratedTransaction);

    }

    /**
     * Find Rated transaction by code
     *
     * @param code ratedTransaction's code
     * @return RatedTransaction
     */
    public RatedTransaction findByCode(String code) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "rt", null);
        qb.addCriterion("code", "=", code, true);
        try {
            return (RatedTransaction) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    public List<Map<String, Object>> getGroupedRTs(List<Long> ratedTransactionIds) {

        Map<String, Object> params = new HashMap<>();
        params.put("ids", ratedTransactionIds);

        String query = "SELECT  string_agg(concat(rt.id, ''), ',') as rated_transaction_ids," +
                " rt.billingAccount.id as billing_account__id, rt.accountingCode.id as accounting_code_id, rt.description as label, SUM(rt.quantity) AS quantity, "
                + "                 rt.unitAmountWithoutTax as unit_amount_without_tax, rt.unitAmountWithTax as unit_amount_with_tax, "
                + "                 SUM(rt.amountWithoutTax) as sum_without_tax, SUM(rt.amountWithTax) as sum_with_tax, "
                + "                 rt.offerTemplate.id as offer_id, rt.serviceInstance.id as service_instance_id,"
                + "                 rt.usageDate as usage_date, rt.startDate as start_date, rt.endDate as end_date,"
                + "                 rt.orderNumber as order_number, rt.subscription.id as subscription_id, rt.taxPercent as tax_percent, rt.tax.id as tax_id, "
                + "                 rt.infoOrder.order.id as order_id, rt.infoOrder.productVersion.id as product_version_id,"
                + "                 rt.infoOrder.orderLot.id as order_lot_id, rt.chargeInstance.id as charge_instance_id, rt.accountingArticle.id as article_id "
                + " FROM RatedTransaction rt WHERE rt.id in (:ids) and rt.accountingArticle.ignoreAggregation = false"
                + " GROUP BY rt.billingAccount.id, rt.accountingCode.id, rt.description, "
                + "         rt.unitAmountWithoutTax, rt.unitAmountWithTax, rt.offerTemplate.id, rt.serviceInstance.id, rt.usageDate, rt.startDate,"
                + "         rt.endDate, rt.orderNumber, rt.subscription.id, rt.taxPercent, rt.tax.id, "
                + "         rt.infoOrder.order.id, rt.infoOrder.productVersion.id, rt.infoOrder.orderLot.id," +
                "           rt.chargeInstance.id, rt.accountingArticle.id";
        List<Map<String, Object>> groupedRTsWithAggregation = getSelectQueryAsMap(query, params);
        groupedRTsWithAggregation.addAll(getNoAggregationRTs(ratedTransactionIds));
        return groupedRTsWithAggregation;
    }

    public List<Map<String, Object>> getNoAggregationRTs(List<Long> ratedTransactionIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ratedTransactionIds);
        String query =
                "SELECT CAST(rt.id as string) as rated_transaction_ids, rt.billingAccount.id as billing_account__id, rt.accountingCode.id as accounting_code_id," +
                " rt.description as label,rt.quantity AS quantity," +
                " rt.amountWithoutTax as sum_without_tax, rt.amountWithTax as sum_with_tax, rt.offerTemplate.id as offer_id, rt.serviceInstance.id as service_instance_id," +
                " rt.startDate as start_date, rt.endDate as end_date, rt.orderNumber as order_number, " +
                " s.id as subscription_id, s.order.id as commercial_order_id, rt.taxPercent as tax_percent, rt.tax.id as tax_id," +
                " rt.infoOrder.order.id as order_id, rt.infoOrder.productVersion.id as product_version_id, rt.infoOrder.orderLot.id as order_lot_id," +
                " rt.chargeInstance.id as charge_instance_id, rt.parameter2 as parameter_2, rt.accountingArticle.id as article_id," +
                " rt.discountedRatedTransaction as discounted_ratedtransaction_id," +
                " rt.unitAmountWithoutTax as unit_amount_without_tax, rt.unitAmountWithTax as unit_amount_with_tax " +
                " FROM RatedTransaction rt left join rt.subscription s " +
                " WHERE rt.id in (:ids) and rt.accountingArticle.ignoreAggregation = true";
        return getSelectQueryAsMap(query, params);
    }

    public List<Map<String, Object>> getGroupedRTsWithAggregation(List<Long> ratedTransactionIds, AggregationConfiguration aggregationConfiguration) {

        Map<String, Object> params = new HashMap<>();
        params.put("ids", ratedTransactionIds);
        String usageDateAggregation = getUsageDateAggregation(aggregationConfiguration, " rt.usage_date ");
        String query = "SELECT  string_agg(concat(rt.id, ''), ',') as rated_transaction_ids, rt.billing_account__id, "
                + "              rt.accounting_code_id, rt.description as label, SUM(rt.quantity) AS quantity, "
                + "              sum(rt.amount_without_tax) as sum_without_tax, sum(rt.amount_with_tax) as sum_with_tax,"
                + "              sum(rt.amount_with_tax) / sum(rt.quantity) as unit_price,"
                + "              rt.offer_id, rt.service_instance_id, "
                +                usageDateAggregation + " as usage_date, min(rt.start_date) as start_date, "
                + "              max(rt.end_date) as end_date, rt.order_number, rt.tax_percent, rt.tax_id, "
                + "              rt.order_id, rt.product_version_id, rt.order_lot_id, charge_instance_id, rt.accounting_article_id ,rt. discounted_Ratedtransaction_id "
                + "    FROM billing_rated_transaction rt WHERE id in (:ids) "
                + "    GROUP BY rt.billing_account__id, rt.accounting_code_id, rt.description, "
                + "             rt.offer_id, rt.service_instance_id, " + usageDateAggregation + ",  "
                + "             rt.order_number, rt.tax_percent, rt.tax_id, "
                + "             rt.order_id, rt.product_version_id, rt.order_lot_id, charge_instance_id, rt.accounting_article_id ,discounted_Ratedtransaction_id, unit_amount_without_tax order by unit_amount_without_tax desc  ";
        return executeNativeSelectQuery(query, params);
    }

    public int makeAsProcessed(List<Long> ratedTransactionIds) {
        return getEntityManager()
                    .createNamedQuery("RatedTransaction.markAsProcessed")
                    .setParameter("listOfIds", ratedTransactionIds)
                    .executeUpdate();
    }

    public void linkRTWithInvoiceLine(Map<Long, List<Long>> iLIdsRtIdsCorrespondence) {
        for (Map.Entry<Long, List<Long>> entry : iLIdsRtIdsCorrespondence.entrySet()) {
            final List<Long> ratedTransactionsIDs = entry.getValue();
            final Long invoiceLineID = entry.getKey();
            linkRTsToIL(ratedTransactionsIDs, invoiceLineID);
        }
    }

    public void linkRTsToIL(final List<Long> ratedTransactionsIDs, final Long invoiceLineID) {
		final int maxValue = ParamBean.getInstance().getPropertyAsInteger("database.number.of.inlist.limit", SHORT_MAX_VALUE);
        if (ratedTransactionsIDs.size() > maxValue) {
            SubListCreator<Long> subLists = new SubListCreator<>(ratedTransactionsIDs, (1 + (ratedTransactionsIDs.size() / maxValue)));
            while (subLists.isHasNext()) {
                linkRTsWithILByIds(invoiceLineID, subLists.getNextWorkSet());
            }
        } else {
            linkRTsWithILByIds(invoiceLineID, ratedTransactionsIDs);
        }
    }

    private void linkRTsWithILByIds( Long invoiceLineId, final List<Long> ids) {
        getEntityManager().createNamedQuery("RatedTransaction.linkRTWithInvoiceLine")
                .setParameter("il", invoiceLineId)
                .setParameter("ids", ids).executeUpdate();
    }

    /**
     * @param aggregationConfiguration
     * @param be
     * @param lastTransactionDate
     * @param invoiceDate
     * @param filter
     * @return
     */
    public List<Map<String, Object>> getGroupedRTsWithAggregation(AggregationConfiguration aggregationConfiguration,
            BillingRun billingRun, IBillableEntity be, Date lastTransactionDate, Date invoiceDate, Filter filter) {

        if (filter != null) {

            // TODO #MEL use of filter must be reviewed
            List<RatedTransaction> ratedTransactions = (List<RatedTransaction>) filterService.filteredListAsObjects(filter, null);
            List<Long> ratedTransactionIds = null;
            if (billingRun.isExceptionalBR()) {
                ratedTransactionIds = ratedTransactions.stream().filter(rt -> (rt.getStatus() == RatedTransactionStatusEnum.OPEN && rt.getBillingRun() == null))
                        .map(RatedTransaction::getId)
                        .collect(toList());
            } else {
                ratedTransactionIds = ratedTransactions.stream().map(RatedTransaction::getId).collect(toList());
            }
            if (!ratedTransactionIds.isEmpty()) {
                return getGroupedRTsWithAggregation(ratedTransactionIds, aggregationConfiguration);
            }
        }

        Map<String, Object> params = buildParams(billingRun, lastTransactionDate);
        String entityCondition = getEntityCondition(be, params);
        String usageDateAggregation = getUsageDateAggregation(aggregationConfiguration);

        final String unitAmount = aggregationConfiguration.isAggregationPerUnitAmount() ?
                "(case when sum(rt.quantity)=0 then sum(rt.amountWithoutTax) else (sum(rt.amountWithoutTax) / sum(rt.quantity)) end) as unit_amount_without_tax, (case when sum(rt.quantity)=0 then sum(rt.amountWithTax) else (sum(rt.amountWithTax) / sum(rt.quantity)) end)  as unit_amount_with_tax," :
                "rt.unitAmountWithoutTax as unit_amount_without_tax, rt.unitAmountWithTax as unit_amount_with_tax,  ";
        final String unitAmountGroupBy = aggregationConfiguration.isAggregationPerUnitAmount() ? "" : " rt.unitAmountWithoutTax, rt.unitAmountWithTax,  ";
        String query =
                "SELECT string_agg_long(rt.id) as rated_transaction_ids, rt.billingAccount.id as billing_account__id, rt.accountingCode.id as accounting_code_id, rt.description as label, SUM(rt.quantity) AS quantity, "
                        + unitAmount + " SUM(rt.amountWithoutTax) as sum_without_tax, SUM(rt.amountWithTax) as sum_with_tax, rt.offerTemplate.id as offer_id, rt.serviceInstance.id as service_instance_id, "
                        + usageDateAggregation + " as usage_date, min(rt.startDate) as start_date, max(rt.endDate) as end_date, rt.orderNumber as order_number, "
                        + " s.id as subscription_id, s.order.id as commercial_order_id, rt.taxPercent as tax_percent, rt.tax.id as tax_id, "
                        + " rt.infoOrder.order.id as order_id, rt.infoOrder.productVersion.id as product_version_id, rt.infoOrder.orderLot.id as order_lot_id, "
                        + " rt.chargeInstance.id as charge_instance_id, rt.parameter2 as parameter_2, rt.accountingArticle.id as article_id, rt.discountedRatedTransaction as discounted_ratedtransaction_id"
                        + " FROM RatedTransaction rt left join rt.subscription s WHERE " + entityCondition
                        + " AND rt.status = 'OPEN' AND :firstTransactionDate <= rt.usageDate AND rt.usageDate < :lastTransactionDate"
                        + " and (rt.invoicingDate is NULL or rt.invoicingDate < :invoiceUpToDate) AND rt.accountingArticle.ignoreAggregation = false"
                        + " GROUP BY rt.billingAccount.id, rt.accountingCode.id, rt.description, rt.offerTemplate.id, rt.serviceInstance.id, " + unitAmountGroupBy
                        + usageDateAggregation + ", rt.startDate, rt.endDate, rt.orderNumber, s.id,  s.order.id, rt.taxPercent, rt.tax.id, "
                        + " rt.infoOrder.order.id, rt.infoOrder.productVersion.id, rt.infoOrder.orderLot.id, rt.chargeInstance.id, rt.parameter2, rt.accountingArticle.id, rt.discountedRatedTransaction";
        List<Map<String, Object>> groupedRTsWithAggregation = getSelectQueryAsMap(query, params);
        groupedRTsWithAggregation.addAll(getGroupedRTsWithoutAggregation(billingRun, be, lastTransactionDate));
        return groupedRTsWithAggregation;
    }

    private Map<String, Object> buildParams(BillingRun billingRun, Date lastTransactionDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("firstTransactionDate", new Date(0));
        params.put("lastTransactionDate", lastTransactionDate);
        params.put("invoiceUpToDate", billingRun.getInvoiceDate());
        return params;
    }

    private String getEntityCondition(IBillableEntity be, Map<String, Object> params) {
        String entityCondition = "";
        if (be instanceof Subscription) {
            params.put("entityKey", be.getId());
            entityCondition = " rt.subscription.id=:entityKey";
        } else if (be instanceof BillingAccount) {
            params.put("entityKey", be.getId());
            entityCondition = " rt.billingAccount.id =:entityKey ";
        } else if (be instanceof Order) {
            params.put("entityKey", ((Order) be).getOrderNumber());
            entityCondition = " rt.orderNumber =:entityKey ";
        }
        return entityCondition;
    }

    private String getUsageDateAggregation(AggregationConfiguration aggregationConfiguration) {
        return getUsageDateAggregation(aggregationConfiguration, " rt.usageDate ");
    }

    private String getUsageDateAggregation(AggregationConfiguration aggregationConfiguration, String usageDateColumn) {
        switch (aggregationConfiguration.getDateAggregationOption()) {
        case MONTH_OF_USAGE_DATE:
            return " TO_CHAR(" + usageDateColumn + ", 'YYYY-MM') ";
        case DAY_OF_USAGE_DATE:
            return " TO_CHAR(" + usageDateColumn + ", 'YYYY-MM-DD') ";
        case WEEK_OF_USAGE_DATE:
            return " TO_CHAR(" + usageDateColumn + ", 'YYYY-WW') ";
        case NO_DATE_AGGREGATION:
            return usageDateColumn;
        }
        return usageDateColumn;
    }

    public List<Map<String, Object>> getGroupedRTsWithoutAggregation(BillingRun billingRun,
                                                                     IBillableEntity be, Date lastTransactionDate) {
        Map<String, Object> params = buildParams(billingRun, lastTransactionDate);
        String entityCondition = getEntityCondition(be, params);
        String query =
                "SELECT CAST(rt.id as string) as rated_transaction_ids, rt.billingAccount.id as billing_account__id, rt.accountingCode.id as accounting_code_id," +
                        " rt.description as label,rt.quantity AS quantity," +
                        " rt.amountWithoutTax as sum_without_tax, rt.amountWithTax as sum_with_tax, rt.offerTemplate.id as offer_id, rt.serviceInstance.id as service_instance_id," +
                        " rt.startDate as start_date, rt.endDate as end_date, rt.orderNumber as order_number, " +
                        " s.id as subscription_id, s.order.id as commercial_order_id, rt.taxPercent as tax_percent, rt.tax.id as tax_id," +
                        " rt.infoOrder.order.id as order_id, rt.infoOrder.productVersion.id as product_version_id, rt.infoOrder.orderLot.id as order_lot_id," +
                        " rt.chargeInstance.id as charge_instance_id, rt.parameter2 as parameter_2, rt.accountingArticle.id as article_id," +
                        " rt.discountedRatedTransaction as discounted_ratedtransaction_id" +
                        " FROM RatedTransaction rt left join rt.subscription s " +
                        " WHERE " + entityCondition + " AND rt.status = 'OPEN' AND :firstTransactionDate <= rt.usageDate " +
                        " AND rt.usageDate < :lastTransactionDate AND (rt.invoicingDate is NULL or rt.invoicingDate < :invoiceUpToDate) " +
                        " AND rt.accountingArticle.ignoreAggregation = true";
        return getSelectQueryAsMap(query, params);
    }

    public List<BillingAccount> applyInvoicingRules(List<RatedTransaction> rTs) {
        List<BillingAccount> billingAccounts = null;
        if (rTs.size() !=0) {
            List<Long> ratedTransactionIds = rTs.stream().map(RatedTransaction::getId).collect(toList());
            
            Query query = getEntityManager().createQuery(
                "SELECT rt FROM RatedTransaction rt WHERE rt.id in (:ids) "
                        + " AND  rt.status = 'OPEN' And rt.originBillingAccount.id is null")
                    .setParameter("ids", ratedTransactionIds);

            List<RatedTransaction> rtsResults = query.getResultList();
            billingAccounts = updateBAForRT(rtsResults);
        }
        return billingAccounts;
    }
    
    public List<BillingAccount> updateBAForRT(List<RatedTransaction> rtsResults) {        
        List<BillingAccount> billingAccountsAfter = new ArrayList<>();
        if (rtsResults.size() !=0) {  
            
            Map<BillingAccount, List<RatedTransaction>> rtGroupedByBA = rtsResults.stream().collect(Collectors.groupingBy(wo -> wo.getBillingAccount()));
            
            for (Entry<BillingAccount, List<RatedTransaction>> rtGrpByBAElement : rtGroupedByBA.entrySet()) {
                BillingAccount billingAccount = rtGrpByBAElement.getKey();
                List<RatedTransaction> lstRatedTransaction = rtGrpByBAElement.getValue();
                boolean isApply = false;
                for(RatedTransaction rt : lstRatedTransaction) {
                    if(rt.getRulesContract() != null) {
                        List<BillingRule> billingRules = billingRulesService.findAllBillingRulesByBillingAccountAndContract(billingAccount, rt.getRulesContract());                
                        isApply = false;
                        for(BillingRule billingRule : billingRules) { 
                            if(!isApply) {
                                Boolean eCriteriaEL = false;
                                try {
                                    eCriteriaEL = checkCriteriaEL(rt, billingRule.getCriteriaEL());
                                } catch (BusinessException e) {
                                    rt.setStatus(RatedTransactionStatusEnum.REJECTED);
                                    rt.setRejectReason("Error evaluating criteriaEL [id=" + billingRule.getId() + ", priority= " + 
                                        billingRule.getPriority() + ", criteriaEL=" + billingRule.getCriteriaEL() + "] for RT [id=" + 
                                        rt.getId() + "]: Error in criteriaEL evaluation");
                                    update(rt);
                                    isApply = true;
                                }
                                if(eCriteriaEL != null && eCriteriaEL) {                            
                                    String eInvoicedBACodeEL = null;
                                    try {
                                        eInvoicedBACodeEL = evaluateInvoicedBACodeEL(rt, billingRule.getInvoicedBACodeEL());
                                    } catch (BusinessException e) {
                                        rt.setStatus(RatedTransactionStatusEnum.REJECTED);
                                        rt.setRejectReason("Error evaluating invoicedBillingAccountCodeEL [id=" + 
                                            billingRule.getId() + ", priority=" + billingRule.getPriority() + 
                                            ", invoicedBillingAccountCodeEL = " + billingRule.getInvoicedBACodeEL() + 
                                            "] for RT [id=" + rt.getId() + "}]: Error in invoicedBillingAccountCodeEL evaluation");
                                        update(rt);
                                        isApply = true;
                                    }
                                    if (eInvoicedBACodeEL != null) {
                                        if ("".equals(eInvoicedBACodeEL)){
                                            rt.setStatus(RatedTransactionStatusEnum.REJECTED);
                                            rt.setRejectReason("Error evaluating invoicedBillingAccountCodeEL [id=" + 
                                                billingRule.getId() + ", priority=" + billingRule.getPriority() + 
                                                ", invoicedBillingAccountCodeEL = " + billingRule.getInvoicedBACodeEL() + 
                                                "] for RT [id=" + rt.getId() + "}]: Error in invoicedBillingAccountCodeEL evaluation");
                                            update(rt);
                                            isApply = true;
                                        }
                                        else {
                                            BillingAccount billingAccountByCode = billingAccountService.findByCode(eInvoicedBACodeEL);
                                            if (billingAccountByCode != null) {
                                                rt.setOriginBillingAccount(rt.getBillingAccount());
                                                rt.setBillingAccount(billingAccountByCode);
                                                update(rt);                                                
                                                if(!isExistInBillingAccountLists(billingAccountsAfter, billingAccountByCode)) {    
                                                    billingAccountsAfter.add(billingAccountByCode);
                                                }
                                                isApply = true;
                                            }
                                            else {
                                                rt.setStatus(RatedTransactionStatusEnum.REJECTED);
                                                rt.setRejectReason("Billing redirection rule [id=" + billingRule.getId() + ", priority= " + 
                                                        billingRule.getPriority() + ", invoicedBillingAccountCodeEL=" + billingRule.getInvoicedBACodeEL() 
                                                        + "] redirects to unknown billing account [code=" + eInvoicedBACodeEL + "] for RT [id=" + 
                                                        rt.getId() + "]");
                                                update(rt);
                                                isApply = true;
                                            } 
                                        }                                        
                                    }
                                    else {
                                        rt.setStatus(RatedTransactionStatusEnum.REJECTED);
                                        rt.setRejectReason("Error evaluating invoicedBillingAccountCodeEL [id=" + 
                                            billingRule.getId() + ", priority=" + billingRule.getPriority() + 
                                            ", invoicedBillingAccountCodeEL = " + billingRule.getInvoicedBACodeEL() + 
                                            "] for RT [id=" + rt.getId() + "}]: Error in invoicedBillingAccountCodeEL evaluation");
                                        update(rt);
                                        isApply = true;
                                    }
                                }
                            }
                        }
                        if(!isApply && billingRules.size() != 0) {
                            //same BillingAccount
                            rt.setOriginBillingAccount(rt.getBillingAccount());
                            update(rt);
                            if(!isExistInBillingAccountLists(billingAccountsAfter, rt.getBillingAccount())) {
                                billingAccountsAfter.add(rt.getBillingAccount());
                            }
                        }
                    }                
                }
            }
        }
        return billingAccountsAfter;
    }
    
    private Boolean isExistInBillingAccountLists(List<BillingAccount> bAs, BillingAccount bA) {
        List<Long> listBAIds = new ArrayList<Long>();
        for (BillingAccount element : bAs) {
            listBAIds.add(element.getId());
        }
        return listBAIds.contains(bA.getId());   
    }

    private Boolean checkCriteriaEL(RatedTransaction rt, String expression) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = expression.replace("\\", "");
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("rt", rt);
        Boolean code = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        if (code != null) {
           return code;
        }
        return null;       
    }
    
    private String evaluateInvoicedBACodeEL(RatedTransaction rt, String expression) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = expression.replace("\\", "");
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("rt", rt);
        String code = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        if (code != null) {
            return code;
        }
        return null;
    }

    public int detachRTFromSubCatAgg(List<Long> rtIds) {
        return getEntityManager().createNamedQuery("RatedTransaction.detachRTFromSubCat")
                .setParameter("rtIds", rtIds)
                .executeUpdate();
    }

    /**
     * Bridge discount RatedTransactions with discounted Rated transaction
     * 
     * @param minId A range of Rated transactions to process - Minimum id
     * @param maxId A range of Rated transactions to process - Maximum id
     */
    public void bridgeDiscountRTs(Long minId, Long maxId) {

        getEntityManager().createNamedQuery("RatedTransaction.massUpdateWithDiscountedRT" + (EntityManagerProvider.isDBOracle() ? "Oracle" : "")).setParameter("minId", minId).setParameter("maxId", maxId)
                .executeUpdate();
    }

    public void reopenRatedTransaction(List<Long> ratedTransactionIds) {
        if(ratedTransactionIds != null && !ratedTransactionIds.isEmpty()) {
            getEntityManager().createNamedQuery("RatedTransaction.reopenRatedTransactions")
                    .setParameter("rtIds", ratedTransactionIds)
                    .setParameter("now", new Date())
                    .executeUpdate();
        }
    }

    public RatedTransaction findByEDR(Long edrId) {
        try {
            return (RatedTransaction) getEntityManager().createNamedQuery("RatedTransaction.findByEDRId").setParameter("EDR_ID", edrId).getSingleResult();

        } catch (NoResultException e) {
            log.warn("No ratedTransaction found with the given EDR id={}", edrId);
            return null;
        }
    }
    
    public RatedTransaction findByServiceInstance(Long serviceInstanceId) {
        try {
            return (RatedTransaction) getEntityManager().createNamedQuery("RatedTransaction.findByServiceInstance").setParameter("serviceInstanceId", serviceInstanceId).getResultList().get(0);

        } catch (NoResultException e) {
            log.warn("No service instance found with the given id={}", serviceInstanceId);
            return null;
        }
    }
    
    /**
	 * @param result
	 * @param billableEntity
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int calculateAccountingArticle(JobExecutionResultImpl result, IBillableEntity billableEntity, Integer pageSize, Integer pageIndex) {
		List<RatedTransaction> ratedTransactions = getRatedTransactionHavingEmptyAccountingArticle(billableEntity, pageSize, pageIndex);
		ratedTransactions.stream().forEach(rt->rt.setAccountingArticle(accountingArticleService.getAccountingArticleByChargeInstance(rt.getChargeInstance())));
	    return ratedTransactions.size();
	}
    
	/**
	 * @param billableEntity
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<RatedTransaction> getRatedTransactionHavingEmptyAccountingArticle(IBillableEntity billableEntity, Integer pageSize, Integer pageIndex) {
		QueryBuilder qb = new QueryBuilder("select rt from RatedTransaction rt ", "rt");
		if (billableEntity instanceof Subscription) {
        	qb.addCriterion("rt.subscription.id ", "=", billableEntity.getId(), false);
        } else if (billableEntity instanceof BillingAccount) {
        	qb.addCriterion("rt.billingAccount.id ", "=", billableEntity.getId(), false);
        } else if (billableEntity instanceof Order) {
        	qb.addCriterion("orderNumber ","=", ((Order) billableEntity).getOrderNumber(), false);;
        }
        qb.addSql(" rt.status='OPEN' and accountingArticle is null ");
        try {
            final Query query = qb.getQuery(getEntityManager());
            if(pageIndex!=null && pageSize!=null) {
            	query.setMaxResults(pageSize).setFirstResult(pageIndex * pageSize);
            }
			return query.getResultList();
        } catch (NoResultException e) {
        	log.error(e.getMessage());
            return null;
        }
	}
    
}