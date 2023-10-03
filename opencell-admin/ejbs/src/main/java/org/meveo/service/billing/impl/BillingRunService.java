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
import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.meveo.commons.utils.ParamBean.getInstance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.async.AmountsToInvoice;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.job.InvoicingJob;
import org.meveo.admin.job.InvoicingJobV2Bean;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.AccountEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunAutomaticActionEnum;
import org.meveo.model.billing.BillingRunList;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.BillingRunTypeEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.job.JobExecutionResultService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.order.OrderService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

/**
 * The Class BillingRunService.
 *
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Tien Lan PHUNG
 * @author Abdelmounaim Akadid
 * @lastModifiedVersion 7.0.0
 */
@Stateless
public class BillingRunService extends PersistenceService<BillingRun> {


    /**
     * The wallet operation service.
     */
    @Inject
    private WalletOperationService walletOperationService;

    /**
     * The billing account service.
     */
    @Inject
    private BillingAccountService billingAccountService;

    /**
     * The rated transaction service.
     */
    @Inject
    private RatedTransactionService ratedTransactionService;

    /**
     * The resource messages.
     */
    @Inject
    private ResourceBundle resourceMessages;

    /**
     * The invoice service.
     */
    @Inject
    private InvoiceService invoiceService;

    /**
     * The billing run extension service.
     */
    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OrderService orderService;

    @Inject
    private CommercialOrderService commercialOrderService;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    /**
     * The invoice agregate service.
     */
    @Inject
    InvoiceAgregateService invoiceAgregateService;

    /**
     * The customer service.
     */
    @Inject
    CustomerService customerService;

    @EJB
    BillingRunService billingRunService;

    /**
     * The rejected billing acoount service.
     */
    @Inject
    RejectedBillingAccountService rejectedBillingAccountService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private InvoiceLineService invoiceLinesService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private  InvoiceLineService invoiceLineService;

    @Inject
    private TradingLanguageService tradingLanguageService;
    
    private static final  int rtPaginationSize = 30000;

	@MeveoAudit
	@Override
	public void create(BillingRun billingRun) throws BusinessException {
		setBillingRunType(billingRun);
        super.create(billingRun);
    }

    @MeveoAudit
	@Override
	public BillingRun update(BillingRun billingRun) throws BusinessException {
		setBillingRunType(billingRun);
        return super.update(billingRun);
	}

	/**
	 * Put the BR type to CYCLE if a BC is attached, EXCEPTIONAL otherwise
	 *
	 * @param billingRun
	 */
	public void setBillingRunType(BillingRun billingRun) {
		if (billingRun.getBillingCycle() == null) {
			billingRun.setRunType(BillingRunTypeEnum.EXCEPTIONAL);
		} else {
			billingRun.setRunType(BillingRunTypeEnum.CYCLE);
		}
	}

    /**
     * Generate pre invoicing reports.
     *
     * @param billingRun the billing run
     * @return the pre invoicing reports DTO
     * @throws BusinessException the business exception
     */
    public PreInvoicingReportsDTO generatePreInvoicingReports(BillingRun billingRun) throws BusinessException {
        log.debug("start generatePreInvoicingReports.......");

        PreInvoicingReportsDTO preInvoicingReportsDTO = new PreInvoicingReportsDTO();

        preInvoicingReportsDTO.setBillingCycleCode(billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getCode() : null);
        preInvoicingReportsDTO.setBillingAccountNumber(billingRun.getBillingAccountNumber());
        preInvoicingReportsDTO.setLastTransactionDate(billingRun.getLastTransactionDate());
        preInvoicingReportsDTO.setInvoiceDate(billingRun.getInvoiceDate());
        preInvoicingReportsDTO.setBillableBillingAccountNumber(billingRun.getBillableBillingAcountNumber());
        preInvoicingReportsDTO.setAmoutWitountTax(billingRun.getPrAmountWithoutTax());

        BillingCycle billingCycle = billingRun.getBillingCycle();

        Date startDate = billingRun.getStartDate();
        Date endDate = billingRun.getEndDate();
        endDate = endDate != null ? endDate : new Date();
        List<BillingAccount> billingAccounts = new ArrayList<>();

        if (billingCycle != null) {
            billingAccounts = billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);
        } else {
            if(billingRun.isExceptionalBR()) {
                billingAccounts = (List<BillingAccount>) ratedTransactionService.getEntityManager()
                                            .createNamedQuery("RatedTransaction.BillingAccountByRTIds")
                                            .setParameter("ids", billingRun.getExceptionalRTIds())
                                            .getResultList();
            } else {
                String[] baIds = billingRun.getSelectedBillingAccounts() == null ? new String[0]:billingRun.getSelectedBillingAccounts().split(",");
                for (String id : baIds) {
                    Long baId = Long.valueOf(id);
                    billingAccounts.add(billingAccountService.findById(baId));
                }
            }
        }

        log.debug("BA in PreInvoicingReport: {}", billingAccounts.size());
        Integer checkBANumber = 0;
        Integer directDebitBANumber = 0;
        Integer tipBANumber = 0;
        Integer wiretransferBANumber = 0;
        Integer creditDebitCardBANumber = 0;

        Integer checkBillableBANumber = 0;
        Integer directDebitBillableBANumber = 0;
        Integer tipBillableBANumber = 0;
        Integer wiretransferBillableBANumber = 0;
        Integer creditDebitCardBillableBANumber = 0;

        BigDecimal checkBillableBAAmountHT = ZERO;
        BigDecimal directDebitBillableBAAmountHT = ZERO;
        BigDecimal tipBillableBAAmountHT = ZERO;
        BigDecimal wiretransferBillableBAAmountHT = ZERO;
        BigDecimal creditDebitCardBillableBAAmountHT = ZERO;

        for (BillingAccount billingAccount : billingAccounts) {

            PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
            PaymentMethodEnum paymentMethodEnum = null;
            if (preferedPaymentMethod != null) {
                paymentMethodEnum = preferedPaymentMethod.getPaymentType();
            }
            // avoiding NPE
            if (paymentMethodEnum != null) {
                switch (paymentMethodEnum) {
                case CHECK:
                    checkBANumber++;
                    break;
                case DIRECTDEBIT:
                    directDebitBANumber++;
                    break;
                case WIRETRANSFER:
                    wiretransferBANumber++;
                    break;

                case CARD:
                    creditDebitCardBANumber++;
                    break;

                default:
                    break;
                }
            }

        }

        List<BillingAccount> listBA = getEntityManager().createNamedQuery("BillingAccount.PreInv", BillingAccount.class).setParameter("billingRunId", billingRun.getId()).getResultList();

        for (BillingAccount billingAccount : listBA) {
            PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
            PaymentMethodEnum paymentMethodEnum = null;
            if (preferedPaymentMethod != null) {
                paymentMethodEnum = preferedPaymentMethod.getPaymentType();
            }

            // avoiding NPE
            if (paymentMethodEnum != null) {
                switch (paymentMethodEnum) {
                case CHECK:
                    checkBillableBANumber++;
                    checkBillableBAAmountHT = checkBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                    break;
                case DIRECTDEBIT:
                    directDebitBillableBANumber++;
                    directDebitBillableBAAmountHT = directDebitBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                    break;
                case WIRETRANSFER:
                    wiretransferBillableBANumber++;
                    wiretransferBillableBAAmountHT = wiretransferBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                    break;

                case CARD:
                    creditDebitCardBillableBANumber++;
                    creditDebitCardBillableBAAmountHT = creditDebitCardBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                    break;

                default:
                    break;
                }
            }
        }

        preInvoicingReportsDTO.setCheckBANumber(checkBANumber);
        preInvoicingReportsDTO.setCheckBillableBAAmountHT(round(checkBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setCheckBillableBANumber(checkBillableBANumber);
        preInvoicingReportsDTO.setDirectDebitBANumber(directDebitBANumber);
        preInvoicingReportsDTO.setDirectDebitBillableBAAmountHT(round(directDebitBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setDirectDebitBillableBANumber(directDebitBillableBANumber);
        preInvoicingReportsDTO.setTipBANumber(tipBANumber);
        preInvoicingReportsDTO.setTipBillableBAAmountHT(round(tipBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setTipBillableBANumber(tipBillableBANumber);
        preInvoicingReportsDTO.setWiretransferBANumber(wiretransferBANumber);
        preInvoicingReportsDTO.setWiretransferBillableBAAmountHT(round(wiretransferBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setWiretransferBillableBANumber(wiretransferBillableBANumber);
        preInvoicingReportsDTO.setCreditDebitCardBANumber(creditDebitCardBANumber);
        preInvoicingReportsDTO.setCreditDebitCardBillableBAAmountHT(round(creditDebitCardBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setCreditDebitCardBillableBANumber(creditDebitCardBillableBANumber);

        return preInvoicingReportsDTO;
    }

    /**
     * Generate post invoicing reports.
     *
     * @param billingRun the billing run
     * @return the post invoicing reports DTO
     * @throws BusinessException the business exception
     */
    public PostInvoicingReportsDTO generatePostInvoicingReports(BillingRun billingRun) throws BusinessException {
        log.info("generatePostInvoicingReports billingRun={}", billingRun.getId());
        PostInvoicingReportsDTO postInvoicingReportsDTO = new PostInvoicingReportsDTO();

        BigDecimal globalAmountHT = ZERO;
        BigDecimal globalAmountTTC = ZERO;

        Integer positiveInvoicesNumber = 0;
        BigDecimal positiveInvoicesAmountHT = ZERO;
        BigDecimal positiveInvoicesAmount = ZERO;
        BigDecimal positiveInvoicesTaxAmount = ZERO;

        Integer negativeInvoicesNumber = 0;
        BigDecimal negativeInvoicesAmountHT = ZERO;
        BigDecimal negativeInvoicesTaxAmount = ZERO;
        BigDecimal negativeInvoicesAmount = ZERO;

        Integer emptyInvoicesNumber = 0;
        Integer electronicInvoicesNumber = 0;

        Integer checkInvoicesNumber = 0;
        Integer directDebitInvoicesNumber = 0;
        Integer tipInvoicesNumber = 0;
        Integer wiretransferInvoicesNumber = 0;
        Integer creditDebitCardInvoicesNumber = 0;
        Integer npmInvoicesNumber = 0;

        BigDecimal checkAmuontHT = ZERO;
        BigDecimal directDebitAmuontHT = ZERO;
        BigDecimal tipAmuontHT = ZERO;
        BigDecimal wiretransferAmuontHT = ZERO;
        BigDecimal creditDebitCardAmountHT = ZERO;
        BigDecimal npmAmountHT = ZERO;

        BigDecimal checkAmuont = ZERO;
        BigDecimal directDebitAmuont = ZERO;
        BigDecimal tipAmuont = ZERO;
        BigDecimal wiretransferAmuont = ZERO;
        BigDecimal creditDebitCardAmount = ZERO;
        BigDecimal npmAmount = ZERO;

        List<Object[]> invoiceSummary = getEntityManager().createNamedQuery("Invoice.portInvoiceReport").setParameter("billingRunId", billingRun.getId()).getResultList();

        for (Object[] invoiceData : invoiceSummary) {

            BigDecimal amountWithTax = (BigDecimal) invoiceData[0];
            BigDecimal amountWithoutTax = (BigDecimal) invoiceData[1];
            BigDecimal amountTax = (BigDecimal) invoiceData[2];
            PaymentMethodEnum paymentMethodType = (PaymentMethodEnum) invoiceData[3];
            Integer yearExpiration = (Integer) invoiceData[4];
            Integer monthExpiration = (Integer) invoiceData[5];
            boolean isElectronicBilling = (boolean) invoiceData[6];

            if ((amountWithoutTax != null) && (amountWithTax != null) && (paymentMethodType != null)) {
                switch (paymentMethodType) {
                case CHECK:
                    checkInvoicesNumber++;
                    checkAmuontHT = checkAmuontHT.add(amountWithoutTax);
                    checkAmuont = checkAmuont.add(amountWithTax);
                    break;
                case DIRECTDEBIT:
                    directDebitInvoicesNumber++;
                    directDebitAmuontHT = directDebitAmuontHT.add(amountWithoutTax);
                    directDebitAmuont = directDebitAmuont.add(amountWithTax);
                    break;
                case WIRETRANSFER:
                    wiretransferInvoicesNumber++;
                    wiretransferAmuontHT = wiretransferAmuontHT.add(amountWithoutTax);
                    wiretransferAmuont = wiretransferAmuont.add(amountWithTax);
                    break;
                case CARD:
                    // check if card is expired
                    if (yearExpiration != null && monthExpiration != null && CardPaymentMethod.isExpired(yearExpiration, monthExpiration)) {
                        npmInvoicesNumber++;
                        npmAmountHT = npmAmountHT.add(amountWithoutTax);
                        npmAmount = npmAmount.add(amountWithTax);
                    } else {
                        creditDebitCardInvoicesNumber++;
                        creditDebitCardAmountHT = creditDebitCardAmountHT.add(amountWithoutTax);
                        creditDebitCardAmount = creditDebitCardAmount.add(amountWithTax);
                    }
                    break;

                default:
                    break;
                }
            }

            if ((amountWithoutTax != null) && (amountWithoutTax.compareTo(ZERO) > 0)) {
                positiveInvoicesNumber++;
                positiveInvoicesAmountHT = positiveInvoicesAmountHT.add(amountWithoutTax);
                positiveInvoicesTaxAmount = positiveInvoicesTaxAmount.add(amountTax == null ? ZERO : amountTax);
                positiveInvoicesAmount = positiveInvoicesAmount.add(amountWithTax);
            } else if ((amountWithoutTax == null) || (amountWithoutTax.compareTo(ZERO) == 0)) {
                emptyInvoicesNumber++;
            } else {
                negativeInvoicesNumber++;
                negativeInvoicesAmountHT = negativeInvoicesAmountHT.add(amountWithoutTax);
                negativeInvoicesTaxAmount = negativeInvoicesTaxAmount.add(amountTax);
                negativeInvoicesAmount = negativeInvoicesAmount.add(amountWithTax);
            }

            if (isElectronicBilling) {
                electronicInvoicesNumber++;
            }

            if ((amountWithoutTax != null) && (amountWithTax != null)) {
                globalAmountHT = globalAmountHT.add(amountWithoutTax);
                globalAmountTTC = globalAmountTTC.add(amountWithTax);
            }

        }

        postInvoicingReportsDTO.setInvoicesNumber(invoiceSummary.size());
        postInvoicingReportsDTO.setCheckAmuont(checkAmuont);
        postInvoicingReportsDTO.setCheckAmuontHT(checkAmuontHT);
        postInvoicingReportsDTO.setCheckInvoicesNumber(checkInvoicesNumber);
        postInvoicingReportsDTO.setDirectDebitAmuont(directDebitAmuont);
        postInvoicingReportsDTO.setDirectDebitAmuontHT(directDebitAmuontHT);
        postInvoicingReportsDTO.setDirectDebitInvoicesNumber(directDebitInvoicesNumber);
        postInvoicingReportsDTO.setElectronicInvoicesNumber(electronicInvoicesNumber);
        postInvoicingReportsDTO.setEmptyInvoicesNumber(emptyInvoicesNumber);

        postInvoicingReportsDTO.setPositiveInvoicesAmountHT(positiveInvoicesAmountHT);
        postInvoicingReportsDTO.setPositiveInvoicesAmount(positiveInvoicesAmount);
        postInvoicingReportsDTO.setPositiveInvoicesTaxAmount(positiveInvoicesTaxAmount);
        postInvoicingReportsDTO.setPositiveInvoicesNumber(positiveInvoicesNumber);

        postInvoicingReportsDTO.setNegativeInvoicesAmountHT(negativeInvoicesAmountHT);
        postInvoicingReportsDTO.setNegativeInvoicesAmount(negativeInvoicesAmount);
        postInvoicingReportsDTO.setNegativeInvoicesTaxAmount(negativeInvoicesTaxAmount);
        postInvoicingReportsDTO.setNegativeInvoicesNumber(negativeInvoicesNumber);

        postInvoicingReportsDTO.setTipAmuont(tipAmuont);
        postInvoicingReportsDTO.setTipAmuontHT(tipAmuontHT);
        postInvoicingReportsDTO.setTipInvoicesNumber(tipInvoicesNumber);

        postInvoicingReportsDTO.setWiretransferAmuont(wiretransferAmuont);
        postInvoicingReportsDTO.setWiretransferAmuontHT(wiretransferAmuontHT);
        postInvoicingReportsDTO.setWiretransferInvoicesNumber(wiretransferInvoicesNumber);

        postInvoicingReportsDTO.setCreditDebitCardAmount(creditDebitCardAmount);
        postInvoicingReportsDTO.setCreditDebitCardAmountHT(creditDebitCardAmountHT);
        postInvoicingReportsDTO.setCreditDebitCardInvoicesNumber(creditDebitCardInvoicesNumber);

        postInvoicingReportsDTO.setNpmAmount(npmAmount);
        postInvoicingReportsDTO.setNpmAmountHT(npmAmountHT);
        postInvoicingReportsDTO.setNpmInvoicesNumber(npmInvoicesNumber);

        postInvoicingReportsDTO.setGlobalAmount(globalAmountHT);

        return postInvoicingReportsDTO;
    }

    /**
     * Round.
     *
     * @param amount the amount
     * @param decimal the decimal
     * @return the big decimal
     */
    public static BigDecimal round(BigDecimal amount, int decimal) {
        if (amount == null) {
            return null;
        }
        amount = amount.setScale(decimal, RoundingMode.HALF_UP);

        return amount;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> cancelAsync(Long billingRunId) {
        try {

            billingRunService.markForCancel(billingRunId);
            billingRunService.cancelBillingRun(billingRunId);

        } catch (BusinessException e) {
            log.error("Failed to cancel a billing run with id={}", billingRunId, e);
        }
        return new AsyncResult<>("OK");
    }

    /**
     * Mark Billing run as "Canceling"
     *
     * @param billingRunId Billing run identifier
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void markForCancel(Long billingRunId) {
        BillingRun billingRun = findById(billingRunId);
        if (BillingRunStatusEnum.CANCELED.equals(billingRun.getStatus())) {
            throw new ValidationException("Cannot cancel a Canceled  billingRun #" + billingRunId);
            // Can try canceling again if got stuck in Canceling status for over 5 minutes
        } else if (BillingRunStatusEnum.CANCELLING.equals(billingRun.getStatus()) && (new Date().getTime() - billingRun.getAuditable().getUpdated().getTime()) < 300000) {
            throw new ValidationException("BillingRun #" + billingRunId + " in still in a process of cancelling");
        } else if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus()) || BillingRunStatusEnum.VALIDATED.equals(billingRun.getStatus())) {
            throw new ValidationException("Cannot cancel a POSTVALIDATED or VALIDATED billingRun #" + billingRunId);
        }
        billingRun.setStatus(BillingRunStatusEnum.CANCELLING);
        update(billingRun);
    }

    /**
     * Mark billing run as canceled, delete any minimum amount transactions, mark rated transactions as open, delete any invoices and invoice aggregates that were created during invoicing process
     *
     * @param billingRun The billing run
     * @throws BusinessException Business exception
     */
    private void cleanBillingRun(BillingRun billingRun) throws BusinessException {

        ratedTransactionService.deleteSupplementalRTs(billingRun);
        ratedTransactionService.uninvoiceRTs(billingRun);

        invoiceService.deleteInvoices(billingRun);
        invoiceAgregateService.deleteInvoiceAgregates(billingRun);

        // Andrius: I see no point of this update, as it has no relevant change:
        // Query queryBA = getEntityManager().createQuery("update " + BillingAccount.class.getName() + " set billingRun=null where billingRun=:billingRun");
        // queryBA.setParameter("billingRun", billingRun);
        // queryBA.executeUpdate();
    }

    /**
     * Checks if is active billing runs exist.
     *
     * @return true, if is active billing runs exist
     */
    @SuppressWarnings("unchecked")
    public boolean isActiveBillingRunsExist() {
        QueryBuilder qb = new QueryBuilder(BillingRun.class, "c");
        qb.startOrClause();
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.NEW);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.PREVALIDATED);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.POSTINVOICED);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.PREINVOICED);
        qb.endOrClause();
        List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();

        return (billingRuns != null) && (!billingRuns.isEmpty());
    }

    /**
     * Gets the billing runs.
     *
     * @param status the status
     * @return the billing runs
     */
    public List<BillingRun> getBillingRuns(BillingRunStatusEnum... status) {
        return getBillingRuns(null, status);
    }

    /**
     * Gets the billing runs.
     *
     * @param code the code
     * @param status the status
     * @return the billing runs
     */
    @SuppressWarnings("unchecked")
    public List<BillingRun> getBillingRuns(String code, BillingRunStatusEnum... status) {

        BillingRunStatusEnum bRStatus;
        log.debug("getBillingRuns ");
        QueryBuilder qb = new QueryBuilder(BillingRun.class, "c", null);

        if (code != null) {
            qb.addCriterion("c.billingCycle.code", "=", code, false);
        }

        qb.startOrClause();
        if (status != null) {
            for (BillingRunStatusEnum billingRunStatusEnum : status) {
                bRStatus = billingRunStatusEnum;
                qb.addCriterionEnum("c.status", bRStatus);
            }
        }
        qb.endOrClause();

        return qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Gets entities.
     *
     * @param billingRun the billing run
     * @return the entity objects
     */
    @SuppressWarnings("unchecked")
	public List<? extends IBillableEntity> getEntitiesToInvoice(BillingRun billingRun, boolean v11Process) {

        BillingCycle billingCycle = billingRun.getBillingCycle();

        if (billingCycle != null) {

            Date startDate = billingRun.getStartDate();
            Date endDate = billingRun.getEndDate();

            if ((startDate != null) && (endDate == null)) {
                endDate = new Date();
            }

            if (billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
                return subscriptionService.findSubscriptions(billingCycle);
            }

            if (billingCycle.getType() == BillingEntityTypeEnum.ORDER) {
                return v11Process ? commercialOrderService.findCommercialOrders(billingCycle)
                        : orderService.findOrders(billingCycle);
            }

            return v11Process? billingAccountService.findBillingAccountsToInvoice(billingRun) : billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);

        } else {
        	if(v11Process) {
        		return billingAccountService.findBillingAccountsToInvoice(billingRun);
        	}
            if(billingRun.isExceptionalBR() &&
                    ((billingRun.getExceptionalILIds() != null && billingRun.getExceptionalILIds().isEmpty()) ||
                            (billingRun.getExceptionalRTIds() != null && billingRun.getExceptionalRTIds().isEmpty()))) {
                return EMPTY_LIST;
            }
            if (billingRun.getExceptionalRTIds() != null && !billingRun.getExceptionalRTIds().isEmpty()) {
                return ratedTransactionService.findBillingAccountsBy(billingRun.getExceptionalRTIds());
            }
            if (billingRun.getExceptionalILIds() != null && !billingRun.getExceptionalILIds().isEmpty()) {
                return invoiceLineService.findBillingAccountsBy(billingRun.getExceptionalILIds());
            }
            List<BillingAccount> result = new ArrayList<>();
            String[] baIds = billingRun.getSelectedBillingAccounts() == null ? new String[0]:billingRun.getSelectedBillingAccounts().split(",");

            for (String id : baIds) {
                result.add(billingAccountService.findById(Long.valueOf(id)));
            }
            return result;
        }
    }
    
    /**
     * Gets entities that are associated with a billing run
     *
     * @param billingRun Billing run
     * @return A list of entities associated with a billing run
     */
    public List<? extends IBillableEntity> getEntitiesByBillingRun(BillingRun billingRun) {

        BillingCycle billingCycle = billingRun.getBillingCycle();

        if (billingCycle != null) {
            if (billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
                return subscriptionService.findSubscriptions(billingRun);

            } else if (billingCycle.getType() == BillingEntityTypeEnum.ORDER) {
                return orderService.findOrders(billingRun);
            }
        }
        return billingAccountService.findBillingAccounts(billingRun);

    }

    /**
     * Launch exceptional invoicing.
     *
     * @param billingAccountIds the billing account ids
     * @param invoiceDate the invoice date
     * @param lastTransactionDate the last transaction date
     * @param processType the process type
     * @return the billing run
     * @throws BusinessException the business exception
     */
    public BillingRun launchExceptionalInvoicing(List<Long> billingAccountIds, Date invoiceDate, Date lastTransactionDate, BillingProcessTypesEnum processType) throws BusinessException {
        log.info("launchExceptionelInvoicing...");

        ParamBean param = paramBeanFactory.getInstance();
        String allowManyInvoicing = param.getProperty("billingRun.allowManyInvoicing", "true");
        boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
        log.info("launchInvoicing allowManyInvoicing=#{}", isAllowed);
        if (isActiveBillingRunsExist() && !isAllowed) {
            throw new BusinessException(resourceMessages.getString("error.invoicing.alreadyLunched"));
        }

        BillingRun billingRun = new BillingRun();
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setProcessDate(new Date());
        billingRun.setProcessType(processType);
        StringBuilder selectedBillingAccounts = new StringBuilder();
        String sep = "";
        boolean isBillable = false;

        if (lastTransactionDate == null) {
            lastTransactionDate = new Date();
        }

        BillingAccount currentBA;
        for (Long baId : billingAccountIds) {
            currentBA = billingAccountService.findById(baId);
            if (currentBA == null) {
                throw new BusinessException("BillingAccount whit id=" + baId + " does not exists");
            }
            selectedBillingAccounts.append(sep).append(baId);
            sep = ",";
            if (!isBillable && ratedTransactionService.isBillingAccountBillable(currentBA, null, lastTransactionDate, invoiceDate)) {
                isBillable = true;
            }
        }

        if (!isBillable) {
            throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));
        }
        log.debug("selectedBillingAccounts = {}",selectedBillingAccounts);
        billingRun.setSelectedBillingAccounts(selectedBillingAccounts.toString());

        billingRun.setInvoiceDate(invoiceDate);
        billingRun.setLastTransactionDate(lastTransactionDate);
        create(billingRun);
        commit();
        return billingRun;
    }

    /**
     * Load rated transaction by billing runs
     * @param billingRuns list
     * @return ratedTransaction list
     */
    public List<RatedTransaction> loadRTsByBillingRuns(List<BillingRun> billingRuns, boolean v11Process){
        List<RatedTransaction> ratedTransactions = new ArrayList<>();
        for(BillingRun billingRun : billingRuns) {
            List<? extends IBillableEntity> billableEntities = getEntitiesToInvoice(billingRun, v11Process );
            for (IBillableEntity be :  billableEntities){
                ratedTransactions.addAll(ratedTransactionService.listRTsToInvoice(be, new Date(0), billingRun.getLastTransactionDate(), billingRun.getLastTransactionDate(),
                        billingRun.isExceptionalBR() ? createFilter(billingRun, false) : null, rtPaginationSize));
            }
        }
        return ratedTransactions;
    }

    /**
     * Invoicing process for the billingRun, launched by invoicingJob.
     *
     * @param billingRun the billing run to process
     * @throws Exception the exception
     */
    public BillingRun applyAutomaticValidationActions(BillingRun billingRun) {
        if (BillingRunStatusEnum.REJECTED.equals(billingRun.getStatus()) || 
                BillingRunStatusEnum.DRAFT_INVOICES.equals(billingRun.getStatus()) ||
                BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus())) {
            List<InvoiceStatusEnum> toMove = new ArrayList<>();
            List<InvoiceStatusEnum> toQuarantine = new ArrayList<>();
            List<InvoiceStatusEnum> toCancel = new ArrayList<>();
            
            if (billingRun.getRejectAutoAction() != null && billingRun.getRejectAutoAction().equals(BillingRunAutomaticActionEnum.CANCEL)) {
                toCancel.add(InvoiceStatusEnum.REJECTED);
            } else if (billingRun.getRejectAutoAction() != null && billingRun.getRejectAutoAction().equals(BillingRunAutomaticActionEnum.MOVE)){
            	toQuarantine.add(InvoiceStatusEnum.REJECTED);
            }

            if (billingRun.getSuspectAutoAction() != null && billingRun.getSuspectAutoAction().equals(BillingRunAutomaticActionEnum.CANCEL)) {
                toCancel.add(InvoiceStatusEnum.SUSPECT);
            } else if(billingRun.getSuspectAutoAction() != null && billingRun.getSuspectAutoAction().equals(BillingRunAutomaticActionEnum.MOVE)){
                toMove.add(InvoiceStatusEnum.SUSPECT);
            }
            
            if (CollectionUtils.isNotEmpty(toMove)) {
                invoiceService.quarantineSuspectedInvoicesByBR(billingRun);
            }
            if (CollectionUtils.isNotEmpty(toQuarantine)) {
                invoiceService.quarantineRejectedInvoicesByBR(billingRun);
            }
            if (CollectionUtils.isNotEmpty(toCancel)) {
                invoiceService.cancelInvoicesByStatus(billingRun, toCancel);
            }
        }
        
        return billingRun;
    }
    
    public BillingRunStatusEnum validateBillingRun(BillingRun billingRun, BillingRunStatusEnum validationStatus) {
        if(validationStatus == BillingRunStatusEnum.INVOICES_GENERATED || BillingRunStatusEnum.INVOICES_GENERATED.equals(billingRun.getStatus()) || BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus())) {
            BillingRunStatusEnum status = validationStatus != null ? validationStatus : BillingRunStatusEnum.POSTINVOICED;
            if(!isBillingRunValid(billingRun)) {
                status = BillingRunStatusEnum.REJECTED;
            }
            return status;
        }
        return null;
    }
    
    public boolean isBillingRunValid(BillingRun billingRun) {
        boolean result = true;
        if (!billingRun.isSkipValidationScript()) {
            if(isBillingRunContainingRejectedInvoices(billingRun.getId())) {
                return false;
            } else if (billingRun.getBillingCycle() == null) {
                return true;
            }
            billingRun = billingRunService.refreshOrRetrieve(billingRun);
            final ScriptInstance billingRunValidationScript = billingRun.getBillingCycle().getBillingRunValidationScript();
            if(billingRunValidationScript!=null) {
                ScriptInterface script = scriptInstanceService.getScriptInstance(billingRunValidationScript.getCode());
                if (script != null) {
                    Map<String, Object> methodContext = new HashMap<>();
                    methodContext.put(Script.CONTEXT_ENTITY, billingRun);
                    methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                    methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                    methodContext.put("billingRun", billingRun);
                    script.execute(methodContext);
                    Object status = methodContext.get(Script.INVOICE_VALIDATION_STATUS);
                    if(status instanceof InvoiceValidationStatusEnum && InvoiceValidationStatusEnum.REJECTED.equals(status)){
                            result = false;
                    }
                    update(billingRun);
                }
            }
        }
        return result;
    }
    
    public boolean isBRValid(BillingRun billingRun) {
        boolean result = true;
        if (!billingRun.isSkipValidationScript()) {
            if (billingRun.getBillingCycle() == null) {
                return true;
            }
            if (isBillingRunContainingRejectedInvoices(billingRun.getId())) {
                return false;
            }
        }
        
        return result;
    }

    public void executeBillingRunValidationScript(BillingRun billingRun) throws BusinessException {
        if (!billingRun.isSkipValidationScript()) {
            //Not Billing Run Exceptionnel
            if (billingRun.getBillingCycle() != null) {
                billingRun = refreshOrRetrieve(billingRun);
                final ScriptInstance billingRunValidationScript = billingRun.getBillingCycle().getBillingRunValidationScript();
                if(billingRunValidationScript!=null) {
                    ScriptInterface script = scriptInstanceService.getScriptInstance(billingRunValidationScript.getCode());
                    if (script != null) {
                        try {
                            Map<String, Object> methodContext = new HashMap<>();
                            methodContext.put(Script.CONTEXT_ENTITY, billingRun);
                            methodContext.put(Script.CONTEXT_CURRENT_USER, currentUser);
                            methodContext.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                            methodContext.put("billingRun", billingRun);
                            script.execute(methodContext);                    
                            update(billingRun);
                        } catch (javax.validation.ValidationException e) { //RuntimeException 
                            throw new BusinessException(e);
                        }
                    }                
                }
            }
        }
    }
    
    /**
     * Apply the threshold rules for the billing account, customer account and customer.
     *
     * @param billingRunId The billing run
     */
    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void applyThreshold(Long billingRunId) {
        log.info("Applying the invoicing threshold for invoices generated by the billing run {}", billingRunId);

        BillingRun billingRun = findById(billingRunId);
        Set<Long> invoicesToRemove = new HashSet<>();
        Map<Class, Map<Long, Map<Long, Amounts>>> discountAmounts = getAmountsMap(invoiceAgregateService.getTotalDiscountAmountByBR(billingRun));
        Map<Class, Map<Long, Map<Long, Amounts>>> discountILAmounts = getAmountsMap(invoiceLinesService.getTotalDiscountAmountByBR(billingRun));
        Map<Class, Map<Long, Map<Long, Amounts>>> positiveRTAmounts = getAmountsMap(ratedTransactionService.getTotalPositiveRTAmountsByBR(billingRun));
        Map<Class, Map<Long, Map<Long, Amounts>>> positiveILAmounts = getAmountsMap(invoiceLinesService.getTotalPositiveILAmountsByBR(billingRun));
        Map<Class, Map<Long, Map<Long, Amounts>>> invoiceableAmounts = getAmountsMap(invoiceService.getTotalInvoiceableAmountByBR(billingRun));

        Set<Long> billableEntitieIds = invoiceableAmounts.get(Subscription.class).keySet();
        Set<Long> rejectedBillingAccounts = new HashSet<>();

        invoicesToRemove.addAll(getInvoicesToRemoveByAccount(billableEntitieIds, discountAmounts.get(Subscription.class),
                 discountILAmounts.get(Subscription.class), positiveRTAmounts.get(Subscription.class), invoiceableAmounts.get(Subscription.class),
            Subscription.class, rejectedBillingAccounts, positiveILAmounts.get(Subscription.class)));

        billableEntitieIds = invoiceableAmounts.get(CommercialOrder.class).keySet();

        invoicesToRemove.addAll(getInvoicesToRemoveByAccount(billableEntitieIds, discountAmounts.get(CommercialOrder.class),
                discountILAmounts.get(CommercialOrder.class), positiveRTAmounts.get(CommercialOrder.class), invoiceableAmounts.get(CommercialOrder.class),
            CommercialOrder.class, rejectedBillingAccounts, positiveILAmounts.get(CommercialOrder.class)));

        billableEntitieIds = invoiceableAmounts.get(BillingAccount.class).keySet();

        invoicesToRemove.addAll(getInvoicesToRemoveByAccount(billableEntitieIds, discountAmounts.get(BillingAccount.class),
                discountILAmounts.get(BillingAccount.class), positiveRTAmounts.get(BillingAccount.class), invoiceableAmounts.get(BillingAccount.class),
            BillingAccount.class, rejectedBillingAccounts, positiveILAmounts.get(BillingAccount.class)));

        invoicesToRemove.addAll(getInvoicesToRemoveByAccount(billableEntitieIds, discountAmounts.get(CustomerAccount.class),
                discountILAmounts.get(CustomerAccount.class), positiveRTAmounts.get(CustomerAccount.class), invoiceableAmounts.get(CustomerAccount.class),
            CustomerAccount.class, rejectedBillingAccounts, positiveILAmounts.get(CustomerAccount.class)));

        invoicesToRemove.addAll(
            getInvoicesToRemoveByAccount(billableEntitieIds, discountAmounts.get(Customer.class),
                    discountILAmounts.get(Customer.class), positiveRTAmounts.get(Customer.class),
                    invoiceableAmounts.get(Customer.class), Customer.class, rejectedBillingAccounts, positiveILAmounts.get(Customer.class)));

        if (!invoicesToRemove.isEmpty()) {
            // Exclude prepaid invoice from applying threshold rules.
            List<Long> excludedPrepaidInvoices = invoiceService.excludePrepaidInvoices(invoicesToRemove);
            log.info("Remove all postpaid invoices that not reach to the invoicing threshold {}", excludedPrepaidInvoices);
            ratedTransactionService.deleteSupplementalRTs(excludedPrepaidInvoices);
            ratedTransactionService.uninvoiceRTs(excludedPrepaidInvoices);
            invoiceLinesService.uninvoiceILs(excludedPrepaidInvoices);//reopen ILs not created from  RTs
            invoiceLinesService.cancelIlForRemoveByInvoices(excludedPrepaidInvoices);//cancell ILs created from RTs
            invoiceAgregateService.deleteInvoiceAgregates(excludedPrepaidInvoices);
            invoiceService.deleteInvoices(excludedPrepaidInvoices);
            rejectedBillingAccounts.forEach(rejectedBillingAccountId -> {
                BillingAccount ba = getEntityManager().getReference(BillingAccount.class, rejectedBillingAccountId);
                rejectedBillingAccountService.create(ba, getEntityManager().getReference(BillingRun.class, billingRun.getId()), "Billing account did not reach invoicing threshold");
            });
        }
    }

    /**
     * Group amounts by billing account, customer account and customer.
     *
     * @param resultSet the result set of the query
     * @return A map of grouped amounts by account class.
     */
    @SuppressWarnings("rawtypes")
    private Map<Class, Map<Long, Map<Long, Amounts>>> getAmountsMap(List<Object[]> resultSet) {
        Map<Long, Map<Long, Amounts>> subscriptionsAmounts = new HashMap<>();
        Map<Long, Map<Long, Amounts>> commercialOrdersAmounts = new HashMap<>();
        Map<Long, Map<Long, Amounts>> baAmounts = new HashMap<>();
        Map<Long, Map<Long, Amounts>> caAmounts = new HashMap<>();
        Map<Long, Map<Long, Amounts>> custAmounts = new HashMap<>();
        for (Object[] result : resultSet) {
            Amounts amounts = new Amounts((BigDecimal) result[0], (BigDecimal) result[1]);



            Long subscriptionId = (Long) result[2];
            Long commercialOrderId = (Long) result[3];
            Long invoiceId = (Long) result[4];
            Long baId = (Long) result[5];
            Long caId = (Long) result[6];
            Long custId = (Long) result[7];

            addInvoiceAmounts(subscriptionsAmounts, amounts, invoiceId, subscriptionId);
            addInvoiceAmounts(commercialOrdersAmounts, amounts, invoiceId, commercialOrderId);
            addInvoiceAmounts(baAmounts, amounts, invoiceId, baId);
            addInvoiceAmounts(caAmounts, amounts, invoiceId, caId);
            addInvoiceAmounts(custAmounts, amounts, invoiceId, custId);

        }
        Map<Class, Map<Long, Map<Long, Amounts>>> accountsAmounts = new HashMap<>();
        accountsAmounts.put(BillingAccount.class, baAmounts);
        accountsAmounts.put(CustomerAccount.class, caAmounts);
        accountsAmounts.put(Customer.class, custAmounts);
        accountsAmounts.put(CommercialOrder.class, commercialOrdersAmounts);
        accountsAmounts.put(Subscription.class, subscriptionsAmounts);

        return accountsAmounts;
    }

    private void addInvoiceAmounts(Map<Long, Map<Long, Amounts>> entityAmounts, Amounts amounts, Long invoiceId, Long entityId) {
        if (entityId != null && invoiceId != null) {
            Map<Long, Amounts> thresholdAmounts = entityAmounts.get(entityId);
            if (thresholdAmounts == null) {
                thresholdAmounts = new TreeMap<>();
                thresholdAmounts.put(invoiceId, amounts.clone());
                entityAmounts.put(entityId, thresholdAmounts);
            } else {
                thresholdAmounts.put(invoiceId, amounts.clone());
            }
        }
    }

    /**
     * Get a list of invoices that not reach to the invoicing threshold.
     *
     * @param billableEntities a list of billable entities
     * @param discountThresholdAmounts the discount amounts summed by the account: Billing account, Custmer account or Customer
     * @param positiveRTThresholdAmounts the positive amounts summed by the account: Billing account, Custmer account or Customer
     * @param invoiceableThresholdAmounts the invoiceable amounts summed by the account: Billing account, Custmer account or Customer
     * @param clazz the account's class
     * @return a list of invoice that not reach the invoicing threshold and that must be removed.
     */
    @SuppressWarnings("rawtypes")
    private List<Long> getInvoicesToRemoveByAccount(Collection<Long> billableEntities, Map<Long, Map<Long, Amounts>> discountThresholdAmounts, Map<Long, Map<Long, Amounts>> discountILThresholdAmounts,  Map<Long, Map<Long, Amounts>> positiveRTThresholdAmounts,
            Map<Long, Map<Long, Amounts>> invoiceableThresholdAmounts, Class clazz, Set<Long> rejectedBillingAccounts, Map<Long, Map<Long, Amounts>> positiveILThresholdAmounts) {
        List<Long> invoicesToRemove = new ArrayList<>();
        List<Long> alreadyProcessedEntities = new ArrayList<>();
        List<Long> alreadyProcessedOrders = new ArrayList<>();
        List<Long> alreadyProcessedSubscriptions = new ArrayList<>();

        for (Long billableEntityId : billableEntities) {
            if (billableEntityId != null) {
                BigDecimal threshold = null;
                ThresholdOptionsEnum checkThreshold = null;
                boolean isThresholdPerEntity = false;
                Object entity;
                Long entityId = billableEntityId;

                if (clazz.equals(CommercialOrder.class)) {
                    entity = commercialOrderService.findById(billableEntityId);

                    if (alreadyProcessedOrders.contains(billableEntityId)) {
                        break;
                    } else {
                        alreadyProcessedOrders.add(billableEntityId);
                    }

                } else if (clazz.equals(Subscription.class)) {
                    if (alreadyProcessedSubscriptions.contains(billableEntityId)) {
                        break;
                    } else {
                        alreadyProcessedSubscriptions.add(billableEntityId);
                    }
                    entity = billableEntityId != null ? subscriptionService.findById(billableEntityId) : null;

                } else {

                    entity = getEntity(billableEntityId, clazz);
                     entityId = ((AccountEntity) entity).getId();
                    if (alreadyProcessedEntities.contains(entityId)) {
                        break;
                    } else {
                        alreadyProcessedEntities.add(entityId);
                    }

                }

                if(entity instanceof Subscription
                       && ((Subscription)entity).getBillingCycle() != null
                ){

                    BillingCycle bc = ((Subscription)entity).getBillingCycle();
                    threshold = bc.getInvoicingThreshold();
                    checkThreshold = bc.getCheckThreshold();
                    isThresholdPerEntity = bc.isThresholdPerEntity();
                }
                else if(entity instanceof CommercialOrder
                        && ((CommercialOrder)entity).getBillingCycle() != null
                ){
                    BillingCycle bc = ((CommercialOrder)entity).getBillingCycle();
                    threshold = bc.getInvoicingThreshold();
                    checkThreshold = bc.getCheckThreshold();
                    isThresholdPerEntity = bc.isThresholdPerEntity();
                }

                 else if (entity instanceof BillingAccount) {
                    BillingAccount ba = (BillingAccount) entity;
                    threshold = ba.getInvoicingThreshold();
                    checkThreshold = ba.getCheckThreshold();
                    isThresholdPerEntity = ba.isThresholdPerEntity();
                    if (threshold == null && ba.getBillingCycle() != null) {
                        threshold = ba.getBillingCycle().getInvoicingThreshold();
                        checkThreshold = ba.getBillingCycle().getCheckThreshold();
                        isThresholdPerEntity = ba.getBillingCycle().isThresholdPerEntity();
                    }
                } else if (entity instanceof CustomerAccount) {
                    threshold = ((CustomerAccount) entity).getInvoicingThreshold();
                    checkThreshold = ((CustomerAccount) entity).getCheckThreshold();
                    isThresholdPerEntity = ((CustomerAccount) entity).isThresholdPerEntity();
                } else if (entity instanceof Customer) {
                    threshold = ((Customer) entity).getInvoicingThreshold();
                    checkThreshold = ((Customer) entity).getCheckThreshold();
                    isThresholdPerEntity = ((Customer) entity).isThresholdPerEntity();
                }

                if (threshold != null && checkThreshold == null) {
                    checkThreshold = ThresholdOptionsEnum.AFTER_DISCOUNT;
                }
                if (threshold == null || checkThreshold == null) {
                    break;
                }

                switch (checkThreshold) {
                case POSITIVE_RT:
                    Map<Long, Amounts> thresholdAmounts = positiveRTThresholdAmounts.get(entityId);
                    if (thresholdAmounts != null) {
                        checkThresholdInvoices(rejectedBillingAccounts, invoicesToRemove, billableEntities, billableEntityId, threshold, isThresholdPerEntity, thresholdAmounts, clazz);
                    }
                    break;
                case AFTER_DISCOUNT:
                    thresholdAmounts = invoiceableThresholdAmounts.get(entityId);
                    if (thresholdAmounts != null) {
                        checkThresholdInvoices(rejectedBillingAccounts, invoicesToRemove, billableEntities, billableEntityId, threshold, isThresholdPerEntity, thresholdAmounts, clazz);
                    }
                    break;
                case BEFORE_DISCOUNT:
                    thresholdAmounts = invoiceableThresholdAmounts.get(entityId);
                    Map<Long, Amounts> discountAmounts = discountThresholdAmounts.get(entityId);
                    Map<Long, Amounts> discountILAmounts = discountILThresholdAmounts.get(entityId);
                    if (thresholdAmounts != null) {
                        if (discountAmounts != null) {
                            thresholdAmounts.keySet().stream().forEach(x -> thresholdAmounts.get(x).addAmounts((discountAmounts.get(x) != null) ? discountAmounts.get(x).negate() : null));
                        }
                        if(discountILAmounts!= null){
                           thresholdAmounts.keySet().stream().forEach(x -> thresholdAmounts.get(x).addAmounts((discountILAmounts.get(x) != null) ? discountILAmounts.get(x).negate() : null));
                        }
                        checkThresholdInvoices(rejectedBillingAccounts, invoicesToRemove, billableEntities, billableEntityId, threshold, isThresholdPerEntity, thresholdAmounts, clazz);
                    }
                    break;
                case POSITIVE_IL :
                    thresholdAmounts = positiveILThresholdAmounts.get(entityId);
                    if (thresholdAmounts != null) {
                        checkThresholdInvoices(rejectedBillingAccounts, invoicesToRemove, billableEntities, billableEntityId, threshold, isThresholdPerEntity, thresholdAmounts, clazz);
                    }
                    break;
                default:
                    break;
                }
            }            
        }
        return invoicesToRemove;
    }

    private void checkThresholdInvoices(Collection<Long> rejectedBillingAccounts, List<Long> invoicesToRemove, Collection<Long> billableEntities, Long billableEntityId, BigDecimal threshold, boolean isThresholdPerEntity,
            Map<Long, Amounts> thresholdAmounts, Class clazz) {
        if (isThresholdPerEntity) {
            BigDecimal totalAmount = ZERO;
            for (Amounts amounts : thresholdAmounts.values()) {
                totalAmount = totalAmount.add((appProvider.isEntreprise()) ? amounts.getAmountWithoutTax() : amounts.getAmountWithTax());
            }
            if (totalAmount.compareTo(threshold) < 0) {
                invoicesToRemove.addAll(thresholdAmounts.keySet());
                if(clazz.equals(CommercialOrder.class) )
                {
                    rejectedBillingAccounts.addAll(billableEntities.stream()
                            .map(commercialOrderService::findById)
                            .filter(Objects::nonNull)
                            .map(commercialOrder->commercialOrder.getBillingAccount().getId()).collect(Collectors.toList()));

                }else if(clazz.equals(Subscription.class)){
                    rejectedBillingAccounts.addAll(billableEntities.stream()
                            .map(subscriptionService::findById)
                            .filter(Objects::nonNull)
                            .map(subscription->subscription.getUserAccount().getBillingAccount().getId()).collect(Collectors.toList()));
                }else {
                    rejectedBillingAccounts.addAll(billableEntities);
                }

            }
        } else {
            thresholdAmounts.keySet().forEach(x -> {
                Amounts amounts = thresholdAmounts.get(x);
                BigDecimal amount = (appProvider.isEntreprise()) ? amounts.getAmountWithoutTax() : amounts.getAmountWithTax();
                if (amount.compareTo(threshold) < 0) {
                    invoicesToRemove.add(x);

                    if(clazz.equals(CommercialOrder.class) )
                {
                    rejectedBillingAccounts.add(commercialOrderService.findById(billableEntityId).getBillingAccount().getId());

                }else if(clazz.equals(Subscription.class)){
                     rejectedBillingAccounts.add(subscriptionService.findById(billableEntityId).getUserAccount().getBillingAccount().getId());
                }else {
                    rejectedBillingAccounts.add(billableEntityId);
                }
                }
            });
        }
    }

    @SuppressWarnings("rawtypes")
    private AccountEntity getEntity(Long billableEntityId, Class clazz) {
        BillingAccount billingAccount = billingAccountService.findById(billableEntityId);
        if (CustomerAccount.class.equals(clazz)) {
            return billingAccount.getCustomerAccount();
        } else if (Customer.class.equals(clazz)) {
            Customer customer = billingAccount.getCustomerAccount().getCustomer();
            return customerService.retrieveIfNotManaged(customer);
        } else {
            return billingAccount;
        }

    }

    /**
     * Get amounts to invoice grouped by a billable entity a configured in billing run
     *
     * @param billingRun Billing run
     * @return A list of Object array consisting billable entity id and amounts
     */
    public List<AmountsToInvoice> getAmountsToInvoice(BillingRun billingRun) {

        if(billingRun.isExceptionalBR()) {
            if(billingRun.getExceptionalRTIds().isEmpty()) {
                return EMPTY_LIST;
            }
            return getEntityManager()
                    .createNamedQuery("RatedTransaction.sumTotalInvoiceableByRtIdInBatch", AmountsToInvoice.class)
                    .setParameter("ids", billingRun.getExceptionalRTIds())
                    .getResultList();

        } else {
            BillingCycle billingCycle = billingRun.getBillingCycle();

            Date startDate = billingRun.getStartDate();
            Date endDate = billingRun.getEndDate();

            if ((startDate != null) && (endDate == null)) {
                endDate = new Date();
            }
            if (endDate != null && startDate == null) {
                startDate = new Date(0);
            }

            String sqlName;
            if (billingCycle.getType() == BillingEntityTypeEnum.SUBSCRIPTION) {
                sqlName = "RatedTransaction.sumTotalInvoiceableBySubscriptionInBatch";
            } else {
                if (startDate == null) sqlName = "RatedTransaction.sumTotalInvoiceableByBAInBatch";
                else sqlName = "RatedTransaction.sumTotalInvoiceableByBAInBatchLimitByNextInvoiceDate";
            }

            TypedQuery<AmountsToInvoice> query = getEntityManager().createNamedQuery(sqlName, AmountsToInvoice.class).setParameter("firstTransactionDate", new Date(0))
            .setParameter("lastTransactionDate", billingRun.getLastTransactionDate()).setParameter("billingCycle", billingCycle).setParameter("invoiceUpToDate", billingRun.getInvoiceDate());

            if (billingCycle.getType() == BillingEntityTypeEnum.BILLINGACCOUNT && startDate != null) {
                startDate = DateUtils.setDateToEndOfDay(startDate);
                if (Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false"))) {
                    endDate = DateUtils.setDateToEndOfDay(endDate);
                } else {
                    endDate = DateUtils.setDateToStartOfDay(endDate);
                }

                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
            }

            return query.getResultList();
        }
    }

    /**
     * Force validate.
     *
     * @param billingRunId the billing run id
     * @throws BusinessException the business exception
     */
    public void forceValidate(Long billingRunId) throws BusinessException {
        BillingRun billingRun = findById(billingRunId);
        if (billingRun == null) {
            throw new BusinessException("Cant find BillingRun with id:" + billingRunId);
        }
        detach(billingRun);
        log.debug("forceValidate, billingRun status={}", billingRun.getStatus());
        switch (billingRun.getStatus()) {

        case POSTINVOICED:
            billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), null, null, BillingRunStatusEnum.VALIDATED, null);
            break;

        case PREINVOICED:
            billingRun = billingRunExtensionService.updateBillingRun(billingRun.getId(), 1, 0, BillingRunStatusEnum.POSTINVOICED, null);
            break;

        case VALIDATED:
        case INVOICES_GENERATED:
        case CANCELED:
        case NEW:
        default:
            throw new BusinessException("BillingRun with status " + billingRun.getStatus() + " cannot be validated");
        }

        List<JobInstance> jobInstances = jobInstanceService.findByJobTemplate(InvoicingJob.class.getSimpleName());
        if (jobInstances.isEmpty()) {
            throw new BusinessException("No matching Invoicing job was found to execute a Billing run");
        }

        JobInstance jobInstance = jobInstances.get(0);
        Map<String, Object> params = new HashMap<>();
        params.put("BillingRuns", Arrays.asList(new EntityReferenceWrapper(BillingRun.class.getName(), null, billingRun.getId().toString())));

        jobExecutionService.executeJob(jobInstance, params, JobLauncherEnum.API);

    }

    /**
     * Create a new Billing run to invoice rejected Billing accounts
     *
     * @param br Billing run
     * @return true, if successful
     * @throws BusinessException the business exception
     */
    public boolean launchInvoicingRejectedBA(BillingRun br) throws BusinessException {
        boolean result = false;
        BillingRun billingRun = new BillingRun();
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setProcessDate(new Date());
        BillingCycle billingCycle = br.getBillingCycle();
        if ((billingCycle != null) && (billingCycle.getInvoiceDateProductionDelayEL() != null)) {
            billingRun.setInvoiceDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), InvoiceService.resolveInvoiceDateDelay(billingCycle.getInvoiceDateProductionDelayEL(), br)));
        } else {
            billingRun.setInvoiceDate(br.getProcessDate());
        }
        if ((billingCycle != null) && (billingCycle.getLastTransactionDateEL() != null)) {
            billingRun.setLastTransactionDate(BillingRunService.resolveLastTransactionDate(billingCycle.getLastTransactionDateEL(), br));

        } else if ((billingCycle != null) && (billingCycle.getLastTransactionDateDelayEL() != null)) {
            billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), BillingRunService.resolveLastTransactionDateDelay(billingCycle.getLastTransactionDateDelayEL(), br)));

        } else {
            billingRun.setLastTransactionDate(billingRun.getProcessDate());
        }

        billingRun.setProcessType(br.getProcessType());
        StringBuilder selectedBillingAccounts = new StringBuilder();
        String sep = "";
        for (RejectedBillingAccount ba : br.getRejectedBillingAccounts()) {
            selectedBillingAccounts.append(sep).append(ba.getId());
            sep = ",";
            if (!result && ratedTransactionService.isBillingAccountBillable(ba.getBillingAccount(), null, billingRun.getLastTransactionDate(), billingRun.getInvoiceDate())) {
                result = true;
                break;
            }
        }
        if (result) {
            log.debug("selectedBillingAccounts= {}", selectedBillingAccounts);
            billingRun.setSelectedBillingAccounts(selectedBillingAccounts.toString());
            create(billingRun);
        }
        return result;
    }

    /**
     * Resolve Last transaction date delay for a given billing run
     * 
     * @param el EL expression to resolve
     * @param billingRun Billing run
     * @return An integer value
     */
    public static Integer resolveLastTransactionDateDelay(String el, BillingRun billingRun) {
        return ValueExpressionWrapper.evaluateExpression(el, Integer.class, billingRun);
    }

    /**
     * Resolve Last transaction date for a given billing run
     * 
     * @param el EL expression to resolve
     * @param billingRun Billing run
     * @return An Date value
     */
    public static Date resolveLastTransactionDate(String el, BillingRun billingRun) {
        return ValueExpressionWrapper.evaluateExpression(el, Date.class, billingRun);
    }

    /**
     * Re-rate transactions that were invoiced by a billing run, mark billing run as canceled, delete any minimum amount transactions, mark rated transactions as open, delete any invoices and invoice aggregates that were
     * created during invoicing process created during invoicing process
     *
     * @param billingRun Billing run to re-rate
     * @return Updated billing run
     */
    public BillingRun rerateBillingRun(BillingRun billingRun) {

        billingRun = refreshOrRetrieve(billingRun);

        if (billingRun.getStatus() == BillingRunStatusEnum.POSTINVOICED || billingRun.getStatus() == BillingRunStatusEnum.POSTVALIDATED) {
            walletOperationService.markToRerateByBR(billingRun);
            cleanBillingRun(billingRun);
        }

        billingRun.setStatus(BillingRunStatusEnum.CANCELED);
        billingRun = update(billingRun);

        return billingRun;
    }

    /**
     * Mark billing run as canceled, delete any minimum amount transactions, mark rated transactions as open, delete any invoices and invoice aggregates that were created during invoicing process
     *
     * @param billingRunId Billing run to cancel Id
     * @return Updated billing run
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BillingRun cancelBillingRun(Long billingRunId) {

        BillingRun billingRun = findById(billingRunId);

        if (billingRun.getStatus() == BillingRunStatusEnum.POSTINVOICED || billingRun.getStatus() == BillingRunStatusEnum.POSTVALIDATED || billingRun.getStatus() == BillingRunStatusEnum.CANCELLING) {
            cleanBillingRun(billingRun);
        }

        billingRun.setStatus(BillingRunStatusEnum.CANCELED);
        billingRun = update(billingRun);

        return billingRun;
    }

    /**
     * Check any invoice is rejected for a given billingRun id.
     * @param billingRunId
     *
     * @return boolean isBillingRunContainingRejectedInvoices
     */
    public boolean isBillingRunContainingRejectedInvoices(Long billingRunId) {
        return getEntityManager().createNamedQuery("Invoice.countRejectedByBillingRun", Long.class).setParameter("billingRunId", billingRunId).getSingleResult() > 0;
    }
    
    public boolean isBillingRunContainingSuspectInvoices(Long billingRunId) {
        return getEntityManager().createNamedQuery("Invoice.countSuspectByBillingRun", Long.class).setParameter("billingRunId", billingRunId).getSingleResult() > 0;
    }

    /**
     * Search if a next quarantine BR exist for the given BR quarantineBRId. if is not found, a new one is created and associated to the BR
     * @param billingRun
     * @param quarantineBRId
     * @return
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BillingRun findOrCreateNextQuarantineBR(Long billingRunId, List<LanguageDescriptionDto> descriptionsTranslated) {
        BillingRun billingRun = findById(billingRunId);
        if (billingRun != null) {
            if (billingRun.getNextBillingRun() != null) {
               return billingRun.getNextBillingRun();
            }
            BillingRun quarantineBillingRun = new BillingRun();
            try {
                BeanUtils.copyProperties(quarantineBillingRun, billingRun);

                Set<BillingRunList> billingRunLists = new HashSet<>();
                billingRunLists.addAll(billingRun.getBillingRunLists());
                quarantineBillingRun.setBillingRunLists(billingRunLists);
                List<JobExecutionResultImpl> billingRunJobExecutions = new ArrayList<>();
                billingRunJobExecutions.addAll(billingRun.getJobExecutions());                   
                quarantineBillingRun.setJobExecutions(billingRunJobExecutions);
                quarantineBillingRun.setBillableBillingAccounts(new ArrayList<>());
                quarantineBillingRun.setBillingAccountNumber(null);
                quarantineBillingRun.setRejectedBillingAccounts(null);
                quarantineBillingRun.setRejectionReason(billingRun.getRejectionReason());
                quarantineBillingRun.setPdfJobExecutionResultId(null);
                quarantineBillingRun.setXmlJobExecutionResultId(null);
                quarantineBillingRun.setInvoices(new ArrayList<>());

                quarantineBillingRun.setPrAmountTax(BigDecimal.ZERO);
                quarantineBillingRun.setPrAmountWithoutTax(BigDecimal.ZERO);
                quarantineBillingRun.setPrAmountWithTax(BigDecimal.ZERO);

                quarantineBillingRun.setStatus(BillingRunStatusEnum.REJECTED);
                quarantineBillingRun.setIsQuarantine(Boolean.TRUE);
                quarantineBillingRun.setOriginBillingRun(billingRun);
                quarantineBillingRun.setId(null);

                if(descriptionsTranslated != null && !descriptionsTranslated.isEmpty()) {
                    quarantineBillingRun.setDescriptionI18n(convertMultiLanguageToMapOfValues(descriptionsTranslated ,null));
                }else {
                    BillingCycle billingCycle = billingRun.getBillingCycle();
                    
                    LanguageDescriptionDto languageDescriptionEn = new LanguageDescriptionDto("ENG", "Billing run (id="+billingRun.getId()+"; billing cycle="+ 
                                                                    (billingCycle != null ? billingCycle.getDescription() : " ") +
                                                                    "; invoice date="+(billingRun.getInvoiceDate()!=null ? new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(billingRun.getInvoiceDate()) : "")+")");  
                    LanguageDescriptionDto languageDescriptionFr = new LanguageDescriptionDto("FRA", "Run de facturation (id="+billingRun.getId()+"; billing cycle="+ 
                                                                    (billingCycle != null ? billingCycle.getDescription() : " ") +
                                                                    "; invoice date="+(billingRun.getInvoiceDate()!=null ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(billingRun.getInvoiceDate()) : "")+")"); 
                    
                    List<LanguageDescriptionDto> newDescriptionsTranslated = new ArrayList<>();
                    newDescriptionsTranslated.add(languageDescriptionEn);
                    newDescriptionsTranslated.add(languageDescriptionFr);
                    quarantineBillingRun.setDescriptionI18n(convertMultiLanguageToMapOfValues(newDescriptionsTranslated ,null));                    
                }

                create(quarantineBillingRun);
                billingRun.setRejectionReason(null);
                billingRun.setNextBillingRun(quarantineBillingRun);
                update(billingRun);
                return quarantineBillingRun;
            } catch (Exception e) {
               log.error(e.getMessage());
               throw new BusinessException(e);
            }
        }
        return null;
    }
    
    /**
     * Creates the aggregates and invoice with invoiceLines.
     * @param billingRun the billing run
     * @param nbRuns the nb runs
     * @param waitingMillis the waiting millis
     * @param jobInstanceId the job instance id
     * @throws BusinessException
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void createAggregatesAndInvoiceWithIl(BillingRun billingRun, long nbRuns, long waitingMillis,
                                                 Long jobInstanceId, JobExecutionResultImpl jobExecutionResult, boolean v11Process) throws BusinessException {
        List<? extends IBillableEntity> entities = getEntitiesToInvoice(billingRun, v11Process);
        billingRun.setBillableBillingAcountNumber(entities.size());
        SubListCreator<? extends IBillableEntity> subListCreator;
        try {
            subListCreator = new SubListCreator<>(entities, (int) nbRuns);
        } catch (Exception e1) {
            throw new BusinessException("cannot create  aggregates and invoice with nbRuns=" + nbRuns);
        }
        List<Future<String>> asyncReturns = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();
        while (subListCreator.isHasNext()) {
            asyncReturns.add(createAggregatesAndInvoiceAsyncWithIL(subListCreator.getNextWorkSet(),
                    billingRun, jobInstanceId, null, lastCurrentUser, !billingRun.isSkipValidationScript(), jobExecutionResult));
            try {
                Thread.sleep(waitingMillis);
            } catch (InterruptedException e) {
                log.error("Failed to create aggregates and invoice waiting for thread", e);
                throw new BusinessException(e);
            }
        }
        for (Future<String> futureItsNow : asyncReturns) {
            try {
                futureItsNow.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to create aggregates and invoice getting future", e);
                throw new BusinessException(e);
            }
        }
    }

    /**
     * Creates the aggregates and invoice async using invoiceLines. One entity at a time in a separate transaction.
     *
     * @param entities the entity objects
     * @param billingRun the billing run
     * @param jobInstanceId the job instance id
     * @param minAmountForAccounts Check if min amount is enabled in any account level
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    private Future<String> createAggregatesAndInvoiceAsyncWithIL(List<? extends IBillableEntity> entities,
                                                                 BillingRun billingRun, Long jobInstanceId,
                                                                 MinAmountForAccounts minAmountForAccounts,
                                                                 MeveoUser lastCurrentUser, boolean automaticInvoiceCheck,
                                                                 JobExecutionResultImpl jobExecutionResult) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        for (IBillableEntity entityToInvoice : entities) {
            if (jobInstanceId != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            try {
                List<Invoice> invoices = invoiceService.createAggregatesAndInvoiceWithILInNewTransaction(entityToInvoice, billingRun,
                        null, null, null, null, minAmountForAccounts,
                        false, automaticInvoiceCheck, false);
                jobExecutionResult.addNbItemsToProcess(invoices.size());
                jobExecutionResult.addNbItemsCorrectlyProcessed(invoices.size());
                updateBillingRunWithStatistics(invoices);
            } catch (Exception exception) {
                throw new BusinessException(exception);
            }
        }

        return new AsyncResult<>("OK");
    }

    private void updateBillingRunWithStatistics(List<Invoice> invoices) {
        BigDecimal amountWithTax = invoices.stream().map(Invoice::getAmountWithTax).reduce(ZERO, BigDecimal::add);
        BigDecimal amountWithoutTax = invoices.stream().map(Invoice::getAmountWithoutTax).reduce(ZERO, BigDecimal::add);
        BigDecimal amountTax = invoices.stream().map(Invoice::getAmountTax).reduce(ZERO, BigDecimal::add);
        InvoicingJobV2Bean.addNewAmounts(amountTax, amountWithoutTax, amountWithTax);
    }
    
    public void updateBillingRunStatistics(BillingRun billingRun) {
    	billingRun = billingRunService.refreshOrRetrieve(billingRun);

        List<BillingAccount> billingAccounts = invoiceService.getInvoicesBillingAccountsByBR(billingRun);
        
        Amounts amounts = invoiceService.getTotalAmountsByBR(billingRun);

        billingRun.setPrAmountTax(amounts.getAmountTax());
        billingRun.setPrAmountWithoutTax(amounts.getAmountWithoutTax());
        billingRun.setPrAmountWithTax(amounts.getAmountWithTax());
        
        billingRun.setBillableBillingAccounts(billingAccounts);
    	billingRun.setBillableBillingAcountNumber(billingAccounts.size());
        
    }

    public void updateBillingRunJobExecution(Long billingRunId, JobExecutionResultImpl result) {
        BillingRun billingRun = billingRunService.findById(billingRunId);
        billingRun.addJobExecutions(result);
        update(billingRun);
    }

    public Filter createFilter(BillingRun billingRun, boolean invoicingV2) {
        QueryBuilder queryBuilder;
        Filter filter = new Filter();
        if(invoicingV2) {
            final int maxValue =
                    getInstance().getPropertyAsInteger("database.number.of.inlist.limit", SHORT_MAX_VALUE);
            final String queryPrefix = "SELECT il from InvoiceLine il WHERE il.id in ";
            if (billingRun.getExceptionalILIds().size() > maxValue) {
                List<List<Long>> rtSubLists = partition(billingRun.getExceptionalILIds(), maxValue);
                List<String> subListIds = new ArrayList<>();
                for(List<Long> ids : rtSubLists) {
                    subListIds.add("(" + ids.stream()
                            .map(String::valueOf)
                            .collect(joining(",")) + ")");
                }
                filter.setPollingQuery(queryPrefix + subListIds.stream()
                        .map(String::valueOf)
                        .collect(joining(" OR id in ")));
            } else {
                filter.setPollingQuery(queryPrefix + " (" +
                        billingRun.getExceptionalILIds().stream().map(String::valueOf)
                                .collect(joining(",")) + ")");
            }
        } else {
            Map<String, Object> filters = billingRun.getFilters();
            String filterValue = QueryBuilder.getFilterByKey(filters, "SQL");
            if (!StringUtils.isBlank(filterValue)) {
                queryBuilder = new QueryBuilder(filterValue);
            } else {
                FilterConverter converter = new FilterConverter(RatedTransaction.class);
                PaginationConfiguration configuration = new PaginationConfiguration(converter.convertFilters(filters));
                queryBuilder = ratedTransactionService.getQuery(configuration, "rt", false);
            }
            filter.setPollingQuery(buildPollingQuery(queryBuilder));
        }
        return filter;
    }

    private String buildPollingQuery(QueryBuilder queryBuilder) {
        String pollingQuery = queryBuilder.getSqlString();
        if(queryBuilder.getParams() != null) {
            for(Map.Entry<String, Object> param : queryBuilder.getParams().entrySet()) {
                Class clazz = param.getValue().getClass();
                String className = clazz.getName();
                if(className.contains("Date")) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String paramDate = df.format(param.getValue());
                    pollingQuery = pollingQuery.replace(":" + param.getKey(), "\'"+ paramDate + "\'");
                }
                else {
                    pollingQuery = pollingQuery.replace(":" + param.getKey(), "\'"+ param.getValue() + "\'");
                }
            }
        }
        return pollingQuery;
    }
    
    public Map<String, String> convertMultiLanguageToMapOfValues(List<LanguageDescriptionDto> translationInfos, Map<String, String> currentValues) throws InvalidParameterException {
        if (translationInfos == null || translationInfos.isEmpty()) {
            return null;
        }

        List<String> supportedLanguages = tradingLanguageService.listLanguageCodes();

        Map<String, String> values = null;
        if (currentValues == null) {
            values = new HashMap<>();
        } else {
            values = currentValues;
        }

        for (LanguageDescriptionDto translationInfo : translationInfos) {
            if (!supportedLanguages.contains(translationInfo.getLanguageCode())) {
                throw new InvalidParameterException("Language " + translationInfo.getLanguageCode() + " is not supported by the provider.");
            }
            if (StringUtils.isBlank(translationInfo.getDescription())) {
                values.remove(translationInfo.getLanguageCode());
            } else {
                values.put(translationInfo.getLanguageCode(), translationInfo.getDescription());
            }
        }

        if (values.isEmpty()) {
            return null;
        } else {
            return values;
        }
    }
    
	public List<Long> getBAsHavingOpenILs(BillingRun billingRun) {
		return getEntityManager().createNamedQuery("InvoiceLine.getBAsHavingOpenILsByBR",Long.class).setParameter("billingRunId", billingRun.getId()).getResultList();
	}
}