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

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.meveo.commons.utils.NumberUtils.computeDerivedAmounts;
import static org.meveo.commons.utils.ParamBean.getInstance;
import static org.meveo.model.BaseEntity.NB_DECIMALS;
import static org.meveo.model.billing.BillingEntityTypeEnum.BILLINGACCOUNT;
import static org.meveo.model.billing.DateAggregationOption.NO_DATE_AGGREGATION;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.job.AggregationConfiguration;
import org.meveo.admin.job.InvoiceLinesFactory;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.generics.GenericRequestMapper;
import org.meveo.api.generics.PersistenceServiceHelper;
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
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.ApplyMinimumModeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DateAggregationOption;
import org.meveo.model.billing.ExtraMinAmount;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.MinAmountData;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.MinAmountsResult;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionAction;
import org.meveo.model.billing.RatedTransactionMinAmountTypeEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RatedTransactionTypeEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.billing.WalletOperationNative;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderInfo;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.IInvoicingMinimumApplicable;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.cpq.BillingRuleService;
import org.meveo.service.cpq.ContractService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.order.OrderService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
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

    private static final String QUERY_FILTER = "a.status = 'OPEN' AND :firstTransactionDate <= a.usageDate AND (a.invoicingDate is NULL or a.invoicingDate < :invoiceUpToDate) AND a.accountingArticle.ignoreAggregation = false ";

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
    private AccountingArticleService accountingArticleService;

    @Inject
    private AccountingCodeService accountingCodeService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private DiscountPlanItemService discountPlanItemService;

    @Inject
    private ContractService contractService;

    @Inject
    private FinanceSettingsService financeSettingsService;

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

//        EntityManager em = getEntityManager();

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
            if (financeSettingsService.isBillingRedirectionRulesEnabled()) {
                applyInvoicingRules(ratedTransaction);
            }
            create(ratedTransaction);
            walletOperation.setRatedTransaction(ratedTransaction);
        }
        walletOperationService.update(walletOperation);
        return ratedTransaction;
    }

    /**
     * Create Rated transaction from wallet operation.
     *
     * @param walletOperations Wallet operations
     * @return A list of Ids of created Rated transactions
     * @throws BusinessException business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Long> createRatedTransactionsInBatch(List<WalletOperationNative> walletOperations) throws BusinessException {

        EntityManager em = getEntityManager();
        boolean eventsEnabled = areEventsEnabled(NotificationEventTypeEnum.CREATED);

        boolean cftEndPeriodEnabled = customFieldTemplateService.areCFTEndPeriodEventsEnabled(new RatedTransaction());

        boolean billingRedirectionEnabled = financeSettingsService.isBillingRedirectionRulesEnabled();

        String providerCode = currentUser.getProviderCode();
        final String schemaPrefix = providerCode != null ? EntityManagerProvider.convertToSchemaName(providerCode) + "." : "";

        // Convert WO to RT and persist RT
        Long[][] woRtIds = new Long[walletOperations.size()][2];

        List<Long> allRtIds = new ArrayList<Long>();

        int i = 0;
        for (WalletOperationNative walletOperation : walletOperations) {
            if (i > 0 && i % 2000 == 0) {
                em.flush();
                em.clear();
            }

            RatedTransaction ratedTransaction = new RatedTransaction();

            ratedTransaction.setCreated(new Date());
            ratedTransaction.setCode(walletOperation.getCode());
            ratedTransaction.setDescription(walletOperation.getDescription());
            if (walletOperation.getChargeInstanceId() != null) {
                ratedTransaction.setChargeInstance(em.getReference(ChargeInstance.class, walletOperation.getChargeInstanceId()));
            }
            ratedTransaction.setUsageDate(walletOperation.getOperationDate());
            ratedTransaction.setUnitAmountWithoutTax(walletOperation.getUnitAmountWithoutTax());
            ratedTransaction.setUnitAmountWithTax(walletOperation.getUnitAmountWithTax());
            ratedTransaction.setUnitAmountTax(walletOperation.getUnitAmountTax());
            ratedTransaction.setQuantity(walletOperation.getQuantity());
            ratedTransaction.setAmountWithoutTax(walletOperation.getAmountWithoutTax());
            ratedTransaction.setAmountWithTax(walletOperation.getAmountWithTax());

            ratedTransaction.setTransactionalUnitAmountWithoutTax(walletOperation.getTransactionalUnitAmountWithoutTax());
            ratedTransaction.setTransactionalUnitAmountWithTax(walletOperation.getTransactionalUnitAmountWithTax());
            ratedTransaction.setTransactionalUnitAmountTax(walletOperation.getTransactionalUnitAmountTax());
            ratedTransaction.setTransactionalAmountWithoutTax(walletOperation.getTransactionalAmountWithoutTax());
            ratedTransaction.setTransactionalAmountWithTax(walletOperation.getTransactionalAmountWithTax());

            ratedTransaction.setInputQuantity(walletOperation.getInputQuantity());
            ratedTransaction.setRawAmountWithTax(walletOperation.getRawAmountWithTax());
            ratedTransaction.setRawAmountWithoutTax(walletOperation.getRawAmountWithoutTax());
            ratedTransaction.setAmountTax(walletOperation.getAmountTax());
            ratedTransaction.setTransactionalAmountTax(walletOperation.getTransactionalAmountTax());
            if (walletOperation.getWalletId() != null) {
                ratedTransaction.setWallet(em.getReference(WalletInstance.class, walletOperation.getWalletId()));
            }
            if (walletOperation.getUserAccountId() != null) {
                ratedTransaction.setUserAccount(em.getReference(UserAccount.class, walletOperation.getUserAccountId()));
            }
            if (walletOperation.getBillingAccountId() != null) {
                ratedTransaction.setBillingAccount(em.getReference(BillingAccount.class, walletOperation.getBillingAccountId()));
            }
            if (walletOperation.getSellerId() != null) {
                ratedTransaction.setSeller(em.getReference(Seller.class, walletOperation.getSellerId()));
            }
            if (walletOperation.getInvoiceSubCategoryId() != null) {
                ratedTransaction.setInvoiceSubCategory(em.getReference(InvoiceSubCategory.class, walletOperation.getInvoiceSubCategoryId()));
            }
            ratedTransaction.setParameter1(walletOperation.getParameter1());
            ratedTransaction.setParameter2(walletOperation.getParameter2());
            ratedTransaction.setParameter3(walletOperation.getParameter3());
            ratedTransaction.setParameterExtra(walletOperation.getParameterExtra());
            ratedTransaction.setOrderNumber(walletOperation.getOrderNumber());
            if (walletOperation.getSubscriptionId() != null) {
                ratedTransaction.setSubscription(em.getReference(Subscription.class, walletOperation.getSubscriptionId()));
            }
            if (walletOperation.getPriceplanId() != null) {
                ratedTransaction.setPriceplan(em.getReference(PricePlanMatrix.class, walletOperation.getPriceplanId()));
            }
            if (walletOperation.getOfferTemplateId() != null) {
                ratedTransaction.setOfferTemplate(em.getReference(OfferTemplate.class, walletOperation.getOfferTemplateId()));
            }
            if (walletOperation.getEdrId() != null) {
                ratedTransaction.setEdr(em.getReference(EDR.class, walletOperation.getEdrId()));
            }
            ratedTransaction.setStartDate(walletOperation.getStartDate());
            ratedTransaction.setEndDate(walletOperation.getEndDate());
            if (walletOperation.getTaxId() != null) {
                ratedTransaction.setTax(em.getReference(Tax.class, walletOperation.getTaxId()));
            }
            ratedTransaction.setTaxPercent(walletOperation.getTaxPercent());
            if (walletOperation.getServiceInstanceId() != null) {
                ratedTransaction.setServiceInstance(em.getReference(ServiceInstance.class, walletOperation.getServiceInstanceId()));
            }
            ratedTransaction.setStatus(RatedTransactionStatusEnum.OPEN);
            ratedTransaction.setUpdated(new Date());
            if (walletOperation.getTaxClassId() != null) {
                ratedTransaction.setTaxClass(em.getReference(TaxClass.class, walletOperation.getTaxClassId()));
            }
            if (walletOperation.getInputUnitOfMeasureId() != null) {
                ratedTransaction.setInputUnitOfMeasure(em.getReference(UnitOfMeasure.class, walletOperation.getInputUnitOfMeasureId()));
            }
            if (walletOperation.getRatingUnitOfMeasureId() != null) {
                ratedTransaction.setRatingUnitOfMeasure(em.getReference(UnitOfMeasure.class, walletOperation.getRatingUnitOfMeasureId()));
            }
            if (walletOperation.getAccountingCodeId() != null) {
                ratedTransaction.setAccountingCode(em.getReference(AccountingCode.class, walletOperation.getAccountingCodeId()));
            }
            if (walletOperation.getAccountingArticleId() != null) {
                ratedTransaction.setAccountingArticle(em.getReference(AccountingArticle.class, walletOperation.getAccountingArticleId()));
            }

            if (walletOperation.getOrderId() != null || walletOperation.getProductVersionId() != null || walletOperation.getOrderLotId() != null) {
                OrderInfo orderInfo = new OrderInfo();

                if (walletOperation.getOrderId() != null) {
                    orderInfo.setOrder(em.getReference(CommercialOrder.class, walletOperation.getOrderId()));
                }
                if (walletOperation.getProductVersionId() != null) {
                    orderInfo.setProductVersion(em.getReference(ProductVersion.class, walletOperation.getProductVersionId()));
                }
                if (walletOperation.getOrderLotId() != null) {
                    orderInfo.setOrderLot(em.getReference(OrderLot.class, walletOperation.getOrderLotId()));
                }
                ratedTransaction.setInfoOrder(orderInfo);
            }
            ratedTransaction.setInvoicingDate(walletOperation.getInvoicingDate());
            ratedTransaction.setUnityDescription(walletOperation.getInputUnitDescription());
            ratedTransaction.setRatingUnitDescription(walletOperation.getRatingUnitDescription());
            ratedTransaction.setSortIndex(walletOperation.getSortIndex());
            ratedTransaction.setCfValues(walletOperation.getCfValues());

            if (walletOperation.getDiscountPlanId() != null) {
                ratedTransaction.setDiscountPlan(em.getReference(DiscountPlan.class, walletOperation.getDiscountPlanId()));
            }
            if (walletOperation.getDiscountPlanItemId() != null) {
                ratedTransaction.setDiscountPlanItem(em.getReference(DiscountPlanItem.class, walletOperation.getDiscountPlanItemId()));
            }
            ratedTransaction.setDiscountPlanType(walletOperation.getDiscountPlanType());
            ratedTransaction.setDiscountValue(walletOperation.getDiscountValue());
            ratedTransaction.setSequence(walletOperation.getSequence());
            if (walletOperation.getRulesContractId() != null) {
                ratedTransaction.setRulesContract(em.getReference(Contract.class, walletOperation.getRulesContractId()));
            }
            ratedTransaction.setUseSpecificPriceConversion(walletOperation.isUseSpecificPriceConversion());
            ratedTransaction.setTransactionalAmountWithoutTax(walletOperation.getTransactionalAmountWithoutTax());
            ratedTransaction.setTransactionalAmountWithTax(walletOperation.getTransactionalAmountWithTax());
            ratedTransaction.setTransactionalAmountTax(walletOperation.getTransactionalAmountTax());
            ratedTransaction.setTransactionalUnitAmountWithoutTax(walletOperation.getTransactionalUnitAmountWithoutTax());
            ratedTransaction.setTransactionalUnitAmountWithTax(walletOperation.getTransactionalUnitAmountWithTax());
            ratedTransaction.setTransactionalUnitAmountTax(walletOperation.getTransactionalUnitAmountTax());
            if (walletOperation.getTradingCurrencyId() != null) {
                ratedTransaction.setTradingCurrency(em.getReference(TradingCurrency.class, walletOperation.getTradingCurrencyId()));
            }
            
            ratedTransaction.setBusinessKey(walletOperation.getBusinessKey());

            if (cftEndPeriodEnabled) {
                customFieldInstanceService.scheduleEndPeriodEvents(ratedTransaction);
            }
            if (billingRedirectionEnabled) {
                applyInvoicingRules(ratedTransaction);
            }
            em.persist(ratedTransaction);

            // Fire notifications
            if (eventsEnabled) {
                entityCreatedEventProducer.fire((BaseEntity) ratedTransaction);
            }

            woRtIds[i][0] = walletOperation.getId();
            woRtIds[i][1] = ratedTransaction.getId();
            allRtIds.add(ratedTransaction.getId());
            i++;
        }

        em.flush();

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
        //em.flush();

        // Mass update WOs with status and RT info
        em.createNamedQuery("WalletOperation.massUpdateWithRTInfoFromPendingTable" + (EntityManagerProvider.isDBOracle() ? "Oracle" : "")).executeUpdate();
        em.createNamedQuery("WalletOperation.deletePendingTable").executeUpdate();

        return allRtIds;
    }

    private List<AttributeValue> fromAttributeInstances(ServiceInstance serviceInstance) {
        if (serviceInstance == null) {
            return emptyList();
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
        String code = aggregatedWo.getCode();
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
        if (StringUtils.isBlank(code)) {
            if (ci != null) {
                code = ci != null ? ci.getCode() : null;
            } else if (si != null) {
                code = si != null ? si.getCode() : null;
            } else {
                code = isc != null ? isc.getCode() : null;
            }
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
        ratedTransaction.setCreated(new Date());
        if(aggregatedWo.getRulesContract() != null && aggregatedWo.getRulesContract().getId() != null) {
            ratedTransaction.setRulesContract(contractService.refreshOrRetrieve(aggregatedWo.getRulesContract()));
        }
        // ratedTransaction.setEdr(aggregatedWo.getEdr());
        WalletInstance wallet = walletService.refreshOrRetrieve(aggregatedWo.getWallet());
        ratedTransaction.setWallet(wallet);
        populateCustomfield(ratedTransaction, aggregatedWo);
        if (!isVirtual) {
            create(ratedTransaction);
            updateAggregatedWalletOperations(aggregatedWo.getWalletOperationsIds(), ratedTransaction);
        }
        setPricePlan(ratedTransaction);
        ratedTransaction.setAccountingArticle(accountingArticleService.refreshOrRetrieve(aggregatedWo.getAccountingArticle()));
        ratedTransaction.setAccountingCode(accountingCodeService.refreshOrRetrieve(aggregatedWo.getAccountingCode()));
        ratedTransaction.setOfferTemplate(offerTemplateService.refreshOrRetrieve(aggregatedWo.getOfferTemplate()));
        ratedTransaction.setServiceInstance(serviceInstanceService.refreshOrRetrieve(aggregatedWo.getServiceInstance()));
        ratedTransaction.setDiscountPlan(discountPlanService.refreshOrRetrieve(aggregatedWo.getDiscountPlan()));
        ratedTransaction.setDiscountPlanType(aggregatedWo.getDiscountPlanType());
        ratedTransaction.setDiscountPlanItem(discountPlanItemService.refreshOrRetrieve(aggregatedWo.getDiscountPlanItem()));
        ratedTransaction.setDiscountedAmount(aggregatedWo.getDiscountedAmount());
        ratedTransaction.setDiscountValue(aggregatedWo.getDiscountValue());

        if(ratedTransaction.getRulesContract() == null) {
            BillingAccount billingAccount = billingAccountService.getBAFetchingCaAndCustomer(ba.getId());
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            Customer customer = customerAccount.getCustomer();
            //Get the list of customers (current and parents)
            List<Customer> customers = new ArrayList<>();
            getCustomer(customer, customers);
            List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
            
          //Get the list of seller (current and parents)
            List<Seller> sellers = new ArrayList<>();
			getSeller(seller, sellers);
			List<Long> sellerIds = sellers.stream().map(Seller::getId).collect(Collectors.toList());
			
            //Get contract by list of customer ids, billing account and customer account
            List<Contract> contracts = contractService.getContractByAccount(ids, billingAccount, customerAccount,sellerIds, null, aggregatedWo.getOperationDate());
            Contract contractWithRules = contractService.lookupSuitableContract(customers, contracts, true);

            ratedTransaction.setRulesContract(contractWithRules);
        }

        applyInvoicingRules(ratedTransaction);

        return ratedTransaction;
    }


    /**
     * Get the customer and all parent customers
     * @param pCustomer Customer
     * @param pCustomerList List of customers (current customer and all parents)
     */
    private void getCustomer(Customer pCustomer, List<Customer> pCustomerList) {
        pCustomerList.add(pCustomer);
        if(pCustomer.getParentCustomer() != null) {
            getCustomer(pCustomer.getParentCustomer(), pCustomerList);
        }
    }
    
    /**
     * Get the seller and all parent sellers
     * @param parentSeller Seller
     * @param parentSellers List of sellers (current seller and all parents)
     */
    private void getSeller(Seller parentSeller, List<Seller> parentSellers) {
    	if(parentSeller != null) {
    		parentSellers.add(parentSeller);
    		if(parentSeller.getSeller() != null) {
    			getSeller(parentSeller.getSeller(), parentSellers);
    		}
    	}
    }

    private void setPricePlan(RatedTransaction ratedTransaction) {
        if(ratedTransaction.getId() != null){
            WalletOperation walletOperation = walletOperationService.findWoByRatedTransactionId(ratedTransaction.getId());
            if(walletOperation != null && ratedTransaction.getPriceplan() == null){
                ratedTransaction.setPriceplan(walletOperation.getPriceplan());
            }
        }
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

    public Long countRTBySubscriptionForDraftInvoice(Subscription subscription) {
        try {
            return (Long) getEntityManager().createNamedQuery("RatedTransaction.countRTBySubscriptionForDraftInvoice")
            		.setParameter("subscription", subscription)
            		.getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countRTBySubscriptionForDraftInvoice", e);
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
        BigDecimal[] unitAmounts = computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), NB_DECIMALS, HALF_UP);
        BigDecimal[] amounts = computeDerivedAmounts(rtMinAmount, rtMinAmount, tax.getPercent(), appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
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
        }else if (entityToInvoice instanceof CommercialOrder) {
            query = getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByOrderNumber", RatedTransaction.class).setParameter("orderNumber", ((CommercialOrder) entityToInvoice).getOrderNumber());
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

        Boolean booleanValue = getInstance().getBooleanValue("billing.minimumRating.global.enabled");
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

        Boolean booleanValue = getInstance().getBooleanValue("billing.minimumRating.global.enabled");
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

        Boolean booleanValue = getInstance().getBooleanValue("billing.minimumRating.global.enabled");
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

        Boolean booleanValue = getInstance().getBooleanValue("billing.minimumRating.global.enabled");
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
    public void uninvoiceRTs(Invoice invoice, RatedTransactionAction rtAction) {
        getEntityManager().createNamedQuery("RatedTransaction.unInvoiceByInvoice")
                .setParameter("invoice", invoice)
                .setParameter("now", new Date())
                .setParameter("NEW_STATUS", (rtAction == null || rtAction == RatedTransactionAction.REOPEN) ? RatedTransactionStatusEnum.OPEN : RatedTransactionStatusEnum.CANCELED)
                .executeUpdate();
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
     * @param description
     * @return
     */
    public RatedTransaction createRatedTransaction(String billingAccountCode, String userAccountCode,
            String subscriptionCode, String serviceInstanceCode, String chargeInstanceCode,
                                                   Date usageDate, BigDecimal unitAmountWithoutTax, BigDecimal quantity,
                                                   String param1, String param2, String param3,
                                                   String paramExtra, String description, String businessKey) {
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
        BigDecimal[] unitAmounts = computeDerivedAmounts(unitAmountWithoutTax, unitAmountWithoutTax,
                taxPercent, appProvider.isEntreprise(), NB_DECIMALS, HALF_UP);
        BigDecimal amountWithoutTax = unitAmountWithoutTax.multiply(quantity);
        BigDecimal[] amounts = computeDerivedAmounts(amountWithoutTax, amountWithoutTax, taxPercent,
                appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
        String rtDescription = description != null && !description.isBlank()
                ? description : chargeTemplate.getDescriptionOrCode();
        RatedTransaction rt = new RatedTransaction(usageDate, unitAmounts[0], unitAmounts[1], unitAmounts[2], quantity,
                amounts[0], amounts[1], amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, userAccount,
                null, param1, param2, param3, paramExtra, null, subscription, null,
                null, null, subscription.getOffer(), null,
                chargeTemplate.getCode(), rtDescription, null, null, subscription.getSeller(), taxInfo.tax,
                taxPercent, serviceInstance, taxClass, null, RatedTransactionTypeEnum.MANUAL, chargeInstance, null);
        rt.setAccountingArticle(accountingArticle);
        rt.setBusinessKey(businessKey);
        
        if (financeSettingsService.isBillingRedirectionRulesEnabled()) {
            applyInvoicingRules(rt);
        }
        create(rt);
        return rt;
    }

    /**
     * Update the rated transaction
     *
     * @param ratedTransaction the rated transaction
     * @param description the description
     * @param unitAmountWithoutTax the unit amount without tax
     * @param quantity the quantity
     * @param param1 the param1
     * @param param2 the param2
     * @param param3 the param3
     * @param paramExtra the param extra
     * @param usageDate RT usage date
     */
    public void updateRatedTransaction(RatedTransaction ratedTransaction, String description,
                                       BigDecimal unitAmountWithoutTax, BigDecimal quantity, String param1,
                                       String param2, String param3, String paramExtra, Date usageDate, String businessKey) {
        ratedTransaction.setDescription(description);
        BigDecimal[] unitAmounts = computeDerivedAmounts(unitAmountWithoutTax, unitAmountWithoutTax,
                ratedTransaction.getTaxPercent(), appProvider.isEntreprise(), NB_DECIMALS, HALF_UP);
        BigDecimal amountWithoutTax = unitAmountWithoutTax.multiply(quantity);
        BigDecimal[] amounts = computeDerivedAmounts(amountWithoutTax, amountWithoutTax,
                ratedTransaction.getTaxPercent(), appProvider.isEntreprise(), appProvider.getRounding(),
                appProvider.getRoundingMode().getRoundingMode());
        ratedTransaction.setUnitAmountWithoutTax(unitAmounts[0]);
        ratedTransaction.setUnitAmountWithTax(unitAmounts[1]);
        ratedTransaction.setUnitAmountTax(unitAmounts[2]);
        ratedTransaction.setQuantity(quantity);
        ratedTransaction.setAmountWithoutTax(amounts[0]);
        ratedTransaction.setAmountWithTax(amounts[1]);
        ratedTransaction.setAmountTax(amounts[2]);
        ratedTransaction.setParameter1(param1);
        ratedTransaction.setParameter2(param2);
        ratedTransaction.setParameter3(param3);
        ratedTransaction.setParameterExtra(paramExtra);
        ratedTransaction.setUsageDate(usageDate);
        if(businessKey !=null)
        	ratedTransaction.setBusinessKey(businessKey);
        

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
                + "                 rt.infoOrder.orderLot.id as order_lot_id, rt.chargeInstance.id as charge_instance_id, rt.accountingArticle.id as article_id, "
                + "                 rt.discountedRatedTransaction as discounted_ratedtransaction_id  "
                + " FROM RatedTransaction rt WHERE rt.id in (:ids) and rt.accountingArticle.ignoreAggregation = false"
                + " GROUP BY rt.billingAccount.id, rt.accountingCode.id, rt.description, "
                + "         rt.unitAmountWithoutTax, rt.unitAmountWithTax, rt.offerTemplate.id, rt.serviceInstance.id, rt.usageDate, rt.startDate,"
                + "         rt.endDate, rt.orderNumber, rt.subscription.id, rt.taxPercent, rt.tax.id, "
                + "         rt.infoOrder.order.id, rt.infoOrder.productVersion.id, rt.infoOrder.orderLot.id," +
                "           rt.chargeInstance.id, rt.accountingArticle.id, rt.discountedRatedTransaction " +
                "   order by rt.unitAmountWithoutTax desc";
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

    public void linkRTsToIL(final List<Long> ratedTransactionsIDs, final Long invoiceLineID, Long billingRunId) {
    	final int maxValue = ParamBean.getInstance().getPropertyAsInteger("database.number.of.inlist.limit", SHORT_MAX_VALUE);
    	
        if (ratedTransactionsIDs.size() > maxValue) {
        	log.info(">"+invoiceLineID+"====>"+ratedTransactionsIDs.size());
            SubListCreator<Long> subLists = new SubListCreator<>(ratedTransactionsIDs, (1 + (ratedTransactionsIDs.size() / maxValue)));
            int i=0;
            while (subLists.isHasNext()) {
                List<Long> nextWorkSet = subLists.getNextWorkSet();
                log.info(">>"+invoiceLineID+"----"+(++i)+"---->"+nextWorkSet.size());
				linkRTsWithILByIds(invoiceLineID, nextWorkSet, billingRunId);
            }
        } else {
            linkRTsWithILByIds(invoiceLineID, ratedTransactionsIDs, billingRunId);
        }
    }

    private void linkRTsWithILByIds( Long invoiceLineId, final List<Long> ids, Long billingRunId) {
        getEntityManager().createNamedQuery("RatedTransaction.linkRTWithInvoiceLine")
                .setParameter("il", invoiceLineId)
                .setParameter("billingRunId", billingRunId)
                .setParameter("ids", ids).executeUpdate();
    }

    /**
     * @param aggregationConfiguration
     * @param be
     * @param lastTransactionDate
     * @return
     */
    public List<Map<String, Object>> getGroupedRTsWithAggregation(AggregationConfiguration aggregationConfiguration,
            BillingRun billingRun, IBillableEntity be, Date lastTransactionDate) {

        BillingCycle billingCycle = billingRun.getBillingCycle();
        Map<String, Object> filter = null;
        if (billingCycle !=null && billingRun.getBillingCycle().getFilters() != null && !billingRun.getBillingCycle().getFilters().isEmpty()) {
            filter = new HashMap<>(billingRun.getBillingCycle().getFilters());
        }
        Map<String, Object> BRfilter = billingRun.getFilters();
		if (BRfilter!=null && !BRfilter.isEmpty()) {
        	filter=BRfilter;
        }
		if(billingCycle!=null && !billingCycle.isDisableAggregation()) {
			aggregationConfiguration = new AggregationConfiguration(billingRun);
		}
        if(aggregationConfiguration!=null) {
            String usageDateAggregation = getUsageDateAggregation(aggregationConfiguration.getDateAggregationOption());
            String unitAmount = appProvider.isEntreprise() ? "unitAmountWithoutTax" : "unitAmountWithTax";
            String unitAmountField = aggregationConfiguration.isAggregationPerUnitAmount() ? "SUM(a.unitAmountWithoutTax)" : unitAmount;

            // the first run of billing run (status is 'NEW' at that moment) should be in a normal run to create new invoice line
            // and to avoid doing unnecessary joins.
            // The next runs of BR (status has already changed to 'OPEN' at that moment) will apply the appending mode on existing invoice lines
            boolean incrementalInvoiceLines = billingRun.getIncrementalInvoiceLines() && billingRun.getStatus() == BillingRunStatusEnum.OPEN;
            List<String> fieldToFetch = buildFieldList(usageDateAggregation, unitAmountField,
            		aggregationConfiguration.isIgnoreSubscriptions(), aggregationConfiguration.isIgnoreOrders(),
                    true, aggregationConfiguration.isUseAccountingArticleLabel(),
                    aggregationConfiguration.getType(), incrementalInvoiceLines);

            Map<String, String> mapToInvoiceLineTable = buildMapToInvoiceLineTable(aggregationConfiguration);
            String query = buildFetchQuery(new PaginationConfiguration(filter, fieldToFetch, mapToInvoiceLineTable.keySet()),
                    getEntityCondition(be), lastTransactionDate, incrementalInvoiceLines, mapToInvoiceLineTable,
                    billingRun.getId());

            if (incrementalInvoiceLines) {
                query = query.replace("a.ivl.", "ivl.");
                query = query + ", ivl.id";
            }

            return getSelectQueryAsMap(query, buildParams(billingRun, lastTransactionDate));
        } else {
            return getGroupedRTsWithoutAggregation(billingRun, be, lastTransactionDate, filter, aggregationConfiguration);
        }
    }

    private String getUsageDateAggregation(DateAggregationOption dateAggregationOption) {
        return getUsageDateAggregation(dateAggregationOption, "usageDate ");
    }

    private String getUsageDateAggregation(DateAggregationOption dateAggregationOption, String usageDateColumn) {
    	return this.getUsageDateAggregation(dateAggregationOption, usageDateColumn, "a");
    }
    
    private String getUsageDateAggregation(DateAggregationOption dateAggregationOption, String usageDateColumn, String alias) {
        switch (dateAggregationOption) {
        case MONTH_OF_USAGE_DATE:
            return " TO_CHAR(" + alias + "." + usageDateColumn + ", 'YYYY-MM') ";
        case DAY_OF_USAGE_DATE:
            return " TO_CHAR(" + alias + "." + usageDateColumn + ", 'YYYY-MM-DD') ";
        case WEEK_OF_USAGE_DATE:
            return " TO_CHAR(" + alias + "." + usageDateColumn + ", 'YYYY-WW') ";
        case NO_DATE_AGGREGATION:
            return usageDateColumn;
        }
        return usageDateColumn;
    }

    private List<String> buildFieldList(String usageDateAggregation,
                                        String unitAmountField, boolean ignoreSubscription,
                                        boolean ignoreOrder, boolean withAggregation,
                                        boolean useAccountingArticleLabel, BillingEntityTypeEnum type,
                                        boolean incrementalInvoiceLines) {
        List<String> fieldToFetch;
        if(withAggregation) {
            fieldToFetch = new ArrayList<>(asList("count(a.id) as count","string_agg_long(a.id) as rated_transaction_ids", "billingAccount.id as billing_account__id",
                    "SUM(a.quantity) as quantity", unitAmountField + " as unit_amount_without_tax", "SUM(a.amountWithoutTax) as sum_without_tax",
                    "SUM(a.amountWithTax) as sum_with_tax", "offerTemplate.id as offer_id", usageDateAggregation + " as usage_date",
                    "min(a.startDate) as start_date", "max(a.endDate) as end_date",
                    "taxPercent as tax_percent", "tax.id as tax_id", "infoOrder.productVersion.id as product_version_id",
                    "accountingArticle.id as article_id", "discountedRatedTransaction as discounted_ratedtransaction_id", "discountPlanType as discount_plan_type", "discountValue as discount_value", 
                    "useSpecificPriceConversion as use_specific_price_conversion",
                    "SUM(a.transactionalUnitAmountWithoutTax) as converted_unit_amount_without_tax",
                    "SUM(a.transactionalUnitAmountTax) as converted_unit_amount_tax",
                    "SUM(a.transactionalUnitAmountWithTax) as converted_unit_amount_with_tax",
                    "SUM(a.transactionalAmountWithoutTax) as sum_converted_amount_without_tax",
                    "SUM(a.transactionalAmountTax) as sum_converted_amount_tax",
                    "SUM(a.transactionalAmountWithTax) as sum_converted_amount_with_tax"));
        } else {
            fieldToFetch = new ArrayList<>(asList("count(a.id) as count","CAST(a.id as string) as rated_transaction_ids",
                    "billingAccount.id as billing_account__id", "description as label", "quantity AS quantity", "amountWithoutTax as sum_without_tax",
                    "amountWithTax as sum_with_tax", "offerTemplate.id as offer_id", "serviceInstance.id as service_instance_id",
                    "startDate as start_date", "endDate as end_date", "orderNumber as order_number", "taxPercent as tax_percent",
                    "tax.id as tax_id", "infoOrder.order.id as order_id", "infoOrder.productVersion.id as product_version_id",
                    "infoOrder.orderLot.id as order_lot_id", "chargeInstance.id as charge_instance_id",
                    "accountingArticle.id as article_id", "discountedRatedTransaction as discounted_ratedtransaction_id",
                    "useSpecificPriceConversion as use_specific_price_conversion",
                    "transactionalUnitAmountWithoutTax as converted_unit_amount_without_tax",
                    "transactionalUnitAmountTax as converted_unit_amount_tax",
                    "transactionalUnitAmountWithTax as converted_unit_amount_with_tax",
                    "transactionalAmountWithoutTax as sum_converted_amount_without_tax",
                    "transactionalAmountTax as sum_converted_amount_tax",
                    "transactionalAmountWithTax as sum_converted_amount_with_tax"));
        }

        if (incrementalInvoiceLines) {
            fieldToFetch.add("ivl.id as invoice_line_id");
            fieldToFetch.add("ivl.amountWithoutTax as amount_without_tax");
            fieldToFetch.add("ivl.amountWithTax as amount_with_tax");
            fieldToFetch.add("ivl.taxRate as tax_rate");
            fieldToFetch.add("ivl.quantity as accumulated_quantity");
            fieldToFetch.add("ivl.validity.from as begin_date");
            fieldToFetch.add("ivl.validity.to as end_date");
            fieldToFetch.add("ivl.unitPrice as unit_price");
        }

        if (BILLINGACCOUNT != type || !ignoreSubscription) {
            fieldToFetch.add("subscription.id as subscription_id");
            fieldToFetch.add("serviceInstance.id as service_instance_id");
        }
        if(!ignoreOrder) {
            fieldToFetch.add("infoOrder.order.id as commercial_order_id");
            fieldToFetch.add("orderNumber as order_number");
            fieldToFetch.add("infoOrder.order.id as order_id");
        }
        if(!useAccountingArticleLabel) {
            fieldToFetch.add("description as label");
        }
        return fieldToFetch;
    }

    private String getEntityCondition(IBillableEntity be) {
        String entityCondition = "";
        if (be instanceof Subscription) {
            entityCondition = " a.subscription.id= " + be.getId();
        } else if (be instanceof BillingAccount) {
            entityCondition = " a.billingAccount.id = " + be.getId();
        } else if (be instanceof Order) {
            entityCondition = " a.orderNumber = '" + ((Order) be).getOrderNumber() + "'";
        } else if (be instanceof CommercialOrder) {
            entityCondition = " a.orderNumber = '" + ((CommercialOrder) be).getOrderNumber() + "'";
        }
        return entityCondition;
    }

    private boolean checkAggFunctions(String field) {
        return field.startsWith("SUM(") || field.startsWith("COUNT(") || field.startsWith("AVG(")
                || field.startsWith("MAX(") || field.startsWith("MIN(") || field.startsWith("COALESCE(SUM(")
                || field.startsWith("STRING_AGG_LONG") || field.startsWith("TO_CHAR(") || field.startsWith("CAST(");
    }

    private String buildFetchQuery(PaginationConfiguration searchConfig, String entityCondition, Date lastTransactionDate,
                                   boolean incrementalInvoiceLines, Map<String, String> mapToInvoiceLineTable,
                                   Long billingRunId) {
        String extraCondition = entityCondition + (lastTransactionDate!=null? " AND a.usageDate < :lastTransactionDate AND ":" AND ") + QUERY_FILTER;

        StringBuilder leftJoinClauseBd = new StringBuilder();
        if (incrementalInvoiceLines) {
            String aliasInvoiceLineTable = "ivl";
            leftJoinClauseBd.append("LEFT JOIN InvoiceLine ").append(aliasInvoiceLineTable).append(" ON ");
            Iterator<String> itr = searchConfig.getGroupBy().iterator();
            String groupByInRT;
            String leftJoinInIL = "";
            String testNullCondition = "";
            while (itr.hasNext()) {
                groupByInRT = itr.next();
                leftJoinInIL = mapToInvoiceLineTable.get(groupByInRT);

                if (checkAggFunctions(groupByInRT.toUpperCase().trim())) {
                    testNullCondition = groupByInRT + " IS NULL AND " + leftJoinInIL + " IS NULL OR ";

                    leftJoinClauseBd.append("(").append(testNullCondition)
                            .append(groupByInRT).append("=").append(leftJoinInIL).append(")");
                }
                else {
                    testNullCondition = "a." + groupByInRT + " IS NULL AND " + aliasInvoiceLineTable + "."
                            + leftJoinInIL + " IS NULL OR ";

                    if (groupByInRT.equals("description")) {
                        leftJoinClauseBd.append("(").append(testNullCondition)
                                .append("a.description=ivl.label OR a.accountingArticle.description=ivl.label)");
                    }
                    else if (groupByInRT.equals("taxPercent")) {
                        leftJoinClauseBd.append("(").append(testNullCondition)
                                .append("a.taxPercent=ivl.taxRate OR a.tax.percent=ivl.taxRate)");
                    }
                    else {
                        leftJoinClauseBd.append("(").append(testNullCondition)
                                .append("a.").append(groupByInRT).append("=").append(aliasInvoiceLineTable).append(".")
                                .append(leftJoinInIL).append(")");
                    }
                }

                if (itr.hasNext())
                    leftJoinClauseBd.append(" AND ");
            }

            leftJoinClauseBd.append("AND ivl.billingRun.id = ").append(billingRunId).append(" ");
            leftJoinClauseBd.append(" AND ivl.discountValue is null and a.discountValue is null ");
        }

        QueryBuilder queryBuilder = nativePersistenceService.getAggregateQuery(entityClass.getCanonicalName(), searchConfig,
                null, extraCondition, leftJoinClauseBd.toString());
        return queryBuilder.getQueryAsString();
    }

    private Map<String, String> buildMapToInvoiceLineTable(AggregationConfiguration aggregationConfiguration) {
        Map<String, String> mapToInvoiceLineTable = new HashMap<>(){{
            put("billingAccount.id", "billingAccount.id");
            put("offerTemplate", "offerTemplate");
            put("taxPercent", "taxRate");
            put("tax.id", "tax.id");
            put("infoOrder.productVersion.id", "productVersion.id");
            put("accountingArticle.id", "accountingArticle.id");
            put("discountedRatedTransaction", "discountedInvoiceLine");
            put("discountValue", "discountValue");
            put("discountPlanType", "discountPlanType");
            put("useSpecificPriceConversion", "useSpecificPriceConversion");
        }};

        String usageDateAggregation = getUsageDateAggregation(aggregationConfiguration.getDateAggregationOption());
        mapToInvoiceLineTable.put(usageDateAggregation, usageDateAggregation.replace("a.", "ivl.")
                .replace("usageDate", "valueDate"));

        boolean ignoreSubscription = BILLINGACCOUNT == aggregationConfiguration.getType() && aggregationConfiguration.isIgnoreSubscriptions();
        if (! ignoreSubscription) {
            mapToInvoiceLineTable.put("subscription.id", "subscription.id");
            mapToInvoiceLineTable.put("serviceInstance", "serviceInstance");
        }

        if (! aggregationConfiguration.isIgnoreOrders()) {
            mapToInvoiceLineTable.put("infoOrder.order.id", "commercialOrder.id");
            mapToInvoiceLineTable.put("orderNumber", "orderNumber");
        }

        if (! aggregationConfiguration.isAggregationPerUnitAmount()) {
            if (appProvider.isEntreprise()) {
                mapToInvoiceLineTable.put("unitAmountWithoutTax", "unitPrice");
            }
            else {
                mapToInvoiceLineTable.put("unitAmountWithTax", "unitPrice");
            }
        }

        if (! aggregationConfiguration.isUseAccountingArticleLabel()) {
            mapToInvoiceLineTable.put("description", "label");
        }

        return mapToInvoiceLineTable;
    }

    private Map<String, Object> buildParams(BillingRun billingRun, Date lastTransactionDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("firstTransactionDate", new Date(0));
        if(lastTransactionDate!=null){
        	params.put("lastTransactionDate", lastTransactionDate);
        }
        params.put("invoiceUpToDate", lastTransactionDate);
        return params;
    }

    public List<Map<String, Object>> getGroupedRTsWithoutAggregation(BillingRun billingRun,
                                                                     IBillableEntity be, Date lastTransactionDate,
                                                                     Map<String, Object> billingCycleFilters,
                                                                     AggregationConfiguration aggregationConfiguration) {
        List<String> fieldToFetch = buildFieldList(null, null,
                aggregationConfiguration.isIgnoreSubscriptions(), aggregationConfiguration.isIgnoreOrders(),
                false, aggregationConfiguration.isUseAccountingArticleLabel(), aggregationConfiguration.getType(),
                billingRun.getIncrementalInvoiceLines());
        String query = buildFetchQuery(new PaginationConfiguration(billingCycleFilters, fieldToFetch, null),
                getEntityCondition(be), lastTransactionDate, billingRun.getIncrementalInvoiceLines(), null,
                billingRun.getId());

        return getSelectQueryAsMap(query, buildParams(billingRun, lastTransactionDate));
    }

    /**
     * Apply invoicing rule for a given set of rated transactions. Changes will be persisted
     * 
     * @param rts A list of rated transactions
     * @return A list of Billing Accounts that RTs were updated to
     */
    public List<BillingAccount> applyInvoicingRules(List<RatedTransaction> rts) {
        Set<BillingAccount> billingAccountsAfter = new HashSet<>();
        if (rts.isEmpty()) {
            return new ArrayList<BillingAccount>();
        }

        for (RatedTransaction rt : rts) {

            boolean isApplied = applyInvoicingRules(rt);
            if (isApplied && !rt.isTransient()) {
                update(rt);
            }

            if (isApplied && rt.getStatus() != RatedTransactionStatusEnum.REJECTED) {
                billingAccountsAfter.add(rt.getBillingAccount());
            }

        }
        return new ArrayList<BillingAccount>(billingAccountsAfter);
    }

    /**
     * Apply invoicing rule for a given set of rated transactions. Rated transaction will be updated with a new Billing Account value. In case an error occur at Billing rule evaluation, Rated Transaction status will be
     * changed to REJECTED. Rated transaction is not persisted.
     *
     * @param ratedTransaction instance from RatedTransaction
     * @param True if Billing rule was applied or an error occured and Rated Transaction status was changed to REJECTED.
     */
    private boolean applyInvoicingRules(RatedTransaction ratedTransaction) {
        if (ratedTransaction == null || ratedTransaction.getRulesContract() == null || ratedTransaction.getStatus() != RatedTransactionStatusEnum.OPEN || ratedTransaction.getOriginBillingAccount() != null) {
            return false;
        }

        List<Object[]> billingRules = getEntityManager().createNamedQuery("BillingRule.findByContractIdForRating").setParameter("contractId", ratedTransaction.getRulesContract().getId()).getResultList();

        for (Object[] billingRule : billingRules) {
            Long brId = (Long) billingRule[0];
            String criteriaEl = (String) billingRule[1];
            String invoiceBACodeEl = (String) billingRule[2];
            try {
                Boolean eCriteriaEL = checkCriteriaEL(ratedTransaction, criteriaEl);

                // Billing rule did not match the criteria - continue with the next billing rule
                if (eCriteriaEL == null || !eCriteriaEL) {
                    continue;
                }

                String eInvoicedBACodeEL = null;
                try {
                    eInvoicedBACodeEL = evaluateInvoicedBACodeEL(ratedTransaction, invoiceBACodeEl);

                    if (eInvoicedBACodeEL != null) {
                        if ("".equals(eInvoicedBACodeEL)) {
                            ratedTransaction.setStatus(RatedTransactionStatusEnum.REJECTED);
                            ratedTransaction.setRejectReason("Error evaluating invoicedBillingAccountCodeEL [id=" + brId + ", invoicedBillingAccountCodeEL = " + invoiceBACodeEl + "]");

                        } else {
                            BillingAccount billingAccountByCode = billingAccountService.findByCode(eInvoicedBACodeEL, true);
                            if (billingAccountByCode != null) {
                                ratedTransaction.setOriginBillingAccount(ratedTransaction.getBillingAccount());
                                ratedTransaction.setBillingAccount(billingAccountByCode);

                            } else {
                                ratedTransaction.setStatus(RatedTransactionStatusEnum.REJECTED);
                                ratedTransaction.setRejectReason(
                                    "Billing redirection rule [id=" + brId + ",  invoicedBillingAccountCodeEL=" + invoiceBACodeEl + "] redirects to unknown billing account [code=" + eInvoicedBACodeEL + "]");
                            }
                        }
                    } else {
                        ratedTransaction.setStatus(RatedTransactionStatusEnum.REJECTED);
                        ratedTransaction.setRejectReason("Error evaluating invoicedBillingAccountCodeEL [id=" + brId + ", invoicedBillingAccountCodeEL = " + invoiceBACodeEl + "]");
                    }
                } catch (BusinessException e) {
                    ratedTransaction.setStatus(RatedTransactionStatusEnum.REJECTED);
                    ratedTransaction.setRejectReason("Error evaluating invoicedBillingAccountCodeEL [id=" + brId + ", invoicedBillingAccountCodeEL = " + invoiceBACodeEl + "]");
                }

            } catch (BusinessException e) {
                ratedTransaction.setStatus(RatedTransactionStatusEnum.REJECTED);
                ratedTransaction.setRejectReason("Error evaluating criteriaEL [id=" + brId + ", criteriaEL=" + criteriaEl + "] for RT [id=" + ratedTransaction.getId() + "]: Error in criteriaEL evaluation");
            }

            // Billing rule either assigned or failed to be assigned
            return true;
        }
        return false;
    }

    private Boolean checkCriteriaEL(RatedTransaction rt, String expression) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = expression.replace("\\", "");
        Map<Object, Object> userMap = new HashMap<>();
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
        Map<Object, Object> userMap = new HashMap<>();
        userMap.put("rt", rt);
        String code = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        if (code != null) {
           return code;
        }
        return null;       
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
		List<ChargeInstance> chargeInstances = readChargeInstancesHavingEmptyAccountingArticle(billableEntity, pageSize, pageIndex);
		chargeInstances.stream().forEach(charge -> updateAccountingArticlesByChargeInstance(charge, accountingArticleService.getAccountingArticleByChargeInstance(charge)));
	    return chargeInstances.size();
	}
    
	public void updateAccountingArticlesByChargeInstance(ChargeInstance charge, AccountingArticle accountingArticle) {
		updateAccountingArticlesByChargeInstanceIds(Arrays.asList(charge.getId()),accountingArticle);
	}
	
	public void updateAccountingArticlesByChargeInstanceIds(List<Long> ids, AccountingArticle accountingArticle) {
		updateAccountingArticlesByChargeInstanceIdsOrOtherCriterias(ids, null, null, accountingArticle);
	}
    
	public void updateAccountingArticlesByChargeInstanceIdsOrOtherCriterias(List<Long> chargeInstances, Long serviceInstanceId, Long offerTemplateId, AccountingArticle accountingArticle) {
		
		String strQuery = "UPDATE RatedTransaction rt SET rt.accountingArticle=:accountingArticle WHERE rt.status='OPEN' and rt.accountingArticle is null ";
		if (chargeInstances != null) {
			strQuery = strQuery + " and rt.chargeInstance.id in(:chargeInstanceIds) ";
		} else {
			strQuery = strQuery + " and rt.chargeInstance.id is null ";
			if (serviceInstanceId != null) {
				strQuery = strQuery + " and rt.serviceInstance.id =:serviceInstanceId ";
			} else {
				strQuery = strQuery + " and rt.serviceInstance.id is null ";
			}
			if (offerTemplateId != null) {
				strQuery = strQuery + " and rt.offerTemplate.id =:offerTemplateId ";
			} else {
				strQuery = strQuery + " and rt.offerTemplate.id is null ";
			}
		}
        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("accountingArticle", accountingArticle);
        if (chargeInstances != null) {
            final int maxValue = Objects.requireNonNull(getInstance()).getPropertyAsInteger("database.number.of.inlist.limit", PersistenceService.SHORT_MAX_VALUE)-1;
            List<List<Long>> chargesSubList = partition(chargeInstances, maxValue);
            for(List<Long> subList: chargesSubList) {
            	query.setParameter("chargeInstanceIds", subList);
            	query.executeUpdate();
            }
		} else {
			if (serviceInstanceId != null) {
				query.setParameter("serviceInstanceId", serviceInstanceId);
			} 
			if (offerTemplateId != null) {
				query.setParameter("offerTemplateId", offerTemplateId);
			}
			query.executeUpdate();
		}
	}

	/**
	 * @param billableEntity
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<ChargeInstance> readChargeInstancesHavingEmptyAccountingArticle(IBillableEntity billableEntity, Integer pageSize, Integer pageIndex) {
		QueryBuilder qb = new QueryBuilder("select distinct rt.chargeInstance from RatedTransaction rt ", "rt");
		if (billableEntity instanceof Subscription) {
        	qb.addCriterion("rt.subscription.id ", "=", billableEntity.getId(), false);
        } else if (billableEntity instanceof BillingAccount) {
        	qb.addCriterion("rt.billingAccount.id ", "=", billableEntity.getId(), false);
        } else if (billableEntity instanceof Order) {
        	qb.addCriterion("orderNumber ","=", ((Order) billableEntity).getOrderNumber(), false);
        }
        qb.addSql(" rt.status='OPEN' and rt.accountingArticle is null ");
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

    public List<BillingAccount> findBillingAccountsBy(List<Long> rtIds) {
        final int maxValue = getInstance().getPropertyAsInteger("database.number.of.inlist.limit", SHORT_MAX_VALUE);
        if (rtIds.size() > maxValue) {
            List<BillingAccount> billingAccounts = new ArrayList<>();
            List<List<Long>> invoiceLineIdsSubList = partition(rtIds, maxValue);
            invoiceLineIdsSubList.forEach(subIdsList -> billingAccounts.addAll(loadBy(subIdsList)));
            return new ArrayList<>(new HashSet<>(billingAccounts));
        } else {
            return loadBy(rtIds);
        }
    }
    private List<BillingAccount> loadBy(List<Long> rtIds) {
        return getEntityManager()
                .createNamedQuery("RatedTransaction.BillingAccountByRTIds")
                .setParameter("ids", rtIds)
                .getResultList();
    }

	@SuppressWarnings("unchecked")
	public List<RatedTransaction> findByFilter(Map<String, Object> filters) {
        PaginationConfiguration configuration = getPaginationConfigurationFromFilter(filters);
        QueryBuilder query = getQuery(configuration);
		return query.getQuery(getEntityManager()).getResultList();
	}

    public Long count(Map<String, Object> filters){
        PaginationConfiguration configuration = getPaginationConfigurationFromFilter(filters);
        return this.count(configuration);
    }

    public void incrementPendingDuplicate(List<Long> rtIds, boolean isPendingNegateExist){
        if(CollectionUtils.isEmpty(rtIds)){
            throw new BusinessApiException("List of rated Transaction is empty");
        }
        Query query = this.getEntityManager().createNamedQuery("RatedTransaction.updatePendingDuplicate");
        query.setParameter("rtI", rtIds);
        query.setParameter("pendingDuplicatesToNegate", isPendingNegateExist ? 1 : 0);
        query.setParameter("pendingDuplicates", isPendingNegateExist ? 0 : 1);
        query.executeUpdate();
    }

    private PaginationConfiguration getPaginationConfigurationFromFilter(Map<String, Object> filters) {
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
        filters = genericRequestMapper.evaluateFilters(filters, entityClass);
        return new PaginationConfiguration(filters);
    }

    /**
     * Cancel billing run associated RTs
     *
     * @param billingRun Billing run
     */
    public void cancelRatedTransaction(BillingRun billingRun) {
        getEntityManager().createNamedQuery("RatedTransaction.cancelRatedTransactionsByBR")
                .setParameter("billingRunId", billingRun.getId())
                .executeUpdate();
    }

    /**
     * Based on ratedTransaction and its new status to update, decide to update or not the corresponding invoice line using EDR Versioning
     *
     * @param newStatus RatedTransactionStatusEnum new status of ratedTransaction to be updated
     * @param ratedTransaction RatedTransaction containing necessary information to update corresponding invoice line
     *                         if allowed
     */
    public RatedTransaction update(RatedTransaction ratedTransaction, RatedTransactionStatusEnum newStatus) {

        if (newStatus == RatedTransactionStatusEnum.CANCELED || newStatus == RatedTransactionStatusEnum.REJECTED
                || newStatus == RatedTransactionStatusEnum.RERATED) {
            RatedTransactionStatusEnum oldStatus = ratedTransaction.getStatus();
            ratedTransaction.setStatus(newStatus);

            if (oldStatus == RatedTransactionStatusEnum.BILLED || oldStatus == RatedTransactionStatusEnum.PROCESSED) {
                InvoiceLine invoiceLine = ratedTransaction.getInvoiceLine();
                if (invoiceLine != null) {
                    // if the status of invoiceLine is not BILLED, we recompute the invoice line
                    if (invoiceLine.getStatus() != InvoiceLineStatusEnum.BILLED) {
                        // recompute the invoiceLine fields by extracting RT amounts and quantity of ratedTransaction
                        recomputeInvoiceLine(invoiceLine, ratedTransaction);

                        // get all discounted RTs linked to principal ratedTransaction
                        List<RatedTransaction> discountedRTs = getEntityManager()
                                .createNamedQuery("RatedTransaction.getDiscountedRTIds", RatedTransaction.class)
                                .setParameter("id", ratedTransaction.getId())
                                .getResultList();

                        // update status of discounted RTs as its principal ratedTransaction
                        if (! discountedRTs.isEmpty()) {
                            getEntityManager().createNamedQuery("RatedTransaction.updateStatusDiscountedRT")
                                    .setParameter("statusToUpdate", ratedTransaction.getStatus())
                                    .setParameter("ids", discountedRTs.stream().map(RatedTransaction::getId).collect(Collectors.toList()))
                                    .executeUpdate();
                        }

                        InvoiceLineStatusEnum statusToUpdate;
                        switch (ratedTransaction.getStatus()) {
                            case CANCELED:
                                statusToUpdate = InvoiceLineStatusEnum.CANCELED;
                                break;
                            case REJECTED:
                                statusToUpdate = InvoiceLineStatusEnum.REJECTED;
                                break;
                            case RERATED:
                                statusToUpdate = InvoiceLineStatusEnum.RERATED;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value of status of ratedTransaction : " + ratedTransaction.getStatus());
                        }

                        // update status of invoice lines generated by discounted RTs
                        discountedRTs.forEach(
                                discountedRT -> getEntityManager().createNamedQuery("InvoiceLine.updateStatusInvoiceLine")
                                .setParameter("statusToUpdate", statusToUpdate)
                                .setParameter("id", discountedRT.getInvoiceLine().getId())
                                .executeUpdate()
                        );
                    }
                    // if the status of invoiceLine is BILLED, we do nothing, the re-computation of invoice line is not allowed
                    else {
                        log.info("Invoice line id = {} created from ratedTransaction id = {} is already billed. " +
                                        "The re-computation of invoice line is not allowed",
                                invoiceLine.getId(), ratedTransaction.getId());
                    }
                }
                else {
                    log.debug("No invoice line created from ratedTransaction id = {} with status {}",
                            ratedTransaction.getId(), oldStatus);
                }
            }
        }

        return update(ratedTransaction);
    }

    /**
     * Recompute the fields of an existing invoice line during rerating process.
     * The amounts must be extracted from the generated invoice line since ratedTransaction is in RERATED
     *
     * @param invoiceLine instance of InvoiceLine
     * @param ratedTransaction instance of RatedTransaction
     */
    public void recomputeInvoiceLine(InvoiceLine invoiceLine, RatedTransaction ratedTransaction) {
        InvoiceLinesFactory linesFactory = new InvoiceLinesFactory();
        BigDecimal deltaAmountWithoutTax = ratedTransaction.getAmountWithoutTax().negate();
        BigDecimal deltaAmountWithTax = ratedTransaction.getAmountWithTax().negate();
        BigDecimal deltaAmountTax = ratedTransaction.getAmountTax().negate();
        BigDecimal[] deltaAmounts = new BigDecimal[] {deltaAmountWithoutTax, deltaAmountWithTax, deltaAmountTax};

        BigDecimal amountWithoutTax = (invoiceLine.getAmountWithoutTax()).subtract(ratedTransaction.getAmountWithoutTax());
        BigDecimal deltaQuantity = ratedTransaction.getQuantity().negate();
        BigDecimal quantity = invoiceLine.getQuantity().subtract(ratedTransaction.getQuantity());
        Date beginDate = invoiceLine.getValidity().getFrom();
        Date endDate = invoiceLine.getValidity().getTo();
        BigDecimal unitPrice = invoiceLine.getUnitPrice();
        BillingRun billingRun = invoiceLine.getBillingRun();
        if (billingRun != null
                && billingRun.getBillingCycle() != null
                && !billingRun.getBillingCycle().isDisableAggregation()
                && billingRun.getBillingCycle().isAggregateUnitAmounts()) {
            unitPrice = quantity.compareTo(ZERO) == 0 ? amountWithoutTax : amountWithoutTax.divide(quantity,
                    appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        }

        linesFactory.update(invoiceLine.getId(), deltaAmounts, deltaQuantity, beginDate, endDate, unitPrice);
    }

    public List<RatedTransaction> getReportRatedTransactions(BillingRun billingRun, Map<String, Object> filters) {
        if(filters != null && !filters.isEmpty()) {
            return (List<RatedTransaction>) getQueryFromFilters(filters, emptyList(), true)
                    .getQuery(getEntityManager()).getResultList();
        } else {
            Map<String, Object> billingRunFilters = billingRun.getBillingCycle() != null
                    ? billingRun.getBillingCycle().getFilters() : billingRun.getFilters();
            if (billingRunFilters == null && billingRun.getBillingCycle() != null) {
                billingRunFilters = new HashMap<>();
                billingRunFilters.put("billingAccount.billingCycle.id", billingRun.getBillingCycle().getId());
            }
            billingRunFilters.put("status", RatedTransactionStatusEnum.OPEN.toString());
            return (List<RatedTransaction>) getQueryFromFilters(billingRunFilters, emptyList(), true)
                    .getQuery(getEntityManager()).getResultList();
        }
    }

    public List<Object[]> getReportStatisticsDetails(BillingRun billingRun,
                                                     List<Long> ratedTransactionIds, Map<String, Object> filters) {
        if(filters != null && !filters.isEmpty()) {
            return getEntityManager()
                    .createNamedQuery("RatedTransaction.findBillingRunReportBilledDetails")
                    .setParameter("ids", ratedTransactionIds)
                    .setParameter("billingRun", billingRun)
                    .getResultList();
        } else {
            return getEntityManager()
                    .createNamedQuery("RatedTransaction.findBillingRunReportDetails")
                    .setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
                    .setParameter("invoiceUpToDate", billingRun.getInvoiceDate())
                    .setParameter("ids", ratedTransactionIds)
                    .getResultList();
        }
    }

    public List<Object[]> getBillingAccountStatisticsDetails(BillingRun billingRun,
                                                             List<Long> ratedTransactionIds, Map<String, Object> filters) {
        if(filters != null && !filters.isEmpty()) {
            return getEntityManager()
                    .createNamedQuery("RatedTransaction.findAmountsPerBillingAccountBilledDetails")
                    .setParameter("billingRun", billingRun)
                    .setParameter("ids", ratedTransactionIds)
                    .setMaxResults(10)
                    .getResultList();
        } else {
            return getEntityManager()
                    .createNamedQuery("RatedTransaction.findAmountsPerBillingAccount")
                    .setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
                    .setParameter("invoiceUpToDate", billingRun.getInvoiceDate())
                    .setParameter("ids", ratedTransactionIds)
                    .setMaxResults(10)
                    .getResultList();
        }
    }

    public List<Object[]> getOfferStatisticsDetails(BillingRun billingRun,
                                                    List<Long> ratedTransactionIds, Map<String, Object> filters) {
        if(filters != null && !filters.isEmpty()) {
            return getEntityManager()
                    .createNamedQuery("RatedTransaction.findAmountsPerOfferBilledDetails")
                    .setParameter("billingRun", billingRun)
                    .setParameter("ids", ratedTransactionIds)
                    .setMaxResults(10)
                    .getResultList();
        } else {
            return getEntityManager()
                    .createNamedQuery("RatedTransaction.findAmountsPerOffer")
                    .setParameter("lastTransactionDate", billingRun.getLastTransactionDate())
                    .setParameter("invoiceUpToDate", billingRun.getInvoiceDate())
                    .setParameter("ids", ratedTransactionIds)
                    .setMaxResults(10)
                    .getResultList();
        }
    }
}
