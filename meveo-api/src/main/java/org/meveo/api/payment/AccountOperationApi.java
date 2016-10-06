package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.MatchingAmountDto;
import org.meveo.api.dto.payment.MatchingAmountsDto;
import org.meveo.api.dto.payment.MatchingCodeDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.RejectedPayment;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingAmountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountOperationApi extends BaseApi {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private MatchingAmountService matchingAmountService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    public void create(AccountOperationDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getType())) {
            missingParameters.add("Type");
            handleMissingParameters();
        }
        AccountOperation accountOperation = null;
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount(), currentUser.getProvider());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
        }

        if ("OCC".equals(postData.getType()) && postData.getOtherCreditAndCharge() != null) {
            // otherCreditAndCharge
            OtherCreditAndCharge otherCreditAndCharge = new OtherCreditAndCharge();
            otherCreditAndCharge.setOperationDate(postData.getOtherCreditAndCharge().getOperationDate());
            accountOperation = (AccountOperation) otherCreditAndCharge;
        } else if ("R".equals(postData.getType()) && postData.getRejectedPayment() != null) {
            // rejectedPayment
            RejectedPayment rejectedPayment = new RejectedPayment();

            rejectedPayment.setRejectedType(postData.getRejectedPayment().getRejectedType());

            rejectedPayment.setBankLot(postData.getRejectedPayment().getBankLot());
            rejectedPayment.setBankReference(postData.getRejectedPayment().getBankReference());
            rejectedPayment.setRejectedDate(postData.getRejectedPayment().getRejectedDate());
            rejectedPayment.setRejectedDescription(postData.getRejectedPayment().getRejectedDescription());
            rejectedPayment.setRejectedCode(postData.getRejectedPayment().getRejectedCode());

            accountOperation = (AccountOperation) rejectedPayment;
        }

        if (accountOperation == null) {
            throw new MeveoApiException("Type and data mismatch OCC=otherCreditAndCharge, R=rejectedPayment.");
        }

        accountOperation.setDueDate(postData.getDueDate());
        accountOperation.setType(postData.getType());
        accountOperation.setTransactionDate(postData.getTransactionDate());
        accountOperation.setTransactionCategory(postData.getTransactionCategory());
        accountOperation.setReference(postData.getReference());
        accountOperation.setAccountCode(postData.getAccountCode());
        accountOperation.setAccountCodeClientSide(postData.getAccountCodeClientSide());
        accountOperation.setAmount(postData.getAmount());
        accountOperation.setMatchingAmount(postData.getMatchingAmount());
        accountOperation.setUnMatchingAmount(postData.getUnMatchingAmount());
        accountOperation.setCustomerAccount(customerAccount);

        accountOperation.setMatchingStatus(postData.getMatchingStatus());

        accountOperation.setOccCode(postData.getOccCode());
        accountOperation.setOccDescription(postData.getOccDescription());
        if (!StringUtils.isBlank(postData.getExcludedFromDunning())) {
            accountOperation.setExcludedFromDunning(postData.getExcludedFromDunning());
        } else {
            accountOperation.setExcludedFromDunning(false);
        }

        accountOperationService.create(accountOperation, currentUser);
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), accountOperation, true, currentUser, true);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        if (postData.getMatchingAmounts() != null) {
            for (MatchingAmountDto matchingAmountDto : postData.getMatchingAmounts().getMatchingAmount()) {
                MatchingAmount matchingAmount = new MatchingAmount();
                matchingAmount.setMatchingAmount(matchingAmountDto.getMatchingAmount());
                matchingAmount.setAccountOperation(accountOperation);
                if (matchingAmountDto.getMatchingCodes() != null) {
                    for (MatchingCodeDto matchingCodeDto : matchingAmountDto.getMatchingCodes().getMatchingCode()) {
                        MatchingCode matchingCode = matchingCodeService.findByCode(matchingCodeDto.getCode(), currentUser.getProvider());
                        if (matchingCode == null) {
                            matchingCode = new MatchingCode();
                            matchingCode.setCode(matchingCodeDto.getCode());
                        }

                        matchingCode.setMatchingType(matchingCodeDto.getMatchingType());

                        matchingCode.setMatchingDate(matchingCodeDto.getMatchingDate());
                        matchingCode.setMatchingAmountCredit(matchingCodeDto.getMatchingAmountCredit());
                        matchingCode.setMatchingAmountDebit(matchingCodeDto.getMatchingAmountDebit());

                        if (matchingCode.isTransient()) {
                            matchingCodeService.create(matchingCode, currentUser);
                        } else {
                            matchingCodeService.update(matchingCode, currentUser);
                        }

                        matchingAmount.setMatchingCode(matchingCode);
                    }
                }

                if (matchingAmount.isTransient()) {
                    matchingAmountService.create(matchingAmount, currentUser);
                } else {
                    matchingAmountService.update(matchingAmount, currentUser);
                }

                accountOperation.getMatchingAmounts().add(matchingAmount);
            }
        }
    }

    public AccountOperationsResponseDto list(String customerAccountCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
        }
        handleMissingParameters();

        AccountOperationsResponseDto result = new AccountOperationsResponseDto();

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        List<AccountOperation> accountOperations = accountOperationService.listAccountOperationByCustomerAccount(customerAccount, provider);

        for (AccountOperation accountOp : accountOperations) {
            AccountOperationDto accountOperationDto = new AccountOperationDto();
            accountOperationDto.setId(accountOp.getId());
            accountOperationDto.setDueDate(accountOp.getDueDate());
            accountOperationDto.setType(accountOp.getType());
            accountOperationDto.setTransactionDate(accountOp.getTransactionDate());
            accountOperationDto.setTransactionCategory(accountOp.getTransactionCategory());
            accountOperationDto.setReference(accountOp.getReference());
            accountOperationDto.setAccountCode(accountOp.getAccountCode());
            accountOperationDto.setAccountCodeClientSide(accountOp.getAccountCodeClientSide());
            accountOperationDto.setAmount(accountOp.getAmount());
            accountOperationDto.setMatchingAmount(accountOp.getMatchingAmount());
            accountOperationDto.setUnMatchingAmount(accountOp.getUnMatchingAmount());
            accountOperationDto.setMatchingStatus(accountOp.getMatchingStatus());
            accountOperationDto.setOccCode(accountOp.getOccCode());
            accountOperationDto.setOccDescription(accountOp.getOccDescription());
            accountOperationDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(accountOp));

            List<MatchingAmount> matchingAmounts = accountOp.getMatchingAmounts();
            MatchingAmountDto matchingAmountDto = null;
            MatchingAmountsDto matchingAmountsDto = new MatchingAmountsDto();
            for (MatchingAmount matchingAmount : matchingAmounts) {
                matchingAmountDto = new MatchingAmountDto();
				if (matchingAmount.getMatchingCode() != null) {
					matchingAmountDto.setMatchingCode(matchingAmount.getMatchingCode().getCode());
				}
                matchingAmountDto.setMatchingAmount(matchingAmount.getMatchingAmount());
                matchingAmountsDto.getMatchingAmount().add(matchingAmountDto);
            }
            accountOperationDto.setMatchingAmounts(matchingAmountsDto);

            result.getAccountOperations().getAccountOperation().add(accountOperationDto);
        }
        return result;

    }

    public void matchOperations(MatchOperationRequestDto postData, User currentUser) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException,
            Exception {
        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }
        if (postData.getAccountOperations() == null || postData.getAccountOperations().getAccountOperation() == null
                || postData.getAccountOperations().getAccountOperation().isEmpty()) {
            throw new BusinessException("no account operations");
        }
        List<Long> operationsId = new ArrayList<Long>();
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccountCode(), currentUser.getProvider());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccountCode());
        }
        if (postData.getAccountOperations() != null) {
            for (AccountOperationDto accountOperation : postData.getAccountOperations().getAccountOperation()) {
                AccountOperation accountOp = null;
                try {
                    accountOp = accountOperationService.findById(accountOperation.getId(), currentUser.getProvider());
                } catch (Exception e) {
                }
                if (accountOp == null) {
                    throw new EntityDoesNotExistsException(AccountOperation.class, accountOperation.getId());
                }
                operationsId.add(accountOp.getId());
            }
            matchingCodeService.matchOperations(customerAccount.getId(), customerAccount.getCode(), operationsId, null, currentUser);
        }

    }

    public void unMatchingOperations(UnMatchingOperationRequestDto postData, User currentUser) throws BusinessException, Exception {
        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(postData.getAccountOperationId())) {
            missingParameters.add("accountOperationId");
        }

        handleMissingParameters();

        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccountCode(), currentUser.getProvider());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccountCode());
        }
        AccountOperation accountOperation = null;
        try {
            accountOperation = accountOperationService.findById(postData.getAccountOperationId(), currentUser.getProvider());
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
            matchingCodeService.unmatching(matchingCodeId, currentUser);
        }
    }

    private void checkingLitigation(LitigationRequestDto postData, User currentUser) throws BusinessException, Exception {
        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccountCode");
        }
        if (StringUtils.isBlank(postData.getAccountOperationId())) {
            missingParameters.add("accountOperationId");
        }

        handleMissingParameters();

        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccountCode(), currentUser.getProvider());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccountCode());
        }
        AccountOperation accountOperation = null;
        try {
            accountOperation = accountOperationService.findById(postData.getAccountOperationId(), currentUser.getProvider());
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

    public void addLitigation(LitigationRequestDto postData, User currentUser) throws BusinessException, Exception {
        checkingLitigation(postData, currentUser);
        recordedInvoiceService.addLitigation(postData.getAccountOperationId(), currentUser);
    }

    public void cancelLitigation(LitigationRequestDto postData, User currentUser) throws BusinessException, Exception {
        checkingLitigation(postData, currentUser);
        recordedInvoiceService.cancelLitigation(postData.getAccountOperationId(), currentUser);
    }

}
