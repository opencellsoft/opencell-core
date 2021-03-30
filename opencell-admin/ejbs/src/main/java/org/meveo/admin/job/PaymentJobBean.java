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

package org.meveo.admin.job;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.job.PaymentJobBean.PaymentItem;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.model.IEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.service.script.payment.AccountOperationFilterScript;
import org.meveo.service.script.payment.DateRangeScript;

/**
 * Job implementation to create payment or payout for all opened account operations.
 * 
 * @author anasseh
 * @author Said Ramli
 * @author Andrius Karpavicius
 */
@Stateless
public class PaymentJobBean extends IteratorBasedJobBean<PaymentItem> {

    private static final long serialVersionUID = -1117556720357725851L;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    /** The account operation service. */
    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentService paymentService;

    private static BigDecimal oneHundred = new BigDecimal("100");

    /**
     * Operation category - Job execution parameter
     */
    private OperationCategoryEnum operationCategory = OperationCategoryEnum.CREDIT;

    /**
     * Payment method type - Job execution parameter
     */
    private PaymentMethodEnum paymentMethodType = PaymentMethodEnum.CARD;

    /**
     * Payment or refund
     */
    private PaymentOrRefundEnum paymentOrRefundEnum = PaymentOrRefundEnum.PAYMENT;

    /**
     * Payment level - Account operation or Customer account - Job execution parameter
     */
    private String paymentPerAOorCA = "CA";

    /**
     * Payment gateway - Job execution parameter
     */
    private PaymentGateway paymentGateway;

    /**
     * From due date - Job execution parameter
     */
    private Date fromDueDate = null;

    /**
     * To due date - Job execution parameter
     */
    private Date toDueDate = null;

    /**
     * Create Account operations - Job execution parameter
     */
    private boolean createAO = true;

    /**
     * Create matching Account operations - Job execution parameter
     */
    private boolean matchingAO = true;

    /**
     * Account operation filtering script - Job execution parameter
     */
    private AccountOperationFilterScript aoFilterScript;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::createPaymentOrPayout, null, null);
        operationCategory = null;
        paymentMethodType = null;
        paymentOrRefundEnum = PaymentOrRefundEnum.PAYMENT;
        paymentPerAOorCA = null;
        paymentGateway = null;
        fromDueDate = null;
        toDueDate = null;
        createAO = true;
        aoFilterScript = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Account operation ids
     */
    private Optional<Iterator<PaymentItem>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        operationCategory = OperationCategoryEnum.CREDIT;
        paymentMethodType = PaymentMethodEnum.CARD;
        paymentOrRefundEnum = PaymentOrRefundEnum.PAYMENT;
        paymentPerAOorCA = "CA";
        paymentGateway = null;
        fromDueDate = null;
        toDueDate = null;
        createAO = true;
        matchingAO = true;

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway") != null) {
            paymentGateway = paymentGatewayService.findByCode(((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "PaymentJob_paymentGateway")).getCode());
        }
        try {
            operationCategory = OperationCategoryEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "PaymentJob_creditOrDebit")).toUpperCase());
            paymentMethodType = PaymentMethodEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "PaymentJob_cardOrDD")).toUpperCase());

            createAO = "YES".equals((String) this.getParamOrCFValue(jobInstance, "PaymentJob_createAO"));
            matchingAO = "YES".equals((String) this.getParamOrCFValue(jobInstance, "PaymentJob_matchingAO"));

            if (operationCategory == OperationCategoryEnum.DEBIT) {
                paymentOrRefundEnum = PaymentOrRefundEnum.REFUND;
            }

            DateRangeScript dateRangeScript = this.getDueDateRangeScript(jobInstance);
            if (dateRangeScript != null) {
                DateRange dueDateRange = dateRangeScript.computeDateRange(new HashMap<>()); // no addtional params are needed right now for computeDateRange, may be in the
                                                                                            // future.
                fromDueDate = dueDateRange.getFrom();
                toDueDate = dueDateRange.getTo();
            } else {
                fromDueDate = (Date) this.getParamOrCFValue(jobInstance, "PaymentJob_fromDueDate");
                toDueDate = (Date) this.getParamOrCFValue(jobInstance, "PaymentJob_toDueDate");
            }

        } catch (Exception e) {
            log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
        }

        if (fromDueDate == null) {
            fromDueDate = new Date(1);
        }
        if (toDueDate == null) {
            toDueDate = DateUtils.addYearsToDate(fromDueDate, 1000);
        }

        List<AccountOperation> aos = new ArrayList<AccountOperation>();

        AccountOperationFilterScript aoFilterScript = getAOScriptInstance(jobInstance);

        if (aoFilterScript == null) {
            aos = accountOperationService.getAOsToPayOrRefund(paymentMethodType, fromDueDate, toDueDate, paymentOrRefundEnum.getOperationCategoryToProcess(), null);
        } else {
            Map<String, Object> methodContext = new HashMap<>();
            methodContext.put(AccountOperationFilterScript.FROM_DUE_DATE, fromDueDate);
            methodContext.put(AccountOperationFilterScript.TO_DUE_DATE, toDueDate);
            methodContext.put(AccountOperationFilterScript.PAYMENT_METHOD, paymentMethodType);
            methodContext.put(AccountOperationFilterScript.CAT_TO_PROCESS, paymentOrRefundEnum.getOperationCategoryToProcess());

            aos = aoFilterScript.filterAoToPay(methodContext);
        }

        List<PaymentItem> paymentItems = new ArrayList<PaymentJobBean.PaymentItem>();
        for (AccountOperation ao : aos) {
            paymentItems.add(new PaymentItem(ao.getCustomerAccount().getId(), ao.getId(), ao.getUnMatchingAmount().multiply(oneHundred).longValue()));
        }

        return Optional.of(new SynchronizedIterator<PaymentItem>(paymentItems));
    }

    /**
     * Create payment or payouts for account operation
     * 
     * @param customerAccountId Account operation id
     * @param jobExecutionResult Job execution result
     */
    private void createPaymentOrPayout(PaymentItem paymentItem, JobExecutionResultImpl jobExecutionResult) {

        List<Long> listAOids = new ArrayList<Long>();
        listAOids.add(paymentItem.accountOperationId);

        CustomerAccount customerAccount = customerAccountService.findById(paymentItem.customerAccountId);
        PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
        try {
            if (operationCategory == OperationCategoryEnum.CREDIT) {
                if (paymentMethodType == PaymentMethodEnum.CARD) {
                    doPaymentResponseDto = paymentService.payByCardToken(customerAccount, paymentItem.amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                } else {
                    doPaymentResponseDto = paymentService.payByMandat(customerAccount, paymentItem.amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                }
            } else {
                if (paymentMethodType == PaymentMethodEnum.CARD) {

                    doPaymentResponseDto = paymentService.refundByCardToken(customerAccount, paymentItem.amountToPay, listAOids, createAO, matchingAO, paymentGateway);

                } else {
                    doPaymentResponseDto = paymentService.refundByMandat(customerAccount, paymentItem.amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                }
            }

            if (PaymentStatusEnum.ERROR == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.REJECTED == doPaymentResponseDto.getPaymentStatus()) {

                this.checkPaymentRetry(doPaymentResponseDto.getErrorCode(), listAOids, aoFilterScript);

                jobExecutionResult.unRegisterSucces();// Reduce success as success is added automatically in main loop of IteratorBasedJobBean
                jobExecutionResult.registerError(paymentItem.accountOperationId, doPaymentResponseDto.getErrorMessage());

            } else if (PaymentStatusEnum.ACCEPTED == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.PENDING == doPaymentResponseDto.getPaymentStatus()) {
                // jobExecutionResult.registerSucces();
            }

        } catch (NoAllOperationUnmatchedException | UnbalanceAmountException e) {
            throw new BusinessException(e);
        }
    }

    private void checkPaymentRetry(String errorCode, List<Long> listAOids, AccountOperationFilterScript aoFilterScript) {
        if (aoFilterScript != null) {
            try {
                Map<String, Object> methodContext = new HashMap<>();
                methodContext.put("ERROR_CODE", errorCode);
                methodContext.put("LIST_AO_IDs", listAOids);
                aoFilterScript.checkPaymentRetry(methodContext);
            } catch (Exception e) {
                log.error(" Error on checkPaymentRetry [{}]", e.getMessage());
            }
        }
    }

    private AccountOperationFilterScript getAOScriptInstance(JobInstance jobInstance) {
        return (AccountOperationFilterScript) this.getJobScriptByCfCode(jobInstance, "PaymentJob_aoFilterScript", AccountOperationFilterScript.class);
    }

    private DateRangeScript getDueDateRangeScript(JobInstance jobInstance) {
        return (DateRangeScript) this.getJobScriptByCfCode(jobInstance, "PaymentJob_dueDateRangeScript", DateRangeScript.class);
    }

    @SuppressWarnings("rawtypes")
    private ScriptInterface getJobScriptByCfCode(JobInstance jobInstance, String scriptCfCode, Class clazz) {
        try {
            EntityReferenceWrapper entityReferenceWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, scriptCfCode);
            if (entityReferenceWrapper != null) {
                final String scriptCode = entityReferenceWrapper.getCode();
                if (scriptCode != null) {
                    log.debug(" looking for ScriptInstance with code :  [{}] ", scriptCode);
                    ScriptInterface si = scriptInstanceService.getScriptInstance(scriptCode);
                    if (si != null && clazz.isInstance(si)) {
                        return si;
                    }
                }
            }
        } catch (Exception e) {
            log.error(" Error on getJobScriptByCfCode : [{}]", e.getMessage());
        }
        return null;
    }

    /**
     * Stores payment job iteration data
     * 
     * @author Andrius Karpavicius
     */
    public class PaymentItem implements Serializable, IEntity {

        private static final long serialVersionUID = -150870412565824278L;

        public Long customerAccountId;
        public Long accountOperationId;
        public Long amountToPay;

        public PaymentItem() {

        }

        public PaymentItem(Long customerAccountId, Long accountOperationId, Long amountToPay) {
            super();
            this.customerAccountId = customerAccountId;
            this.accountOperationId = accountOperationId;
            this.amountToPay = amountToPay;
        }

        // Fake methods to simulate IEntity for error handing in job processing
        @Override
        public Serializable getId() {
            return accountOperationId;
        }

        @Override
        public void setId(Long id) {
        }

        @Override
        public boolean isTransient() {
            return false;
        }

    }
}