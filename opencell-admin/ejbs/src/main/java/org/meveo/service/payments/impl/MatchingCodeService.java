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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.model.payments.WriteOff;
import org.meveo.service.base.PersistenceService;

/**
 * MatchingCode service implementation.
 *
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Stateless
public class MatchingCodeService extends PersistenceService<MatchingCode> {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

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
        BigDecimal amountToMatch = BigDecimal.ZERO;
        BigDecimal amountCredit = amount;
        BigDecimal amountDebit = amount;
        boolean fullMatch = false;
        boolean withWriteOff = false;
        boolean withRefund = false;
        List<PaymentScheduleInstanceItem> listPaymentScheduleInstanceItem = new ArrayList<PaymentScheduleInstanceItem>();
        
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

            amountToMatch = BigDecimal.ZERO;
            fullMatch = false;

            MatchingAmount matchingAmount = new MatchingAmount();
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
                if (amountCredit.compareTo(accountOperation.getUnMatchingAmount()) >= 0) {
                    fullMatch = true;
                    amountToMatch = accountOperation.getUnMatchingAmount();
                    amountCredit = amountCredit.subtract(amountToMatch);
                } else {
                    fullMatch = false;
                    amountToMatch = amountCredit;
                    amountCredit = BigDecimal.ZERO;
                }

            } else {
                if (amountDebit.compareTo(accountOperation.getUnMatchingAmount()) >= 0) {
                    fullMatch = true;
                    amountToMatch = accountOperation.getUnMatchingAmount();
                    amountDebit = amountDebit.subtract(amountToMatch);
                } else {
                    fullMatch = false;
                    amountToMatch = amountDebit;
                    amountDebit = BigDecimal.ZERO;
                }
            }
            
            if(accountOperation instanceof RecordedInvoice) {
                Invoice invoice = ((RecordedInvoice)accountOperation).getInvoice();
                if(withWriteOff) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.ABANDONED);
                } else if(withRefund) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.REFUNDED);
                } else if(fullMatch) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.PAID);
                } else if(!fullMatch) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.PPAID);
                }
            }
            
            accountOperation.setMatchingAmount(accountOperation.getMatchingAmount().add(amountToMatch));
            accountOperation.setUnMatchingAmount(accountOperation.getUnMatchingAmount().subtract(amountToMatch));
            accountOperation.setMatchingStatus(fullMatch ? MatchingStatusEnum.L : MatchingStatusEnum.P);
            matchingAmount.setMatchingAmount(amountToMatch);
            matchingAmount.updateAudit(currentUser);
            matchingAmount.setAccountOperation(accountOperation);
            matchingAmount.setMatchingCode(matchingCode);

            accountOperation.getMatchingAmounts().add(matchingAmount);
            matchingCode.getMatchingAmounts().add(matchingAmount);
        }

        // Leave AO to be matched as partial to the end. It will be matched only if any unmatched amount remains.
        if (aoToMatchLast != null) {

            AccountOperation accountOperation = accountOperationService.findById(aoToMatchLast.getId());
            amountToMatch = BigDecimal.ZERO;
            fullMatch = false;
            if (accountOperation instanceof RecordedInvoice && ((RecordedInvoice) accountOperation).getPaymentScheduleInstanceItem() != null) {
                listPaymentScheduleInstanceItem.add(((RecordedInvoice) accountOperation).getPaymentScheduleInstanceItem());
            }

            MatchingAmount matchingAmount = new MatchingAmount();
            if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
                if (amountCredit.compareTo(accountOperation.getUnMatchingAmount()) >= 0) {
                    fullMatch = true;
                    amountToMatch = accountOperation.getUnMatchingAmount();
                    amountCredit = amountCredit.subtract(amountToMatch);
                } else {
                    fullMatch = false;
                    amountToMatch = amountCredit;
                    amountCredit = BigDecimal.ZERO;
                }

            } else {
                if (amountDebit.compareTo(accountOperation.getUnMatchingAmount()) >= 0) {
                    fullMatch = true;
                    amountToMatch = accountOperation.getUnMatchingAmount();
                    amountDebit = amountDebit.subtract(amountToMatch);
                } else {
                    fullMatch = false;
                    amountToMatch = amountDebit;
                    amountDebit = BigDecimal.ZERO;
                }
            }
            
            if(accountOperation instanceof RecordedInvoice) {
                Invoice invoice = ((RecordedInvoice)accountOperation).getInvoice();
                if(withWriteOff) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.ABANDONED);
                } else if(withRefund) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.REFUNDED);
                } else if(fullMatch) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.PAID);
                } else if(!fullMatch) {
                    invoice.setPaymentStatus(InvoicePaymentStatusEnum.PPAID);
                }
            }

            accountOperation.setMatchingAmount(accountOperation.getMatchingAmount().add(amountToMatch));
            accountOperation.setUnMatchingAmount(accountOperation.getUnMatchingAmount().subtract(amountToMatch));
            accountOperation.setMatchingStatus(fullMatch ? MatchingStatusEnum.L : MatchingStatusEnum.P);
            matchingAmount.setMatchingAmount(amountToMatch);
            matchingAmount.updateAudit(currentUser);
            matchingAmount.setAccountOperation(accountOperation);
            matchingAmount.setMatchingCode(matchingCode);

            accountOperation.getMatchingAmounts().add(matchingAmount);
            matchingCode.getMatchingAmounts().add(matchingAmount);
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
        if (!MatchingStatusEnum.L.name().equals(accountOperation.getMatchingStatus().name())) {
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
                operation.setUnMatchingAmount(operation.getUnMatchingAmount().add(matchingAmount.getMatchingAmount()));
                operation.setMatchingAmount(operation.getMatchingAmount().subtract(matchingAmount.getMatchingAmount()));
                if (BigDecimal.ZERO.compareTo(operation.getMatchingAmount()) == 0) {
                    operation.setMatchingStatus(MatchingStatusEnum.O);
                    if (operation instanceof RecordedInvoice) {
                        Invoice invoice = ((RecordedInvoice)operation).getInvoice();
                        invoice.setPaymentStatus(InvoicePaymentStatusEnum.PAID);
                    }
                } else {
                    operation.setMatchingStatus(MatchingStatusEnum.P);
                    if (operation instanceof RecordedInvoice) {
                        Invoice invoice = ((RecordedInvoice)operation).getInvoice();
                        invoice.setPaymentStatus(InvoicePaymentStatusEnum.PPAID);
                    }
                }
                operation.getMatchingAmounts().remove(matchingAmount);
                accountOperationService.update(operation);
                log.info("cancel one accountOperation!");
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
        List<AccountOperation> listOcc = new ArrayList<AccountOperation>();
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
                amoutDebit = amoutDebit.add(accountOperation.getUnMatchingAmount());
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
        BigDecimal balance = amoutDebit.subtract(amoutCredit);
        balance = balance.abs();
        BigDecimal matchedAmount = amoutDebit;

        log.info("matchOperations  balance:" + balance);

        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            matching(listOcc, matchedAmount, null, matchingTypeEnum);
            matchingReturnObject.setOk(true);
            log.info("matchOperations successful : no partial");

            return matchingReturnObject;
        }

        if (matchedAmount.compareTo(amoutCredit) > 0) {
            matchedAmount = amoutCredit;
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

        // log.info("matchOperations successful customerAccountId:{} customerAccountCode:{} operationIds:{} user:{}",
        // customerAccountId, customerAccountCode,
        // operationIds, user == null ? "null" : user.getName());
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

            List<Long> matchingCodesToUnmatch = new ArrayList<Long>();
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