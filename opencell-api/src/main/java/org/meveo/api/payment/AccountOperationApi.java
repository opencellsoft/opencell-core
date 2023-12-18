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

package org.meveo.api.payment;

import static java.util.Optional.ofNullable;
import static org.meveo.commons.utils.ReflectionUtils.getSubclassObjectByDiscriminatorValue;
import static org.meveo.model.payments.AccountOperationStatus.EXPORTED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.TransferAccountOperationDto;
import org.meveo.api.dto.account.TransferCustomerAccountDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.MatchingAmountDto;
import org.meveo.api.dto.payment.MatchingCodeDto;
import org.meveo.api.dto.payment.OtherCreditAndChargeDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.dto.response.payment.MatchedOperationDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.ObjectFilter;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationRejectionReason;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Journal;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.model.payments.RejectedPayment;
import org.meveo.model.payments.WriteOff;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.JournalReportService;
import org.meveo.service.payments.impl.MatchingAmountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AccountOperationApi.
 *
 * @author Edward P. Legaspi
 * @author anasseh
 * @author melyoussoufi
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class AccountOperationApi extends BaseApi {

    private static final String PPL_INSTALLMENT = "PPL_INSTALLMENT";

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The account operation service. */
    @Inject
    private AccountOperationService accountOperationService;

    /** The matching code service. */
    @Inject
    private MatchingCodeService matchingCodeService;

    /** The matching amount service. */
    @Inject
    private MatchingAmountService matchingAmountService;

    
    @Inject
    private AccountingCodeService accountingCodeService;
    
    @Inject
    private JournalReportService journalService;

    @Inject
    private PaymentPlanService paymentPlanService;
    
    @Inject
    private OCCTemplateService oCCTemplateService;

    /**
     * Create account operation.
     *
     * @param postData AccountOperation resource
     * @return account operation id
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public Long create(AccountOperationDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getType())) {
            missingParameters.add("Type");
            handleMissingParameters();
        }

        Object aoSubclassObject = getSubclassObjectByDiscriminatorValue(AccountOperation.class, postData.getType());
        AccountOperation accountOperation = null;
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount());
        OperationCategoryEnum transactionCategory = postData.getTransactionCategory();
        if(transactionCategory == null){
            transactionCategory = ((AccountOperation)aoSubclassObject).getTransactionCategory();
        }
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
        }

        if (aoSubclassObject == null) {
            throw new MeveoApiException("Type and data mismatch OCC=otherCreditAndCharge, R=rejectedPayment, W=writeOff.");
        }

        if (aoSubclassObject instanceof OtherCreditAndCharge) {
            // otherCreditAndCharge
            OtherCreditAndCharge otherCreditAndCharge = new OtherCreditAndCharge();
            if (postData.getOtherCreditAndCharge() != null) {
                otherCreditAndCharge.setOperationDate(postData.getOtherCreditAndCharge().getOperationDate());
            }
            accountOperation = otherCreditAndCharge;
        } else if (aoSubclassObject instanceof RejectedPayment) {
            // rejectedPayment
            RejectedPayment rejectedPayment = new RejectedPayment();

            if (postData.getRejectedPayment() != null) {
                rejectedPayment.setRejectedType(postData.getRejectedPayment().getRejectedType());

                rejectedPayment.setBankLot(postData.getRejectedPayment().getBankLot());
                rejectedPayment.setBankReference(postData.getRejectedPayment().getBankReference());
                rejectedPayment.setRejectedDate(postData.getRejectedPayment().getRejectedDate());
                rejectedPayment.setRejectedDescription(postData.getRejectedPayment().getRejectedDescription());
                rejectedPayment.setRejectedCode(postData.getRejectedPayment().getRejectedCode());
            }
            accountOperation = rejectedPayment;
        } else if (aoSubclassObject instanceof WriteOff) {
            WriteOff writeOff = new WriteOff();
            transactionCategory = OperationCategoryEnum.CREDIT;
            accountOperation = writeOff;
        }

        if(aoSubclassObject instanceof RecordedInvoice) {
        	accountOperation = new RecordedInvoice();
            accountOperation.setAccountingDate(postData.getTransactionDate());
        }

        if(aoSubclassObject instanceof Refund) {
        	accountOperation = new Refund();
        }

        if(aoSubclassObject instanceof Payment) {
        	accountOperation = new Payment();
            accountOperation.setAccountingDate(postData.getCollectionDate());
        }

        accountOperation.setDueDate(postData.getDueDate());
        if(postData.getDueDate() == null) {
            accountOperation.setDueDate(new Date());
        }
        OtherCreditAndChargeDto otherCreditAndCharge = postData.getOtherCreditAndCharge();
        if (aoSubclassObject instanceof OtherCreditAndCharge && otherCreditAndCharge != null && otherCreditAndCharge.getOperationDate() == null) {
            ((OtherCreditAndCharge) aoSubclassObject).setOperationDate(new Date());
        }
        accountOperation.setType(postData.getType());

        accountOperation.setTransactionCategory(transactionCategory);
        accountOperation.setReference(postData.getReference());
        if (!StringUtils.isBlank(postData.getAccountingCode())) {
            AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountingCode());
            if (accountingCode == null) {
                throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountingCode());
            }
            accountOperation.setAccountingCode(accountingCode);
        } else {
            // backward compatibility
            if (!StringUtils.isBlank(postData.getAccountCode())) {
                AccountingCode accountingCode = accountingCodeService.findByCode(postData.getAccountCode());
                if (accountingCode == null) {
                    throw new EntityDoesNotExistsException(AccountingCode.class, postData.getAccountCode());
                }
                accountOperation.setAccountingCode(accountingCode);
            } 
        }
        
        OCCTemplate occTemplate = oCCTemplateService.findByCode(postData.getCode());
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + postData.getCode());
        }
        if (!occTemplate.isManualCreationEnabled()) {
            throw new BusinessException(String.format("Creation is prohibited; occTemplate %s is not allowed for manual creation", postData.getCode()));
        }
        
        accountOperation.setAccountCodeClientSide(postData.getAccountCodeClientSide());
        accountOperation.setAmount(postData.getAmount());
        accountOperation.setMatchingAmount(postData.getMatchingAmount());
        if(postData.getMatchingAmount() == null){
            accountOperation.setMatchingAmount(BigDecimal.ZERO);
        }
        accountOperation.setUnMatchingAmount(postData.getUnMatchingAmount());
        accountOperation.setCustomerAccount(customerAccount);

        accountOperation.setBankLot(postData.getBankLot());
        accountOperation.setBankReference(postData.getBankReference());
        accountOperation.setDepositDate(postData.getDepositDate());
        accountOperation.setBankCollectionDate(postData.getBankCollectionDate());

        accountOperation.setMatchingStatus(postData.getMatchingStatus());
        if (postData.getMatchingStatus() == null) {
            accountOperation.setMatchingStatus(MatchingStatusEnum.O);
        }

        accountOperation.setCode(postData.getCode());
        if(StringUtils.isBlank(postData.getCode())){
            accountOperation.setCode(((BusinessEntity)aoSubclassObject).getCode());
        }
        accountOperation.setDescription(postData.getDescription());
        if (!StringUtils.isBlank(postData.getPaymentMethod())) {
            accountOperation.setPaymentMethod(PaymentMethodEnum.valueOf(postData.getPaymentMethod()));
        }
        accountOperation.setTaxAmount(postData.getTaxAmount());
        accountOperation.setAmountWithoutTax(postData.getAmountWithoutTax());
        accountOperation.setOrderNumber(postData.getOrderNumber());
        accountOperation.setCollectionDate(postData.getCollectionDate() == null ? postData.getBankCollectionDate() : postData.getCollectionDate());
        
        if (!StringUtils.isBlank(postData.getJournalCode())) {
        	Journal journal = journalService.findByCode(postData.getJournalCode());
            if (journal == null) {
                throw new EntityDoesNotExistsException(Journal.class, postData.getJournalCode());
            }
            accountOperation.setJournal(journal);
        }
        accountOperation.setStatus(postData.getStatus());
        accountOperation.setReason(postData.getReason());
        accountOperation.setAccountingExportFile(postData.getAccountingExportFile());
        accountOperation.setPaymentInfo(postData.getPaymentInfo());
        accountOperation.setPaymentInfo1(postData.getPaymentInfo1());
        accountOperation.setPaymentInfo2(postData.getPaymentInfo2());
        accountOperation.setPaymentInfo3(postData.getPaymentInfo3());
        accountOperation.setPaymentInfo4(postData.getPaymentInfo4());
        accountOperation.setPaymentInfo5(postData.getPaymentInfo5());
        accountOperation.setPaymentInfo6(postData.getPaymentInfo6());

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), accountOperation, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        accountOperationService.handleAccountingPeriods(accountOperation);
        accountOperationService.create(accountOperation);

        if (postData.getMatchingAmounts() != null && postData.getMatchingAmounts().getMatchingAmount() != null) {
            for (MatchingAmountDto matchingAmountDto : postData.getMatchingAmounts().getMatchingAmount()) {
                MatchingAmount matchingAmount = new MatchingAmount();
                matchingAmount.setMatchingAmount(matchingAmountDto.getMatchingAmount());
                matchingAmount.setAccountOperation(accountOperation);
                if (matchingAmountDto.getMatchingCodes() != null) {
                    for (MatchingCodeDto matchingCodeDto : matchingAmountDto.getMatchingCodes().getMatchingCode()) {
                        MatchingCode matchingCode = matchingCodeService.findByCode(matchingCodeDto.getCode());
                        if (matchingCode == null) {
                            matchingCode = new MatchingCode();
                            matchingCode.setCode(matchingCodeDto.getCode());
                        }

                        matchingCode.setMatchingType(matchingCodeDto.getMatchingType());

                        matchingCode.setMatchingDate(matchingCodeDto.getMatchingDate());
                        matchingCode.setMatchingAmountCredit(matchingCodeDto.getMatchingAmountCredit());
                        matchingCode.setMatchingAmountDebit(matchingCodeDto.getMatchingAmountDebit());

                        if (matchingCode.isTransient()) {
                            matchingCodeService.create(matchingCode);
                        } else {
                            matchingCodeService.update(matchingCode);
                        }

                        matchingAmount.setMatchingCode(matchingCode);
                    }
                }

                if (matchingAmount.isTransient()) {
                    matchingAmountService.create(matchingAmount);
                } else {
                    matchingAmountService.update(matchingAmount);
                }

                accountOperation.getMatchingAmounts().add(matchingAmount);
            }
        }
        return accountOperation.getId();
    }

    /**
     * List.
     * 
     * @param pagingAndFiltering paging and filtering
     * @return the account operations response dto
     * @throws MeveoApiException the meveo api exception
     */
    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "accountOperations.accountOperation", itemPropertiesToFilter = {
            @FilterProperty(property = "customerAccount", entityClass = CustomerAccount.class)})
    public AccountOperationsResponseDto list(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration("id", PagingAndFiltering.SortOrder.DESCENDING, null, pagingAndFiltering,
            AccountOperation.class);

        Long totalCount = accountOperationService.count(paginationConfiguration);

        AccountOperationsResponseDto result = new AccountOperationsResponseDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<AccountOperation> accountOperations = accountOperationService.list(paginationConfiguration);
            if (accountOperations != null) {
                for (AccountOperation accountOperation : accountOperations) {
                    AccountOperationDto accountOperationDto = new AccountOperationDto(accountOperation, entityToDtoConverter.getCustomFieldsDTO(accountOperation, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                    setAuditableFieldsDto(accountOperation, accountOperationDto);
                    result.getAccountOperations().getAccountOperation().add(accountOperationDto);
                }
            }
        }
        return result;
    }
    
    /**
     * List.
     * 
     * @param customerAccountCode customerAccountCode
     * @return the account operations response dto
     * @throws MeveoApiException the meveo api exception
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = CustomerAccount.class))
    public AccountOperationsResponseDto listByCustomerAccountCode(String customerAccountCode, Integer firstRow, Integer numberOfRows) throws MeveoApiException {
        
        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }
        
        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        AccountOperationsResponseDto result = new AccountOperationsResponseDto();
        List<AccountOperation> accountOperations = accountOperationService.listByCustomerAccount(customerAccount, firstRow, numberOfRows);
        if (accountOperations != null) {
            for (AccountOperation accountOperation : accountOperations) {
                AccountOperationDto accountOperationDto = new AccountOperationDto(accountOperation, entityToDtoConverter.getCustomFieldsDTO(accountOperation, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                result.getAccountOperations().getAccountOperation().add(accountOperationDto);
            }
        }

        return result;
    }

    /**
     * Match operations.
     *
     * @param postData the post data
     * @throws BusinessException the business exception
     * @throws NoAllOperationUnmatchedException the no all operation unmatched exception
     * @throws UnbalanceAmountException the unbalance amount exception
     * @throws Exception the exception
     */
    public void matchOperations(MatchOperationRequestDto postData)
            throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, Exception {
        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }
        if (postData.getAccountOperations() == null || postData.getAccountOperations().getAccountOperation() == null
                || postData.getAccountOperations().getAccountOperation().isEmpty()) {
            throw new BusinessException("no account operations");
        }
        List<Long> operationsId = new ArrayList<>();
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccountCode());
        }
        if (postData.getAccountOperations() != null) {
            for (AccountOperationDto accountOperation : postData.getAccountOperations().getAccountOperation()) {
                AccountOperation accountOp = null;
                try {
                    accountOp = accountOperationService.findById(accountOperation.getId());
                } catch (Exception e) {
                    Logger log = LoggerFactory.getLogger(this.getClass());
                    log.info("An exception is raised ! Cannot find accountOp by ID");
                }
                if (accountOp == null) {
                    throw new EntityDoesNotExistsException(AccountOperation.class, accountOperation.getId());
                }
                operationsId.add(accountOp.getId());
            }
            matchingCodeService.matchOperations(customerAccount.getId(), customerAccount.getCode(), operationsId, null);
        }

    }

    /**
     * Un matching operations.
     *
     * @param postData the post data
     * @throws BusinessException the business exception
     * @throws Exception the exception
     */
    public void unMatchingOperations(UnMatchingOperationRequestDto postData) throws BusinessException, Exception {
        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(postData.getAccountOperationId())) {
            missingParameters.add("accountOperationId");
        }

        handleMissingParameters();

        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccountCode());
        }
        AccountOperation accountOperation = null;
        try {
            accountOperation = accountOperationService.findById(postData.getAccountOperationId());
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.info("An exception is raised ! Cannot find accountOperation by ID");
        }
        if (accountOperation == null) {
            throw new EntityDoesNotExistsException(AccountOperation.class, postData.getAccountOperationId());
        }
        if (!customerAccount.getAccountOperations().contains(accountOperation)) {
            throw new BusinessException("The operationId " + postData.getAccountOperationId() + " is not for the customerAccount " + customerAccount.getCode());
        }

        List<Long> matchingCodesToUnmatch = new ArrayList<>();

        // Check if the postData.getMatchingAmountIds() value are contained in accountOperation.getMatchingAmounts()
        // That should avoid pass invalid of incorrect id, and after unmatch all related matchingAmout for the AO
        if (CollectionUtils.isNotEmpty(postData.getMatchingAmountIds()) && CollectionUtils.isNotEmpty(accountOperation.getMatchingAmounts())) {
            List<Long> requestMathchingAmountIds = new ArrayList<>(postData.getMatchingAmountIds());
            List<Long> aoMatchingAmountIds = accountOperation.getMatchingAmounts().stream()
                    .map(MatchingAmount::getId)
                    .collect(Collectors.toList());

            requestMathchingAmountIds.removeAll(aoMatchingAmountIds);

            if (CollectionUtils.isNotEmpty(requestMathchingAmountIds)) {
                throw new BusinessException("Those matchingAmoutIds " + requestMathchingAmountIds + " are not present for AO passed to unmatch=" + accountOperation.getId());
            }
        }

        // PPL Aos id : used to update payment plan and invoice status, if umatch operation is made on a createdAos
        List<Long> pplAosIds = new ArrayList<>();

        for (MatchingAmount matchingAmount : accountOperation.getMatchingAmounts()) {
            if (CollectionUtils.isNotEmpty(postData.getMatchingAmountIds()) && !postData.getMatchingAmountIds().contains(matchingAmount.getId())) {
                continue;
            } else {
                MatchingCode matchingCode = matchingAmount.getMatchingCode();
                if (matchingCode != null) {
                    pplAosIds = getUmatchedPPLAosId(matchingCode);
                    matchingCodesToUnmatch.add(matchingCode.getId());
                }
            }
        }

        for (Long matchingCodeId : matchingCodesToUnmatch) {
            matchingCodeService.unmatching(matchingCodeId);
        }

        // Update PaymentPlan/Invoice related to those for which the unmatching is made
        paymentPlanService.toActivate(pplAosIds);

    }

    private List<Long> getUmatchedPPLAosId(MatchingCode matchingCode) {
        return Optional.ofNullable(matchingCode.getMatchingAmounts()).orElse(Collections.emptyList())
                .stream().filter(ma -> PPL_INSTALLMENT.equals(ma.getAccountOperation().getCode()))
                .map(matchingAmount -> matchingAmount.getAccountOperation().getId())
                .collect(Collectors.toList());

    }

    /**
     * Checking litigation.
     *
     * @param postData the post data
     * @throws BusinessException the business exception
     * @throws Exception the exception
     */
    private void checkingLitigation(LitigationRequestDto postData) throws BusinessException, Exception {
        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(postData.getAccountOperationId())) {
            missingParameters.add("accountOperationId");
        }

        handleMissingParameters();

        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccountCode());
        }
        AccountOperation accountOperation = null;
        try {
            accountOperation = accountOperationService.findById(postData.getAccountOperationId());
        } catch (Exception e) {
        }
        if (accountOperation == null) {
            throw new EntityDoesNotExistsException(AccountOperation.class, postData.getAccountOperationId());
        }
        if (!customerAccount.getAccountOperations().contains(accountOperation)) {
            throw new BusinessException("The operationId " + postData.getAccountOperationId() + " is not for the customerAccount " + customerAccount.getCode());
        }

    }

    /**
     * Adds the litigation.
     *
     * @param postData the post data
     * @throws BusinessException the business exception
     * @throws Exception the exception
     */
    public void addLitigation(LitigationRequestDto postData) throws BusinessException, Exception {
        checkingLitigation(postData);
        accountOperationService.addLitigation(postData.getAccountOperationId());
    }

    /**
     * Cancel litigation.
     *
     * @param postData the post data
     * @throws BusinessException the business exception
     * @throws Exception the exception
     */
    public void cancelLitigation(LitigationRequestDto postData) throws BusinessException, Exception {
        checkingLitigation(postData);
        accountOperationService.cancelLitigation(postData.getAccountOperationId());
    }

    /**
     * Find.
     *
     * @param id the id
     * @return the account operation dto
     * @throws MeveoApiException the meveo api exception
     */
    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "customerAccount", entityClass = CustomerAccount.class)})
    public AccountOperationDto find(Long id) throws MeveoApiException {
        AccountOperation ao = accountOperationService.findById(id);
        if (ao != null) {

            AccountOperationDto accountOperationDto = new AccountOperationDto(ao, entityToDtoConverter.getCustomFieldsDTO(ao, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
            setAuditableFieldsDto(ao, accountOperationDto);
            return accountOperationDto;
        } else {
            throw new EntityDoesNotExistsException(AccountOperation.class, id);
        }
    }

    /**
     * List matched operations.
     *
     * @param accountOperationId the account operation id
     * @return the list
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws MissingParameterException the missing parameter exception
     */
    public List<MatchedOperationDto> listMatchedOperations(Long accountOperationId) throws EntityDoesNotExistsException, MissingParameterException {

        List<MatchedOperationDto> matchedOperationsDtos = new ArrayList<>();

        if (accountOperationId == null) {
            missingParameters.add("accountOperationId");
        }
        handleMissingParameters();

        AccountOperation accountOperation = accountOperationService.findById(accountOperationId);

        if (accountOperation == null) {
            throw new EntityDoesNotExistsException(AccountOperation.class, accountOperationId);
        }

        for (MatchingAmount matchingAmountPrimary : accountOperation.getMatchingAmounts()) {
            MatchingCode matchingCode = matchingAmountPrimary.getMatchingCode();
            for (MatchingAmount matchingAmount : matchingCode.getMatchingAmounts()) {
                matchedOperationsDtos.add(new MatchedOperationDto(matchingCode, matchingAmount));
            }
        }

        return matchedOperationsDtos;
    }

    /**
     * Transfer an account operation from a customer account to an other.
     *
     * @param transferAccountOperationDto the transfer account operation Dto
     */
    public void transferAccountOperation(TransferAccountOperationDto transferAccountOperationDto) {

        if (StringUtils.isBlank(transferAccountOperationDto.getFromCustomerAccountCode())) {
            missingParameters.add("fromCustomerAccountCode");
        }
        if (StringUtils.isBlank(transferAccountOperationDto.getAccountOperationId())) {
            missingParameters.add("accountOperationId");
        }
        if (transferAccountOperationDto.getToCustomerAccounts() == null || transferAccountOperationDto.getToCustomerAccounts().isEmpty()) {
            missingParameters.add("toCustomerAccounts");
        } else {
            for (int i = 0; i < transferAccountOperationDto.getToCustomerAccounts().size(); i++) {
                TransferCustomerAccountDto transferCustomerAccountDto = transferAccountOperationDto.getToCustomerAccounts().get(i);
                if (StringUtils.isBlank(transferCustomerAccountDto.getToCustomerAccountCode())) {
                    missingParameters.add("customerAccounts[" + i + "].toCustomerAccountCode");
                }
                if (transferCustomerAccountDto.getAmount() == null || transferCustomerAccountDto.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                    missingParameters.add("customerAccounts[" + i + "].amount");
                }
            }
        }

        handleMissingParameters();

        accountOperationService.transferAccountOperation(transferAccountOperationDto);
    }

    /**
     * Update accounting date of an existing account operation
     *
     * @param aoID : accounting operation identifier
     * @param accountingDate : accounting operation date
     * @return updated account operation
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public AccountOperation updateAccountingDate(Long aoID, Date accountingDate) throws MeveoApiException, BusinessException {
        AccountOperation accountOperation = ofNullable(accountOperationService.findById(aoID))
                .orElseThrow(() -> new EntityDoesNotExistsException(AccountOperation.class, aoID));
        if (accountOperation.getStatus().equals(EXPORTED)) {
            throw new BusinessException("Can not update accounting date, account operation is EXPORTED");
        }
        accountOperation.setAccountingDate(accountingDate);
        accountOperation.setReason(AccountOperationRejectionReason.FORCED);
        
        return accountOperationService.update(accountOperation);
    }

	/**
	 * @param id
	 * @param newStatus
	 */
	public void updateStatus(Long id, String newStatus) {
		AccountOperationStatus statusEnum = AccountOperationStatus.valueOf(newStatus);
        AccountOperation accountOperation = ofNullable(accountOperationService.findById(id))
                .orElseThrow(() -> new EntityDoesNotExistsException(AccountOperation.class, id));
        if(AccountOperationStatus.POSTED.equals(accountOperation.getStatus()) && AccountOperationStatus.EXPORTED.equals(statusEnum)) {
        	accountOperation.setStatus(statusEnum);
        	accountOperationService.update(accountOperation);
        } else {
        	throw new ConflictException("not possible to change accountOperation status from '"+accountOperation.getStatus()+"' to '"+newStatus+"'");
        }
		
	}
}