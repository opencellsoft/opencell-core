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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.UnrolledbackBusinessException;
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
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.ChargeInstance;
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
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.api.dto.ConsumptionDTO;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
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

    /**
     * @param userAccount user account
     * @return list
     */
    public List<RatedTransaction> getRatedTransactionsInvoiced(UserAccount userAccount) {
        if ((userAccount == null) || (userAccount.getWallet() == null)) {
            return null;
        }
        return getEntityManager().createNamedQuery("RatedTransaction.listInvoiced", RatedTransaction.class).setParameter("wallet", userAccount.getWallet()).getResultList();
    }

    /**
     * @param subscription subscription
     * @param infoType info type
     * @param billingCycle billing cycle
     * @param sumarizeConsumption summary consumption
     * @return instance of ConsumptionDTO
     * @throws IncorrectSusbcriptionException exception for incorrect subscription
     */
    @SuppressWarnings("unchecked")
    // FIXME: edward please use Named queries
    public ConsumptionDTO getConsumption(Subscription subscription, String infoType, Integer billingCycle, boolean sumarizeConsumption) throws IncorrectSusbcriptionException {

        Date lastBilledDate = null;
        ConsumptionDTO consumptionDTO = new ConsumptionDTO();

        // If billing has been run already, use last billing date plus a day as
        // filtering FROM value
        // Otherwise leave it null, so it wont be included in a query
        if (subscription.getUserAccount().getBillingAccount().getBillingRun() != null) {
            lastBilledDate = subscription.getUserAccount().getBillingAccount().getBillingRun().getEndDate();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(lastBilledDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            lastBilledDate = calendar.getTime();

        }

        if (sumarizeConsumption) {

            QueryBuilder qb = new QueryBuilder("select sum(amount1WithTax), sum(usageAmount) from " + RatedTransaction.class.getSimpleName());
            qb.addCriterionEntity("subscription", subscription);
            qb.addCriterion("subUsageCode1", "=", infoType, false);
            qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
            String baseSql = qb.getSqlString();

            // Summarize invoiced transactions
            String sql = baseSql + " and status='BILLED'";

            Query query = getEntityManager().createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            Object[] results = (Object[]) query.getSingleResult();

            consumptionDTO.setAmountCharged((BigDecimal) results[0]);
            consumptionDTO.setConsumptionCharged(((Long) results[1]).intValue());

            // Summarize not invoiced transactions
            sql = baseSql + " and status<>'BILLED'";

            query = getEntityManager().createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            results = (Object[]) query.getSingleResult();

            consumptionDTO.setAmountUncharged((BigDecimal) results[0]);
            consumptionDTO.setConsumptionUncharged(((Long) results[1]).intValue());

        } else {

            QueryBuilder qb = new QueryBuilder(
                "select sum(amount1WithTax), sum(usageAmount), groupingId, case when status='BILLED' then 'true' else 'false' end from " + RatedTransaction.class.getSimpleName());
            qb.addCriterionEntity("subscription", subscription);
            qb.addCriterion("subUsageCode1", "=", infoType, false);
            qb.addCriterionDateRangeFromTruncatedToDay("usageDate", lastBilledDate);
            qb.addSql("groupingId is not null");
            String sql = qb.getSqlString() + " group by groupingId, case when status='BILLED' then 'true' else 'false' end";

            Query query = getEntityManager().createQuery(sql);

            for (Entry<String, Object> param : qb.getParams().entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {

                BigDecimal amount = (BigDecimal) result[0];
                int consumption = ((Long) result[1]).intValue();
                boolean charged = Boolean.parseBoolean((String) result[3]);
                // boolean roaming =
                // RatedTransaction.translateGroupIdToRoaming(groupId);
                // boolean upload =
                // RatedTransaction.translateGroupIdToUpload(groupId);

                if (charged) {

                    // if (!roaming && !upload) {
                    consumptionDTO.setIncomingNationalConsumptionCharged(consumption);
                    // } else if (roaming && !upload) {
                    // consumptionDTO.setIncomingRoamingConsumptionCharged(consumption);
                    // } else if (!roaming && upload) {
                    // consumptionDTO.setOutgoingNationalConsumptionCharged(consumption);
                    // } else {
                    // consumptionDTO.setOutgoingRoamingConsumptionCharged(consumption);
                    // }

                    consumptionDTO.setConsumptionCharged(consumptionDTO.getConsumptionCharged() + consumption);
                    consumptionDTO.setAmountCharged(consumptionDTO.getAmountCharged().add(amount));

                } else {
                    // if (!roaming && !upload) {
                    consumptionDTO.setIncomingNationalConsumptionUncharged(consumption);
                    // } else if (roaming && !upload) {
                    // consumptionDTO.setIncomingRoamingConsumptionUncharged(consumption);
                    // } else if (!roaming && upload) {
                    // consumptionDTO.setOutgoingNationalConsumptionUncharged(consumption);
                    // } else {
                    // consumptionDTO.setOutgoingRoamingConsumptionUncharged(consumption);
                    // }
                    consumptionDTO.setConsumptionUncharged(consumptionDTO.getConsumptionUncharged() + consumption);
                    consumptionDTO.setAmountUncharged(consumptionDTO.getAmountUncharged().add(amount));
                }
            }
        }

        return consumptionDTO;

    }

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
     * Check if Order has any not yet billed Rated transactions
     * 
     * @param billingAccount billing account
     * @param orderNumber order number.
     * @param firstTransactionDate firstTransactionDate.
     * @param lastTransactionDate lastTransactionDate.
     * @return true/false
     */
    public Boolean isOrderBillable(String orderNumber, Date firstTransactionDate, Date lastTransactionDate) {
        long count = 0;
        TypedQuery<Long> q = getEntityManager().createNamedQuery("RatedTransaction.countNotInvoicedOpenByOrder", Long.class);
        count = q.setParameter("orderNumber", orderNumber).setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
            .getSingleResult();
        log.debug("isOrderBillable ,orderNumber={}) : {}", orderNumber, count);
        return count > 0 ? true : false;
    }

    /**
     * This method is only for generating Xml invoice {@link org.meveo.service.billing.impl.XMLInvoiceCreator #createXMLInvoice(Long, java.io.File, boolean, boolean)
     * createXMLInvoice}
     * <p>
     * If the provider's displayFreeTransacInInvoice of the current invoice is <tt>false</tt>, RatedTransaction with amount=0 don't show up in the XML.
     * </p>
     * 
     * @param wallet wallet instance
     * @param invoice invoice
     * @param invoiceSubCategory invoice sub category
     * @return list of rated transaction.
     */
    public List<RatedTransaction> getRatedTransactionsForXmlInvoice(WalletInstance wallet, Invoice invoice, InvoiceSubCategory invoiceSubCategory) {

        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c", Arrays.asList("priceplan"));
        qb.addCriterionEntity("c.wallet", wallet);
        qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);
        qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
        qb.addCriterionEntity("c.invoice", invoice);

        if (!appProvider.isDisplayFreeTransacInInvoice()) {
            qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
        }

        qb.addOrderCriterionAsIs("c.usageDate", true);

        @SuppressWarnings("unchecked")
        List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

        return ratedTransactions;

    }

    /**
     * @param invoice invoice to get rated transaction.
     * @return list of rated transactions
     */
    public List<RatedTransaction> getRatedTransactionsForXmlInvoice(Invoice invoice) {

        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c");
        qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
        qb.addCriterionEntity("c.invoice", invoice);

        if (!appProvider.isDisplayFreeTransacInInvoice()) {
            qb.addCriterion("c.amountWithoutTax", "<>", BigDecimal.ZERO, false);
        }

        qb.addOrderCriterionAsIs("c.usageDate", true);
        @SuppressWarnings("unchecked")
        List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

        return ratedTransactions;

    }

    /**
     * @param wallet wallet contains the wallet operation.
     * @param invoice invoice to get from
     * @param invoiceSubCategory invoice sub category
     * @return list of rated transactions.
     */
    public List<RatedTransaction> getRatedTransactions(WalletInstance wallet, Invoice invoice, InvoiceSubCategory invoiceSubCategory) {

        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c");
        qb.addCriterionEnum("c.status", RatedTransactionStatusEnum.BILLED);
        qb.addCriterionEntity("c.wallet", wallet);
        qb.addCriterionEntity("c.invoice", invoice);
        qb.addCriterionEntity("c.invoiceSubCategory", invoiceSubCategory);
        qb.addOrderCriterionAsIs("c.usageDate", true);

        @SuppressWarnings("unchecked")
        List<RatedTransaction> ratedTransactions = qb.getQuery(getEntityManager()).getResultList();

        return ratedTransactions;

    }

    /**
     * @param id wallet operation id
     * @return re-rated transaction id
     * @throws UnrolledbackBusinessException un rolledback business exception
     */
    public int reratedByWalletOperationId(Long id) throws UnrolledbackBusinessException {
        int result = 0;
        List<RatedTransaction> ratedTransactions = getEntityManager().createNamedQuery("RatedTransaction.listByWalletOperationId", RatedTransaction.class)
            .setParameter("walletOperationId", id).getResultList();
        for (RatedTransaction ratedTransaction : ratedTransactions) {
            BillingRun billingRun = ratedTransaction.getBillingRun();
            if ((billingRun != null) && (billingRun.getStatus() != BillingRunStatusEnum.CANCELED)) {
                throw new UnrolledbackBusinessException("A rated transaction " + ratedTransaction.getId() + " forbid rerating of wallet operation " + id);
            }
            ratedTransaction.setStatus(RatedTransactionStatusEnum.RERATED);
            result++;
        }
        return result;
    }

    /**
     * @param walletOperationId wallet operation i
     * @return list of rated transactions
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> getNotBilledRatedTransactions(Long walletOperationId) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c");
        qb.addCriterionEntity("c.walletOperationId", walletOperationId);
        qb.addCriterion("c.status", "!=", RatedTransactionStatusEnum.BILLED, false);
        try {
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error on get not billed rated transactions ", e);
            return null;
        }
    }

    /**
     * @param BillingRun billing run
     * @return list of rated transactions for given billing run.
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> getRatedTransactionsByBillingRun(BillingRun BillingRun) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "c");
        qb.addCriterionEntity("c.billingRun", BillingRun);
        try {
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to get ratedTransactions ny nillingRun", e);
            return null;
        }

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
     * @param walletOperationId wallet operation i
     * @throws BusinessException business exception
     */
    public void createRatedTransaction(Long walletOperationId) throws BusinessException {
        WalletOperation walletOperation = walletOperationService.findById(walletOperationId);

        createRatedTransaction(walletOperation, false);
    }

    /**
     * Convert Wallet operations to Rated transactions for a given billable entity up to a given date
     * 
     * @param entity entity to bill
     * @param invoicingDate invoicing date
     * @throws BusinessException business exception.
     */
    public void createRatedTransaction(IBillableEntity entity, Date invoicingDate) throws BusinessException {
        List<WalletOperation> walletOps = new ArrayList<WalletOperation>();
        if (entity instanceof BillingAccount) {
            BillingAccount billingAccount = billingAccountService.findById(((BillingAccount) entity).getId());
            List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
            for (UserAccount ua : userAccounts) {
                walletOps.addAll(walletOperationService.listToInvoiceByUserAccount(invoicingDate, ua));
            }
        } else if (entity instanceof Subscription) {
            walletOps.addAll(walletOperationService.listToInvoiceBySubscription(invoicingDate, (Subscription) entity));
        } else if (entity instanceof Order) {
            walletOps.addAll(walletOperationService.listToInvoiceByOrder(invoicingDate, (Order) entity));
        }

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

        if (!isVirtual) {
            create(ratedTransaction);
        }
        walletOperation.setStatus(WalletOperationStatusEnum.TREATED);
        walletOperation.setRatedTransaction(ratedTransaction);

        if (!isVirtual) {
            walletOperationService.updateNoCheck(walletOperation);
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
                tempWo.setRatedTransaction(ratedTransaction);
            }
        }

        return ratedTransaction;
    }

    /**
     * @param invoice invoice
     * @return list of rated transactions for given invoice
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> listByInvoice(Invoice invoice) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "r");
        qb.addCriterionEntity("invoice", invoice);
        qb.addOrderCriterion("id", true);

        try {
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param walletInstance wallet's instance
     * @param invoiceSubCategory invoice sub category
     * @return list of open rated transaction for a given invoice sub category.
     */
    public List<RatedTransaction> openRTbySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory) {
        return openRTbySubCat(walletInstance, invoiceSubCategory, null, null);
    }

    /**
     * @param walletInstance wallet instance
     * @param invoiceSubCategory invoice sub category
     * @param from checking date
     * @param to checking date
     * @return list of rated transaction
     */
    @SuppressWarnings("unchecked")
    public List<RatedTransaction> openRTbySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder(RatedTransaction.class, "rt");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("rt.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("rt.wallet", walletInstance);
        qb.addSql("rt.invoice is null");
        qb.addCriterionEnum("rt.status", RatedTransactionStatusEnum.OPEN);
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
            getEntityManager().createNamedQuery("RatedTransaction.setStatusToCanceledByRsCodes").setParameter("rsToCancelCodes", rsToCancelIds).executeUpdate();
        }
    }

    /**
     * Update billing account total amounts.
     *
     * @param entity entity
     * @param billingRun the billing run
     * @return Updated entity
     * @throws BusinessException the business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBillableEntity updateEntityTotalAmounts(IBillableEntity entity, BillingRun billingRun) throws BusinessException {

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

        calculateAmountsAndCreateMinAmountTransactions(entity, null, billingRun.getLastTransactionDate(), true);

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
                log.debug("updateEntityTotalAmounts  invoicingThreshold( stop invoicing)  baCode:{}, amountWithoutTax:{} ,invoicingThreshold:{}", entity.getCode(), invoiceAmount,
                    invoicingThreshold);
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
     * Create min amounts rated transactions and set invoiceable amounts to the billable entity
     *
     * @param billableEntity The billable entity
     * @param lastTransactionDate Last transaction date
     * @param calculateAndUpdateTotalAmounts Should total amounts be calculated and entity updated with those amounts
     * @throws BusinessException General business exception
     */
    public void calculateAmountsAndCreateMinAmountTransactions(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate,
            boolean calculateAndUpdateTotalAmounts) throws BusinessException {

        Amounts totalInvoiceableAmounts = null;

        List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();

        Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

        if (billableEntity instanceof Order && calculateAndUpdateTotalAmounts) {
            totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);

        } else if (billableEntity instanceof Subscription) {

            BillingAccount billingAccount = ((Subscription) billableEntity).getUserAccount().getBillingAccount();

            Map<Long, Map<String, Amounts>> createdAmountServices = createMinRTForServices(billableEntity, billingAccount, lastTransactionDate, minRatingDate,
                minAmountTransactions);

            Map<String, Amounts> createdAmountSubscription = createMinRTForSubscriptions(billableEntity, billingAccount, lastTransactionDate, minRatingDate, minAmountTransactions,
                createdAmountServices);

            if (calculateAndUpdateTotalAmounts) {
                // Get total invoiceable amount for subscription and add created amounts during min RT creation
                totalInvoiceableAmounts = computeTotalInvoiceableAmountForSubscription((Subscription) billableEntity, new Date(0), lastTransactionDate);

                // Sum up
                for (Map<String, Amounts> amountInfo : createdAmountServices.values()) {
                    for (Amounts amounts : amountInfo.values()) {
                        totalInvoiceableAmounts.addAmounts(amounts);
                    }
                }
                for (Amounts amounts : createdAmountSubscription.values()) {
                    totalInvoiceableAmounts.addAmounts(amounts);
                }
            }

        } else if (billableEntity instanceof BillingAccount) {

            BillingAccount billingAccount = (BillingAccount) billableEntity;

            Map<Long, Map<String, Amounts>> createdAmountServices = createMinRTForServices(billableEntity, billingAccount, lastTransactionDate, minRatingDate,
                minAmountTransactions);

            Map<String, Amounts> createdAmountSubscription = createMinRTForSubscriptions(billableEntity, billingAccount, lastTransactionDate, minRatingDate, minAmountTransactions,
                createdAmountServices);

            if (calculateAndUpdateTotalAmounts || isAppliesMinRTForBA(billingAccount, null)) {
                // Get total invoiceable amount for billing account and add created amounts during min RT creation for service and subscription
                totalInvoiceableAmounts = computeTotalInvoiceableAmountForBillingAccount(billingAccount, new Date(0), lastTransactionDate);

                // Sum up
                for (Map<String, Amounts> serviceAmountInfo : createdAmountServices.values()) {
                    for (Amounts amounts : serviceAmountInfo.values()) {
                        totalInvoiceableAmounts.addAmounts(amounts);
                    }
                }
                for (Amounts amounts : createdAmountSubscription.values()) {
                    totalInvoiceableAmounts.addAmounts(amounts);
                }

                if (isAppliesMinRTForBA(billingAccount, totalInvoiceableAmounts)) {

                    Map<String, Amounts> extraAmounts = new HashMap<>();
                    extraAmounts.putAll(createdAmountSubscription);
                    for (Map<String, Amounts> serviceAmountInfo : createdAmountServices.values()) {

                        for (Entry<String, Amounts> amountInfo : serviceAmountInfo.entrySet()) {
                            if (extraAmounts.containsKey(amountInfo.getKey())) {
                                extraAmounts.get(amountInfo.getKey()).addAmounts(amountInfo.getValue());
                            } else {
                                extraAmounts.put(amountInfo.getKey(), amountInfo.getValue());
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
     * @param minAmountTransactions Newly created minimum amount Rated transactions. ARE UPDATED by this method. Rated transactions created in this method are appended.
     * @return A map of amounts created with subscription id as a main key and a secondary map of "&lt;seller.id&gt;_&lt;invoiceSubCategory.id&gt; as a key a and amounts as values"
     *         as a value
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("unchecked")
    private Map<Long, Map<String, Amounts>> createMinRTForServices(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate,
            List<RatedTransaction> minAmountTransactions) throws BusinessException {

        // Only interested in services with minAmount condition
        // Calculate amounts on service level grouped by invoice category and service instance
        // Calculate a total sum of amounts on service level
        List<Object[]> amountsList = computeInvoiceableAmountForServices(billableEntity, new Date(0), lastTransactionDate);
        if (amountsList.isEmpty()) {
            return new HashMap<>();
        }

        // Service id as a key and array of <min amount>, <min amount label>, <total amounts>, map of <Invoice subCategory id, amounts], serviceInstance>
        Map<Long, Object[]> serviceInstanceToMinAmount = new HashMap<>();

        EntityManager em = getEntityManager();

        for (Object[] amounts : amountsList) {
            BigDecimal invSubcategoryAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal invSubcategoryAmountWithTax = (BigDecimal) amounts[1];

            Long invSubCategoryId = (Long) amounts[2];
            ServiceInstance serviceInstance = em.find(ServiceInstance.class, amounts[3]);

            // Resolve if minimal invoice amount rule applies
            if (!serviceInstanceToMinAmount.containsKey(serviceInstance.getId())) {

                String minAmountEL = serviceInstance.getMinimumAmountEl();
                String minAmountLabelEL = serviceInstance.getMinimumLabelEl();

                BigDecimal minAmount = null;
                String minAmountLabel = null;

                if (!StringUtils.isBlank(minAmountEL)) {
                    minAmount = evaluateMinAmountExpression(minAmountEL, null, null, serviceInstance);
                    minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, null, serviceInstance);
                }
                if (minAmount == null) {
                    serviceInstanceToMinAmount.put(serviceInstance.getId(), null);
                    continue;
                }

                serviceInstanceToMinAmount.put(serviceInstance.getId(), new Object[] { minAmount, minAmountLabel, new Amounts(), new HashMap<Long, Amounts>(), serviceInstance });
            }
            Object[] minAmountInfo = serviceInstanceToMinAmount.get(serviceInstance.getId());
            // Does not apply
            if (minAmountInfo == null) {
                continue;
            }

            ((Amounts) minAmountInfo[2]).addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
            ((Map<Long, Amounts>) minAmountInfo[3]).put(invSubCategoryId, new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null));

            // Service amount exceed the minimum amount per service
            if (((BigDecimal) minAmountInfo[0])
                .compareTo(appProvider.isEntreprise() ? ((Amounts) minAmountInfo[2]).getAmountWithoutTax() : ((Amounts) minAmountInfo[2]).getAmountWithTax()) <= 0) {
                serviceInstanceToMinAmount.put(serviceInstance.getId(), null);
                continue;
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
                    amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, null, null, null, null, null, null,
                    RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode() + "_" + serviceInstance.getCode(), minAmountLabel, null, null, seller, tax, tax.getPercent(),
                    serviceInstance);

                minAmountTransactions.add(ratedTransaction);

                // Remember newly "created" transaction amounts, as they are not persisted yet to DB
                minRTAmountSubscriptionMap.put(mapKey, new Amounts(amounts[0], amounts[1], amounts[2]));

                totalRatio = totalRatio.add(ratio);
            }
        }

        return minRTAmountMap;
    }

    /**
     * Create Rated transactions to reach minimum invoiced amount per subscription level. Only those subscriptions that have minimum invoice amount rule are considered. Updates
     * minAmountTransactions parameter.
     * 
     * @param billableEntity Entity to bill - entity for which minimum rated transactions should be created
     * @param billingAccount Billing account to associate new minimum amount Rated transactions with
     * @param lastTransactionDate Last transaction date
     * @param minRatingDate Date to assign to newly created minimum amount Rated transactions
     * @param minAmountTransactions Newly created minimum amount Rated transactions. ARE UPDATED by this method. Rated trancastions created in this method are appended.
     * @param extraAmountsPerSubscription Additional Rated transaction amounts created to reach minimum invoicing amount per service. A map of amounts created with subscription id
     *        as a main key and a secondary map of "&lt;seller.id&gt;_&lt;invoiceSubCategory.id&gt; as a key a and amounts as values" as a value
     * @return Additional Rated transaction amounts created to reach minimum invoicing amount per subscription. A map of &lt;seller.id&gt;_&lt;invoiceSubCategory.id&gt; as a key a
     *         and amounts as values
     * @throws BusinessException General Business exception
     */
    @SuppressWarnings("unchecked")
    private Map<String, Amounts> createMinRTForSubscriptions(IBillableEntity billableEntity, BillingAccount billingAccount, Date lastTransactionDate, Date minRatingDate,
            List<RatedTransaction> minAmountTransactions, Map<Long, Map<String, Amounts>> extraAmountsPerSubscription) throws BusinessException {

        // Only interested in subscriptions with minAmount condition
        // Calculate amounts on subscription level grouped by invoice category and subscription
        // Calculate a total sum of amounts on subscription level
        List<Object[]> amountsList = computeInvoiceableAmountForSubscriptions(billableEntity, new Date(0), lastTransactionDate);
        if (amountsList.isEmpty()) {
            return new HashMap<>();
        }

        // Subscription id as a key and array of <min amount>, <min amount label>, <total amounts>, map of <Invoice subCategory id, amounts], subscription>
        Map<Long, Object[]> subscriptionToMinAmount = new HashMap<>();

        EntityManager em = getEntityManager();

        for (Object[] amounts : amountsList) {
            BigDecimal invSubcategoryAmountWithoutTax = (BigDecimal) amounts[0];
            BigDecimal invSubcategoryAmountWithTax = (BigDecimal) amounts[1];
            Long invSubCategoryId = (Long) amounts[2];
            Subscription subscription = em.find(Subscription.class, amounts[3]);

            // Resolve if minimal invoice amount rule applies
            if (!subscriptionToMinAmount.containsKey(subscription.getId())) {

                String minAmountEL = subscription.getMinimumAmountEl();
                String minAmountLabelEL = subscription.getMinimumLabelEl();

                BigDecimal minAmount = null;
                String minAmountLabel = null;

                if (!StringUtils.isBlank(minAmountEL)) {
                    minAmount = evaluateMinAmountExpression(minAmountEL, null, subscription, null);
                    minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, null, subscription, null);
                }
                if (minAmount == null) {
                    subscriptionToMinAmount.put(subscription.getId(), null);
                    continue;
                }

                subscriptionToMinAmount.put(subscription.getId(), new Object[] { minAmount, minAmountLabel, new Amounts(), new HashMap<Long, Amounts>(), subscription });

                // Append extra amounts from service level
                if (extraAmountsPerSubscription.containsKey(subscription.getId())) {

                    Object[] subscriptionToMinAmountInfo = subscriptionToMinAmount.get(subscription.getId());
                    Map<String, Amounts> extraAmounts = extraAmountsPerSubscription.get(subscription.getId());

                    for (Entry<String, Amounts> amountInfo : extraAmounts.entrySet()) {
                        ((Amounts) subscriptionToMinAmountInfo[2]).addAmounts(amountInfo.getValue());
                        // Key consist of sellerId_invoiceSubCategoryId. Interested in invoiceSubCategoryId only
                        ((Map<Long, Amounts>) subscriptionToMinAmountInfo[3]).put(Long.parseLong(amountInfo.getKey().split("_")[1]), amountInfo.getValue().clone());
                    }
                }
            }
            Object[] minAmountInfo = subscriptionToMinAmount.get(subscription.getId());
            // Does not apply
            if (minAmountInfo == null) {
                continue;
            }

            ((Amounts) minAmountInfo[2]).addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
            Amounts subCatAmounts = ((Map<Long, Amounts>) minAmountInfo[3]).get(invSubCategoryId);
            if (subCatAmounts == null) {
                subCatAmounts = new Amounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
                ((Map<Long, Amounts>) minAmountInfo[3]).put(invSubCategoryId, subCatAmounts);
            } else {
                subCatAmounts.addAmounts(invSubcategoryAmountWithoutTax, invSubcategoryAmountWithTax, null);
            }

            // Service amount exceed the minimum amount per service
            if (((BigDecimal) minAmountInfo[0])
                .compareTo(appProvider.isEntreprise() ? ((Amounts) minAmountInfo[2]).getAmountWithoutTax() : ((Amounts) minAmountInfo[2]).getAmountWithTax()) <= 0) {
                subscriptionToMinAmount.put(subscription.getId(), null);
                continue;
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
                    amounts[2], RatedTransactionStatusEnum.OPEN, null, billingAccount, null, invoiceSubCategory, null, null, null, null, null, subscription, null, null, null, null,
                    null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SU.getCode() + "_" + subscription.getCode(), minAmountLabel, null, null, seller, tax, tax.getPercent(),
                    null);

                minAmountTransactions.add(ratedTransaction);

                // Remember newly "created" transaction amounts, as they are not persisted yet to DB
                minRTAmountMap.put(mapKey, new Amounts(amounts[0], amounts[1], amounts[2]));

                totalRatio = totalRatio.add(ratio);
            }
        }

        return minRTAmountMap;
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

        // Interested in Billing accounts with minimum amount criteria
        BigDecimal minAmount = null;
        String minAmountLabel = null;

        String minAmountEL = billingAccount.getMinimumAmountEl();
        String minAmountLabelEL = billingAccount.getMinimumLabelEl();

        if (!StringUtils.isBlank(minAmountEL)) {
            minAmount = evaluateMinAmountExpression(minAmountEL, billingAccount, null, null);
            minAmountLabel = evaluateMinAmountLabelExpression(minAmountLabelEL, billingAccount, null, null);
        }

        if (minAmount == null) {
            return new HashMap<>();
        }

        // <Seller.id>_<InvoiceSubCategory.id> as a key and amounts as values
        Map<String, Amounts> baToMinAmounts = new HashMap<>();

        // Calculate amounts on billing account level grouped by invoice category and seller
        // Calculate a total sum of amounts for billing account
        List<Object[]> amountsList = computeInvoiceableAmountForBillingAccount(billingAccount, new Date(0), lastTransactionDate);
        if (amountsList.isEmpty()) {
            return new HashMap<>();
        }

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

        EntityManager em = getEntityManager();
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

    /**
     * Summed rated transaction amounts applied on services, that have minimum invoiceable amount rule, grouped by invoice subCategory for a given billable entity.
     * 
     * @param billableEntity Billable entity
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @return Summed rated transaction amounts as array: sum of amounts without tax, sum of amounts with tax, invoice subcategory id, serviceInstance
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> computeInvoiceableAmountForServices(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate) {

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
     * Obtain the last ID value of the Rated Transaction
     * 
     * @return Last ID value
     */
    public Long getLastRatedTransactionId() {

        try {
            return (long) getEntityManager().createNamedQuery("RatedTransaction.getLastId").getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }

    /**
     * Get a summary of invoiceable RTs for a given billable entity and date range.
     * 
     * @param entity Billable entity (subscription, billing account or order)
     * @param firstTransactionDate Usage date range - start date
     * @param lastTransactionDate Usage date range - end date
     * @return A list of RT summary information: billingAccount.id, seller.id, userAccount.id, wallet.id, invoiceSubCategory.id, tax.id, sum(amountWithoutTax), sum(amountWithTax),
     *         count
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> listSummaryOfRTsToInvoice(IBillableEntity entity, Date firstTransactionDate, Date lastTransactionDate) {

        if (entity instanceof Order) {
            return getEntityManager().createNamedQuery("RatedTransaction.summarizeToInvoiceByOrderNumber").setParameter("orderNumber", ((Order) entity).getOrderNumber())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();
        } else if (entity instanceof Subscription) {
            return getEntityManager().createNamedQuery("RatedTransaction.summarizeToInvoiceByBillingAccount").setParameter("subscription", entity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();

        } else if (entity instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("RatedTransaction.summarizeToInvoiceByBillingAccount").setParameter("billingAccount", entity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();
        }
        return new ArrayList<Object[]>();
    }

    /**
     * Update Rated transactions with invoice information in batch
     * 
     * @param entity Entity billed
     * @param firstTransactionDate Usage date range - start date
     * @param lastTransactionDate Usage date range - end date
     * @param invoice Invoice to associate with rated transaction
     * @param subCategoryAggregate Invoice subcategory aggregate to associate with rated transaction
     * @param lastId Last rated transaction id. A simple check to not update newly added records
     * @param previousTax Previous tax
     */
    public void updateInvoicesRTsBySummary(IBillableEntity entity, Date firstTransactionDate, Date lastTransactionDate, Invoice invoice,
            SubCategoryInvoiceAgregate subCategoryAggregate, Long lastId, Tax previousTax) {

        if (entity instanceof Order) {
            getEntityManager().createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfoByOrderSummary").setParameter("billingRun", invoice.getBillingRun())
                .setParameter("invoice", invoice).setParameter("invoiceAgregateF", subCategoryAggregate).setParameter("orderNumber", ((Order) entity).getOrderNumber())
                .setParameter("billingAccount", invoice.getBillingAccount()).setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate).setParameter("seller", invoice.getSeller())
                .setParameter("userAccount", subCategoryAggregate.getUserAccount()).setParameter("wallet", subCategoryAggregate.getWallet())
                .setParameter("invoiceSubCategory", subCategoryAggregate.getInvoiceSubCategory()).setParameter("tax", previousTax).setParameter("lastId", lastId).executeUpdate();

        } else if (entity instanceof Subscription) {
            getEntityManager().createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfoBySubscriptionSummary").setParameter("billingRun", invoice.getBillingRun())
                .setParameter("invoice", invoice).setParameter("invoiceAgregateF", subCategoryAggregate).setParameter("subscription", entity)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("seller", invoice.getSeller())
                .setParameter("userAccount", subCategoryAggregate.getUserAccount()).setParameter("wallet", subCategoryAggregate.getWallet())
                .setParameter("invoiceSubCategory", subCategoryAggregate.getInvoiceSubCategory()).setParameter("tax", previousTax).setParameter("lastId", lastId).executeUpdate();

        } else if (entity instanceof BillingAccount) {
            getEntityManager().createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfoByBASummary").setParameter("billingRun", invoice.getBillingRun())
                .setParameter("invoice", invoice).setParameter("invoiceAgregateF", subCategoryAggregate).setParameter("billingAccount", invoice.getBillingAccount())
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("seller", invoice.getSeller())
                .setParameter("userAccount", subCategoryAggregate.getUserAccount()).setParameter("wallet", subCategoryAggregate.getWallet())
                .setParameter("invoiceSubCategory", subCategoryAggregate.getInvoiceSubCategory()).setParameter("tax", previousTax).setParameter("lastId", lastId).executeUpdate();
        }
    }

    /**
     * Get a list of invoiceable Rated transactions for a given billable entity and date range or from a filter
     * 
     * @param entity Billable entity (subscription, billing account or order)
     * @param firstTransactionDate Usage date range - start date
     * @param lastTransactionDate Usage date range - end date
     * @param ratedTransactionFilter Filter returning a list of rated transactions
     * @return A list of RT summary information: seller.id, userAccount.id, wallet.id, invoiceSubCategory.id, tax.id, id, amountWithoutTax, amountWithTax, orderNumber in BA and
     *         subscription case or billingAccount.id in order case
     * @throws BusinessException General exception
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> listRTsToInvoice(IBillableEntity entity, Date firstTransactionDate, Date lastTransactionDate, Filter ratedTransactionFilter) throws BusinessException {

        if (ratedTransactionFilter != null) {
            List<RatedTransaction> ratedTransactions = (List<RatedTransaction>) filterService.filteredListAsObjects(ratedTransactionFilter);

            return ratedTransactions.stream()
                .map(rt -> new Object[] { rt.getSeller().getId(), rt.getUserAccount() != null ? rt.getUserAccount().getId() : null,
                        rt.getWallet() != null ? rt.getWallet().getId() : null, rt.getInvoiceSubCategory().getId(), rt.getTax().getId(), rt.getAmountWithoutTax(),
                        rt.getAmountWithTax(), entity instanceof Order ? rt.getBillingAccount().getId() : rt.getOrderNumber() })
                .collect(Collectors.toList());

        } else {

            if (entity instanceof Order) {
                return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByOrderNumberFlat").setParameter("orderNumber", ((Order) entity).getOrderNumber())
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();
            } else if (entity instanceof Subscription) {
                return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceBySubscriptionFlat").setParameter("subscription", entity)
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();

            } else if (entity instanceof BillingAccount) {
                return getEntityManager().createNamedQuery("RatedTransaction.listToInvoiceByBillingAccountFlat").setParameter("billingAccount", entity)
                    .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).getResultList();
            }
        }
        return new ArrayList<Object[]>();
    }
}