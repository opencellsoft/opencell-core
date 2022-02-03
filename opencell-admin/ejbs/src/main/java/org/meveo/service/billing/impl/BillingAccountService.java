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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.job.v2.invoicing.RefactoredInvoicingJob;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.threshold.ThresholdLevelEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.AccountService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.invoicing.impl.BillingAccountDetailsItem;
import org.meveo.service.billing.invoicing.impl.InvoicingItem;

/**
 * The Class BillingAccountService.
 * @author anasseh
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2.1
 */
@Stateless
public class BillingAccountService extends AccountService<BillingAccount> {

    /** The user account service. */
    @Inject
    private UserAccountService userAccountService;


    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

    /**
     * Inits the billing account.
     *
     * @param billingAccount the billing account
     */
    public void initBillingAccount(BillingAccount billingAccount) {
        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        if (billingAccount.getSubscriptionDate() == null) {
            billingAccount.setSubscriptionDate(new Date());
        }

        if (billingAccount.getNextInvoiceDate() == null) {
            billingAccount.setNextInvoiceDate(new Date());
        }
    }

    /**
     * Creates the billing account.
     *
     * @param billingAccount the billing account
     * @throws BusinessException the business exception
     */
    public void createBillingAccount(BillingAccount billingAccount) throws BusinessException {

        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        if (billingAccount.getSubscriptionDate() == null) {
            billingAccount.setSubscriptionDate(new Date());
        }

        if (billingAccount.getNextInvoiceDate() == null) {
            billingAccount.setNextInvoiceDate(new Date());
        }

        create(billingAccount);
    }

    /**
     * Update electronic billing.
     *
     * @param billingAccount the billing account
     * @param electronicBilling the electronic billing
     * @return the billing account
     * @throws BusinessException the business exception
     */
    public BillingAccount updateElectronicBilling(BillingAccount billingAccount, Boolean electronicBilling) throws BusinessException {
        billingAccount.setElectronicBilling(electronicBilling);
        return update(billingAccount);
    }

    /**
     * Update billing account discount.
     *
     * @param billingAccount the billing account
     * @param ratedDiscount the rated discount
     * @return the billing account
     * @throws BusinessException the business exception
     */
    public BillingAccount updateBillingAccountDiscount(BillingAccount billingAccount, BigDecimal ratedDiscount) throws BusinessException {
        billingAccount.setDiscountRate(ratedDiscount);
        return update(billingAccount);
    }

    /**
     * Billing account termination.
     *
     * @param billingAccount the billing account
     * @param terminationDate the termination date
     * @param terminationReason the termination reason
     * @return the billing account
     * @throws BusinessException the business exception
     */
    @MeveoAudit
    public BillingAccount billingAccountTermination(BillingAccount billingAccount, Date terminationDate, SubscriptionTerminationReason terminationReason) throws BusinessException {
        if (terminationDate == null) {
            terminationDate = new Date();
        }

        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
        for (UserAccount userAccount : userAccounts) {
            userAccountService.userAccountTermination(userAccount, terminationDate, terminationReason);
        }
        billingAccount.setTerminationReason(terminationReason);
        billingAccount.setTerminationDate(terminationDate);
        billingAccount.setStatus(AccountStatusEnum.TERMINATED);
        return update(billingAccount);
    }

    /**
     * Billing account cancellation.
     *
     * @param billingAccount the billing account
     * @param terminationDate the termination date
     * @return the billing account
     * @throws BusinessException the business exception
     */
    @MeveoAudit
    public BillingAccount billingAccountCancellation(BillingAccount billingAccount, Date terminationDate) throws BusinessException {
        if (terminationDate == null) {
            terminationDate = new Date();
        }
        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
        for (UserAccount userAccount : userAccounts) {
            userAccountService.userAccountCancellation(userAccount, terminationDate);
        }
        billingAccount.setTerminationDate(terminationDate);
        billingAccount.setStatus(AccountStatusEnum.CANCELED);
        return update(billingAccount);
    }

    /**
     * Billing account reactivation.
     *
     * @param billingAccount the billing account
     * @param activationDate the activation date
     * @return the billing account
     * @throws BusinessException the business exception
     */
    @MeveoAudit
    public BillingAccount billingAccountReactivation(BillingAccount billingAccount, Date activationDate) throws BusinessException {
        if (activationDate == null) {
            activationDate = new Date();
        }
        if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED && billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
            throw new ElementNotResiliatedOrCanceledException("billing account", billingAccount.getCode());
        }

        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        return update(billingAccount);
    }

    /**
     * Close billing account.
     *
     * @param billingAccount the billing account
     * @return the billing account
     * @throws BusinessException the business exception
     */
    @MeveoAudit
    public BillingAccount closeBillingAccount(BillingAccount billingAccount) throws BusinessException {

        /**
         * *
         * 
         * @Todo : ajouter la condition : l'encours de facturation est vide :
         */
        if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED && billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
            throw new ElementNotResiliatedOrCanceledException("billing account", billingAccount.getCode());
        }
        billingAccount.setStatus(AccountStatusEnum.CLOSED);
        return update(billingAccount);
    }

    /**
     * Invoice list.
     *
     * @param billingAccount the billing account
     * @return the list
     * @throws BusinessException the business exception
     */
    public List<Invoice> invoiceList(BillingAccount billingAccount) throws BusinessException {
        List<Invoice> invoices = billingAccount.getInvoices();
        Collections.sort(invoices, new Comparator<Invoice>() {
            public int compare(Invoice c0, Invoice c1) {

                return c1.getInvoiceDate().compareTo(c0.getInvoiceDate());
            }
        });
        return invoices;
    }

    /**
     * Invoice detail.
     *
     * @param invoiceReference the invoice reference
     * @return the invoice
     */
    public Invoice InvoiceDetail(String invoiceReference) {
        try {
            QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
            qb.addCriterion("i.invoiceNumber", "=", invoiceReference, true);
            return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ex) {
            log.debug("invoice search returns no result for reference={}.", invoiceReference);
        }
        return null;
    }

    /**
     * Find billing accounts.
     *
     * @param billingCycle the billing cycle
     * @param startdate the startdate
     * @param endDate the end date
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<BillingAccount> findBillingAccounts(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(BillingAccount.class, "b", null);
            qb.addCriterionEntity("b.billingCycle.id", billingCycle.getId());

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                if (Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false"))) {
                	qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate);
                } else {
                	qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate, false, false);
                }

            }

            qb.addOrderCriterionAsIs("id", true);

            String brLotSize = paramBeanFactory.getInstance().getProperty("billingRun.lot.size", null);
            if (!StringUtils.isBlank(brLotSize)) {
                log.info("Using param billingRun.lot.size={}", brLotSize);
                return (List<BillingAccount>) qb.getQuery(getEntityManager()).setMaxResults(Integer.parseInt(brLotSize)).getResultList();
            } else {
                return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
            }

        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }

    public List<Long> findNotProcessedBillingAccounts(BillingRun billingRun) {
    	
        try {        	
        	Date startDate = billingRun.getStartDate();
        	Date endDate = billingRun.getEndDate();

        	if(endDate==null) {
        		endDate=new Date();
        	}
        	
            QueryBuilder qb = new QueryBuilder ("select b.id from BillingAccount b ");                       
            qb.addCriterionEntity("b.billingCycle.id", billingRun.getBillingCycle().getId());
            qb.addSql("((b.billingRun.id is null) OR (b.billingRun.id<> :billingRunId))");
            qb.addCriterionDateRangeToTruncatedToDay("b.nextInvoiceDate", endDate, false, true);

            if (startDate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("b.nextInvoiceDate", startDate);
            }

            return(List<Long>) qb.getQuery(getEntityManager()).setParameter("billingRunId", billingRun.getId()).getResultList();
           
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }
        return null;
    }

    /**
     * List billing accounts that are associated with a given billing run
     * 
     * @param billingRun Billing run
     * @return A list of Billing accounts
     */
    public List<BillingAccount> findBillingAccounts(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("BillingAccount.listByBillingRun", BillingAccount.class).setParameter("billingRunId", billingRun.getId()).getResultList();
    }

    /**
     * Find billing account ids.
     *
     * @param billingCycle the billing cycle
     * @param startdate the startdate
     * @param endDate the end date
     * @return the list
     */
    public List<Long> findBillingAccountIds(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(BillingAccount.class, "b", null);
            qb.addCriterionEntity("b.billingCycle.id", billingCycle.getId());

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate, false, false);
            }

            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }

    /**
     * List by customer account.
     *
     * @param customerAccount the customer account
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<BillingAccount> listByCustomerAccount(CustomerAccount customerAccount) {
        QueryBuilder qb = new QueryBuilder(BillingAccount.class, "c");
        qb.addCriterionEntity("customerAccount", customerAccount);
        qb.addOrderCriterionAsIs("c.id", true);
        try {
            return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list by customer account", e);
            return null;
        }
    }

    /**
     * List billingAccounts by customer account with paginationConfiguration
     *
     * @param customerAccount the customer account
     * @return the list of billingAccounts
     */
    @SuppressWarnings("unchecked")
    public List<BillingAccount> listByCustomerAccount(CustomerAccount customerAccount, PaginationConfiguration config) {
        QueryBuilder qb = getQuery(config);
        qb.addCriterionEntity("customerAccount", customerAccount);

        try {
            return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list by customer account", e);
            return null;
        }
    }

    /**
     * Determine if billing account is exonerated from taxes - check either a flag or EL expressions in customer's customerCategory.
     *
     * @param ba The BillingAccount
     * @return True if billing account is exonerated from taxes
     */
    public boolean isExonerated(BillingAccount ba) {

        boolean isExonerated = false;
        if (ba == null) {
            return false;
        }
        CustomerCategory customerCategory = ba.getCustomerAccount().getCustomer().getCustomerCategory();
        if (customerCategory == null) {
            return false;
        }
        if (customerCategory.getExoneratedFromTaxes()) {
            return true;
        }

        if (!StringUtils.isBlank(customerCategory.getExonerationTaxEl())) {
            isExonerated = ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(customerCategory.getExonerationTaxEl(), "ba", ba);
        }
        return isExonerated;
    }
    
    public boolean isExonerated(BillingAccount ba, Boolean customerCategoryExoneratedFromTaxes, String exonerationTaxEl) {
        if (customerCategoryExoneratedFromTaxes.booleanValue()) {
            return true;
        }
        if (!StringUtils.isBlank(exonerationTaxEl)) {
        	ba = refreshOrRetrieve(ba);
            return ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(exonerationTaxEl, "ba", ba);
        }
        return false;
    }

    /**
     * Find billing accounts by billing run.
     *
     * @param billingRunId Billing run id
     * @return A list of billing account identifiers
     */
    public List<Long> findBillingAccountIdsByBillingRun(Long billingRunId) {
        return getEntityManager().createNamedQuery("BillingAccount.listIdsByBillingRunId", Long.class).setParameter("billingRunId", billingRunId).getResultList();
    }

    public BillingAccount instantiateDiscountPlans(BillingAccount entity, List<DiscountPlan> discountPlans) throws BusinessException {
        List<DiscountPlanInstance> toAdd = new ArrayList<>();
        for (DiscountPlan dp : discountPlans) {
            instantiateDiscountPlan(entity, dp);
        }

        if (!toAdd.isEmpty()) {
            entity.getAllDiscountPlanInstances().addAll(toAdd);
        }

        return entity;
    }

    public void terminateDiscountPlans(BillingAccount entity, List<DiscountPlanInstance> dpis) throws BusinessException {
        if (dpis == null) {
            return;
        }

        for (DiscountPlanInstance dpi : dpis) {
            terminateDiscountPlan(entity, dpi);
        }
    }

    public BillingAccount instantiateDiscountPlan(BillingAccount entity, DiscountPlan dp) throws BusinessException {
        for (UserAccount userAccount : entity.getUsersAccounts()) {
            UserAccount userAccountById = userAccountService.findById(userAccount.getId());
            for (Subscription subscription : userAccountById.getSubscriptions()) {
                for (DiscountPlanInstance discountPlanInstance : subscription.getDiscountPlanInstances()) {
                    if (dp.getCode().equals(discountPlanInstance.getDiscountPlan().getCode())) {
                        throw new BusinessException("DiscountPlan " + dp.getCode() + " is already instantiated in subscription " + subscription.getCode() + ".");
                    }
                }
            }
        }
        return (BillingAccount) discountPlanInstanceService.instantiateDiscountPlan(entity, dp, null);
    }

    public void terminateDiscountPlan(BillingAccount entity, DiscountPlanInstance dpi) throws BusinessException {
        discountPlanInstanceService.terminateDiscountPlan(entity, dpi);
    }

	/**
	 * @param billableAmountSummary 
	 * @param billingRun
	 * @param lastTransactionDate 
	 * @param nextInoiceDateLimits 
	 * @param nbRuns 
	 * @return 
	 */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> linkBillableEntitiesToBR(BillingRun billingRun, Date lastTransactionDate, Date[] nextInoiceDateLimits, Long min, Long max) {
        BillingCycle billingCycle = billingRun.getBillingCycle();
        String sqlName = billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION ? "Subscription.updateBR" :
        	nextInoiceDateLimits == null ? "BillingAccount.updateBR" : "BillingAccount.updateBRLimitByNextInvoiceDate";
        
        Query query = getEntityManager().createNamedQuery(sqlName).setParameter("firstTransactionDate", new Date(0))
                .setParameter("lastTransactionDate", lastTransactionDate).setParameter("billingCycle", billingCycle);
        query.setParameter("billingRun", billingRun);
        if (billingCycle.getType() == BillingEntityTypeEnum.BILLINGACCOUNT && nextInoiceDateLimits != null) {
            query.setParameter("startDate", nextInoiceDateLimits[0]).setParameter("endDate", nextInoiceDateLimits[1]);
        }

    	query.setParameter("min", min).setParameter("max", max);
    	int updated = query.executeUpdate();
    	log.info("LINK {} BAs TO BR",updated);
        return new AsyncResult<String>("OK");
    }
    
	public List<BillingAccountDetailsItem> getInvoicingItems(BillingRun billingRun, BillingCycle billingCycle, Date lastTransactionDate, int pageSize, int pageIndex) {
		String thresholdAmountColumn = "";
		String sumPositiveAmounts="";
		final ThresholdLevelEnum thresholdLevel = billingCycle.getThresholdLevel();
		if (ThresholdLevelEnum.INVOICE.equals(thresholdLevel)) {
			switch (thresholdLevel) {
			case CUSTOMER: thresholdAmountColumn = ", c.invoicingThreshold as "; break;
			case CUSTOMER_ACCOUNT: thresholdAmountColumn = ", ca.invoicingThreshold as "; break;
			case BILLING_ACCOUNT: thresholdAmountColumn = ", ba.invoicingThreshold as "; break;
			case INVOICE: thresholdAmountColumn = ", (case when ba.invoicingThreshold is not null then ba.invoicingThreshold else (case when ca.invoicingThreshold is not null then ca.invoicingThreshold else c.invoicingThreshold end) end) as "; 
			sumPositiveAmounts=ThresholdOptionsEnum.POSITIVE_RT.equals(billingCycle.getCheckThreshold())? (appProvider.isEntreprise() ? ", sum(case when rt.amountWithoutTax > 0 then rt.amountWithoutTax else 0 end) ":", sum(case when rt.amountWithTax > 0 then rt.amountWithTax else 0 end) "):"";break;
			default: break;
			}
		}
		String thresholdAlias = thresholdAmountColumn == "" ? "" : " col_10_0_ ";
		String thresholdGroupBy = thresholdAlias == "" ? "" : ", " + thresholdAlias;
		String BillingAccountDetailsQuery = "select b.id, b.tradingLanguage.id, b.nextInvoiceDate, b.electronicBilling, ca.dueDateDelayEL, cc.exoneratedFromTaxes, cc.exonerationTaxEl, m.id, m.paymentType, string_agg(concat(CAST(dpi.discountPlan.id as string),'|',CAST(dpi.startDate AS string),'|',CAST(dpi.endDate AS string)),',') "
    			+ thresholdAmountColumn + thresholdAlias
    			+ " FROM BillingAccount b left join b.customerAccount ca left join ca.customer c left join c.customerCategory cc "
    			+ " left join ca.paymentMethods m "
    			+ " left join b.discountPlanInstances dpi "
    			+ " where b.billingRun.id=:billingRunId and (m is null or m.preferred=true) "
    			+ " group by b.id, b.tradingLanguage.id, b.nextInvoiceDate, b.electronicBilling, ca.dueDateDelayEL, cc.exoneratedFromTaxes, cc.exonerationTaxEl, m.id, m.paymentType "+ thresholdGroupBy
    			+ " order by b.id";

		//split to 2 queries to avoid hibernate 'firstResult/maxResults specified with collection fetch; applying in memory!' 
		List<Object[]> resultList = getEntityManager().createQuery(BillingAccountDetailsQuery).setParameter("billingRunId", billingRun.getId()).setMaxResults(pageSize)
			      .setFirstResult(pageIndex * pageSize).getResultList();
		if(resultList==null || resultList.isEmpty()) {
			return new ArrayList<BillingAccountDetailsItem>();
		}
		
		
		final Map<Long, BillingAccountDetailsItem> billingAccountDetailsMap = resultList.stream().map(x-> new BillingAccountDetailsItem(x)).collect(Collectors.toMap(BillingAccountDetailsItem::getBillingAccountId, Function.identity()));
		
		final String invoicingItemsQuery = "select rt.billingAccount.id, rt.seller.id, w.id, w.walletTemplate.id, rt.invoiceSubCategory.id, rt.userAccount.id, rt.tax.id, sum(rt.amountWithoutTax), sum(rt.amountWithTax), sum(rt.amountTax), count(rt.id),"
				+ " (case  when count(rt.id)<:limitUpdateById then (string_agg(cast(rt.id as string),',')) else (CAST(min(rt.id) AS text)||','||CAST(max(rt.id) AS text)) end) "+sumPositiveAmounts
				+ " FROM RatedTransaction rt left join rt.wallet w "
				+ " where rt.billingAccount.id in (:ids) and rt.status='OPEN' and rt.usageDate<:lastTransactionDate "
				+ " group by rt.billingAccount.id, rt.seller.id, w.id, w.walletTemplate.id, rt.invoiceSubCategory.id, rt.userAccount.id, rt.tax.id "
				+ " order by rt.billingAccount.id";
		Query query = getEntityManager().createQuery(invoicingItemsQuery).setParameter("ids", billingAccountDetailsMap.keySet()).setParameter("lastTransactionDate", lastTransactionDate).setParameter("limitUpdateById", RefactoredInvoicingJob.LIMIT_UPDATE_BY_ID);
		final Map<Long, List<InvoicingItem>> itemsByBAID = ((List<Object[]>)query.getResultList()).stream().map(x-> new InvoicingItem(x)).collect(Collectors.groupingBy(InvoicingItem::getBillingAccountId));
		log.info("======= InvoicingItems ======="+itemsByBAID.size());
		billingAccountDetailsMap.values().stream().forEach(x->x.setInvoicingItems(itemsByBAID.get(x.getBillingAccountId())));
		return billingAccountDetailsMap.values().stream().collect(Collectors.toList());
	}

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unlinkRejectedBAs(Long id) {
		getEntityManager().createNamedQuery("BillingAccount.unlinkRejected").setParameter("billingRunId", id).executeUpdate();
	}

}