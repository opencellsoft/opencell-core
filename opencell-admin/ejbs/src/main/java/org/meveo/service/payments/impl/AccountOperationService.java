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
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.DiscriminatorValue;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.SerializationUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.account.TransferAccountOperationDto;
import org.meveo.api.dto.account.TransferCustomerAccountDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

/**
 * AccountOperation service implementation.
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.2.2
 */
@Stateless
public class AccountOperationService extends PersistenceService<AccountOperation> {

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The o CC template service. */
    @Inject
    private OCCTemplateService oCCTemplateService;

    /** The matching code service. */
    @Inject
    private MatchingCodeService matchingCodeService;

    /**
     * Account operation action Enum
     */
    public enum AccountOperationActionEnum {
        s("Source AO"), t("transfer AO"), c("cancel AO");

        private String label;

        AccountOperationActionEnum(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }
    }

    /**
     * Gets the account operations.
     *
     * @param date date
     * @param operationCode code of operation.
     * @return list of account operations.
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> getAccountOperations(Date date, String operationCode) {
        Query query = getEntityManager().createQuery("from " + getEntityClass().getSimpleName() + " a where a.occCode=:operationCode and  a.transactionDate=:date")
            .setParameter("date", date).setParameter("operationCode", operationCode);

        return query.getResultList();
    }

    /**
     * Gets the account operation.
     *
     * @param amount account operation account
     * @param customerAccount customer account
     * @param transactionType transaction type.
     * @return account operation.
     */
    @SuppressWarnings("unchecked")
    public AccountOperation getAccountOperation(BigDecimal amount, CustomerAccount customerAccount, String transactionType) {

        Query query = getEntityManager()
            .createQuery("from " + getEntityClass().getSimpleName() + " a where a.amount=:amount and  a.customerAccount=:customerAccount and  a.type=:transactionType")
            .setParameter("amount", amount).setParameter("transactionType", transactionType).setParameter("customerAccount", customerAccount);
        List<AccountOperation> accountOperations = query.getResultList();

        return accountOperations.size() > 0 ? accountOperations.get(0) : null;
    }
    
    @SuppressWarnings("unchecked")
    public List<AccountOperation> listByCustomerAccount(CustomerAccount customerAccount, Integer firstRow, Integer numberOfRows) {
        try {
            
            Query query = getEntityManager().createNamedQuery("AccountOperation.listByCustomerAccount");
            query.setParameter("customerAccount", customerAccount);
            
            if (firstRow != null) {
                query.setFirstResult(firstRow);
            }
            if (numberOfRows != null) {
                query.setMaxResults(numberOfRows);
            }
            
            return query.getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list AccountOperation by customerAccount", e);
            return null;
        }
    }

    /**
     * Find by reference.
     *
     * @param reference reference of account operation.
     * @return account operation.
     */
    public AccountOperation findByReference(String reference) {
        try {
            QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a");
            qb.addCriterion("reference", "=", reference, false);
            return (AccountOperation) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    /**
     * Set the discriminatorValue value, so it would be available in the list of entities right away.
     * 
     * @param aop account operation.
     * @return id of account operation.
     * @throws BusinessException business exception.
     */
    public Long createAndReturnId(AccountOperation aop) throws BusinessException {

        if (aop.getClass().isAnnotationPresent(DiscriminatorValue.class)) {
            aop.setType(aop.getClass().getAnnotation(DiscriminatorValue.class).value());
        }

        super.create(aop);
        return aop.getId();

    }

    /**
     * Gets the a os to pay.
     *
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @param opCatToProcess the op cat to process
     * @param customerAccountId the customer account id
     * @return the a os to pay
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> getAOsToPayOrRefundByCA(Date fromDueDate, Date toDueDate, OperationCategoryEnum opCatToProcess,
            Long customerAccountId) {
        try {
            return (List<AccountOperation>) getEntityManager().createNamedQuery("AccountOperation.listAoToPayOrRefundByCA")
                .setParameter("caIdIN", customerAccountId).setParameter("fromDueDateIN", fromDueDate).setParameter("toDueDateIN", toDueDate)
                .setParameter("opCatToProcessIN", opCatToProcess).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Gets the a os to pay.
     *
     * @param paymentMethodEnum the payment method enum
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @param opCatToProcess the op cat to process
     * @param seller the seller
     * @return the a os to pay
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> getAOsToPayOrRefund(PaymentMethodEnum paymentMethodEnum, Date fromDueDate, Date toDueDate, OperationCategoryEnum opCatToProcess, Seller seller) {
        try {
            String queryName = "AccountOperation.listAoToPayOrRefundWithoutCA";
            if (seller != null) {
                queryName = "AccountOperation.listAoToPayOrRefundWithoutCAbySeller";
            }
            Query query = getEntityManager().createNamedQuery(queryName).setParameter("paymentMethodIN", paymentMethodEnum).setParameter("fromDueDateIN", fromDueDate)
                .setParameter("toDueDateIN", toDueDate).setParameter("opCatToProcessIN", opCatToProcess);

            if (seller != null) {
                query.setParameter("sellerIN", seller);
            }
            return (List<AccountOperation>) query.getResultList();

        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return all AccountOperation with invoiceDate date more than n years old
     * 
     * @param nYear age of the account operation
     * @return Filtered list of account operations
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> listInactiveAccountOperations(int nYear) {
        QueryBuilder qb = new QueryBuilder(AccountOperation.class, "e");
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);

        qb.addCriterionDateRangeToTruncatedToDay("transactionDate", higherBound, true, false);

        return (List<AccountOperation>) qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Return all unpaid AccountOperation with invoiceDate date more than n years old
     * 
     * @param nYear age of the account operation
     * @return Filtered list of account operations
     */
    @SuppressWarnings("unchecked")
    public List<AccountOperation> listUnpaidAccountOperations(int nYear) {
        QueryBuilder qb = new QueryBuilder(AccountOperation.class, "e");
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);

        qb.addCriterionDateRangeToTruncatedToDay("transactionDate", higherBound, true, false);
        qb.startOrClause();
        qb.addCriterionEnum("matchingStatus", MatchingStatusEnum.L);
        qb.addCriterionEnum("matchingStatus", MatchingStatusEnum.P);
        qb.endOrClause();

        return (List<AccountOperation>) qb.getQuery(getEntityManager()).getResultList();
    }

    /**
     * Bulk delete.
     *
     * @param inactiveAccountOps the inactive account ops
     * @throws BusinessException the business exception
     */
    public void bulkDelete(List<AccountOperation> inactiveAccountOps) throws BusinessException {
        for (AccountOperation e : inactiveAccountOps) {
            remove(e);
        }
    }

    /**
     * Count unmatched AOs by CA.
     * 
     * @param customerAccount Customer Account.
     * @return count of unmatched AOs.
     */
    public Long countUnmatchedAOByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("AccountOperation.countUnmatchedAOByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countUnmatchedAOs by CA", e);
            return null;
        }
    }

    /**
     * Find by order number.
     *
     * @param orderNumber The order number
     * @return account operation.
     */
    public AccountOperation findByOrderNumber(String orderNumber) {
        try {
            QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a");
            qb.addCriterion("orderNumber", "=", orderNumber, false);
            return (AccountOperation) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    /**
     * Get the account operation reference
     *
     * @param accountOperationId the account operation Id
     * @param accountOperationReference the account operation reference
     * @param accountOperationAction the account operation action ('s' : source, 't' : transfer, 'c' cancelation)
     * @return the account operation reference
     */
    public String getRefrence(Long accountOperationId, String accountOperationReference, String accountOperationAction) {
        return !StringUtils.isBlank(accountOperationReference) ? accountOperationAction + "_" + accountOperationId + "_" + accountOperationReference : accountOperationReference;
    }

    /**
     * Create the new account operation on the fromCustomerAccountCode to settle the old one.
     *
     * @param accountOperation the account operation to transfer
     * @param amount the amount to transfer
     * @return the other credit and charge
     * @throws BusinessException business exception
     */
    public OtherCreditAndCharge createFromAccountOperation(AccountOperation accountOperation, BigDecimal amount) throws BusinessException {

        ParamBean paramBean = paramBeanFactory.getInstance();
        String debitOccTemplateCode = null;

        if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT) {
            debitOccTemplateCode = paramBean.getProperty("occ.transferAccountOperation.credit", "CRD_TRS");
        } else if (accountOperation.getTransactionCategory() == OperationCategoryEnum.CREDIT) {
            debitOccTemplateCode = paramBean.getProperty("occ.transferAccountOperation.debit", "DBT_TRS");
        } else {
            throw new BusinessException("Unrecognized operation category for the account operation with  " + accountOperation.getId() + "id");
        }

        OCCTemplate occTemplate = oCCTemplateService.findByCode(debitOccTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find AO Template with code:" + debitOccTemplateCode);
        }

        OtherCreditAndCharge newAccountOperation = new OtherCreditAndCharge();
        newAccountOperation.setMatchingAmount(BigDecimal.ZERO);
        newAccountOperation.setMatchingStatus(MatchingStatusEnum.O);
        newAccountOperation.setUnMatchingAmount(amount);
        newAccountOperation.setAmount(amount);
        newAccountOperation.setCustomerAccount(accountOperation.getCustomerAccount());
        newAccountOperation.setAccountingCode(occTemplate.getAccountingCode());
        newAccountOperation.setCode(occTemplate.getCode());
        newAccountOperation.setDescription(occTemplate.getDescription());
        newAccountOperation.setTransactionCategory(occTemplate.getOccCategory());
        newAccountOperation.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        newAccountOperation.setTransactionDate(new Date());
        newAccountOperation.setDueDate(new Date());

        if (accountOperation.getCustomerAccount() != null) {
            accountOperation.getCustomerAccount().getAccountOperations().add(newAccountOperation);
        }
        create(newAccountOperation);

        newAccountOperation.setReference(getRefrence(newAccountOperation.getId(), accountOperation.getReference(), AccountOperationActionEnum.c.name()));

        List<Long> accountOperations = new ArrayList<>();
        accountOperations.add(accountOperation.getId());
        accountOperations.add(newAccountOperation.getId());
        try {
            matchingCodeService.matchOperations(accountOperation.getCustomerAccount().getId(), null, accountOperations, null);
        } catch (Exception e) {
            log.error("Error on payment callback processing:", e);
            throw new BusinessException(e.getMessage(), e);
        }
        return newAccountOperation;
    }

    /**
     * Create the new account operation on the toCustomerAccountCode which is equivalent to the transfer.
     *
     * @param accountOperation the account operation to transfer
     * @param toCustomerAccount the destination customer account
     * @param amount the amount to transfer
     * @return the account operation.
     * @throws BusinessException business exception
     */
    public AccountOperation createToAccountOperation(AccountOperation accountOperation, CustomerAccount toCustomerAccount, BigDecimal amount) throws BusinessException {

        AccountOperation newAccountOperation = SerializationUtils.clone(accountOperation);
        newAccountOperation.setId(null);
        newAccountOperation.setMatchingAmount(BigDecimal.ZERO);
        newAccountOperation.setMatchingStatus(MatchingStatusEnum.O);
        newAccountOperation.setUnMatchingAmount(amount);
        newAccountOperation.setAmount(amount);
        newAccountOperation.setCustomerAccount(toCustomerAccount);
        newAccountOperation.setAccountingWritings(new ArrayList<>());
        newAccountOperation.setInvoices(null);
        newAccountOperation.setMatchingAmounts(new ArrayList<>());
        newAccountOperation.setTransactionDate(new Date());
        // newAccountOperation.setDueDate(new Date());
        create(newAccountOperation);

        newAccountOperation.setReference(getRefrence(newAccountOperation.getId(), accountOperation.getReference(), AccountOperationActionEnum.t.name()));
        return newAccountOperation;
    }

    /**
     * Transfer an account operation from a customer account to an other.
     *
     * @param accountOperation the account operation
     * @param transferCustomerAccountDto destination customer account
     * @throws BusinessException business exception
     */
    public void transferAccountOperation(AccountOperation accountOperation, TransferCustomerAccountDto transferCustomerAccountDto) throws BusinessException {

        String toCustomerAccountCode = transferCustomerAccountDto.getToCustomerAccountCode();
        BigDecimal amount = transferCustomerAccountDto.getAmount();

        // check if it is not the same account
        if (toCustomerAccountCode.equalsIgnoreCase(accountOperation.getCustomerAccount().getCode())) {
            throw new BusinessException("the source customer account is the same as the destination customer account");
        }

        CustomerAccount toCustomerAccount = customerAccountService.findByCode(toCustomerAccountCode);
        if (toCustomerAccount == null) {
            throw new BusinessException("The destination customer account with code : " + toCustomerAccountCode + " is not found");
        }

        // Create the new account operation on the fromCustomerAccountCode to settle the old one.
        createFromAccountOperation(accountOperation, amount);

        // Create the new account operation on the toCustomerAccountCode which is equivalent to the transfer.
        createToAccountOperation(accountOperation, toCustomerAccount, amount);

    }

    /**
     * Transfer an account operation from a customer account to an other.
     *
     * @param transferAccountOperationDto the transfer account operation Dto
     * @throws BusinessException business exception
     */
    public void transferAccountOperation(TransferAccountOperationDto transferAccountOperationDto) throws BusinessException {

        String fromCustomerAccountCode = transferAccountOperationDto.getFromCustomerAccountCode();
        Long accountOperationId = transferAccountOperationDto.getAccountOperationId();

        // Get the account operation to transfer
        AccountOperation accountOperation = findById(accountOperationId);
        if (accountOperation == null) {
            throw new BusinessException("the account operation " + accountOperationId + " not found");
        }

        CustomerAccount fromCustomerAccount = customerAccountService.findByCode(fromCustomerAccountCode);
        if (fromCustomerAccount == null) {
            throw new BusinessException("The source customer account with code : " + fromCustomerAccountCode + " is not found");
        }

        if (!fromCustomerAccount.equals(accountOperation.getCustomerAccount())) {
            throw new BusinessException(
                "the account operation " + accountOperationId + " to be the transfer doesn't belong to the source customer account " + fromCustomerAccountCode);
        }

        if (transferAccountOperationDto.getToCustomerAccounts() != null && !transferAccountOperationDto.getToCustomerAccounts().isEmpty()) {
            // Unmatching the accountOperation
            if (accountOperation.getMatchingStatus() == MatchingStatusEnum.L || accountOperation.getMatchingStatus() == MatchingStatusEnum.P) {
                matchingCodeService.unmatchingOperationAccount(accountOperation);
            }
            for (TransferCustomerAccountDto toCustomerAccount : transferAccountOperationDto.getToCustomerAccounts()) {
                transferAccountOperation(accountOperation, toCustomerAccount);
            }

            // Update the old account operation
            accountOperation.setReference(getRefrence(accountOperation.getId(), accountOperation.getReference(), AccountOperationActionEnum.s.name()));
        }
    }
}