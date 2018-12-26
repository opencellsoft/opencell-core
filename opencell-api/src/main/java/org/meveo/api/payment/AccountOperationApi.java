package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.MatchingAmountDto;
import org.meveo.api.dto.payment.MatchingCodeDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.dto.response.payment.MatchedOperationDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.RejectedPayment;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingAmountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

/**
 * The Class AccountOperationApi.
 *
 * @author Edward P. Legaspi
 * @author anasseh
 * 
 * @lastModifiedVersion 5.0
 */
@Stateless
public class AccountOperationApi extends BaseApi {

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

    /** The recorded invoice service. */
    @Inject
    private RecordedInvoiceService recordedInvoiceService;
    
    @Inject
    private AccountingCodeService accountingCodeService;

    /**
     * Creates the.
     *
     * @param postData the post data
     * @return the long
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public Long create(AccountOperationDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getType())) {
            missingParameters.add("Type");
            handleMissingParameters();
        }
        AccountOperation accountOperation = null;
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
        }

        if ("OCC".equals(postData.getType()) && postData.getOtherCreditAndCharge() != null) {
            // otherCreditAndCharge
            OtherCreditAndCharge otherCreditAndCharge = new OtherCreditAndCharge();
            otherCreditAndCharge.setOperationDate(postData.getOtherCreditAndCharge().getOperationDate());
            accountOperation = otherCreditAndCharge;
        } else if ("R".equals(postData.getType()) && postData.getRejectedPayment() != null) {
            // rejectedPayment
            RejectedPayment rejectedPayment = new RejectedPayment();

            rejectedPayment.setRejectedType(postData.getRejectedPayment().getRejectedType());

            rejectedPayment.setBankLot(postData.getRejectedPayment().getBankLot());
            rejectedPayment.setBankReference(postData.getRejectedPayment().getBankReference());
            rejectedPayment.setRejectedDate(postData.getRejectedPayment().getRejectedDate());
            rejectedPayment.setRejectedDescription(postData.getRejectedPayment().getRejectedDescription());
            rejectedPayment.setRejectedCode(postData.getRejectedPayment().getRejectedCode());

            accountOperation = rejectedPayment;
        }

        if (accountOperation == null) {
            throw new MeveoApiException("Type and data mismatch OCC=otherCreditAndCharge, R=rejectedPayment.");
        }

        accountOperation.setDueDate(postData.getDueDate());
        accountOperation.setType(postData.getType());
        accountOperation.setTransactionDate(postData.getTransactionDate());
        accountOperation.setTransactionCategory(postData.getTransactionCategory());
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
        accountOperation.setAccountCodeClientSide(postData.getAccountCodeClientSide());
        accountOperation.setAmount(postData.getAmount());
        accountOperation.setMatchingAmount(postData.getMatchingAmount());
        accountOperation.setUnMatchingAmount(postData.getUnMatchingAmount());
        accountOperation.setCustomerAccount(customerAccount);

        accountOperation.setBankLot(postData.getBankLot());
        accountOperation.setBankReference(postData.getBankReference());
        accountOperation.setDepositDate(postData.getDepositDate());
        accountOperation.setBankCollectionDate(postData.getBankCollectionDate());

        accountOperation.setMatchingStatus(postData.getMatchingStatus());

        accountOperation.setOccCode(postData.getOccCode());
        accountOperation.setOccDescription(postData.getOccDescription());
        if (!StringUtils.isBlank(postData.getPaymentMethod())) {
            accountOperation.setPaymentMethod(PaymentMethodEnum.valueOf(postData.getPaymentMethod()));
        }
        accountOperation.setTaxAmount(postData.getTaxAmount());
        accountOperation.setAmountWithoutTax(postData.getAmountWithoutTax());
        accountOperation.setOrderNumber(postData.getOrderNumber());

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
    public AccountOperationsResponseDto list(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration("id", org.primefaces.model.SortOrder.DESCENDING, null, pagingAndFiltering,
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
                    result.getAccountOperations().getAccountOperation().add(accountOperationDto);
                }
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
    public void matchOperations(MatchOperationRequestDto postData) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException, Exception {
        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }
        if (postData.getAccountOperations() == null || postData.getAccountOperations().getAccountOperation() == null
                || postData.getAccountOperations().getAccountOperation().isEmpty()) {
            throw new BusinessException("no account operations");
        }
        List<Long> operationsId = new ArrayList<Long>();
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
        }
        if (accountOperation == null) {
            throw new EntityDoesNotExistsException(AccountOperation.class, postData.getAccountOperationId());
        }
        if (!customerAccount.getAccountOperations().contains(accountOperation)) {
            throw new BusinessException("The operationId " + postData.getAccountOperationId() + " is not for the customerAccount " + customerAccount.getCode());
        }
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
            matchingCodeService.unmatching(matchingCodeId);
        }
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

        if (!(accountOperation instanceof RecordedInvoice)) {
            throw new BusinessException("The operationId " + postData.getAccountOperationId() + " should be invoice");
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
        recordedInvoiceService.addLitigation(postData.getAccountOperationId());
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
        recordedInvoiceService.cancelLitigation(postData.getAccountOperationId());
    }

    /**
     * Find.
     *
     * @param id the id
     * @return the account operation dto
     * @throws MeveoApiException the meveo api exception
     */
    public AccountOperationDto find(Long id) throws MeveoApiException {
        AccountOperation ao = accountOperationService.findById(id);
        if (ao != null) {
            return new AccountOperationDto(ao, entityToDtoConverter.getCustomFieldsDTO(ao, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
        } else {
            throw new EntityDoesNotExistsException(AccountOperation.class, id);
        }
    }

    /**
     * Update payment method for all customerAccount AO's if customerAccountCode is set.Or single AO if aoId is set.
     *
     * @param customerAccountCode the customer account code
     * @param aoId the ao id
     * @param paymentMethod the payment method
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
     */
    public void updatePaymentMethod(String customerAccountCode, Long aoId, PaymentMethodEnum paymentMethod)
            throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(customerAccountCode) && StringUtils.isBlank(aoId)) {
            missingParameters.add("customerAccountCode or aoId");
        }
        if (StringUtils.isBlank(paymentMethod)) {
            missingParameters.add("paymentMethod");
        }
        handleMissingParameters();

        if (!StringUtils.isBlank(customerAccountCode)) {
            CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
            if (customerAccount == null) {
                throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
            }
            for (AccountOperation ao : customerAccount.getAccountOperations()) {
                updatePaymentMethod(ao, paymentMethod);
            }
        } else {
            AccountOperation ao = accountOperationService.findById(aoId);
            if (ao == null) {
                throw new EntityDoesNotExistsException(AccountOperation.class, aoId);
            }
            updatePaymentMethod(ao, paymentMethod);
        }

    }

    /**
     * Update payment method.
     *
     * @param ao the ao
     * @param paymentMethod the payment method
     * @throws BusinessException the business exception
     */
    private void updatePaymentMethod(AccountOperation ao, PaymentMethodEnum paymentMethod) throws BusinessException {
        if (MatchingStatusEnum.O == ao.getMatchingStatus()) {
            ao.setPaymentMethod(paymentMethod);
            accountOperationService.update(ao);
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
}