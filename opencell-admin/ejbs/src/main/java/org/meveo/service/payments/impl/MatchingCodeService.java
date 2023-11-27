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
package org.meveo.service.payments.impl;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static org.meveo.model.payments.OperationCategoryEnum.CREDIT;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.Updated;
import org.meveo.model.BaseEntity;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.model.payments.UnMatchingAmount;
import org.meveo.model.payments.UnMatchingCode;
import org.meveo.model.payments.WriteOff;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.service.accountingscheme.JournalEntryService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;

/**
 * MatchingCode service implementation.
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Stateless
public class MatchingCodeService extends PersistenceService<MatchingCode> {

    private static final String PPL_INSTALLMENT = "PPL_INSTALLMENT";
    private static final String PPL_CREATION = "PPL_CREATION";
    private static final String INVOICE_TYPE_SECURITY_DEPOSIT = "SECURITY_DEPOSIT";
    private static final String XCH_LOSS = "XCH_LOSS";
    private static final String XCH_GAIN = "XCH_GAIN";
    private static final String CAN_SD = "CAN_SD";

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private PaymentPlanService paymentPlanService;

    @Inject
    private SecurityDepositService securityDepositService;

    @Inject
    @Updated
    private Event<BaseEntity> entityUpdatedEventProducer;

    @Inject
    private JournalEntryService journalEntryService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private UnMatchingCodeService unMatchingCodeService;

    @Inject
    private UnMatchingAmountService unMatchingAmountService;
    @Inject
    private OCCTemplateService occTemplateService;

    @Inject
    private PaymentHistoryService paymentHistoryService;

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    /**
     * Match account operations.
     * 
     * @param listOcc Account operations to match
     * @param amount Amount to match
     * @param aoToMatchLast Account operation to match partially - can be explicitly specified in case more than two operations are matched
     * @param matchingTypeEnum matching type
     * @throws BusinessException Business exception
     */
    private void matching(List<AccountOperation> listOcc, BigDecimal amount, AccountOperation aoToMatchLast, MatchingTypeEnum matchingTypeEnum) throws BusinessException {

        MatchingCode matchingCode = new MatchingCode();
        BigDecimal amountToMatch = ZERO;
        BigDecimal functionalAmountToMatch = ZERO;
        BigDecimal amountCredit = amount;
        BigDecimal amountDebit = amount;
        BigDecimal functionalCreditAmount = amount;
        BigDecimal functionalDebitAmount = amount;
        BigDecimal invoiceRate = null;
        BigDecimal paymentRate = null;
        boolean fullMatch = false;
        boolean withWriteOff = false;
        boolean withRefund = false;
        boolean isToTriggerCollectionPlanLevelsJob = false;
        List<PaymentScheduleInstanceItem> listPaymentScheduleInstanceItem = new ArrayList<>();
        List<AccountOperation> aosToGenerateMatchingCode = new ArrayList<>();
        BigDecimal appliedRate = ONE;
        BigDecimal invoiceTransactionalUnMatchingAmount = ZERO;

        // Param for security deposit
        Invoice sdInvoice = null;
        List<AccountOperation> securityDepositAOPs = new ArrayList<>();

        // For PaymentPlan, new AO OOC PPL_CREATION shall match all debit one, and recreate new AOS DEBIT OCC PPL_INSTALLMENT recording to the number of installment of Plan
        // Specially for this case, Invoice will pass to PENDING_PLAN status
        boolean isPplCreationCreditAo = false;

        
        for (AccountOperation accountOperation : listOcc) {
            if (accountOperation instanceof WriteOff) {
                withWriteOff = true;
            } else if (accountOperation instanceof Refund) {
                withRefund = true;
            }
        }

        // log.debug("AKK will match for amount {} partial match is for {}", amount, aoToMatchLast != null ? aoToMatchLast.getId() + "_" + aoToMatchLast.getReference() : null);
        for (AccountOperation accountOperation : listOcc) {

            if (accountOperation instanceof RecordedInvoice && ((RecordedInvoice) accountOperation).getPaymentScheduleInstanceItem() != null) {
                listPaymentScheduleInstanceItem.add(((RecordedInvoice) accountOperation).getPaymentScheduleInstanceItem());
            }

            if (aoToMatchLast != null && accountOperation.getId().equals(aoToMatchLast.getId())) {
                continue;
            }

            amountToMatch = ZERO;
            fullMatch = false;

            MatchingAmount matchingAmount = new MatchingAmount();
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
                // Functional Amounts
                if (functionalCreditAmount.compareTo(accountOperation.getTransactionalUnMatchingAmount()) >= 0) {
                    fullMatch = true;
                    functionalAmountToMatch = accountOperation.getUnMatchingAmount();
                    functionalCreditAmount = functionalCreditAmount.subtract(functionalAmountToMatch);

                } else {
                    fullMatch = false;
                    functionalAmountToMatch = functionalCreditAmount;
                    functionalCreditAmount = ZERO;
                }
                
                // Transactional Amounts
                if (amountCredit.compareTo(accountOperation.getTransactionalUnMatchingAmount()) >= 0) {
                    amountToMatch = accountOperation.getTransactionalUnMatchingAmount();
                    amountCredit = amountCredit.subtract(amountToMatch);
                } else {
                    amountToMatch = amountCredit;
                    amountCredit = ZERO;
                }

                if (PPL_CREATION.equals(accountOperation.getCode())) {
                    isPplCreationCreditAo = true;
                }

                // Builld list with AOP for SecurityDeposit, that will be stored in SD Transaction
                // Transaction shall only have a Payment AOP : No AOI please
                // no need to check AO type, if the invoice is SD, this list will be used in other call, to create SD Transaction
                securityDepositAOPs.add(accountOperation);

            } else {
                // Functional Amounts
                if (functionalDebitAmount.compareTo(accountOperation.getTransactionalUnMatchingAmount()) >= 0) {
                	fullMatch = true;
                	functionalAmountToMatch = accountOperation.getUnMatchingAmount();
                    functionalDebitAmount = functionalDebitAmount.subtract(functionalAmountToMatch);
                } else {
                	fullMatch = false;
                    functionalAmountToMatch = functionalDebitAmount;
                    functionalDebitAmount = ZERO;
                }
                
                // Transactional Amounts
                if (amountDebit.compareTo(accountOperation.getTransactionalUnMatchingAmount()) >= 0) {
                    amountToMatch = accountOperation.getTransactionalUnMatchingAmount();
                    amountDebit = amountDebit.subtract(amountToMatch);
                } else {
                    amountToMatch = amountDebit;
                    amountDebit = ZERO;
                }
            }

            if (accountOperation instanceof RecordedInvoice) {
                Invoice invoice = ((RecordedInvoice) accountOperation).getInvoice();
                if (invoice != null) {
                    if (invoice.getInvoiceType() != null && INVOICE_TYPE_SECURITY_DEPOSIT.equals(invoice.getInvoiceType().getCode())) {
                        sdInvoice = invoice;
                    }

                    if (withWriteOff) {
                        log.info("matching - [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.ABANDONED + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.ABANDONED);
                    } else if (withRefund) {
                        log.info("matching - [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.REFUNDED + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.REFUNDED);
                    } else if (isPplCreationCreditAo) {
                        log.info("matching - [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.PENDING_PLAN + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.PENDING_PLAN);
                    } else if (fullMatch) {
                        log.info("matching - [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.PAID + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.PAID);
                        aosToGenerateMatchingCode.add(accountOperation);
                        if (InvoicePaymentStatusEnum.PAID == invoice.getPaymentStatus()) {
                            isToTriggerCollectionPlanLevelsJob = true;
                        }

                    } else if (!fullMatch) {
                        log.info("matching - [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.PPAID + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.PPAID);
                    } 
                    invoice.setPaymentStatusDate(new Date());
                    appliedRate = invoice.getAppliedRate();
                    entityUpdatedEventProducer.fire(invoice);

                }
            }

            invoiceTransactionalUnMatchingAmount = accountOperation.getTransactionalUnMatchingAmount();
            BigDecimal matchedAmount;
            BigDecimal transactionMatchedAmount;
            if (0 != amountToMatch.longValue()) {
                // add baseMatchingAmount to avoid having TransactionalMatchingAmount = MatchingAmount * 2
                matchedAmount = accountOperation.getMatchingAmount();
                transactionMatchedAmount = accountOperation.getTransactionalMatchingAmount();
                accountOperation.setTransactionalUnMatchingAmount(
                        accountOperation.getTransactionalUnMatchingAmount().subtract(amountToMatch));
                accountOperation.setTransactionalMatchingAmount(accountOperation.getTransactionalAmount()
                        .subtract(accountOperation.getTransactionalUnMatchingAmount()));
                BigDecimal computedMatchingAmount =
                        (accountOperation.getAmount().multiply(accountOperation.getTransactionalMatchingAmount()))
                                .divide(accountOperation.getTransactionalAmount(),
                                        appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
                accountOperation.setMatchingAmount(computedMatchingAmount);
                accountOperation.setUnMatchingAmount((accountOperation.getAmount().subtract(computedMatchingAmount)).abs());
                accountOperation.setMatchingStatus(fullMatch ? MatchingStatusEnum.L : MatchingStatusEnum.P);

                matchingAmount.setMatchingAmount((matchedAmount.subtract(accountOperation.getMatchingAmount())).abs());
                matchingAmount.setTransactionalMatchingAmount((transactionMatchedAmount
                        .subtract(accountOperation.getTransactionalMatchingAmount()).abs()));
                matchingAmount.updateAudit(currentUser);
                matchingAmount.setAccountOperation(accountOperation);
                matchingAmount.setMatchingCode(matchingCode);

                accountOperation.getMatchingAmounts().add(matchingAmount);
                matchingCode.getMatchingAmounts().add(matchingAmount);
                if(accountOperation instanceof Payment
                        || CREDIT.equals(accountOperation.getTransactionCategory())) {
                    paymentRate = accountOperation.getAppliedRate();
                } else {
                    invoiceRate = accountOperation.getAppliedRate();
                }
                if(aoToMatchLast == null) {
                    TradingCurrency transactionalCurrency = accountOperation.getTransactionalCurrency() != null ? accountOperation.getTransactionalCurrency() : null;
                    TradingCurrency functionalCurrency = null;
                    Currency currency = appProvider.getCurrency();
                    if (currency != null && !StringUtils.isBlank(currency.getCurrencyCode())) {
                        functionalCurrency = tradingCurrencyService.findByTradingCurrencyCode(currency.getCurrencyCode());
                    }
                    if (transactionalCurrency != null && !transactionalCurrency.equals(functionalCurrency)) {
                        createExchangeGainLoss(accountOperation, matchingAmount, invoiceRate, paymentRate);
                    }
                }
            }
        }

        // Leave AO to be matched as partial to the end. It will be matched only if any unmatched amount remains.
        if (aoToMatchLast != null) {

            AccountOperation accountOperation = accountOperationService.findById(aoToMatchLast.getId());
            amountToMatch = ZERO;
            fullMatch = false;
            if (accountOperation instanceof RecordedInvoice && ((RecordedInvoice) accountOperation).getPaymentScheduleInstanceItem() != null) {
                listPaymentScheduleInstanceItem.add(((RecordedInvoice) accountOperation).getPaymentScheduleInstanceItem());
            }

            MatchingAmount matchingAmount = new MatchingAmount();
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {

                // Transactional Amounts
                if (amountCredit.compareTo(accountOperation.getTransactionalUnMatchingAmount()) >= 0) {
                    fullMatch = true;
                    amountToMatch = accountOperation.getTransactionalUnMatchingAmount();
                    amountCredit = amountCredit.subtract(amountToMatch);
                } else {
                    amountToMatch = amountCredit;
                    amountCredit = ZERO;
                }

                // Functional Amounts
                if (functionalCreditAmount.compareTo(accountOperation.getUnMatchingAmount()) >= 0) {
                    functionalAmountToMatch = accountOperation.getTransactionalUnMatchingAmount();
                    functionalCreditAmount = amountCredit.subtract(amountToMatch);
                } else {
                    functionalAmountToMatch = functionalCreditAmount;
                    functionalCreditAmount = ZERO;
                }

                // Builld list with AOP for SecurityDeposit, that will be stored in SD Transaction
                // Transaction shall only have a Payment AOP : No AOI please
                // no need to check AO type, if the invoice is SD, this list will be used in other call, to create SD Transaction
                securityDepositAOPs.add(accountOperation);

            } else {
                // Transactional Amounts
                if (amountDebit.compareTo(accountOperation.getTransactionalUnMatchingAmount()) >= 0) {
                    fullMatch = true;
                    amountToMatch = accountOperation.getTransactionalUnMatchingAmount();
                    amountDebit = amountDebit.subtract(amountToMatch);
                } else {
                    fullMatch = false;
                    amountToMatch = amountDebit;
                    amountDebit = ZERO;
                }

                // Functional Amounts
                if (functionalDebitAmount.compareTo(accountOperation.getUnMatchingAmount()) >= 0) {
                    functionalAmountToMatch = accountOperation.getUnMatchingAmount();
                    functionalDebitAmount = functionalDebitAmount.subtract(functionalAmountToMatch);
                } else {
                    functionalAmountToMatch = functionalDebitAmount;
                    functionalDebitAmount = ZERO;
                }
            }


            if(accountOperation instanceof RecordedInvoice) {
                Invoice invoice = ((RecordedInvoice)accountOperation).getInvoice();
                if (invoice != null) {
                    if (invoice.getInvoiceType() != null && INVOICE_TYPE_SECURITY_DEPOSIT.equals(invoice.getInvoiceType().getCode())) {
                        sdInvoice = invoice;
                    }
                    if(withWriteOff) {
                        log.info("matching- [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.ABANDONED + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.ABANDONED);
                    } else if(withRefund) {
                        log.info("matching- [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.REFUNDED + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.REFUNDED);
                    } else if(fullMatch) {
                        log.info("matching- [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.PAID + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.PAID);
                        aosToGenerateMatchingCode.add((RecordedInvoice) accountOperation);
                        if (InvoicePaymentStatusEnum.PAID == invoice.getPaymentStatus()) {
                            isToTriggerCollectionPlanLevelsJob = true;
                        }
                    } else if(!fullMatch) {
                        log.info("matching- [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.PPAID + "]");
                        invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.PPAID);
                    }
                    invoice.setPaymentStatusDate(new Date());

                }
                
            }

            invoiceTransactionalUnMatchingAmount = accountOperation.getTransactionalUnMatchingAmount();
            BigDecimal matchedAmount;
            BigDecimal transactionMatchedAmount;
            if(0 != amountToMatch.longValue()) {
                matchedAmount = accountOperation.getMatchingAmount();
                transactionMatchedAmount = accountOperation.getTransactionalMatchingAmount();
                if(invoiceTransactionalUnMatchingAmount.compareTo(amountToMatch) <= 0) {
                    accountOperation.setTransactionalUnMatchingAmount(
                            (invoiceTransactionalUnMatchingAmount.subtract(amountToMatch)).abs());
                } else {
                    accountOperation.setTransactionalUnMatchingAmount(accountOperation.
                            getTransactionalUnMatchingAmount().subtract(amountToMatch));
                }
                accountOperation.setTransactionalMatchingAmount(
                        accountOperation.getTransactionalAmount().subtract(accountOperation.getTransactionalUnMatchingAmount()));
                BigDecimal computedMatchingAmount =
                        (accountOperation.getAmount().multiply(accountOperation.getTransactionalMatchingAmount()))
                                .divide(accountOperation.getTransactionalAmount(),
                                        appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
                accountOperation.setUnMatchingAmount((accountOperation.getAmount().subtract(computedMatchingAmount)).abs());
                accountOperation.setMatchingAmount(computedMatchingAmount);
                matchingAmount.setMatchingAmount((matchedAmount.subtract(accountOperation.getMatchingAmount())).abs());
                matchingAmount.setTransactionalMatchingAmount((transactionMatchedAmount
                        .subtract(accountOperation.getTransactionalMatchingAmount()).abs()));
                boolean isMatched = fullMatch && accountOperation.getTransactionalUnMatchingAmount().compareTo(ZERO) == 0;
                accountOperation.setMatchingStatus(isMatched ? MatchingStatusEnum.L : MatchingStatusEnum.P);

                matchingAmount.updateAudit(currentUser);
                matchingAmount.setAccountOperation(accountOperation);
                matchingAmount.setMatchingCode(matchingCode);

                accountOperation.getMatchingAmounts().add(matchingAmount);
                matchingCode.getMatchingAmounts().add(matchingAmount);

                if(accountOperation instanceof Payment
                        || CREDIT.equals(accountOperation.getTransactionCategory())) {
                    paymentRate = accountOperation.getAppliedRate();
                } else {
                    invoiceRate = accountOperation.getAppliedRate();
                }
                TradingCurrency transactionalCurrency = accountOperation.getTransactionalCurrency() != null ? accountOperation.getTransactionalCurrency() : null;
                TradingCurrency functionalCurrency = null;
                Currency currency = appProvider.getCurrency();
                if (currency != null && !StringUtils.isBlank(currency.getCurrencyCode())) {
                    functionalCurrency = tradingCurrencyService.findByTradingCurrencyCode(currency.getCurrencyCode());
                }
                if (transactionalCurrency != null && !transactionalCurrency.equals(functionalCurrency)) {
                    createExchangeGainLoss(accountOperation, matchingAmount, invoiceRate, paymentRate);
                }
            }
        }

        if (isToTriggerCollectionPlanLevelsJob) {
            invoiceService.triggersCollectionPlanLevelsJob();
        }

        matchingCode.setMatchingAmountDebit(amount);
        matchingCode.setMatchingAmountCredit(amount);
        matchingCode.setMatchingDate(new Date());
        matchingCode.setMatchingType(matchingTypeEnum);
        create(matchingCode);
        if (!listPaymentScheduleInstanceItem.isEmpty()) {
            for (PaymentScheduleInstanceItem paymentScheduleInstanceItem : listPaymentScheduleInstanceItem) {
                paymentScheduleInstanceItemService.applyOneShotPS(paymentScheduleInstanceItem);
            }
        }

        // send matched PPL Aos to check if PaymentPlan shall be passed to COMPLETE status
        paymentPlanService.toComplete(listOcc.stream()
                .filter(accountOperation -> PPL_INSTALLMENT.equals(accountOperation.getCode()) && MatchingStatusEnum.L == accountOperation.getMatchingStatus())
                .map(AccountOperation::getId)
                .collect(toList()));

        // generate matchingCode for related AOs
        aosToGenerateMatchingCode.forEach(accountOperation -> journalEntryService.assignMatchingCodeToJournalEntries(accountOperation, null));

        //  case when totally matched AOs
        updateMatchedSecurityDeposit(amountToMatch, sdInvoice, securityDepositAOPs);

    }

    private void createExchangeGainLoss(AccountOperation accountOperation,
                                        MatchingAmount matchingAmount, BigDecimal invoiceRate, BigDecimal paymentRate) {
        if (invoiceRate != null && paymentRate != null
                &&  paymentRate.compareTo(invoiceRate) != 0) {
            AccountOperation exchangeDeltaAccountOperation = new AccountOperation();
            BigDecimal computedInvoiceAmount =
                    matchingAmount.getTransactionalMatchingAmount().divide(invoiceRate,
                            appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
            BigDecimal computedPaymentAmount =
                    matchingAmount.getTransactionalMatchingAmount().divide(paymentRate,
                            appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
            BigDecimal exchangeAmountDelta = computedPaymentAmount.subtract(computedInvoiceAmount);
            BigDecimal exchangeAmount = exchangeAmountDelta.abs();
            if(exchangeAmount.compareTo(ZERO) > 0) {
                OCCTemplate template = null;
                if (exchangeAmountDelta.compareTo(ZERO) < 0) {
                    exchangeDeltaAccountOperation.setCode(XCH_LOSS);
                    template = occTemplateService.findByCode(exchangeDeltaAccountOperation.getCode());
                    exchangeDeltaAccountOperation.setTransactionCategory(template.getOccCategory());
                    exchangeDeltaAccountOperation.setDescription(template.getDescription());
                } else {
                    exchangeDeltaAccountOperation.setCode(XCH_GAIN);
                    template = occTemplateService.findByCode(exchangeDeltaAccountOperation.getCode());
                    exchangeDeltaAccountOperation.setTransactionCategory(template.getOccCategory());
                    exchangeDeltaAccountOperation.setDescription(template.getDescription());
                }
                exchangeDeltaAccountOperation.setCustomerAccount(accountOperation.getCustomerAccount());
                exchangeDeltaAccountOperation.setAccountingCode(accountOperation.getAccountingCode());
                exchangeDeltaAccountOperation.setMatchingStatus(MatchingStatusEnum.L);
                exchangeDeltaAccountOperation.setJournal(accountOperation.getJournal());
                exchangeDeltaAccountOperation.setAccountCodeClientSide(accountOperation.getAccountCodeClientSide());
                exchangeDeltaAccountOperation.setTransactionDate(new Date());
                exchangeDeltaAccountOperation.setAmount(exchangeAmount);
                exchangeDeltaAccountOperation.setMatchingAmount(exchangeAmount);
                exchangeDeltaAccountOperation.setTransactionalMatchingAmount(ZERO);
                exchangeDeltaAccountOperation.setTransactionalAmount(ZERO);
                exchangeDeltaAccountOperation.setTransactionalUnMatchingAmount(ZERO);
                exchangeDeltaAccountOperation.setTransactionalAmountWithoutTax(ZERO);

                MatchingAmount exchangeMatchingAmount = new MatchingAmount();
                exchangeMatchingAmount.updateAudit(currentUser);
                exchangeMatchingAmount.setAccountOperation(exchangeDeltaAccountOperation);
                exchangeMatchingAmount.setMatchingCode(matchingAmount.getMatchingCode());
                exchangeMatchingAmount.setMatchingAmount(exchangeDeltaAccountOperation.getMatchingAmount());
                exchangeMatchingAmount.setTransactionalMatchingAmount(exchangeDeltaAccountOperation.getTransactionalMatchingAmount());
                accountOperation.getMatchingAmounts().add(matchingAmount);
                matchingAmount.getMatchingCode().getMatchingAmounts().add(exchangeMatchingAmount);

                accountOperationService.create(exchangeDeltaAccountOperation);
            }
        }
    }

    /**
     * Update SecurityDeposit related to a DEB_SD AO
     * 
     * @param amountToMatch
     * @param invoice
     */
	private void updateMatchedSecurityDeposit(BigDecimal amountToMatch, Invoice invoice, List<AccountOperation> securityDepositAOPs) {
        if (invoice == null) {
            return;
        }

        if (CollectionUtils.isEmpty(securityDepositAOPs)) {
            return;
        }

		Optional<SecurityDeposit> osd = securityDepositService.getSecurityDepositByInvoiceId(invoice.getId());
		if(osd.isPresent() && osd.map(SecurityDeposit::getTemplate).isPresent()) {
			SecurityDeposit securityDeposit = osd.get();
			SecurityDepositTemplate securityDepositTemplate = osd.map(SecurityDeposit::getTemplate).get();
			// Check that max Amount is not reached
            // For existing sd without currentBalance : avoid NPE
            if (securityDeposit.getCurrentBalance() == null) {
                securityDeposit.setCurrentBalance(ZERO);
            }
			if(securityDepositTemplate.getMaxAmount() != null && securityDepositTemplate.getMaxAmount().compareTo(securityDeposit.getCurrentBalance().add(amountToMatch)) < 0) {
				throw new BusinessException("The current balance + amount to credit must be less than or equal to the maximum amount of the Template");
			}
			securityDeposit.setCurrentBalance(ofNullable(securityDeposit.getCurrentBalance()).orElse(ZERO).add(amountToMatch));
			// Update SD.Amount for VALIDATED and HOLD SecurityDeposit
			if(Arrays.asList(SecurityDepositStatusEnum.HOLD, SecurityDepositStatusEnum.VALIDATED).contains(securityDeposit.getStatus())) {
				securityDeposit.setAmount(securityDeposit.getAmount().subtract(amountToMatch));
				if(ZERO.compareTo(securityDeposit.getAmount()) >= 0) {
					securityDeposit.setAmount(null);
					securityDeposit.setStatus(SecurityDepositStatusEnum.LOCKED);
				} else {
					securityDeposit.setStatus(SecurityDepositStatusEnum.HOLD);
				}
			}
			// Create SD Transaction if SecurityDeposit is not Canceled
            securityDepositAOPs.forEach(sdAop -> {
                        if (!sdAop.getCode().equalsIgnoreCase(CAN_SD)) {
                            securityDepositService.createSecurityDepositTransaction(securityDeposit, amountToMatch,
                                    SecurityDepositOperationEnum.CREDIT_SECURITY_DEPOSIT, OperationCategoryEnum.CREDIT, sdAop);
                        }
                    }
            );

		}
	}

    /**
     * @param aoID account operation id
     * @throws BusinessException business exception.
     */
    public void unmatchingByAOid(Long aoID) throws BusinessException {

        AccountOperation accountOperation = accountOperationService.findById(aoID);
        if (accountOperation == null) {
            throw new BusinessException("Cant find account operation by id:" + aoID);
        }
        if (MatchingStatusEnum.L != accountOperation.getMatchingStatus() && MatchingStatusEnum.P != accountOperation.getMatchingStatus() ) {
            throw new BusinessException("The account operation is unmatched");
        }
        unmatching(accountOperation.getMatchingAmounts().get(0).getMatchingCode().getId());

    }

    /**
     * @param idMatchingCode id of matching code
     * @throws BusinessException business exception
     */
    public void unmatching(Long idMatchingCode) throws BusinessException {
        log.info("start cancelMatching with id {}", idMatchingCode);
        PaymentScheduleInstanceItem paymentScheduleInstanceItem = null;
        if (idMatchingCode == null) {
            throw new BusinessException("Error when idMatchingCode is null!");
        }
        MatchingCode matchingCode = findById(idMatchingCode);
        if (matchingCode == null) {
            log.warn("Error when found a null matchingCode!");
            throw new BusinessException("Error when found a null matchingCode!");
        }
        List<MatchingAmount> matchingAmounts = matchingCode.getMatchingAmounts();

        // Build unmatching code instance
        UnMatchingCode unMatchingCode = new UnMatchingCode(matchingCode);
        unMatchingCodeService.create(unMatchingCode);

        if (matchingAmounts != null) {
            log.info("matchingAmounts.size:" + matchingAmounts.size());
            for (MatchingAmount matchingAmount : matchingAmounts) {
                AccountOperation operation = matchingAmount.getAccountOperation();
                if (operation.getMatchingStatus() != MatchingStatusEnum.P && operation.getMatchingStatus() != MatchingStatusEnum.L) {
                    throw new BusinessException("Error:matchingCode containt unMatching operation");
                }
                if (operation instanceof RecordedInvoice && ((RecordedInvoice) operation).getPaymentScheduleInstanceItem() != null) {
                    paymentScheduleInstanceItem = ((RecordedInvoice) operation).getPaymentScheduleInstanceItem();
                }
                BigDecimal baseUnMatchingAmount = operation.getUnMatchingAmount();
                BigDecimal calculatedMatchingAmount = operation.getMatchingAmount().subtract(matchingAmount.getMatchingAmount());
                BigDecimal calculatedUnMatchingAmount = baseUnMatchingAmount.add(matchingAmount.getMatchingAmount());
                operation.setUnMatchingAmount(calculatedUnMatchingAmount);
                operation.setMatchingAmount(calculatedMatchingAmount);
                operation.setTransactionalUnMatchingAmount(operation.
                        getTransactionalUnMatchingAmount().add(ofNullable(matchingAmount.getTransactionalMatchingAmount()).orElse(ZERO)));
                operation.setTransactionalMatchingAmount(operation.
                        getTransactionalMatchingAmount().subtract(ofNullable(matchingAmount.getTransactionalMatchingAmount()).orElse(ZERO)));
                if (ZERO.compareTo(operation.getMatchingAmount()) == 0) {
                    operation.setMatchingStatus(MatchingStatusEnum.O);
                    if (operation instanceof RecordedInvoice) {
                        Invoice invoice = ((RecordedInvoice)operation).getInvoice();
                        if (invoice != null) {
                            log.info("unmatching (op.MatchingAmount == 0) - [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                    invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.UNPAID + "]");
                            invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.UNPAID);
                            invoice.setPaymentStatusDate(new Date());
                            entityUpdatedEventProducer.fire(invoice);
                        }
                    }
                } else {
                    operation.setMatchingStatus(MatchingStatusEnum.P);
                    if (operation instanceof RecordedInvoice) {
                        Invoice invoice = ((RecordedInvoice)operation).getInvoice();
                        if (invoice != null) {
                            log.info("unmatching (op.MatchingAmount <> 0) - [Inv.id : " + invoice.getId() + " - oldPaymentStatus : " + 
                                    invoice.getPaymentStatus() + " - newPaymentStatus : " + InvoicePaymentStatusEnum.PPAID + "]");
                            invoiceService.checkAndUpdatePaymentStatus(invoice, invoice.getPaymentStatus(), InvoicePaymentStatusEnum.PPAID);
                            invoice.setPaymentStatusDate(new Date());
                        }
                    }
                }
                if(matchingAmount.getAccountOperation() != null
                        && (XCH_LOSS.equalsIgnoreCase(matchingAmount.getAccountOperation().getCode())
                        || XCH_GAIN.equalsIgnoreCase(matchingAmount.getAccountOperation().getCode()))) {
                    accountOperationService.remove(matchingAmount.getAccountOperation());
                } else {
                    operation.getMatchingAmounts().remove(matchingAmount);
                    accountOperationService.update(operation);
                }
                log.info("cancel one accountOperation!");

                // Build unMatchingAmount instance
                UnMatchingAmount unMatchingAmount = new UnMatchingAmount(matchingAmount, unMatchingCode);
                unMatchingAmountService.create(unMatchingAmount);
            }
        }
        log.info("remove matching code ....");
        remove(matchingCode);
        if (paymentScheduleInstanceItem != null) {
            paymentScheduleInstanceItemService.applyOneShotRejectPS(paymentScheduleInstanceItem);
        }
        log.info("successfully end cancelMatching!");
    }

    /**
     * Match account operations of a given customer account.
     * 
     * @param customerAccountId Customer account - id or
     * @param customerAccountCode Customer account - code
     * @param operationIds Ids of account operations to match
     * @param aoToMatchLast An operation to match last - most likely will be matched partially
     * @return Information on matched operations
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException no all operation un matched exception
     * @throws UnbalanceAmountException un balance amount exception
     * @throws Exception general exception
     */
    public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds, Long aoToMatchLast)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, Exception {
        return matchOperations(customerAccountId, customerAccountCode, operationIds, aoToMatchLast, MatchingTypeEnum.M);
    }
    

    public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds, Long aoToMatchLast, BigDecimal amountForUnmatching)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, Exception {
        return matchOperations(customerAccountId, customerAccountCode, operationIds, aoToMatchLast, MatchingTypeEnum.M, amountForUnmatching);
    }

    /**
     * Match account operations of a given customer account.
     * 
     * @param customerAccountId Customer account - id or
     * @param customerAccountCode Customer account - code
     * @param operationIds Ids of account operations to match
     * @param aoToMatchLast An operation to match last - most likely will be matched partially
     * @param matchingTypeEnum Matching type
     * @return Information on matched operations
     * @throws BusinessException business exception
     * @throws NoAllOperationUnmatchedException no all operation un matched exception.
     * @throws UnbalanceAmountException un balance amount exception
     */
    public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds, Long aoToMatchLast, MatchingTypeEnum matchingTypeEnum)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        log.info("matchOperations   customerAccountId:{}  customerAccountCode:{} operationIds:{} ", new Object[] { customerAccountId, customerAccountCode, operationIds });
        CustomerAccount customerAccount = customerAccountService.findCustomerAccount(customerAccountId, customerAccountCode);

        BigDecimal amoutDebit = new BigDecimal(0);
        BigDecimal amoutCredit = new BigDecimal(0);
        List<AccountOperation> listOcc = new ArrayList<>();
        MatchingReturnObject matchingReturnObject = new MatchingReturnObject();
        matchingReturnObject.setOk(false);

        int cptOccDebit = 0, cptOccCredit = 0, cptPartialAllowed = 0;
        AccountOperation accountOperationForPartialMatching = null;

        for (Long id : operationIds) {
            AccountOperation accountOperation = accountOperationService.findById(id);
            if (accountOperation == null) {
                throw new BusinessException("Cannot find account operation with id:" + id);
            }
            listOcc.add(accountOperation);

        }

        for (AccountOperation accountOperation : listOcc) {
            if (!accountOperation.getCustomerAccount().getCode().equals(customerAccount.getCode())) {
                log.warn("matchOperations The operationId " + accountOperation.getId() + " is not for the customerAccount");
                throw new BusinessException("The operationId " + accountOperation.getId() + " is not for the customerAccount");
            }
            if (accountOperation.getMatchingStatus() != MatchingStatusEnum.O && accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
                log.warn("matchOperations The operationId " + accountOperation.getId() + " is already matching");
                throw new NoAllOperationUnmatchedException("The operationId " + accountOperation.getId() + " is already matching");
            }
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT) {
                cptOccDebit++;
                amoutDebit = amoutDebit.add(accountOperation.getTransactionalUnMatchingAmount());
            }
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
                cptOccCredit++;
                amoutCredit = amoutCredit.add(accountOperation.getTransactionalUnMatchingAmount());
            }
        }
        if (cptOccCredit == 0) {
            throw new BusinessException("matchingService.noCreditOps");
        }
        if (cptOccDebit == 0) {
            throw new BusinessException("matchingService.noDebitOps");
        }
        BigDecimal balance = amoutDebit.subtract(amoutCredit);
        balance = balance.abs();
        BigDecimal matchedAmount = amoutDebit;

        log.info("matchOperations  balance: {}", balance);

        AccountOperation payment = listOcc.stream()
                .filter(accountOperation -> accountOperation instanceof Payment
                        || accountOperation instanceof OtherCreditAndCharge)
                .findFirst()
                .orElse(null);
		
        if (payment != null && payment.getReference() != null) {
			PaymentHistory paymentHistory = paymentHistoryService.findHistoryByPaymentId(payment.getReference());
			if (paymentHistory != null) {
				List<Long> aoIdsToPay = operationIds.stream().filter(aoId -> !aoId.equals(payment.getId())).collect(toList());
				if (paymentHistory.getListAoPaid() == null || paymentHistory.getListAoPaid().isEmpty()) {
					List<AccountOperation> aoToPay = new ArrayList<>();
					for (Long aoId : aoIdsToPay) {
						aoToPay.add(accountOperationService.findById(aoId));
					}
					for (AccountOperation ao : aoToPay) {
						if (ao != null) {
							if (ao.getPaymentHistories() == null) {
								ao.setPaymentHistories(new ArrayList<>());
							}
							ao.getPaymentHistories().add(paymentHistory);

							if (paymentHistory.getListAoPaid() == null) {
								paymentHistory.setListAoPaid(new ArrayList<>());
							}
							paymentHistory.getListAoPaid().add(ao);
						}
					}
				}
			}
		}


        if (balance.compareTo(ZERO) == 0) {
            matching(listOcc, matchedAmount, null, matchingTypeEnum);
            matchingReturnObject.setOk(true);
            log.info("matchOperations successful : no partial");

            return matchingReturnObject;
        }

        matchedAmount = amoutCredit;
        if(amoutCredit.compareTo(amoutDebit) > 0) {
            matchedAmount = amoutDebit;
        }

        if (aoToMatchLast != null) {
            matching(listOcc, matchedAmount, accountOperationService.findById(aoToMatchLast), matchingTypeEnum);
            matchingReturnObject.setOk(true);
            log.info("matchOperations successful :  partial ok (idPartial recu)");
            return matchingReturnObject;
        }
        // debit 200,60 ; credit 150 => balance = 110
        for (AccountOperation accountOperation : listOcc) {
            PartialMatchingOccToSelect p = new PartialMatchingOccToSelect();
            p.setAccountOperation(accountOperation);
            p.setPartialMatchingAllowed(false);
            if (amoutCredit.compareTo(amoutDebit) > 0) {
                if (OperationCategoryEnum.CREDIT.name().equals(accountOperation.getTransactionCategory().name())) {
                    if (balance.compareTo(accountOperation.getTransactionalUnMatchingAmount()) <= 0) {
                        p.setPartialMatchingAllowed(true);
                        cptPartialAllowed++;
                        accountOperationForPartialMatching = accountOperation;
                    }
                }
            } else {
                if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT) {
                    if (balance.compareTo(accountOperation.getTransactionalUnMatchingAmount()) <= 0) {
                        p.setPartialMatchingAllowed(true);
                        cptPartialAllowed++;
                        accountOperationForPartialMatching = accountOperation;
                    }
                }
            }
            matchingReturnObject.getPartialMatchingOcc().add(p);
        }

        if (cptPartialAllowed == 1) {
            matching(listOcc, matchedAmount, accountOperationForPartialMatching, matchingTypeEnum);
            matchingReturnObject.setOk(true);
            log.info("matchOperations successful :  partial ok (un idPartial possible)");
            return matchingReturnObject;
        }

        if (cptPartialAllowed == 0) {
            throw new BusinessException("matchingService.matchingImpossible");
        }
        log.info("matchOperations successful :  partial  (plusieurs idPartial possible)");
        matchingReturnObject.setOk(false);

        log.debug("matchingReturnObject.getPartialMatchingOcc().size:"
                + (matchingReturnObject.getPartialMatchingOcc() == null ? null : matchingReturnObject.getPartialMatchingOcc().size()));

        return matchingReturnObject;
    }

        public MatchingReturnObject matchOperations(Long customerAccountId, String customerAccountCode, List<Long> operationIds, Long aoToMatchLast, MatchingTypeEnum matchingTypeEnum, BigDecimal amount)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {

        log.info("matchOperations   customerAccountId:{}  customerAccountCode:{} operationIds:{} ", new Object[] { customerAccountId, customerAccountCode, operationIds });
        CustomerAccount customerAccount = customerAccountService.findCustomerAccount(customerAccountId, customerAccountCode);


        BigDecimal amoutCredit = new BigDecimal(0);
        List<AccountOperation> listOcc = new ArrayList<>();
        MatchingReturnObject matchingReturnObject = new MatchingReturnObject();
        matchingReturnObject.setOk(false);

        int cptOccDebit = 0, cptOccCredit = 0, cptPartialAllowed = 0;
        AccountOperation accountOperationForPartialMatching = null;

        for (Long id : operationIds) {
            AccountOperation accountOperation = accountOperationService.findById(id);
            if (accountOperation == null) {
                throw new BusinessException("Cannot find account operation with id:" + id);
            }
            listOcc.add(accountOperation);

        }

        for (AccountOperation accountOperation : listOcc) {
            if (!accountOperation.getCustomerAccount().getCode().equals(customerAccount.getCode())) {
                log.warn("matchOperations The operationId " + accountOperation.getId() + " is not for the customerAccount");
                throw new BusinessException("The operationId " + accountOperation.getId() + " is not for the customerAccount");
            }
            if (accountOperation.getMatchingStatus() != MatchingStatusEnum.O && accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
                log.warn("matchOperations The operationId " + accountOperation.getId() + " is already matching");
                throw new NoAllOperationUnmatchedException("The operationId " + accountOperation.getId() + " is already matching");
            }
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT) {
                cptOccDebit++;
            }
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
                cptOccCredit++;
                amoutCredit = amoutCredit.add(accountOperation.getUnMatchingAmount());
            }
        }
        if (cptOccCredit == 0) {
            throw new BusinessException("matchingService.noCreditOps");
        }
        if (cptOccDebit == 0) {
            throw new BusinessException("matchingService.noDebitOps");
        }
            BigDecimal balance = amount.subtract(amoutCredit);
            balance = balance.abs();


            log.info("matchOperations  balance: {}", balance);

            AccountOperation payment = listOcc.stream()
                    .filter(accountOperation -> accountOperation instanceof Payment
                            || accountOperation instanceof OtherCreditAndCharge)
                    .findFirst()
                    .orElse(null);
            PaymentHistory paymentHistory = paymentHistoryService.findHistoryByPaymentId(payment.getReference());
            if (payment != null && paymentHistory != null) {
                List<Long> aoIdsToPay = operationIds.stream().filter(aoId -> !aoId.equals(payment.getId())).collect(toList());
                if (paymentHistory.getListAoPaid() == null || paymentHistory.getListAoPaid().isEmpty()) {
                    List<AccountOperation> aoToPay = new ArrayList<>();
                    for (Long aoId : aoIdsToPay) {
                        aoToPay.add(accountOperationService.findById(aoId));
                    }
                    for (AccountOperation ao : aoToPay) {
                        if (ao != null) {
                            if (ao.getPaymentHistories() == null) {
                                ao.setPaymentHistories(new ArrayList<>());
                            }
                            ao.getPaymentHistories().add(paymentHistory);

                            if (paymentHistory.getListAoPaid() == null) {
                                paymentHistory.setListAoPaid(new ArrayList<>());
                            }
                            paymentHistory.getListAoPaid().add(ao);
                        }
                    }
                }
            }
        if (balance.compareTo(ZERO) == 0) {
            matching(listOcc, amount, null, matchingTypeEnum);
            matchingReturnObject.setOk(true);
            log.info("matchOperations successful : no partial");

            return matchingReturnObject;
        }

        if (amount.compareTo(amoutCredit) > 0) {
            amount = amoutCredit;
        }

        if (aoToMatchLast != null) {
            matching(listOcc, amount, accountOperationService.findById(aoToMatchLast), matchingTypeEnum);
            matchingReturnObject.setOk(true);
            log.info("matchOperations successful :  partial ok (idPartial recu)");
            return matchingReturnObject;
        }
        // debit 200,60 ; credit 150 => balance = 110
        for (AccountOperation accountOperation : listOcc) {
            PartialMatchingOccToSelect p = new PartialMatchingOccToSelect();
            p.setAccountOperation(accountOperation);
            p.setPartialMatchingAllowed(false);
            if (amoutCredit.compareTo(amount) > 0) {
                if (OperationCategoryEnum.CREDIT.name().equals(accountOperation.getTransactionCategory().name())) {
                    if (balance.compareTo(accountOperation.getUnMatchingAmount()) <= 0) {
                        p.setPartialMatchingAllowed(true);
                        cptPartialAllowed++;
                        accountOperationForPartialMatching = accountOperation;
                    }
                }
            } else {
                if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT) {
                    if (balance.compareTo(accountOperation.getUnMatchingAmount()) <= 0) {
                        p.setPartialMatchingAllowed(true);
                        cptPartialAllowed++;
                        accountOperationForPartialMatching = accountOperation;
                    }
                }
            }
            matchingReturnObject.getPartialMatchingOcc().add(p);
        }

        if (cptPartialAllowed >= 1) {
            matching(listOcc, amount, accountOperationForPartialMatching, matchingTypeEnum);
            matchingReturnObject.setOk(true);
            log.info("matchOperations successful :  partial ok (un idPartial possible)");
            return matchingReturnObject;
        }

        if (cptPartialAllowed == 0) {
            throw new BusinessException("matchingService.matchingImpossible");
        }
        log.info("matchOperations successful :  partial  (plusieurs idPartial possible)");
        matchingReturnObject.setOk(false);

        log.debug("matchingReturnObject.getPartialMatchingOcc().size:"
                + (matchingReturnObject.getPartialMatchingOcc() == null ? null : matchingReturnObject.getPartialMatchingOcc().size()));

        return matchingReturnObject;
    }

    /**
     * @param code code of finding matching code
     * @return found matching code.
     */
    public MatchingCode findByCode(String code) {
        QueryBuilder qb = new QueryBuilder(MatchingCode.class, "m", null);
        qb.addCriterion("code", "=", code, true);

        try {
            return (MatchingCode) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * unmatching operation account
     *
     * @param accountOperation the account operation to unmatch
     * @throws BusinessException the business exception
     */
    public void unmatchingOperationAccount(AccountOperation accountOperation) throws BusinessException {
        List<MatchingAmount> matchingAmounts = accountOperation.getMatchingAmounts();
        if (matchingAmounts != null && !matchingAmounts.isEmpty()) {

            List<Long> matchingCodesToUnmatch = new ArrayList<>();
            Iterator<MatchingAmount> iterator = accountOperation.getMatchingAmounts().iterator();
            while (iterator.hasNext()) {
                MatchingAmount matchingAmount = iterator.next();
                MatchingCode matchingCode = matchingAmount.getMatchingCode();
                if (matchingCode != null) {
                    matchingCodesToUnmatch.add(matchingCode.getId());
                }
            }
            for (Long matchingCodeId : matchingCodesToUnmatch) {
                unmatching(matchingCodeId);
            }
        }
    }

}