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
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.DiscriminatorValue;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.SerializationUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.account.TransferAccountOperationDto;
import org.meveo.api.dto.account.TransferCustomerAccountDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.accounting.AccountingOperationAction;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.AccountingPeriodForceEnum;
import org.meveo.model.accounting.AccountingPeriodStatusEnum;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.*;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.accounting.impl.AccountingPeriodService;
import org.meveo.service.accounting.impl.SubAccountingPeriodService;
import org.meveo.service.base.PersistenceService;
import org.meveo.util.ApplicationProvider;

/**
 * AccountOperation service implementation.
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.2.2
 */
@Stateless
public class AccountOperationService extends PersistenceService<AccountOperation> {

    private static final String CLOSED_PERIOD_ERROR_DETAIL = "Closed period";

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The o CC template service. */
    @Inject
    private OCCTemplateService oCCTemplateService;

    /** The matching code service. */
    @Inject
    private MatchingCodeService matchingCodeService;
    
    @Inject
    private AccountingPeriodService accountingPeriodService;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private SubAccountingPeriodService subAccountingPeriodService;

    public AccountOperation createDeferralPayments(AccountOperation accountOperation, PaymentMethodEnum selectedPaymentMethod, Date paymentDate) {
        if(!appProvider.isPaymentDeferral()) {
            throw new BusinessException("Payment Deferral is not allowed");
        }

        if(selectedPaymentMethod != null
                && accountOperation.getCustomerAccount().getPaymentMethods().stream().noneMatch(paymentMethod1 -> paymentMethod1.getPaymentType().equals(selectedPaymentMethod))){
            throw new BusinessException("the selected paymentMethod does not belong to the account operation customer account");
        }
        
        LocalDate paymentLocalDate = LocalDate.ofInstant(paymentDate.toInstant(), ZoneId.systemDefault());
        
        LocalDate collectionDate = accountOperation.getCollectionDate() != null
        		?LocalDate.ofInstant(accountOperation.getCollectionDate().toInstant(), ZoneId.systemDefault())
        				:LocalDate.now();
        
        if ((paymentLocalDate.toEpochDay() <= collectionDate.toEpochDay())) {
            throw new BusinessException("the paymentDate should be greated than the current collection date");
        }
        int maxDelay = appProvider.getMaximumDelay() == null ? 0 : appProvider.getMaximumDelay();
        if ((paymentLocalDate.toEpochDay() - collectionDate.toEpochDay()) > maxDelay) {
            throw new BusinessException("the paymentDate should not exceed the current collection date by more than " + maxDelay);
        }
        
        if(appProvider.getMaximumDeferralPerInvoice() != null && accountOperation.getPaymentDeferralCount() != null) {
            if(accountOperation.getPaymentDeferralCount() + 1 > appProvider.getMaximumDeferralPerInvoice()){
                throw new BusinessException("the payment deferral count should not exceeds the configured maximum deferral per invoice.");
            }
        }
        if(selectedPaymentMethod != null){
            DayOfWeek paymentDateDayOfWeek = paymentLocalDate.plusDays(3).getDayOfWeek();
            if(PaymentMethodEnum.DIRECTDEBIT.equals(selectedPaymentMethod)
                    && (DayOfWeek.SATURDAY.equals(paymentDateDayOfWeek) || DayOfWeek.SUNDAY.equals(paymentDateDayOfWeek))){
                throw new BusinessException("the paymentDate plus three days must not be a saturday or sunday.");
            }
            accountOperation.setPaymentMethod(selectedPaymentMethod);
        }
        accountOperation.setPaymentDeferralCount(accountOperation.getPaymentDeferralCount()+1);
        accountOperation.setCollectionDate(paymentDate);
        accountOperation.setPaymentAction(PaymentActionEnum.PENDING_PAYMENT);
        return update(accountOperation);
    }

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

        create(aop);
        return aop.getId();

    }

    public Long createAndReturnReference(AccountOperation accountOperation) {
        fillOperationNumber(accountOperation);
        super.create(accountOperation);
        return accountOperation.getId();
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
            StringBuilder queryName = new StringBuilder("SELECT ao FROM AccountOperation AS ao, PaymentMethod AS pm")
                    .append(" WHERE ao.transactionCategory=:opCatToProcessIN ")
                    .append(" AND ao.type IN ('I','OCC') ")
                    .append(" AND (ao.matchingStatus ='O' OR ao.matchingStatus ='P') ")
                    .append(" AND ao.customerAccount.excludedFromPayment = FALSE ")
                    .append(" AND ao.customerAccount.id = pm.customerAccount.id ")
                    .append(" AND pm.paymentType =:paymentMethodIN ")
                    .append(" AND pm.preferred IS TRUE ")
                    .append(" AND ao.unMatchingAmount <> 0 ")
                    .append(" AND ao.collectionDate >=:fromDueDateIN ")
                    .append(" AND ao.collectionDate <:toDueDateIN ");

            if (seller != null) {
                queryName.append(" AND ao.seller =:sellerIN ");
            }

            if (OperationCategoryEnum.CREDIT == opCatToProcess) {
                queryName.append(" AND ao.code IN (:REFUNDABLE_ADJUSTEMENT_CODES) ");
            }

            Query query = getEntityManager().createQuery(queryName.toString())
                    .setParameter("paymentMethodIN", paymentMethodEnum)
                    .setParameter("fromDueDateIN", fromDueDate)
                    .setParameter("toDueDateIN", toDueDate)
                    .setParameter("opCatToProcessIN", opCatToProcess);

            if (seller != null) {
                query.setParameter("sellerIN", seller);
            }

            if (OperationCategoryEnum.CREDIT == opCatToProcess) {
                query.setParameter("REFUNDABLE_ADJUSTEMENT_CODES", System.getProperty("refundable.adjustement.codes", "ADJ_REF"));
            }

            return (List<AccountOperation>) query.getResultList();

        } catch (NoResultException e) {
            log.error("error = {}", e);
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
        qb.addCriterionEnum("matchingStatus", MatchingStatusEnum.O);
        qb.endOrClause();
        qb.startOrClause();
        qb.addCriterion("type", "=", "I", false);
        qb.addCriterion("type", "=", "OCC", false);
        qb.endOrClause();

        return (List<AccountOperation>) qb.getQuery(getEntityManager()).getResultList();
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
        
        this.handleAccountingPeriods(newAccountOperation);
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
        newAccountOperation.setAccountingSchemeEntries(new HashSet<>());
        newAccountOperation.setMatchingAmount(BigDecimal.ZERO);
        newAccountOperation.setMatchingStatus(MatchingStatusEnum.O);
        newAccountOperation.setUnMatchingAmount(amount);
        newAccountOperation.setAmount(amount);
        newAccountOperation.setCustomerAccount(toCustomerAccount);
        newAccountOperation.setAccountingEntries(new ArrayList<>());
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

    /**
     * Get accounting date of account operation
     *
     * @param accountOperation the account operation
     * @return accounting date
     */
    public Date getAccountingDate(AccountOperation accountOperation) {
        Date accountingDate = accountOperation.getAccountingDate();
        if(accountingDate == null){
            accountingDate = accountOperation.getTransactionDate();
            if (accountOperation instanceof Refund ||
                    accountOperation instanceof Payment ||
                    accountOperation instanceof RejectedPayment) {
                accountingDate = accountOperation.getCollectionDate() != null ? accountOperation.getCollectionDate()
                        : accountOperation.getDueDate();
            }
        }
        return accountingDate;
    }

    /**
	 * Step 1 : verify if the account operation is on open period.<br>
	 * Step 2 : Step 1’s condition is KO, AO_Job looks at the rule configured in accounting cycle.
	 * 
	 * @param accountOperation
	 */
	public void handleAccountingPeriods(AccountOperation accountOperation) {

        // Si aucune AP n'est définie dans le système, le système doit considérer toute l'année comme une AP open
        long count = accountingPeriodService.count();
        if (count == 0) {
            accountOperation.setStatus(AccountOperationStatus.POSTED);
            log.warn("No accounting period has been defined on this system");
            return;
        }

        // Recalculate the accounting date
        accountOperation.setAccountingDate(null);
        Date accountingDate = getAccountingDate(accountOperation);
        if (accountingDate == null) {
            rejectAccountOperation(accountOperation);
            log.warn("No transaction date found for these account operations : {}", accountOperation.getCode());
            return;
        }
        accountOperation.setAccountingDate(accountingDate);

        AccountingPeriod accountingPeriod = accountingPeriodService.findAccountingPeriodByDate(accountOperation.getAccountingDate());

        // If the accountingPeriod not found or it's closed.
        if (accountingPeriod == null || (accountingPeriod.getAccountingPeriodStatus() == AccountingPeriodStatusEnum.CLOSED &&
                accountingPeriod.getAccountingOperationAction() != AccountingOperationAction.FORCE)) {
            rejectAccountOperation(accountOperation);
            log.warn("No accounting period has been defined for this date : {}", accountOperation.getAccountingDate());
            return;
        } else if (accountingPeriod.getAccountingPeriodStatus() == AccountingPeriodStatusEnum.CLOSED) {
            accountingPeriod = accountingPeriodService.findOpenAccountingPeriod();
            if (accountingPeriod == null) {
                rejectAccountOperation(accountOperation);
                log.warn("No open accounting period has been founded");
                return;
            }
            accountOperation.setAccountingDate(accountingPeriod.getStartDate());
            forceAccountOperation(accountOperation, accountingPeriod);
            return;
        }

        // Case in which the accountingPeriod is found and it's open.
        AccountingOperationAction action = accountingPeriod.getAccountingOperationAction();
        // NO SUB ACCOUTING PERIOD USED
        if (Boolean.FALSE.equals(accountingPeriod.isUseSubAccountingCycles())) {
            accountOperation.setStatus(AccountOperationStatus.POSTED);
            // SUB ACCOUTING PERIOD ARE USED
        } else {
            SubAccountingPeriod subAccountingPeriod = subAccountingPeriodService.findByAccountingPeriod(accountingPeriod, accountOperation.getAccountingDate());
            if (subAccountingPeriod != null && subAccountingPeriod.isOpen()) {
                accountOperation.setStatus(AccountOperationStatus.POSTED);
            } else {
                if (action == AccountingOperationAction.FORCE) {
                    forceAccountOperation(accountOperation, accountingPeriod);
                } else {
                    rejectAccountOperation(accountOperation);
                }
            }
        }
    }
	
	private void rejectAccountOperation(AccountOperation accountOperation) {
		accountOperation.setAccountingDate(null);
		accountOperation.setStatus(AccountOperationStatus.REJECTED);
		accountOperation.setReason(AccountOperationRejectionReason.CLOSED_PERIOD);
        accountOperation.setErrorDetail(CLOSED_PERIOD_ERROR_DETAIL);
	}

	public void forceAccountOperation(AccountOperation accountOperation, AccountingPeriod accountingPeriod) {
		accountOperation.setStatus(AccountOperationStatus.POSTED);
		accountOperation.setReason(AccountOperationRejectionReason.FORCED);
		// setting the accoutingDate
		SubAccountingPeriod nextOpenSubAccountingPeriod = subAccountingPeriodService.findNextOpenSubAccountingPeriod(accountOperation.getAccountingDate());
		if (nextOpenSubAccountingPeriod != null && nextOpenSubAccountingPeriod.getStartDate() != null) {
			setAccountingDate(accountOperation, accountingPeriod, nextOpenSubAccountingPeriod.getStartDate());
		} else {
			rejectAccountOperation(accountOperation);
			log.warn("No open sub accounting period found");
		}
	}

	private void setAccountingDate(AccountOperation accountOperation, AccountingPeriod accountingPeriod, Date dateReference) {

		Date accoutingOperationDate = null;
		int customDay = Optional.ofNullable(accountingPeriod.getForceCustomDay()).orElse(0);
		
		AccountingPeriodForceEnum option = accountingPeriod.getForceOption();
		switch (option) {
		case FIRST_DAY:
			accoutingOperationDate = DateUtils.setDayToDate(dateReference, 1);
			break;
			
		case FIRST_SUNDAY:
			accoutingOperationDate = DateUtils.setDayOfWeekToDate(dateReference, Calendar.SUNDAY);
			break;
			
		case CUSTOM_DAY:
			Integer lastDayOfMonth = DateUtils.getActualMaximumDayForDate(dateReference);
			Integer firstDayOfMonth = DateUtils.getActualMinimumDayForDate(dateReference);
			if (customDay > lastDayOfMonth) {
				accoutingOperationDate = DateUtils.setDayToDate(dateReference, lastDayOfMonth);
			} else if (customDay < firstDayOfMonth) {
				accoutingOperationDate = DateUtils.setDayToDate(dateReference, firstDayOfMonth);
			} else {
				accoutingOperationDate = DateUtils.setDayToDate(dateReference, customDay);
			}
			break;
			
		default:
			break;
		} 
		
		accountOperation.setAccountingDate(accoutingOperationDate);
	}
	
    public int updateAOOperationActionToNone(List<Long> AOIds) throws BusinessException {
        String strQuery = "UPDATE AccountOperation o SET o.operationAction=org.meveo.model.payments.OperationActionEnum.NONE " + " WHERE o.id in (:AOIds) ";
        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("AOIds", AOIds);
        int affectedRecords = query.executeUpdate();
        log.debug("updated record AO to operation action equal to None count={}", affectedRecords);
        return affectedRecords;
    }

	/**
	 * @param status 
	 * @param list
	 */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateStatusInNewTransaction(List<AccountOperation> accountOperations, AccountOperationStatus status, String errorDetail) {
    	accountOperations.stream().forEach(ao -> {
			ao.setStatus(status);
            if (StringUtils.isNotBlank(errorDetail)) {
               ao.setErrorDetail(errorDetail);
            }
			update(ao);});
	}

    public List<AccountOperation> listByInvoice(Invoice invoice) {
        if(invoice == null){
            return null;
        }
        try {
            Query query = getEntityManager().createNamedQuery("AccountOperation.listByInvoice", AccountOperation.class);
            query.setParameter("invoice", invoice);
            return query.getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list AccountOperation by invoice", e);
            return null;
        }
    }
    
    /**
     * @param accountOperationId the accountOperation id.
     * @throws BusinessException business exception
     */
    public void addLitigation(Long accountOperationId) throws BusinessException {
        if (accountOperationId == null) {
            throw new BusinessException("accountOperationId is null");
        }
        addLitigation(findById(accountOperationId));
    }

    /**
     * @param accountOperation the accountOperation.
     * @throws BusinessException business exception.
     */
    public void addLitigation(AccountOperation accountOperation) throws BusinessException {

        if (accountOperation == null) {
            throw new BusinessException("accountOperation is null");
        }
        log.info("addLitigation accountOperation.Reference:" + accountOperation.getReference() + "status:" + accountOperation.getMatchingStatus());

        accountOperation.setMatchingStatus(MatchingStatusEnum.I);
        update(accountOperation);
        log.info("addLitigation accountOperation.Reference:" + accountOperation.getReference() + " ok");
    }

    /**
     * @param accountOperationId accountOperation id.
     * @throws BusinessException business exception.
     */
    public void cancelLitigation(Long accountOperationId) throws BusinessException {
        if (accountOperationId == null) {
            throw new BusinessException("accountOperationId is null");
        }
        cancelLitigation(findById(accountOperationId));
    }

    /**
     * @param accountOperation recored invoice
     * @throws BusinessException business exception.
     */
    public void cancelLitigation(AccountOperation accountOperation) throws BusinessException {

        if (accountOperation == null) {
            throw new BusinessException("accountOperation is null");
        }
        log.info("cancelLitigation accountOperation.Reference:" + accountOperation.getReference());
        if (accountOperation.getMatchingStatus() != MatchingStatusEnum.I) {
            throw new BusinessException("accountOperation is not on Litigation");
        }
        if(accountOperation.getAmount().compareTo(accountOperation.getMatchingAmount()) == 0) {
        	accountOperation.setMatchingStatus(MatchingStatusEnum.L);
        }else
        if(accountOperation.getAmount().compareTo(accountOperation.getUnMatchingAmount()) == 0) {
        	accountOperation.setMatchingStatus(MatchingStatusEnum.O);
        }else {
        	accountOperation.setMatchingStatus(MatchingStatusEnum.P);
        }
        
        update(accountOperation);
        log.info("cancelLitigation accountOperation.Reference:" + accountOperation.getReference() + " ok , status:"+ accountOperation.getMatchingStatus());
    }

    @SuppressWarnings("unchecked")
    public List<AccountOperation> findAoByStatus(boolean onlyClosedPeriods, AccountOperationStatus... statuses) {
        String namedQueryName = onlyClosedPeriods ? "AccountOperation.findAoClosedSubPeriodByStatus"
                : "AccountOperation.findAoByStatus";

        Query query = getEntityManager().createNamedQuery(namedQueryName);
        query.setParameter("AO_STATUS", Arrays.asList(statuses));

        return (List<AccountOperation>) query.getResultList();

    }

    public void resetOperationNumberSequence() {
        getEntityManager().createNativeQuery("ALTER SEQUENCE account_operation_number_seq RESTART WITH 1").executeUpdate();
    }

    public void fillOperationNumber(AccountOperation accountOperation)
    {
        BigInteger operationNumber =(BigInteger) getEntityManager().createNativeQuery("select nextval('account_operation_number_seq')").getSingleResult();
        accountOperation.setOperationNumber(operationNumber.longValue());
    }

    @Override
    public void create(AccountOperation entity) {
        fillOperationNumber(entity);
        super.create(entity);
    }

    @SuppressWarnings("unchecked")
    public List<AccountOperation> findByCustomerAccount(List<Long> aoIds, Long customerAccountId) {
        return getEntityManager().createNamedQuery("AccountOperation.findByCustomerAccount")
                .setParameter("AO_IDS", aoIds)
                .setParameter("CUSTOMERACCOUNT_ID", customerAccountId)
                .getResultList();

    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<AccountOperation> findAoWithoutMatchingCode() {
        List<AccountOperation> results =  getEntityManager().createNamedQuery("JournalEntry.findAoWithoutMatchingCode")
                .getResultList();

        // fetch lazies needed join
        Optional.ofNullable(results).orElse(Collections.emptyList())
                .forEach(recordedInvoice -> recordedInvoice.getMatchingAmounts().forEach(matchingAmount -> {
                            Optional.ofNullable(matchingAmount.getMatchingCode().getMatchingAmounts()).orElse(Collections.emptyList())
                                    .forEach(ma -> {
                                        ma.getAccountOperation().getMatchingAmounts().size();
                                    });
                                }));

        return results;

    }
}
